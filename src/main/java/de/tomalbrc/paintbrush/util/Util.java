package de.tomalbrc.paintbrush.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.gen.TextureAtlasGenerator;
import de.tomalbrc.paintbrush.impl.gen.TextureGenerator;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Util {
    public static Map<BlockState, Set<StateModelVariant>> stateSetMap(ResourcePackBuilder resourcePackBuilder, Block block) {
        var blockId = BuiltInRegistries.BLOCK.getKey(block);
        var stateDefString = resourcePackBuilder.getStringDataOrSource("assets/" + blockId.getNamespace() + "/blockstates/" + blockId.getPath() + ".json");

        assert stateDefString != null;

        var parsedBlockStateDef = JsonParser.parseString(stateDefString);
        var variants = parsedBlockStateDef.getAsJsonObject().get("variants").getAsJsonObject();

        return getBlockStateSetMap(variants, blockId);
    }

    public static <T, K> Collection<T> distinctByField(Collection<T> collection, Function<T, K> keyExtractor) {
        Set<K> seen = new HashSet<>();
        return collection.stream()
                .filter(element -> seen.add(keyExtractor.apply(element)))
                .collect(Collectors.toList());
    }

    public static List<JsonObject> addBlockPermutations(ResourcePackBuilder resourcePackBuilder, Map<BlockState, Set<StateModelVariant>> map) throws Exception {
        List<JsonObject> r = new ArrayList<>();

        for (Map.Entry<BlockState, Set<StateModelVariant>> variantEntry : map.entrySet()) {
            Set<StateModelVariant> variantSet = variantEntry.getValue();
            var distinct = distinctByField(variantSet, StateModelVariant::model);

            for (StateModelVariant variant : distinct) {
                var modelPath = AssetPaths.model(variant.model()) + ".json";

                var modelData = resourcePackBuilder.getStringDataOrSource(modelPath);
                if (modelData != null) {
                    colorModel(resourcePackBuilder, modelData, variant.model(), r);
                }
            }
        }

        return r;
    }

    private static void colorModel(ResourcePackBuilder resourcePackBuilder, String modelData, ResourceLocation modelPath, List<JsonObject> r) throws Exception {
        var json = ModelAsset.fromJson(modelData);

        for (String value : json.textures().values()) {
            if (!value.startsWith("#")) {
                // we change the namespace here so it uses the copied texture and not the one in the minecraft namespace
                var parsed = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, ResourceLocation.parse(value).getPath());
                JsonObject source = TextureAtlasGenerator.getAtlasSourceJson(parsed);
                r.add(source);
            }
        }

        for (DyeColor dye : DyeColor.values()) {
            var path = AssetPaths.model(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, modelPath.getPath() + "_" + dye.getName())) + ".json";

            var modelBuilder = ModelAsset.builder();
            if (json.parent().isPresent())
                modelBuilder.parent(json.parent().get());

            for (Map.Entry<String, String> entry : json.textures().entrySet()) {
                if (!entry.getValue().startsWith("#")) {
                    var valId = ResourceLocation.parse(entry.getValue());

                    ResourceLocation textureLocation = ResourceLocation.parse(entry.getValue());

                    byte[] textureData = resourcePackBuilder.getDataOrSource(AssetPaths.texture(textureLocation) + ".png");
                    BufferedImage img = normalizeToARGB(ImageIO.read(new ByteArrayInputStream(textureData)));

                    // copy the texture so resourcepack can override the texture without breaking stuff
                    resourcePackBuilder.addData(AssetPaths.texture(PaintBrushMod.MODID, textureLocation.getPath()) + ".png", textureData);

                    var paletteKeyImage = TextureGenerator.generatePaletteKey(img);
                    resourcePackBuilder.addData("assets/" + PaintBrushMod.MODID + "/textures/colormap/color_palettes/" + valId.getPath() + "_key" + ".png", TextureGenerator.data(paletteKeyImage));

                    var paletted = TextureGenerator.generatePaletteColor(paletteKeyImage, dye.getName());
                    resourcePackBuilder.addData("assets/" + PaintBrushMod.MODID + "/textures/colormap/color_palettes/" + valId.getPath() + "_" + dye.getName() + ".png", paletted);

                    modelBuilder.texture(entry.getKey(), ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, textureLocation.getPath() + "_" + dye.getName()).toString());
                }
            }

            resourcePackBuilder.addData(path, modelBuilder.build().toBytes());
        }
    }

    private static @NotNull Map<BlockState, Set<StateModelVariant>> getBlockStateSetMap(JsonObject variants, ResourceLocation blockId) {
        Map<BlockState, Set<StateModelVariant>> map = new IdentityHashMap<>();
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            BlockStateParser.BlockResult parsed;
            String str = String.format("%s[%s]", blockId, entry.getKey());
            try {
                parsed = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, str, false);
                if (entry.getValue().isJsonArray()) {
                    Set<StateModelVariant> stateModelVariants = new HashSet<>();
                    for (JsonElement jsonElement : entry.getValue().getAsJsonArray()) {
                        StateModelVariant.CODEC.decode(JsonOps.INSTANCE, jsonElement).ifSuccess(x -> {
                            var modelVar = x.getFirst().getFirst();
                            stateModelVariants.add(modelVar);
                        });
                    }
                    map.put(parsed.blockState(), stateModelVariants);
                } else {
                    var o = entry.getValue().getAsJsonObject();
                    StateModelVariant.CODEC.decode(JsonOps.INSTANCE, entry.getValue()).ifSuccess(x -> {
                        var modelVar = x.getFirst().getFirst();
                        map.put(parsed.blockState(), Set.of(modelVar));
                    });
                }

            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid BlockState value: " + str);
            }
        }
        return map;
    }

    private static BufferedImage normalizeToARGB(BufferedImage sourceImage) {
        if (sourceImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            return sourceImage;
        }

        BufferedImage normalizedImage = new BufferedImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = normalizedImage.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.drawImage(sourceImage, 0, 0, null);
        g2d.dispose();

        return normalizedImage;
    }
}

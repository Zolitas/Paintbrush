package de.tomalbrc.paintbrush.util;

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
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.tomalbrc.paintbrush.util.Data.TEXTURE_REDIRECT;

public class Util {
    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, id);
    }

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

    public static List<JsonObject> addBlockPermutations(ResourcePackBuilder resourcePackBuilder, Block block, Map<BlockState, Set<StateModelVariant>> map) throws Exception {
        List<JsonObject> r = new ArrayList<>();

        for (Map.Entry<BlockState, Set<StateModelVariant>> variantEntry : map.entrySet()) {
            Set<StateModelVariant> variantSet = variantEntry.getValue();
            var distinct = distinctByField(variantSet, StateModelVariant::model);

            for (StateModelVariant variant : distinct) {
                var modelPath = AssetPaths.model(variant.model()) + ".json";

                var modelData = resourcePackBuilder.getStringDataOrSource(modelPath);
                if (modelData != null) {
                    colorModel(resourcePackBuilder, block, modelData, variant.model(), r);
                }
            }
        }

        return r;
    }

    private static void colorModel(ResourcePackBuilder resourcePackBuilder, Block block, String modelData, ResourceLocation modelPath, List<JsonObject> r) throws Exception {
        var json = ModelAsset.fromJson(modelData);

        for (String value : json.textures().values()) {
            if (!value.startsWith("#")) {
                var parsed = ResourceLocation.parse(value);
                JsonObject source = TextureAtlasGenerator.getAtlasSourceJson(parsed);
                r.add(source);
            }
        }

        for (DyeColor dye : DyeColor.values()) {
            var path = AssetPaths.model(modelPath.withSuffix("_" + dye.getName())) + ".json";

            var modelBuilder = ModelAsset.builder();
            if (json.parent().isPresent())
                modelBuilder.parent(json.parent().get());

            for (Map.Entry<String, String> entry : json.textures().entrySet()) {
                if (!entry.getValue().startsWith("#")) {
                    var valId = ResourceLocation.parse(entry.getValue());

                    var id = ResourceLocation.parse(entry.getValue());
                    var paletteIdRedirect = TEXTURE_REDIRECT.getOrDefault(id, id);
                    var img = ImageIO.read(new ByteArrayInputStream(resourcePackBuilder.getDataOrSource(AssetPaths.texture(paletteIdRedirect) + ".png")));
                    var paletteKeyImage = TextureGenerator.generatePaletteKey(img);
                    resourcePackBuilder.addData("assets/minecraft/textures/colormap/color_palettes/" + valId.getPath() + "_key" + ".png", TextureGenerator.data(paletteKeyImage));

                    var paletted = TextureGenerator.generatePaletteColor(paletteKeyImage, dye.getName());
                    resourcePackBuilder.addData("assets/minecraft/textures/colormap/color_palettes/" + valId.getPath() + "_" + dye.getName() + ".png", paletted);

                    var suffixedId = id.withSuffix("_" + dye.getName());
                    modelBuilder.texture(entry.getKey(), ResourceLocation.fromNamespaceAndPath("minecraft", suffixedId.getPath()).toString());
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
}

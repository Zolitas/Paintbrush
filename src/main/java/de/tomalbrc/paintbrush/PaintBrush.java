package de.tomalbrc.paintbrush;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import de.tomalbrc.paintbrush.impl.ModBlocks;
import de.tomalbrc.paintbrush.impl.ModItems;
import de.tomalbrc.paintbrush.impl.block.FallingTexturedBlock;
import de.tomalbrc.paintbrush.impl.block.TexturedBlock;
import de.tomalbrc.paintbrush.impl.block.TexturedPillarBlock;
import de.tomalbrc.paintbrush.impl.gen.TextureAtlasGenerator;
import de.tomalbrc.paintbrush.impl.gen.TextureGenerator;
import de.tomalbrc.paintbrush.impl.item.LargePaintBrushItem;
import de.tomalbrc.paintbrush.impl.item.PaintBrushItem;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.soundpatcher.api.SoundPatcher;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaintBrush implements ModInitializer {
    public static ResourcePackBuilder VBUILDER;

    public static Map<Block, Holder.Reference<Block>> TAGS = new Reference2ReferenceArrayMap<>();

    public static Map<Block, Map<DyeColor, Map<BlockState, BlockState>>> MODELMAP_BY_BLOCK_DYE = new Reference2ObjectOpenHashMap<>();

    public static Map<Block, Block> MODEL_REMAP = ImmutableMap.<Block, Block>builder()
//            .put(Blocks.ACACIA_LOG, Blocks.OAK_LOG) // TODO: stripping logic
//            .put(Blocks.SPRUCE_LOG, Blocks.OAK_LOG)
//            .put(Blocks.DARK_OAK_LOG, Blocks.OAK_LOG)
//            .put(Blocks.MANGROVE_LOG, Blocks.OAK_LOG)
//            .put(Blocks.JUNGLE_LOG, Blocks.OAK_LOG)

            .put(Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_OAK_LOG)

            .put(Blocks.OAK_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.BIRCH_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.BAMBOO_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.ACACIA_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.SPRUCE_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.DARK_OAK_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.CHERRY_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.MANGROVE_PLANKS, Blocks.PALE_OAK_PLANKS)
            .put(Blocks.JUNGLE_PLANKS, Blocks.PALE_OAK_PLANKS)

            .put(Blocks.RED_SAND, Blocks.SAND)

            .build();

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.markAsRequired();
        VBUILDER = PolymerResourcePackUtils.createBuilder(FabricLoader.getInstance().getGameDir().resolve("polymer/a"));

        var list = ReferenceArrayList.of(
                Blocks.GLOWSTONE,
                //Blocks.CHISELED_NETHER_BRICKS, too dark
                Blocks.SANDSTONE,
                Blocks.SAND,
                Blocks.DEEPSLATE,
                Blocks.COBBLED_DEEPSLATE,
                Blocks.TUFF,
                Blocks.CHISELED_TUFF,
                Blocks.TUFF_BRICKS,
                Blocks.CHISELED_TUFF_BRICKS,
                Blocks.POLISHED_TUFF,
                Blocks.COBBLESTONE,
                Blocks.MOSSY_COBBLESTONE,
                Blocks.STONE,
                Blocks.STONE_BRICKS,
                Blocks.MOSSY_STONE_BRICKS,
                Blocks.CHISELED_STONE_BRICKS,
                Blocks.SMOOTH_STONE,
                Blocks.NETHERRACK,
                Blocks.AMETHYST_BLOCK,
                Blocks.CALCITE,
                Blocks.POLISHED_DEEPSLATE,
                Blocks.IRON_BLOCK,
                Blocks.QUARTZ_BLOCK,
                Blocks.QUARTZ_BRICKS,
                Blocks.CHISELED_QUARTZ_BLOCK,

                //Blocks.BIRCH_LOG,
                //Blocks.PALE_OAK_LOG,
                //Blocks.OAK_LOG,

                Blocks.DIORITE,
                Blocks.POLISHED_DIORITE,
                Blocks.ANDESITE,
                Blocks.POLISHED_ANDESITE,
                Blocks.DRIPSTONE_BLOCK,

                Blocks.STRIPPED_OAK_LOG,
                Blocks.PALE_OAK_PLANKS
        );
        list.addAll(MODEL_REMAP.keySet());

        var terramap = ImmutableMap.<DyeColor, Map<BlockState, BlockState>>builder()
                .put(DyeColor.WHITE, ImmutableMap.of(Blocks.WHITE_TERRACOTTA.defaultBlockState(), Blocks.WHITE_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.ORANGE, ImmutableMap.of(Blocks.ORANGE_TERRACOTTA.defaultBlockState(), Blocks.ORANGE_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.MAGENTA, ImmutableMap.of(Blocks.MAGENTA_TERRACOTTA.defaultBlockState(), Blocks.MAGENTA_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.LIGHT_BLUE, ImmutableMap.of(Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.YELLOW, ImmutableMap.of(Blocks.YELLOW_TERRACOTTA.defaultBlockState(), Blocks.YELLOW_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.LIME, ImmutableMap.of(Blocks.LIME_TERRACOTTA.defaultBlockState(), Blocks.LIME_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.PINK, ImmutableMap.of(Blocks.PINK_TERRACOTTA.defaultBlockState(), Blocks.PINK_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.GRAY, ImmutableMap.of(Blocks.GRAY_TERRACOTTA.defaultBlockState(), Blocks.GRAY_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.LIGHT_GRAY, ImmutableMap.of(Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.CYAN, ImmutableMap.of(Blocks.CYAN_TERRACOTTA.defaultBlockState(), Blocks.CYAN_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.PURPLE, ImmutableMap.of(Blocks.PURPLE_TERRACOTTA.defaultBlockState(), Blocks.PURPLE_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.BLUE, ImmutableMap.of(Blocks.BLUE_TERRACOTTA.defaultBlockState(), Blocks.BLUE_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.BROWN, ImmutableMap.of(Blocks.BROWN_TERRACOTTA.defaultBlockState(), Blocks.BROWN_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.GREEN, ImmutableMap.of(Blocks.GREEN_TERRACOTTA.defaultBlockState(), Blocks.GREEN_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.RED, ImmutableMap.of(Blocks.RED_TERRACOTTA.defaultBlockState(), Blocks.RED_TERRACOTTA.defaultBlockState()))
                .put(DyeColor.BLACK, ImmutableMap.of(Blocks.BLACK_TERRACOTTA.defaultBlockState(), Blocks.BLACK_TERRACOTTA.defaultBlockState()))
                .build();

        for (Map.Entry<DyeColor, Map<BlockState, BlockState>> entry : terramap.entrySet()) {
            var newmap = new Reference2ReferenceArrayMap<>(terramap);
            var block = entry.getValue().values().iterator().next().getBlock();
            MODELMAP_BY_BLOCK_DYE.put(block, newmap);
            list.add(block);
        }
        MODELMAP_BY_BLOCK_DYE.put(Blocks.TERRACOTTA, terramap);
        list.add(Blocks.TERRACOTTA);

        var concretemap = ImmutableMap.<DyeColor, Map<BlockState, BlockState>>builder()
                .put(DyeColor.WHITE, ImmutableMap.of(Blocks.WHITE_CONCRETE.defaultBlockState(), Blocks.WHITE_CONCRETE.defaultBlockState()))
                .put(DyeColor.ORANGE, ImmutableMap.of(Blocks.ORANGE_CONCRETE.defaultBlockState(), Blocks.ORANGE_CONCRETE.defaultBlockState()))
                .put(DyeColor.MAGENTA, ImmutableMap.of(Blocks.MAGENTA_CONCRETE.defaultBlockState(), Blocks.MAGENTA_CONCRETE.defaultBlockState()))
                .put(DyeColor.LIGHT_BLUE, ImmutableMap.of(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(), Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState()))
                .put(DyeColor.YELLOW, ImmutableMap.of(Blocks.YELLOW_CONCRETE.defaultBlockState(), Blocks.YELLOW_CONCRETE.defaultBlockState()))
                .put(DyeColor.LIME, ImmutableMap.of(Blocks.LIME_CONCRETE.defaultBlockState(), Blocks.LIME_CONCRETE.defaultBlockState()))
                .put(DyeColor.PINK, ImmutableMap.of(Blocks.PINK_CONCRETE.defaultBlockState(), Blocks.PINK_CONCRETE.defaultBlockState()))
                .put(DyeColor.GRAY, ImmutableMap.of(Blocks.GRAY_CONCRETE.defaultBlockState(), Blocks.GRAY_CONCRETE.defaultBlockState()))
                .put(DyeColor.LIGHT_GRAY, ImmutableMap.of(Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState(), Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState()))
                .put(DyeColor.CYAN, ImmutableMap.of(Blocks.CYAN_CONCRETE.defaultBlockState(), Blocks.CYAN_CONCRETE.defaultBlockState()))
                .put(DyeColor.PURPLE, ImmutableMap.of(Blocks.PURPLE_CONCRETE.defaultBlockState(), Blocks.PURPLE_CONCRETE.defaultBlockState()))
                .put(DyeColor.BLUE, ImmutableMap.of(Blocks.BLUE_CONCRETE.defaultBlockState(), Blocks.BLUE_CONCRETE.defaultBlockState()))
                .put(DyeColor.BROWN, ImmutableMap.of(Blocks.BROWN_CONCRETE.defaultBlockState(), Blocks.BROWN_CONCRETE.defaultBlockState()))
                .put(DyeColor.GREEN, ImmutableMap.of(Blocks.GREEN_CONCRETE.defaultBlockState(), Blocks.GREEN_CONCRETE.defaultBlockState()))
                .put(DyeColor.RED, ImmutableMap.of(Blocks.RED_CONCRETE.defaultBlockState(), Blocks.RED_CONCRETE.defaultBlockState()))
                .put(DyeColor.BLACK, ImmutableMap.of(Blocks.BLACK_CONCRETE.defaultBlockState(), Blocks.BLACK_CONCRETE.defaultBlockState()))
                .build();

        for (Map.Entry<DyeColor, Map<BlockState, BlockState>> entry : concretemap.entrySet()) {
            var newmap = new Reference2ReferenceArrayMap<>(concretemap);
            var block = entry.getValue().values().iterator().next().getBlock();
            MODELMAP_BY_BLOCK_DYE.put(block, newmap);
            list.add(block);
        }


        var glassmap = ImmutableMap.<DyeColor, Map<BlockState, BlockState>>builder()
                .put(DyeColor.WHITE, ImmutableMap.of(Blocks.WHITE_STAINED_GLASS.defaultBlockState(), Blocks.WHITE_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.ORANGE, ImmutableMap.of(Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), Blocks.ORANGE_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.MAGENTA, ImmutableMap.of(Blocks.MAGENTA_STAINED_GLASS.defaultBlockState(), Blocks.MAGENTA_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.LIGHT_BLUE, ImmutableMap.of(Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(), Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.YELLOW, ImmutableMap.of(Blocks.YELLOW_STAINED_GLASS.defaultBlockState(), Blocks.YELLOW_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.LIME, ImmutableMap.of(Blocks.LIME_STAINED_GLASS.defaultBlockState(), Blocks.LIME_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.PINK, ImmutableMap.of(Blocks.PINK_STAINED_GLASS.defaultBlockState(), Blocks.PINK_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.GRAY, ImmutableMap.of(Blocks.GRAY_STAINED_GLASS.defaultBlockState(), Blocks.GRAY_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.LIGHT_GRAY, ImmutableMap.of(Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(), Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.CYAN, ImmutableMap.of(Blocks.CYAN_STAINED_GLASS.defaultBlockState(), Blocks.CYAN_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.PURPLE, ImmutableMap.of(Blocks.PURPLE_STAINED_GLASS.defaultBlockState(), Blocks.PURPLE_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.BLUE, ImmutableMap.of(Blocks.BLUE_STAINED_GLASS.defaultBlockState(), Blocks.BLUE_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.BROWN, ImmutableMap.of(Blocks.BROWN_STAINED_GLASS.defaultBlockState(), Blocks.BROWN_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.GREEN, ImmutableMap.of(Blocks.GREEN_STAINED_GLASS.defaultBlockState(), Blocks.GREEN_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.RED, ImmutableMap.of(Blocks.RED_STAINED_GLASS.defaultBlockState(), Blocks.RED_STAINED_GLASS.defaultBlockState()))
                .put(DyeColor.BLACK, ImmutableMap.of(Blocks.BLACK_STAINED_GLASS.defaultBlockState(), Blocks.BLACK_STAINED_GLASS.defaultBlockState()))
                .build();

        for (Map.Entry<DyeColor, Map<BlockState, BlockState>> entry : glassmap.entrySet()) {
            var newmap = new Reference2ReferenceArrayMap<>(glassmap);
            var block = entry.getValue().values().iterator().next().getBlock();
            MODELMAP_BY_BLOCK_DYE.put(block, newmap);
            list.add(block);
        }
        MODELMAP_BY_BLOCK_DYE.put(Blocks.GLASS, glassmap);
        list.add(Blocks.GLASS);

        LogUtils.getLogger().info("Colored variants: {}", list.size() * DyeColor.values().length);

        registerPaintBlocks(list);
        for (DyeColor dyeColor : DyeColor.values()) {
            ModItems.registerItem("paintbrush_" + dyeColor.getName(), properties -> new PaintBrushItem(dyeColor, properties), new Item.Properties());
            ModItems.registerItem("large_paintbrush_" + dyeColor.getName(), properties -> new LargePaintBrushItem(dyeColor, properties), new Item.Properties());
        }

        var copied = new Reference2ReferenceArrayMap<>(ModBlocks.BLOCK_COLOR_MAP);
        for (Map.Entry<Block, Map<DyeColor, Block>> entry : copied.entrySet()) {
            for (Block value : entry.getValue().values()) {
                ModBlocks.BLOCK_COLOR_MAP.put(value, entry.getValue());
            }
        }

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(resourcePackBuilder -> {
            try {
                var stone = "assets/minecraft/textures/block/stone.png";
                var img = resourcePackBuilder.getDataOrSource(stone);
                resourcePackBuilder.addData(stone, TextureGenerator.data(ImageConverter.convertGrayscaleToRGB(ImageIO.read(new ByteArrayInputStream(img)))));

                JsonObject o = new JsonObject();
                JsonArray array = new JsonArray();

                for (var block : list) {
                    var res = addBlockPermutations(resourcePackBuilder, block, stateSetMap(resourcePackBuilder, block));
                    for (JsonObject ob : res) {
                        array.add(ob);
                    }
                }

                o.add("sources", array);

                resourcePackBuilder.addStringData("assets/minecraft/atlases/blocks.json", o.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Map<DyeColor, Block> registerPaintBlock(Block vanillaBlock, Map<BlockState, Set<StateModelVariant>> stateSetMap) {
        Map<DyeColor, Block> blockMap = new IdentityHashMap<>();

        boolean first = true;
        for (var dye : DyeColor.values()) {
            var blockId = BuiltInRegistries.BLOCK.getKey(vanillaBlock);
            var newId = blockId.withSuffix("_" + dye.getName());

            Map<BlockState, BlockState> localMap = null;
            if (MODEL_REMAP.containsKey(vanillaBlock) && MODELMAP_BY_BLOCK_DYE.containsKey(MODEL_REMAP.get(vanillaBlock)) && MODELMAP_BY_BLOCK_DYE.get(MODEL_REMAP.get(vanillaBlock)).containsKey(dye)) {
                localMap = MODELMAP_BY_BLOCK_DYE.get(MODEL_REMAP.get(vanillaBlock)).get(dye);
            }

            if (localMap == null && MODELMAP_BY_BLOCK_DYE.containsKey(vanillaBlock) && MODELMAP_BY_BLOCK_DYE.get(vanillaBlock).containsKey(dye)) {
                localMap = MODELMAP_BY_BLOCK_DYE.get(vanillaBlock).get(dye);
            }

            if (localMap == null) {
                localMap = new IdentityHashMap<>();
                for (Map.Entry<BlockState, Set<StateModelVariant>> entry : stateSetMap.entrySet()) {
                    var mapped = entry.getValue().stream().map(variant -> PolymerBlockModel.of(variant.model().withSuffix("_" + dye.getName()), variant.x(), variant.y(), variant.uvlock(), variant.weigth()));
                    localMap.put(entry.getKey(), PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, mapped.toList().toArray(new PolymerBlockModel[0])));
                }

                var b = MODEL_REMAP.getOrDefault(vanillaBlock, vanillaBlock);
                MODELMAP_BY_BLOCK_DYE.computeIfAbsent(b, x -> new Reference2ObjectOpenHashMap<>()).put(dye, localMap);
            }

            Block rblock;
            Map<BlockState, BlockState> finalLocalMap = localMap;
            if (vanillaBlock instanceof SandBlock sandBlock) {
                rblock = ModBlocks.registerBlock(newId.getPath(), properties -> new FallingTexturedBlock(new ColorRGBA(sandBlock.getDustColor(null, null, null)), properties, finalLocalMap), BlockBehaviour.Properties.ofFullCopy(vanillaBlock).overrideLootTable(vanillaBlock.getLootTable()));
            } else if (vanillaBlock instanceof RotatedPillarBlock pillarBlock) {
                rblock = ModBlocks.registerBlock(newId.getPath(), properties -> new TexturedPillarBlock(properties, finalLocalMap), BlockBehaviour.Properties.ofFullCopy(vanillaBlock).overrideLootTable(vanillaBlock.getLootTable()));
            } else {
                rblock = ModBlocks.registerBlock(newId.getPath(), properties -> new TexturedBlock(properties, finalLocalMap), BlockBehaviour.Properties.ofFullCopy(vanillaBlock).overrideLootTable(vanillaBlock.getLootTable()));
            }
            Item.BY_BLOCK.put(rblock, vanillaBlock.asItem());

            if (first) SoundPatcher.convertIntoServerSound(rblock.defaultBlockState().getSoundType());

            var tags = BuiltInRegistries.BLOCK.get(BuiltInRegistries.BLOCK.getKey(Blocks.TERRACOTTA)).orElseThrow();
            TAGS.put(rblock, tags);

            var vanillaFire = FlammableBlockRegistry.getDefaultInstance().get(vanillaBlock);
            if (vanillaFire != null) {
                FlammableBlockRegistry.getDefaultInstance().add(rblock, vanillaFire.getBurnChance(), vanillaFire.getSpreadChance());
            }

            blockMap.put(dye, rblock);

            first = false;
        }

        ModBlocks.BLOCK_COLOR_MAP.put(vanillaBlock, blockMap);

        return blockMap;
    }

    private static void registerPaintBlocks(Collection<Block> list) {
        LogUtils.getLogger().info("FULL_BLOCKS before: {}", PolymerBlockResourceUtils.getBlocksLeft(BlockModelType.FULL_BLOCK));

        for (Block block : list) {
            registerPaintBlock(block, stateSetMap(VBUILDER, block));
        }

        LogUtils.getLogger().info("FULL_BLOCKS left: {}", PolymerBlockResourceUtils.getBlocksLeft(BlockModelType.FULL_BLOCK));
    }

    private static Map<BlockState, Set<StateModelVariant>> stateSetMap(ResourcePackBuilder resourcePackBuilder, Block block) {
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

    private static List<JsonObject> addBlockPermutations(ResourcePackBuilder resourcePackBuilder, Block block, Map<BlockState, Set<StateModelVariant>> map) throws Exception {
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
                JsonObject source = TextureAtlasGenerator.getAtlasSourceJson(resourcePackBuilder, modelPath, value);
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
                    var img = ImageIO.read(new ByteArrayInputStream(resourcePackBuilder.getDataOrSource(AssetPaths.texture(id) + ".png")));
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

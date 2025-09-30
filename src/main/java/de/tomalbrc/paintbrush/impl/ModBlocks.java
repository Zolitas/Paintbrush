package de.tomalbrc.paintbrush.impl;


import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.*;

public class ModBlocks {
    private static List<PaintBlockCollection> PAINT_BLOCK_COLLECTIONS = null;

    public static List<PaintBlockCollection> getPaintBlockCollections() {
        if (PAINT_BLOCK_COLLECTIONS == null) {
            PAINT_BLOCK_COLLECTIONS = initPaintBlockCollections();
        }

        return PAINT_BLOCK_COLLECTIONS;
    }

    private static List<PaintBlockCollection> initPaintBlockCollections() {
        List<PaintBlockCollection> collections = new ArrayList<>();

        fillWithStandardCollections(collections);
        fillWithVanillaCollections(collections);
        fillWithSharedCollections(collections);

        return collections;
    }

    private static void fillWithVanillaCollections(List<PaintBlockCollection> collections) {
        Map<DyeColor, Block> terracottaMap = new HashMap<>();
        terracottaMap.put(DyeColor.WHITE, Blocks.WHITE_TERRACOTTA);
        terracottaMap.put(DyeColor.ORANGE, Blocks.ORANGE_TERRACOTTA);
        terracottaMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_TERRACOTTA);
        terracottaMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_TERRACOTTA);
        terracottaMap.put(DyeColor.YELLOW, Blocks.YELLOW_TERRACOTTA);
        terracottaMap.put(DyeColor.LIME, Blocks.LIME_TERRACOTTA);
        terracottaMap.put(DyeColor.PINK, Blocks.PINK_TERRACOTTA);
        terracottaMap.put(DyeColor.GRAY, Blocks.GRAY_TERRACOTTA);
        terracottaMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_TERRACOTTA);
        terracottaMap.put(DyeColor.CYAN, Blocks.CYAN_TERRACOTTA);
        terracottaMap.put(DyeColor.PURPLE, Blocks.PURPLE_TERRACOTTA);
        terracottaMap.put(DyeColor.BLUE, Blocks.BLUE_TERRACOTTA);
        terracottaMap.put(DyeColor.BROWN, Blocks.BROWN_TERRACOTTA);
        terracottaMap.put(DyeColor.GREEN, Blocks.GREEN_TERRACOTTA);
        terracottaMap.put(DyeColor.RED, Blocks.RED_TERRACOTTA);
        terracottaMap.put(DyeColor.BLACK, Blocks.BLACK_TERRACOTTA);
        collections.add(PaintBlockCollection.vanilla(Blocks.TERRACOTTA, terracottaMap));

        Map<DyeColor, Block> concreteMap = new HashMap<>();
        concreteMap.put(DyeColor.WHITE, Blocks.WHITE_CONCRETE);
        concreteMap.put(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE);
        concreteMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE);
        concreteMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE);
        concreteMap.put(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE);
        concreteMap.put(DyeColor.LIME, Blocks.LIME_CONCRETE);
        concreteMap.put(DyeColor.PINK, Blocks.PINK_CONCRETE);
        concreteMap.put(DyeColor.GRAY, Blocks.GRAY_CONCRETE);
        concreteMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE);
        concreteMap.put(DyeColor.CYAN, Blocks.CYAN_CONCRETE);
        concreteMap.put(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE);
        concreteMap.put(DyeColor.BLUE, Blocks.BLUE_CONCRETE);
        concreteMap.put(DyeColor.BROWN, Blocks.BROWN_CONCRETE);
        concreteMap.put(DyeColor.GREEN, Blocks.GREEN_CONCRETE);
        concreteMap.put(DyeColor.RED, Blocks.RED_CONCRETE);
        concreteMap.put(DyeColor.BLACK, Blocks.BLACK_CONCRETE);
        collections.add(PaintBlockCollection.vanilla(Blocks.WHITE_CONCRETE, concreteMap));

        Map<DyeColor, Block> glassMap = new HashMap<>();
        glassMap.put(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS);
        glassMap.put(DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS);
        glassMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS);
        glassMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS);
        glassMap.put(DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS);
        glassMap.put(DyeColor.LIME, Blocks.LIME_STAINED_GLASS);
        glassMap.put(DyeColor.PINK, Blocks.PINK_STAINED_GLASS);
        glassMap.put(DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS);
        glassMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS);
        glassMap.put(DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS);
        glassMap.put(DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS);
        glassMap.put(DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS);
        glassMap.put(DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS);
        glassMap.put(DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS);
        glassMap.put(DyeColor.RED, Blocks.RED_STAINED_GLASS);
        glassMap.put(DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS);
        collections.add(PaintBlockCollection.vanilla(Blocks.GLASS, glassMap));

        Map<DyeColor, Block> woolMap = new HashMap<>();
        woolMap.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        woolMap.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        woolMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        woolMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        woolMap.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        woolMap.put(DyeColor.LIME, Blocks.LIME_WOOL);
        woolMap.put(DyeColor.PINK, Blocks.PINK_WOOL);
        woolMap.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        woolMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        woolMap.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        woolMap.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        woolMap.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        woolMap.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        woolMap.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        woolMap.put(DyeColor.RED, Blocks.RED_WOOL);
        woolMap.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
        collections.add(PaintBlockCollection.vanilla(Blocks.WHITE_WOOL, woolMap));

        Map<DyeColor, Block> carpetMap = new HashMap<>();
        carpetMap.put(DyeColor.WHITE, Blocks.WHITE_CARPET);
        carpetMap.put(DyeColor.ORANGE, Blocks.ORANGE_CARPET);
        carpetMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET);
        carpetMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET);
        carpetMap.put(DyeColor.YELLOW, Blocks.YELLOW_CARPET);
        carpetMap.put(DyeColor.LIME, Blocks.LIME_CARPET);
        carpetMap.put(DyeColor.PINK, Blocks.PINK_CARPET);
        carpetMap.put(DyeColor.GRAY, Blocks.GRAY_CARPET);
        carpetMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET);
        carpetMap.put(DyeColor.CYAN, Blocks.CYAN_CARPET);
        carpetMap.put(DyeColor.PURPLE, Blocks.PURPLE_CARPET);
        carpetMap.put(DyeColor.BLUE, Blocks.BLUE_CARPET);
        carpetMap.put(DyeColor.BROWN, Blocks.BROWN_CARPET);
        carpetMap.put(DyeColor.GREEN, Blocks.GREEN_CARPET);
        carpetMap.put(DyeColor.RED, Blocks.RED_CARPET);
        carpetMap.put(DyeColor.BLACK, Blocks.BLACK_CARPET);
        collections.add(PaintBlockCollection.vanilla(Blocks.WHITE_CARPET, carpetMap));

        Map<DyeColor, Block> concretePowderMap = new HashMap<>();
        concretePowderMap.put(DyeColor.WHITE, Blocks.WHITE_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.LIME, Blocks.LIME_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.PINK, Blocks.PINK_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.GRAY, Blocks.GRAY_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.CYAN, Blocks.CYAN_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.BLUE, Blocks.BLUE_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.BROWN, Blocks.BROWN_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.GREEN, Blocks.GREEN_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.RED, Blocks.RED_CONCRETE_POWDER);
        concretePowderMap.put(DyeColor.BLACK, Blocks.BLACK_CONCRETE_POWDER);
        collections.add(PaintBlockCollection.vanilla(Blocks.WHITE_CONCRETE_POWDER, concretePowderMap));

        Map<DyeColor, Block> glazedTerracottaMap = new HashMap<>();
        glazedTerracottaMap.put(DyeColor.WHITE, Blocks.WHITE_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.ORANGE, Blocks.ORANGE_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.YELLOW, Blocks.YELLOW_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.LIME, Blocks.LIME_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.PINK, Blocks.PINK_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.GRAY, Blocks.GRAY_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.CYAN, Blocks.CYAN_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.PURPLE, Blocks.PURPLE_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.BLUE, Blocks.BLUE_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.BROWN, Blocks.BROWN_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.GREEN, Blocks.GREEN_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.RED, Blocks.RED_GLAZED_TERRACOTTA);
        glazedTerracottaMap.put(DyeColor.BLACK, Blocks.BLACK_GLAZED_TERRACOTTA);
        collections.add(PaintBlockCollection.vanilla(Blocks.WHITE_GLAZED_TERRACOTTA, glazedTerracottaMap));
    }

    private static void fillWithSharedCollections(List<PaintBlockCollection> collections) {
        PaintBlockCollection planksCollection = PaintBlockCollection.standard(Blocks.OAK_PLANKS);
        collections.add(planksCollection);
        collections.add(PaintBlockCollection.shared(Blocks.BIRCH_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.SPRUCE_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.JUNGLE_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.ACACIA_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.DARK_OAK_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.CRIMSON_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.WARPED_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.MANGROVE_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.CHERRY_PLANKS, planksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.PALE_OAK_PLANKS, planksCollection));

        PaintBlockCollection logsCollection = PaintBlockCollection.standard(Blocks.OAK_LOG);
        collections.add(logsCollection);
        collections.add(PaintBlockCollection.shared(Blocks.BIRCH_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.SPRUCE_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.JUNGLE_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.ACACIA_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.DARK_OAK_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.CRIMSON_STEM, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.WARPED_STEM, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.MANGROVE_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.CHERRY_LOG, logsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.PALE_OAK_LOG, logsCollection));

        PaintBlockCollection strippedLogsCollection = PaintBlockCollection.standard(Blocks.STRIPPED_OAK_LOG);
        collections.add(strippedLogsCollection);
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_BIRCH_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_SPRUCE_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_JUNGLE_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_ACACIA_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_DARK_OAK_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_CRIMSON_STEM, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_WARPED_STEM, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_MANGROVE_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_CHERRY_LOG, strippedLogsCollection));
        collections.add(PaintBlockCollection.shared(Blocks.STRIPPED_PALE_OAK_LOG, strippedLogsCollection));

        PaintBlockCollection cobblestoneCollection = PaintBlockCollection.standard(Blocks.COBBLESTONE);
        collections.add(cobblestoneCollection);
        collections.add(PaintBlockCollection.shared(Blocks.MOSSY_COBBLESTONE, cobblestoneCollection));

        PaintBlockCollection stoneBricksCollection = PaintBlockCollection.standard(Blocks.STONE_BRICKS);
        collections.add(stoneBricksCollection);
        collections.add(PaintBlockCollection.shared(Blocks.MOSSY_STONE_BRICKS, stoneBricksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.CRACKED_STONE_BRICKS, stoneBricksCollection));

        PaintBlockCollection polishedBlackstoneBricksCollection = PaintBlockCollection.standard(Blocks.POLISHED_BLACKSTONE_BRICKS);
        collections.add(polishedBlackstoneBricksCollection);
        collections.add(PaintBlockCollection.shared(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, polishedBlackstoneBricksCollection));

        PaintBlockCollection netherBricksCollection = PaintBlockCollection.standard(Blocks.NETHER_BRICKS);
        collections.add(netherBricksCollection);
        collections.add(PaintBlockCollection.shared(Blocks.CRACKED_NETHER_BRICKS, netherBricksCollection));
        collections.add(PaintBlockCollection.shared(Blocks.RED_NETHER_BRICKS, netherBricksCollection));

        PaintBlockCollection deepslateTilesCollection = PaintBlockCollection.standard(Blocks.DEEPSLATE_TILES);
        collections.add(deepslateTilesCollection);
        collections.add(PaintBlockCollection.shared(Blocks.CRACKED_DEEPSLATE_TILES, deepslateTilesCollection));

        PaintBlockCollection deepslateBricksCollection = PaintBlockCollection.standard(Blocks.DEEPSLATE_BRICKS);
        collections.add(deepslateBricksCollection);
        collections.add(PaintBlockCollection.shared(Blocks.CRACKED_DEEPSLATE_BRICKS, deepslateBricksCollection));

        PaintBlockCollection sandCollection = PaintBlockCollection.standard(Blocks.SAND);
        collections.add(sandCollection);
        collections.add(PaintBlockCollection.shared(Blocks.RED_SAND, sandCollection));

        PaintBlockCollection sandstoneCollection = PaintBlockCollection.standard(Blocks.SANDSTONE);
        collections.add(sandstoneCollection);
        collections.add(PaintBlockCollection.shared(Blocks.RED_SANDSTONE, sandstoneCollection));

        PaintBlockCollection smoothSandstoneCollection = PaintBlockCollection.standard(Blocks.SMOOTH_SANDSTONE);
        collections.add(smoothSandstoneCollection);
        collections.add(PaintBlockCollection.shared(Blocks.SMOOTH_RED_SANDSTONE, smoothSandstoneCollection));

        PaintBlockCollection cutSandstoneCollection = PaintBlockCollection.standard(Blocks.CUT_SANDSTONE);
        collections.add(cutSandstoneCollection);
        collections.add(PaintBlockCollection.shared(Blocks.CUT_RED_SANDSTONE, cutSandstoneCollection));

        PaintBlockCollection nyliumCollection = PaintBlockCollection.standard(Blocks.WARPED_NYLIUM);
        collections.add(nyliumCollection);
        collections.add(PaintBlockCollection.shared(Blocks.CRIMSON_NYLIUM, nyliumCollection));
    }

    private static void fillWithStandardCollections(List<PaintBlockCollection> collections) {
        Block[] standardBlocks = {
                Blocks.DEEPSLATE,
                Blocks.COBBLED_DEEPSLATE,
                Blocks.POLISHED_DEEPSLATE,
                Blocks.CHISELED_DEEPSLATE,

                Blocks.TUFF,
                Blocks.CHISELED_TUFF,
                Blocks.TUFF_BRICKS,
                Blocks.CHISELED_TUFF_BRICKS,
                Blocks.POLISHED_TUFF,
                Blocks.CALCITE,

                Blocks.STONE,
                Blocks.CHISELED_STONE_BRICKS,
                Blocks.SMOOTH_STONE,

                Blocks.GRANITE,
                Blocks.POLISHED_GRANITE,
                Blocks.ANDESITE,
                Blocks.POLISHED_ANDESITE,
                Blocks.DIORITE,
                Blocks.POLISHED_DIORITE,

                Blocks.BRICKS,
                Blocks.MUD_BRICKS,

                Blocks.NETHERRACK,
                Blocks.CHISELED_NETHER_BRICKS,
                Blocks.BASALT,
                Blocks.SMOOTH_BASALT,
                Blocks.POLISHED_BASALT,

                Blocks.BLACKSTONE,
                Blocks.POLISHED_BLACKSTONE,
                Blocks.CHISELED_POLISHED_BLACKSTONE,

                Blocks.QUARTZ_BLOCK,
                Blocks.QUARTZ_BRICKS,
                Blocks.CHISELED_QUARTZ_BLOCK,
                Blocks.QUARTZ_PILLAR,
                Blocks.SMOOTH_QUARTZ,

                Blocks.END_STONE,
                Blocks.END_STONE_BRICKS,
                Blocks.PURPUR_BLOCK,
                Blocks.PURPUR_PILLAR,

                Blocks.DIRT,
                Blocks.GRAVEL,
                Blocks.PACKED_MUD,
        };

        Arrays.stream(standardBlocks).map(PaintBlockCollection::standard).forEach(collections::add);
    }
}

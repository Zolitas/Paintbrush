package de.tomalbrc.paintbrush.impl;


import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.*;
import java.util.stream.Collectors;

public class ModBlocks {
    private static List<PaintBlockCollection> PAINT_BLOCK_COLLECTIONS = null;

    public static List<PaintBlockCollection> getPaintBlockCollections() {
        if (PAINT_BLOCK_COLLECTIONS == null) {
            PAINT_BLOCK_COLLECTIONS = initPaintBlockCollections();
        }

        return PAINT_BLOCK_COLLECTIONS;
    }

    private static List<PaintBlockCollection> initPaintBlockCollections() {
        Block[] standardBlocks = {
                Blocks.GLOWSTONE,
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
                Blocks.DIORITE,
                Blocks.POLISHED_DIORITE,
                Blocks.ANDESITE,
                Blocks.POLISHED_ANDESITE,
                Blocks.DRIPSTONE_BLOCK,
                Blocks.STRIPPED_OAK_LOG,
                Blocks.OAK_PLANKS
        };

        return Arrays.stream(standardBlocks)
                .map(PaintBlockCollection::standard)
                .collect(Collectors.toList());
    }
}

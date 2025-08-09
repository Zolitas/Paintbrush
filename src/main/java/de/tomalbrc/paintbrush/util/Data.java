package de.tomalbrc.paintbrush.util;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class Data {
    public static Map<ResourceLocation, ResourceLocation> TEXTURE_REDIRECT = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(ResourceLocation.withDefaultNamespace("block/mossy_cobblestone"), ResourceLocation.withDefaultNamespace("block/cobblestone"))
            .put(ResourceLocation.withDefaultNamespace("block/mossy_stone_bricks"), ResourceLocation.withDefaultNamespace("block/stone_bricks"))
            .build();

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

            .put(Blocks.PALE_OAK_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.BIRCH_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.BAMBOO_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.ACACIA_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.SPRUCE_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.DARK_OAK_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.CHERRY_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.MANGROVE_PLANKS, Blocks.OAK_PLANKS)
            .put(Blocks.JUNGLE_PLANKS, Blocks.OAK_PLANKS)

            .put(Blocks.RED_SAND, Blocks.SAND)

            .build();


    public static ReferenceArrayList<Block> prepare() {
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
                Blocks.OAK_PLANKS
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

        return list;
    }
}

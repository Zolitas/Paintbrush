package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.block.FallingTexturedBlock;
import de.tomalbrc.paintbrush.impl.block.TexturedBlock;
import de.tomalbrc.paintbrush.impl.block.TexturedPillarBlock;
import de.tomalbrc.paintbrush.util.Util;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.soundpatcher.api.SoundPatcher;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static de.tomalbrc.paintbrush.util.Data.*;

public class ModBlocks {
    public static Map<Block, Map<DyeColor, Block>> BLOCK_COLOR_MAP = new IdentityHashMap<>();

    private static void registerPaintBlock(Block vanillaBlock, Map<BlockState, Set<StateModelVariant>> stateSetMap) {
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

    }

    public static void registerPaintBlocks(Collection<Block> list) {
        int start = PolymerBlockResourceUtils.getBlocksLeft(BlockModelType.FULL_BLOCK);
        PaintBrushMod.LOGGER.info("FULL_BLOCKS before: {}", start);

        for (Block block : list) {
            registerPaintBlock(block, Util.stateSetMap(PaintBrushMod.VBUILDER, block));
        }

        int end = PolymerBlockResourceUtils.getBlocksLeft(BlockModelType.FULL_BLOCK);
        PaintBrushMod.LOGGER.info("FULL_BLOCKS left: {}, used {}", end, start-end);
    }

    public static <T extends Block> T registerBlock(String resourceKey, Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties) {
        T block = function.apply(properties.setId(key(resourceKey)));
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }

    public static ResourceKey<Block> key(String id) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace(id));
    }
}

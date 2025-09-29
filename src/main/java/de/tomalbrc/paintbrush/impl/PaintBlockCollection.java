package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.block.FallingTexturedBlock;
import de.tomalbrc.paintbrush.impl.block.TexturedBlock;
import de.tomalbrc.paintbrush.impl.block.TexturedPillarBlock;
import de.tomalbrc.paintbrush.util.Util;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.soundpatcher.api.SoundPatcher;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class PaintBlockCollection {
    private final Block originalBlock;
    private final Map<DyeColor, Block> paintedBlocks = new HashMap<>();

    private PaintBlockCollection(Block originalBlock) {
        this.originalBlock = originalBlock;
    }

    public Block getOriginalBlock() {
        return originalBlock;
    }

    public Block getPaintedBlock(DyeColor dyeColor) {
        return paintedBlocks.get(dyeColor);
    }

    public void setPaintedBlock(DyeColor dyeColor, Block block) {
        paintedBlocks.put(dyeColor, block);
    }

    public boolean isPartOfCollection(Block block) {
        return block == originalBlock || paintedBlocks.containsValue(block);
    }

    public boolean isPaintedBlock(Block block) {
        return paintedBlocks.containsValue(block);
    }

    public static PaintBlockCollection standard(Block originalBlock) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock);

        ResourceLocation originalBlockLocation = BuiltInRegistries.BLOCK.getKey(originalBlock);

        SoundPatcher.convertIntoServerSound(originalBlock.defaultBlockState().getSoundType());

        for (DyeColor dye : DyeColor.values()) {
            ResourceLocation paintedBlockLocation = ResourceLocation.fromNamespaceAndPath(
                    PaintBrushMod.MODID,
                    originalBlockLocation.getPath() + "_" + dye.getName()
            );

            Block paintedBlock;
            BlockBehaviour.Properties paintedBlockProperties = BlockBehaviour.Properties
                    .ofFullCopy(originalBlock)
                    .overrideLootTable(originalBlock.getLootTable())
                    .setId(ResourceKey.create(Registries.BLOCK, paintedBlockLocation));

            Map<BlockState, BlockState> stateMap = new HashMap<>();

            Util.stateSetMap(PaintBrushMod.VBUILDER, originalBlock).forEach(
                    (blockState, stateModelVariants) -> {
                        PolymerBlockModel[] blockModels = stateModelVariants.stream().map(stateModelVariant ->
                                PolymerBlockModel.of(
                                        ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, stateModelVariant.model().getPath() + "_" + dye.getName()),
                                        stateModelVariant.x(),
                                        stateModelVariant.y(),
                                        stateModelVariant.uvlock(),
                                        stateModelVariant.weigth()
                                )
                        ).toList().toArray(new PolymerBlockModel[0]);

                        BlockState newBlockState = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, blockModels);
                        stateMap.put(blockState, newBlockState);
                    }
            );

            if (originalBlock instanceof SandBlock originalSandBlock) {
                paintedBlock = new FallingTexturedBlock(new ColorRGBA(originalSandBlock.getDustColor(null, null, null)), paintedBlockProperties, stateMap);
            }
            else if (originalBlock instanceof RotatedPillarBlock) {
                paintedBlock = new TexturedPillarBlock(paintedBlockProperties, stateMap);
            }
            else {
                paintedBlock = new TexturedBlock(paintedBlockProperties, stateMap);
            }
            Item.BY_BLOCK.put(paintedBlock, originalBlock.asItem());
            Registry.register(BuiltInRegistries.BLOCK, paintedBlockLocation, paintedBlock);

            collection.setPaintedBlock(dye, paintedBlock);
        }
        return collection;
    }
}

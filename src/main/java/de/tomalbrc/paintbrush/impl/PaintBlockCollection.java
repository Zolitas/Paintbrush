package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.block.FallingTexturedBlock;
import de.tomalbrc.paintbrush.impl.block.StatefulBlock;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.linux.Stat;

import java.util.*;

public class PaintBlockCollection {
    private final Block originalBlock;
    private final Map<DyeColor, Block> paintedBlocks = new HashMap<>();
    private final boolean canBeScraped;
    private final boolean shouldGenerateModels;

    private PaintBlockCollection(Block originalBlock, boolean canBeScraped, boolean shouldGenerateModels) {
        this.originalBlock = originalBlock;
        this.canBeScraped = canBeScraped;
        this.shouldGenerateModels = shouldGenerateModels;
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

    public boolean canBeScraped() {
        return canBeScraped;
    }

    public boolean shouldGenerateModels() {
        return shouldGenerateModels;
    }

    public static PaintBlockCollection vanilla(Block originalBlock, Map<DyeColor, Block> paintedBlocks) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock, false, false);

        paintedBlocks.forEach(collection::setPaintedBlock);

        return collection;
    }

    public static PaintBlockCollection standard(Block originalBlock) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock, true, true);

        ResourceLocation originalBlockLocation = BuiltInRegistries.BLOCK.getKey(originalBlock);

        SoundPatcher.convertIntoServerSound(originalBlock.defaultBlockState().getSoundType());

        for (DyeColor dye : DyeColor.values()) {
            ResourceLocation paintedBlockLocation = ResourceLocation.fromNamespaceAndPath(
                    PaintBrushMod.MODID,
                    originalBlockLocation.getPath() + "_" + dye.getName()
            );

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

            collection.setPaintedBlock(dye, createPaintedBlock(originalBlock, paintedBlockProperties, stateMap, paintedBlockLocation));
        }
        return collection;
    }

    public static PaintBlockCollection shared(Block originalBlock, PaintBlockCollection textureSource) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock, true, false);

        ResourceLocation originalBlockLocation = BuiltInRegistries.BLOCK.getKey(originalBlock);

        SoundPatcher.convertIntoServerSound(originalBlock.defaultBlockState().getSoundType());

        for (DyeColor dye : DyeColor.values()) {
            ResourceLocation paintedBlockLocation = ResourceLocation.fromNamespaceAndPath(
                    PaintBrushMod.MODID,
                    originalBlockLocation.getPath() + "_" + dye.getName()
            );

            BlockBehaviour.Properties paintedBlockProperties = BlockBehaviour.Properties
                    .ofFullCopy(originalBlock)
                    .overrideLootTable(originalBlock.getLootTable())
                    .setId(ResourceKey.create(Registries.BLOCK, paintedBlockLocation));

            Map<BlockState, BlockState> stateMap = new HashMap<>();
            Block sourceBlock = textureSource.getPaintedBlock(dye);
            
            if (sourceBlock instanceof StatefulBlock statefulBlock) {
                Map<BlockState, BlockState> sourceStateMap = statefulBlock.getStateMap();

                originalBlock.getStateDefinition().getPossibleStates().forEach(originalState -> {
                    BlockState textureState = sourceStateMap.entrySet().stream()
                            .filter(entry -> hasSameProperties(originalState, entry.getKey()))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(sourceStateMap.values().iterator().next());
                    stateMap.put(originalState, textureState);
                });
            }

            collection.setPaintedBlock(dye, createPaintedBlock(originalBlock, paintedBlockProperties, stateMap, paintedBlockLocation));
        }
        return collection;
    }

    private static @NotNull Block createPaintedBlock(Block originalBlock, BlockBehaviour.Properties paintedBlockProperties, Map<BlockState, BlockState> stateMap, ResourceLocation paintedBlockLocation) {
        Block paintedBlock;

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

        return paintedBlock;
    }

    private static boolean hasSameProperties(BlockState state1, BlockState state2) {
        return state1.getProperties().stream().allMatch(property -> {
            if (state2.hasProperty(property)) {
                return state1.getValue(property).equals(state2.getValue(property));
            }
            return false;
        }) && state2.getProperties().stream().allMatch( property -> {
            if (state1.hasProperty(property)) {
                return state2.getValue(property).equals(state1.getValue(property));
            }
            return false;
        });
    }
}

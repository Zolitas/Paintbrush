package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.datagen.MineableTool;
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
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Collection of painted blocks for a single original block.
 * Can be initialized by {@link #standard(Block, MineableTool) }, {@link #shared(Block, PaintBlockCollection)} or {@link #vanilla(Block, Map)}.
 */
public class PaintBlockCollection {
    private final Block originalBlock;
    private final Map<DyeColor, Block> paintedBlocks = new HashMap<>();
    private final boolean canBeScraped;
    private final boolean shouldGenerateModels;
    private final boolean shouldGenerateBlockStates;
    private final MineableTool tool;

    private PaintBlockCollection(Block originalBlock, boolean canBeScraped, boolean shouldGenerateModels,
                                 boolean shouldGenerateBlockStates, @Nullable MineableTool tool) {
        this.originalBlock = originalBlock;
        this.canBeScraped = canBeScraped;
        this.shouldGenerateModels = shouldGenerateModels;
        this.shouldGenerateBlockStates = shouldGenerateBlockStates;
        this.tool = tool;
    }

    public Block getOriginalBlock() {
        return originalBlock;
    }

    public Block getPaintedBlock(DyeColor dyeColor) {
        return paintedBlocks.get(dyeColor);
    }

    public Collection<Block> getAllPaintedBlocks() {
        return paintedBlocks.values();
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

    public MineableTool getTool() {
        return tool;
    }

    public boolean canBeScraped() {
        return canBeScraped;
    }

    public boolean shouldGenerateModels() {
        return shouldGenerateModels;
    }

    public boolean shouldGenerateBlockStates() {
        return shouldGenerateBlockStates;
    }

    /**
     * This collection does not generate new blocks or models but replaces blocks with the given ones when a paintbrush is used.
     * @param originalBlock The original block, needs to be part of the built-in block registry.
     * @param paintedBlocks A map of dye colors to painted blocks.
     * @return The painted block collection.
     */
    public static PaintBlockCollection vanilla(Block originalBlock, Map<DyeColor, Block> paintedBlocks) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock, false, false, false, null);

        paintedBlocks.forEach(collection::setPaintedBlock);

        return collection;
    }

    /**
     * The standard collection. This generates new custom blocks and models for all dyes.
     * @param originalBlock The original block, needs to be part of the built-in block registry.
     * @param tool The tool that should speed up mining of the painted blocks. Can be null.
     * @return The painted block collection.
     */
    public static PaintBlockCollection standard(Block originalBlock, @Nullable MineableTool tool) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock, true, true, true, tool);

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

    /**
     * This collection does not generate new models but uses the models of the given source collection.
     * Note: Using this collection does not use a new noteblock-blockstate and so doesn't fill up the limit
     * @param originalBlock The original block, needs to be part of the built-in block registry.
     * @param sourceCollection The source collection where it takes the models from.
     * @return The painted block collection.
     */
    public static PaintBlockCollection shared(Block originalBlock, PaintBlockCollection sourceCollection) {
        PaintBlockCollection collection = new PaintBlockCollection(originalBlock, true, false, true, sourceCollection.getTool());

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
            Block sourceBlock = sourceCollection.getPaintedBlock(dye);
            
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

        if (originalBlock instanceof FallingBlock originalFallingBlock) {
            paintedBlock = new FallingTexturedBlock(new ColorRGBA(originalFallingBlock.getDustColor(null, null, null)), paintedBlockProperties, stateMap);
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

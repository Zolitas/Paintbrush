package de.tomalbrc.paintbrush.datagen;

import de.tomalbrc.paintbrush.impl.ModBlocks;
import de.tomalbrc.paintbrush.impl.PaintBlockCollection;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data generator that generates "mineable" block tags for painted blocks.
 * This should be read from the original block tag file in the future so it
 * captures all block tags and doesn't have to be hardcoded.
 */
public class PaintBlockTagDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(PaintBlockTagProvider::new);
    }

    private static class PaintBlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public PaintBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            List<PaintBlockCollection> paintBlockCollections = ModBlocks.getPaintBlockCollections();

            for (MineableTool tool : MineableTool.values()) {
                List<PaintBlockCollection> collectionsWithCorrectTool = paintBlockCollections
                        .stream()
                        .filter(paintBlockCollection -> paintBlockCollection.shouldGenerateBlockStates() && paintBlockCollection.getTool() == tool)
                        .toList();

                if (collectionsWithCorrectTool.isEmpty()) continue;

                List<Block> paintedBlocksToAdd = new ArrayList<>();
                collectionsWithCorrectTool.forEach(paintBlockCollection -> paintedBlocksToAdd.addAll(paintBlockCollection.getAllPaintedBlocks()));

                valueLookupBuilder(tool.getAssociatedTagKey()).addAll(paintedBlocksToAdd);
            }
        }
    }
}

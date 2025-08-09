package de.tomalbrc.paintbrush.impl.block;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

public class PaintBucketBlock extends LayeredCauldronBlock implements PolymerTexturedBlock {
    public PaintBucketBlock(Biome.Precipitation precipitation, CauldronInteraction.InteractionMap interactionMap, Properties properties) {
        super(precipitation, interactionMap, properties);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return null;
    }
}

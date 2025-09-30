package de.tomalbrc.paintbrush.impl.block;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Map;

public class TexturedBlock extends Block implements PolymerTexturedBlock, StatefulBlock {
    final Map<BlockState, BlockState> state;

    public TexturedBlock(Properties properties, Map<BlockState, BlockState> state) {
        super(properties);

        this.state = state;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return this.state.values().iterator().next();
    }

    @Override
    public Map<BlockState, BlockState> getStateMap() {
        return state;
    }
}

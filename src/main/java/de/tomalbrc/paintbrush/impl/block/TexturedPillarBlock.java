package de.tomalbrc.paintbrush.impl.block;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Map;

public class TexturedPillarBlock extends RotatedPillarBlock implements PolymerTexturedBlock, StatefulBlock {
    final Map<BlockState, BlockState> state;

    public TexturedPillarBlock(Properties properties, Map<BlockState, BlockState> map) {
        super(properties);
        this.state = map;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        var key = this.state.keySet().iterator().next().getBlock().withPropertiesOf(blockState);
        return this.state.get(key);
    }

    @Override
    public Map<BlockState, BlockState> getStateMap() {
        return state;
    }
}

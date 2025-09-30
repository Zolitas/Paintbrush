package de.tomalbrc.paintbrush.impl.block;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface StatefulBlock {
    public Map<BlockState, BlockState> getStateMap();
}

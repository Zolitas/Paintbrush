package de.tomalbrc.paintbrush.datagen;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum MineableTool {
    PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE),
    AXE(BlockTags.MINEABLE_WITH_AXE),
    SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL),
    HOE(BlockTags.MINEABLE_WITH_HOE);

    private final TagKey<Block> associatedTagKey;

    MineableTool(TagKey<Block> associatedTagKey) {
        this.associatedTagKey = associatedTagKey;
    }

    public TagKey<Block> getAssociatedTagKey() {
        return associatedTagKey;
    }
}

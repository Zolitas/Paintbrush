package de.tomalbrc.paintbrush.impl;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ModBlocks {
    public static Map<Block, Map<DyeColor, Block>> BLOCK_COLOR_MAP = new IdentityHashMap<>();

    public static <T extends Block> T registerBlock(String resourceKey, Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties) {
        T block = function.apply(properties.setId(key(resourceKey)));
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }

    public static ResourceKey<Block> key(String id) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace(id));
    }
}

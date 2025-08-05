package de.tomalbrc.paintbrush.impl;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ModItems {
    public static Map<Block, Map<DyeColor, Block>> BLOCK_COLOR_MAP = new IdentityHashMap<>();

    public static <T extends Item> T registerItem(String resourceKey, Function<Item.Properties, T> function, Item.Properties properties) {
        T item = function.apply(properties.setId(key(resourceKey)));
        ENTRIES.put(ResourceLocation.withDefaultNamespace(resourceKey), item);
        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    public static ResourceKey<Item> key(String id) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(id));
    }

    public static final Object2ObjectOpenHashMap<ResourceLocation, Item> ENTRIES = new Object2ObjectOpenHashMap<>();
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
            .title(Component.literal("Paintbrush Items").withStyle(ChatFormatting.DARK_GRAY))
            .icon(Items.BRUSH::getDefaultInstance)
            .displayItems((parameters, output) -> ENTRIES.values().forEach(output::accept))
            .build();

    static {
        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceLocation.fromNamespaceAndPath("paintbrush", "items"), ITEM_GROUP);
    }
}

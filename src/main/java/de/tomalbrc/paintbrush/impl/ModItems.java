package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.impl.item.*;
import de.tomalbrc.paintbrush.util.Util;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
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
    public static final Object2ObjectOpenHashMap<ResourceLocation, Item> ENTRIES = new Object2ObjectOpenHashMap<>();
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
            .title(Component.literal("Paintbrush Items").withStyle(ChatFormatting.DARK_GRAY))
            .icon(Items.BRUSH::getDefaultInstance)
            .displayItems((parameters, output) -> ENTRIES.values().forEach(output::accept))
            .build();

    public static Item PAINTBRUSH;
    public static Item LARGE_PAINTBRUSH;
    public static Item EMPTY_PAINT_GUN;

    public static void register() {
        for (DyeColor dyeColor : DyeColor.values()) {
            ModItems.registerItem("paintbrush_" + dyeColor.getName(), properties -> new PaintBrushItem(dyeColor, properties, ResourceLocation.withDefaultNamespace("paintbrush_paint")), new Item.Properties().component(DataComponents.MAX_DAMAGE, 64));
            ModItems.registerItem("large_paintbrush_" + dyeColor.getName(), properties -> new LargePaintBrushItem(dyeColor, properties, ResourceLocation.withDefaultNamespace("large_paintbrush_paint")), new Item.Properties().component(DataComponents.MAX_DAMAGE, 128));
            ModItems.registerItem("paintgun_" + dyeColor.getName(), properties -> new PaintGun(dyeColor, properties, ResourceLocation.withDefaultNamespace("paintgun")), new Item.Properties().stacksTo(1));
        }
        PAINTBRUSH = ModItems.registerItem("paintbrush", properties -> new EmptyBrush(properties, "Paintbrush"), new Item.Properties().stacksTo(1));
        LARGE_PAINTBRUSH = ModItems.registerItem("large_paintbrush", properties -> new EmptyBrush(properties, "Large Paintbrush"), new Item.Properties().stacksTo(1));
        EMPTY_PAINT_GUN = ModItems.registerItem("paintgun_empty", EmptyPaintGun::new, new Item.Properties().stacksTo(1));

        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceLocation.fromNamespaceAndPath("paintbrush", "items"), ITEM_GROUP);
    }

    public static <T extends Item> T registerItem(String resourceKey, Function<Item.Properties, T> function, Item.Properties properties) {
        T item = function.apply(properties.setId(key(resourceKey)));
        ENTRIES.put(Util.id(resourceKey), item);
        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    public static ResourceKey<Item> key(String id) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(id));
    }
}

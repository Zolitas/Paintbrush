package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.item.*;
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

import java.util.*;
import java.util.function.Function;

public class ModItems {
    public static final Object2ObjectOpenHashMap<ResourceLocation, Item> ENTRIES = new Object2ObjectOpenHashMap<>();
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab.Builder(null, -1)
            .title(Component.literal("Paintbrush Items").withStyle(ChatFormatting.DARK_GRAY))
            .icon(Items.BRUSH::getDefaultInstance)
            .displayItems((parameters, output) -> ENTRIES.values().forEach(output::accept))
            .build();

    public static Item PAINTBRUSH;
    public static Item LARGE_PAINTBRUSH;
    public static Item EMPTY_PAINT_GUN;

    public static Map<ResourceLocation, Item> PAINTBRUSH_ITEMS = new HashMap<>();
    public static Map<ResourceLocation, Item> LARGE_PAINTBRUSH_ITEMS = new HashMap<>();
    public static Map<ResourceLocation, Item> PAINTGUN_ITEMS = new HashMap<>();

    public static void register() {
        for (DyeColor dyeColor : DyeColor.values()) {
            ResourceLocation paintbrushLocation = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintbrush_" + dyeColor.getName());
            PAINTBRUSH_ITEMS.put(paintbrushLocation, ModItems.registerItem(paintbrushLocation, properties -> new PaintBrushItem(dyeColor, properties, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintbrush_paint")), new Item.Properties().component(DataComponents.MAX_DAMAGE, 64)));

            ResourceLocation largePaintbrushLocation = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "large_paintbrush_" + dyeColor.getName());
            LARGE_PAINTBRUSH_ITEMS.put(largePaintbrushLocation, ModItems.registerItem(largePaintbrushLocation, properties -> new LargePaintBrushItem(dyeColor, properties, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "large_paintbrush_paint")), new Item.Properties().component(DataComponents.MAX_DAMAGE, 128)));

            ResourceLocation paintgunLocation = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintgun_" + dyeColor.getName());
            PAINTGUN_ITEMS.put(paintgunLocation, ModItems.registerItem(paintgunLocation, properties -> new PaintGun(dyeColor, properties, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintgun")), new Item.Properties().stacksTo(1)));
        }
        ResourceLocation paintbrushLocation = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintbrush");
        PAINTBRUSH = ModItems.registerItem(paintbrushLocation, properties -> new EmptyBrush(properties, "Paintbrush"), new Item.Properties().stacksTo(1));
        PAINTBRUSH_ITEMS.put(paintbrushLocation, PAINTBRUSH);

        ResourceLocation largePaintbrushLocation = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "large_paintbrush");
        LARGE_PAINTBRUSH = ModItems.registerItem(largePaintbrushLocation, properties -> new EmptyBrush(properties, "Large Paintbrush"), new Item.Properties().stacksTo(1));
        LARGE_PAINTBRUSH_ITEMS.put(largePaintbrushLocation, LARGE_PAINTBRUSH);

        ResourceLocation paintgunLocation = ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintgun_empty");
        EMPTY_PAINT_GUN = ModItems.registerItem(paintgunLocation, EmptyPaintGun::new, new Item.Properties().stacksTo(1));
        PAINTGUN_ITEMS.put(paintgunLocation, EMPTY_PAINT_GUN);

        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "items"), ITEM_GROUP);
    }

    public static <T extends Item> T registerItem(ResourceLocation resourceLocation, Function<Item.Properties, T> function, Item.Properties properties) {
        T item = function.apply(properties.setId(key(resourceLocation)));
        ENTRIES.put(resourceLocation, item);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }

    public static ResourceKey<Item> key(ResourceLocation resourceLocation) {
        return ResourceKey.create(Registries.ITEM, resourceLocation);
    }
}

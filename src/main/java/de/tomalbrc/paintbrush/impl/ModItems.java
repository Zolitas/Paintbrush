package de.tomalbrc.paintbrush.impl;

import de.tomalbrc.paintbrush.PaintBrushMod;
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
            ModItems.registerItem(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintbrush_" + dyeColor.getName()), properties -> new PaintBrushItem(dyeColor, properties, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintbrush_paint")), new Item.Properties().component(DataComponents.MAX_DAMAGE, 64));
            ModItems.registerItem(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "large_paintbrush_" + dyeColor.getName()), properties -> new LargePaintBrushItem(dyeColor, properties, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "large_paintbrush_paint")), new Item.Properties().component(DataComponents.MAX_DAMAGE, 128));
            ModItems.registerItem(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintgun_" + dyeColor.getName()), properties -> new PaintGun(dyeColor, properties, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintgun")), new Item.Properties().stacksTo(1));
        }
        PAINTBRUSH = ModItems.registerItem(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintbrush"), properties -> new EmptyBrush(properties, "Paintbrush"), new Item.Properties().stacksTo(1));
        LARGE_PAINTBRUSH = ModItems.registerItem(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "large_paintbrush"), properties -> new EmptyBrush(properties, "Large Paintbrush"), new Item.Properties().stacksTo(1));
        EMPTY_PAINT_GUN = ModItems.registerItem(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, "paintgun_empty"), EmptyPaintGun::new, new Item.Properties().stacksTo(1));

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

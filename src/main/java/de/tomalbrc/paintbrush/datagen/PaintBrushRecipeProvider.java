package de.tomalbrc.paintbrush.datagen;

import de.tomalbrc.paintbrush.PaintBrushMod;
import de.tomalbrc.paintbrush.impl.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PaintBrushRecipeProvider extends FabricRecipeProvider {

    public PaintBrushRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registryLookup.lookup(Registries.ITEM).orElseThrow();

                // todo: add recipes for paintgun
                buildPaintbrushRecipes(itemLookup);
                buildColoredPaintbrushRecipes(itemLookup);
            }

            private void buildPaintbrushRecipes(HolderLookup.RegistryLookup<Item> itemLookup) {
                ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.TOOLS, ModItems.PAINTBRUSH)
                        .pattern("f")
                        .pattern("i")
                        .pattern("s")
                        .define('f', Items.FEATHER)
                        .define('i', Items.IRON_INGOT)
                        .define('s', Items.STICK)
                        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                        .unlockedBy("has_brush", has(Items.BRUSH))
                        .save(exporter, createRecipeResourceKey("paintbrush"));

                ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.TOOLS, ModItems.LARGE_PAINTBRUSH)
                        .pattern("f")
                        .pattern("d")
                        .pattern("s")
                        .define('f', Items.FEATHER)
                        .define('d', Items.DIAMOND)
                        .define('s', Items.STICK)
                        .unlockedBy("has_diamond", has(Items.DIAMOND))
                        .unlockedBy("has_brush", has(Items.BRUSH))
                        .save(exporter, createRecipeResourceKey("large_paintbrush"));
            }

            private void buildColoredPaintbrushRecipes(HolderLookup.RegistryLookup<Item> itemLookup) {
                buildColoredItemRecipes(itemLookup, "paintbrush", ModItems.PAINTBRUSH_ITEMS, ModItems.PAINTBRUSH);
                buildColoredItemRecipes(itemLookup, "large_paintbrush", ModItems.LARGE_PAINTBRUSH_ITEMS, ModItems.LARGE_PAINTBRUSH);
            }

            private void buildColoredItemRecipes(HolderLookup.RegistryLookup<Item> itemLookup, String baseId, Map<ResourceLocation, Item> paintbrushItems, Item unlockItem) {
                for (DyeColor dye : DyeColor.values()) {
                    for (int dyeCount = 1; dyeCount <= 8; dyeCount++) {
                        Item coloredBrush = paintbrushItems.get(ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, baseId + "_" + dye.getName()));

                        ItemStack result = new ItemStack(coloredBrush, 1);
                        int maxDamage = result.getMaxDamage();
                        result.setDamageValue(maxDamage / 8 * (8 - dyeCount));

                        ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.TOOLS, result)
                                .requires(Ingredient.of(paintbrushItems.values().toArray(new Item[0])))
                                .requires(DyeItem.byColor(dye), dyeCount)
                                .unlockedBy("has_unlock_item", has(unlockItem))
                                .group(baseId + "_" + dye.getName())
                                .save(exporter, createRecipeResourceKey(baseId + "_" + dye.getName() + "_with_" + dyeCount + "_dye"));
                    }
                }
            }

            private ResourceKey<Recipe<?>> createRecipeResourceKey(String path) {
                return ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(PaintBrushMod.MODID, path));
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "PaintBrushRecipeProvider";
    }
}

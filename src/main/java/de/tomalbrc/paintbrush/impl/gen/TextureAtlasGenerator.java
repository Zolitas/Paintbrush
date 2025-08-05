package de.tomalbrc.paintbrush.impl.gen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class TextureAtlasGenerator {
    public static JsonObject getAtlasSourceJson(ResourcePackBuilder resourcePackBuilder, ResourceLocation modelPath, String blockTexture) throws Exception {
        var valId = ResourceLocation.parse(blockTexture);
        String namespace = "minecraft";
        String paletteKey = namespace + ":colormap/color_palettes/" + valId.getPath() + "_key";

        JsonObject source = new JsonObject();
        source.addProperty("type", "paletted_permutations");
        source.addProperty("palette_key", paletteKey);

        JsonObject permutations = new JsonObject();
        for (DyeColor dye : DyeColor.values()) {
            permutations.addProperty(dye.getName(), namespace + ":colormap/color_palettes/" + valId.getPath() + "_" + dye.getName());
        }
        source.add("permutations", permutations);

        JsonArray textures = new JsonArray();
        textures.add(blockTexture);
        source.add("textures", textures);

        return source;
    }
}

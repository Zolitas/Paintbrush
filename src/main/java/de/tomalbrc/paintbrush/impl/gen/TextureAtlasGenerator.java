package de.tomalbrc.paintbrush.impl.gen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class TextureAtlasGenerator {
    public static JsonObject getAtlasSourceJson(ResourceLocation blockTexture) throws Exception {
        String namespace = "minecraft";
        String paletteKey = namespace + ":colormap/color_palettes/" + blockTexture.getPath() + "_key";

        JsonObject source = new JsonObject();
        source.addProperty("type", "paletted_permutations");
        source.addProperty("palette_key", paletteKey);

        JsonObject permutations = new JsonObject();
        for (DyeColor dye : DyeColor.values()) {
            permutations.addProperty(dye.getName(), namespace + ":colormap/color_palettes/" + blockTexture.getPath() + "_" + dye.getName());
        }
        source.add("permutations", permutations);

        JsonArray textures = new JsonArray();
        textures.add(blockTexture.toString());
        source.add("textures", textures);

        return source;
    }
}

package de.tomalbrc.paintbrush;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import de.tomalbrc.paintbrush.impl.ModBlocks;
import de.tomalbrc.paintbrush.impl.ModItems;
import de.tomalbrc.paintbrush.impl.PaintBlockCollection;
import de.tomalbrc.paintbrush.util.Util;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import java.util.List;


public class PaintBrushMod implements ModInitializer {
    public static ResourcePackBuilder VBUILDER;
    public static Logger LOGGER = LogUtils.getLogger();
    public static String MODID = "paintbrush";

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();
        VBUILDER = PolymerResourcePackUtils.createBuilder(FabricLoader.getInstance().getGameDir().resolve("polymer/a"));

        List<PaintBlockCollection> paintBlockCollections = ModBlocks.getPaintBlockCollections();
        ModItems.register();

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(resourcePackBuilder -> {
            try {
                JsonObject o = new JsonObject();
                JsonArray array = new JsonArray();

                for (PaintBlockCollection collection : paintBlockCollections) {
                    List<JsonObject> jsonObjects = Util.addBlockPermutations(resourcePackBuilder, Util.stateSetMap(resourcePackBuilder, collection.getOriginalBlock()));
                    jsonObjects.forEach(array::add);
                }

                o.add("sources", array);

                resourcePackBuilder.addStringData("assets/minecraft/atlases/blocks.json", o.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

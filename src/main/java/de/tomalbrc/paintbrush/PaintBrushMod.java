package de.tomalbrc.paintbrush;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import de.tomalbrc.paintbrush.impl.ModBlocks;
import de.tomalbrc.paintbrush.impl.ModItems;
import de.tomalbrc.paintbrush.impl.gen.TextureGenerator;
import de.tomalbrc.paintbrush.util.Data;
import de.tomalbrc.paintbrush.util.ImageConverter;
import de.tomalbrc.paintbrush.util.Util;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.Map;

public class PaintBrushMod implements ModInitializer {
    public static ResourcePackBuilder VBUILDER;
    public static Logger LOGGER = LogUtils.getLogger();
    public static String MODID = "paintbrush";

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MODID);
        PolymerResourcePackUtils.markAsRequired();
        VBUILDER = PolymerResourcePackUtils.createBuilder(FabricLoader.getInstance().getGameDir().resolve("polymer/a"));

        var list = Data.prepare();

        LOGGER.info("Colored variants: {} for {} blocks", list.size() * DyeColor.values().length, list.size());

        ModBlocks.registerPaintBlocks(list);
        ModItems.register();

        var copied = new Reference2ReferenceArrayMap<>(ModBlocks.BLOCK_COLOR_MAP);
        for (Map.Entry<Block, Map<DyeColor, Block>> entry : copied.entrySet()) {
            for (Block value : entry.getValue().values()) {
                ModBlocks.BLOCK_COLOR_MAP.put(value, entry.getValue());
            }
        }

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(resourcePackBuilder -> {
            try {
                var stone = "assets/minecraft/textures/block/stone.png";
                var img = resourcePackBuilder.getDataOrSource(stone);
                resourcePackBuilder.addData(stone, TextureGenerator.data(ImageConverter.convertGrayscaleToRGB(ImageIO.read(new ByteArrayInputStream(img)))));

                JsonObject o = new JsonObject();
                JsonArray array = new JsonArray();

                for (var block : list) {
                    var res = Util.addBlockPermutations(resourcePackBuilder, block, Util.stateSetMap(resourcePackBuilder, block));
                    for (JsonObject ob : res) {
                        array.add(ob);
                    }
                }

                o.add("sources", array);

                resourcePackBuilder.addStringData("assets/minecraft/atlases/blocks.json", o.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

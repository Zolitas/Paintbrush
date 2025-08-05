package de.tomalbrc.paintbrush.impl.gen;

import net.minecraft.world.item.DyeColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class TextureGenerator {
    public static byte[] generatePaletteColor(BufferedImage keyImg, String name) throws Exception {
        int w = keyImg.getWidth();
        int h = keyImg.getHeight();

        int dye = DyeColor.byName(name, DyeColor.BLACK).getTextureDiffuseColor();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = keyImg.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = (argb) & 0xFF;

                // Multiply blend
                int nr = r * ((dye >> 16) & 0xFF) / 255;
                int ng = g * ((dye >> 8) & 0xFF) / 255;
                int nb = b * (dye & 0xFF) / 255;

                int resultArgb = (a << 24) | (nr << 16) | (ng << 8) | nb;
                out.setRGB(x, y, resultArgb);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(out, "PNG", baos);

        return baos.toByteArray();
    }

    public static BufferedImage generatePaletteKey(BufferedImage sourceImage) throws IOException {
        Set<Integer> uniqueColors = new LinkedHashSet<>();
        for (int y = 0; y < sourceImage.getHeight(); y++) {
            for (int x = 0; x < sourceImage.getWidth(); x++) {
                int argb = sourceImage.getRGB(x, y);
                if (((argb >> 24) & 0xFF) != 0) {
                    uniqueColors.add(argb);
                }
            }
        }

        int paletteSize = uniqueColors.size();
        if (paletteSize == 0) {
            throw new IllegalArgumentException("Image contains no non-transparent pixels.");
        }

        BufferedImage paletteImage = new BufferedImage(paletteSize, 1, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        for (Integer color : uniqueColors) {
            paletteImage.setRGB(x++, 0, color);
        }

        return paletteImage;
    }

    public static byte[] data(BufferedImage paletteImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(paletteImage, "PNG", baos);
        return baos.toByteArray();
    }
}

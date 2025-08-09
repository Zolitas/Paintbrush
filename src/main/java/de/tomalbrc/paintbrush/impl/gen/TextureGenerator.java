package de.tomalbrc.paintbrush.impl.gen;

import net.minecraft.world.item.DyeColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class TextureGenerator {
    public static byte[] generatePaletteColor(BufferedImage src, String name) throws IOException {
        int w = src.getWidth(), h = src.getHeight();

        DyeColor d = DyeColor.byName(name, DyeColor.BLACK);
        int dye = d.getTextureDiffuseColor();
        int dr = (dye >> 16) & 0xFF;
        int dg = (dye >> 8) & 0xFF;
        int db = dye & 0xFF;

        float[] hsb = Color.RGBtoHSB(dr, dg, db, null);
        float hue = hsb[0], sat = hsb[1];

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);
                int a = (argb >>> 24),
                        r = (argb >> 16) & 0xFF,
                        g = (argb >> 8) & 0xFF,
                        b = argb & 0xFF;

                // (luma 30/59/11)
                int gray = (r * 30 + g * 59 + b * 11) / 100;
                float bri = gray / 255f;

                float newBri;
                int rgb;
                switch (d) {
                    case WHITE:
                        // 40% toward white
                        newBri = bri;// + (1f - bri) * 0.5f;
                        float tt = 0.75f;
                        rgb = Color.HSBtoRGB(0f, 0f, (newBri + hsb[2]) / 2f);
                        break;
                    case LIGHT_GRAY:
                        // 20% toward white
                        newBri = bri + (1f - bri) * 0.1f;
                        rgb = Color.HSBtoRGB(0f, 0f, newBri);
                        break;
                    case GRAY:
                        // darken 50%
                        newBri = bri * 0.5f;
                        rgb = Color.HSBtoRGB(0f, 0f, newBri);
                        break;
                    case BLACK:
                        // darken 70%
                        newBri = bri * 0.3f;
                        rgb = Color.HSBtoRGB(0f, 0f, newBri);
                        break;
                    default:
                        // colored dyes: hue/sat from dye, brightness from gray
                        float t = 0.75f;                    // 75% of original brightness
                        float bb = bri * t + hsb[2] * (1f - t);
                        rgb = Color.HSBtoRGB(hue, sat, bb - 0.075f);
                }

                out.setRGB(x, y, (a << 24) | (rgb & 0x00FFFFFF));
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

package de.tomalbrc.paintbrush;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

// for grayscale stuff like stone
public class ImageConverter {
    public static BufferedImage convertGrayscaleToRGB(BufferedImage grayImage) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();

        WritableRaster grayRaster = grayImage.getRaster();
        BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = grayRaster.getSample(x, y, 0);
                int argb = (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
                rgbImage.setRGB(x, y, argb);
            }
        }

        return rgbImage;
    }
}
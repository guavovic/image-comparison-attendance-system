package io.github.guavovic.facepoint.recognition;

import java.awt.image.BufferedImage;

final class ColorHistogram {

    private ColorHistogram() {
    }

    static int[] of(BufferedImage image) {
        int[] histogram = new int[256];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);
                int red = (color >> 16) & 0xFF;
                histogram[red]++;
            }
        }

        return histogram;
    }
}

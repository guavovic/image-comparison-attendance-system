package io.github.guavovic.pontofacial.recognition;

import java.awt.image.BufferedImage;

final class ColorHistogram {

    static final int CHANNELS = 3;
    static final int LEVELS = 256;

    private ColorHistogram() {
    }

    static double[][] of(BufferedImage image) {
        double[][] histogram = new double[CHANNELS][LEVELS];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);
                histogram[0][(color >> 16) & 0xFF]++;
                histogram[1][(color >> 8) & 0xFF]++;
                histogram[2][color & 0xFF]++;
            }
        }

        double pixels = (double) image.getWidth() * image.getHeight();
        for (double[] channel : histogram) {
            for (int i = 0; i < channel.length; i++) {
                channel[i] /= pixels;
            }
        }

        return histogram;
    }
}

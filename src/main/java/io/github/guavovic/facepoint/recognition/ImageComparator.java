package io.github.guavovic.facepoint.recognition;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class ImageComparator {

    public static final double THRESHOLD = 0.91;

    private static final int SIZE = 100;

    public Comparison compare(BufferedImage reference, BufferedImage other) {
        BufferedImage resizedReference = resize(reference);
        BufferedImage resizedOther = resize(other);
        double[][] referenceHistogram = ColorHistogram.of(resizedReference);
        double[][] otherHistogram = ColorHistogram.of(resizedOther);

        double pixel = pixelSimilarity(resizedReference, resizedOther);
        return new Comparison(pixel, pixel, histogramSimilarity(referenceHistogram, otherHistogram),
                distanceSimilarity(referenceHistogram, otherHistogram));
    }

    private static double histogramSimilarity(double[][] reference, double[][] other) {
        double sum = 0;

        for (int channel = 0; channel < ColorHistogram.CHANNELS; channel++) {
            for (int i = 0; i < ColorHistogram.LEVELS; i++) {
                sum += Math.min(reference[channel][i], other[channel][i]);
            }
        }

        return sum / ColorHistogram.CHANNELS;
    }

    private static double pixelSimilarity(BufferedImage reference, BufferedImage other) {
        int width = reference.getWidth();
        int height = reference.getHeight();
        long totalDifference = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = reference.getRGB(x, y);
                int b = other.getRGB(x, y);

                totalDifference += Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF))
                        + Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF))
                        + Math.abs((a & 0xFF) - (b & 0xFF));
            }
        }

        return 1.0 - (double) totalDifference / ((long) width * height * 3 * 255);
    }

    private static double distanceSimilarity(double[][] reference, double[][] other) {
        double sum = 0;

        for (int channel = 0; channel < ColorHistogram.CHANNELS; channel++) {
            double euclidean = 1.0 - DistanceMetrics.euclidean(reference[channel], other[channel]) / Math.sqrt(2);
            double manhattan = 1.0 - DistanceMetrics.manhattan(reference[channel], other[channel]) / 2;
            sum += (euclidean + manhattan) / 2;
        }

        return sum / ColorHistogram.CHANNELS;
    }

    private static BufferedImage resize(BufferedImage image) {
        BufferedImage resized = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, SIZE, SIZE, null);
        graphics.dispose();
        return resized;
    }
}

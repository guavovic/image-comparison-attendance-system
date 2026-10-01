package io.github.guavovic.facepoint.recognition;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class ImageComparator {

    public static final double THRESHOLD = 0.85;

    private static final int SIZE = 100;
    private static final double HISTOGRAM_WEIGHT = 0.4;
    private static final double PIXEL_WEIGHT = 0.3;
    private static final double DISTANCE_WEIGHT = 0.3;

    public double similarity(BufferedImage reference, BufferedImage other) {
        BufferedImage resizedReference = resize(reference);
        BufferedImage resizedOther = resize(other);
        int[] referenceHistogram = ColorHistogram.of(resizedReference);
        int[] otherHistogram = ColorHistogram.of(resizedOther);

        return HISTOGRAM_WEIGHT * histogramSimilarity(referenceHistogram, otherHistogram)
                + PIXEL_WEIGHT * pixelSimilarity(resizedReference, resizedOther)
                + DISTANCE_WEIGHT * distanceSimilarity(referenceHistogram, otherHistogram);
    }

    private static double histogramSimilarity(int[] reference, int[] other) {
        double sum = 0;

        for (int i = 0; i < reference.length; i++) {
            sum += Math.abs(reference[i] - other[i]);
        }

        double mean = sum / reference.length;
        return 1.0 - (mean / 255);
    }

    private static double pixelSimilarity(BufferedImage reference, BufferedImage other) {
        int width = reference.getWidth();
        int height = reference.getHeight();
        int totalDifference = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = reference.getRGB(x, y);
                int b = other.getRGB(x, y);

                int red = Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF));
                int green = Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF));
                int blue = Math.abs((a & 0xFF) - (b & 0xFF));

                totalDifference += (red + green + blue) / 3;
            }
        }

        double meanDifference = (double) totalDifference / (width * height);
        return 1.0 - (meanDifference / 255.0);
    }

    private static double distanceSimilarity(int[] reference, int[] other) {
        double maxEuclidean = Math.sqrt(255 * 255 * reference.length);
        double maxManhattan = 255 * reference.length;

        double euclidean = 1.0 - (DistanceMetrics.euclidean(reference, other) / maxEuclidean);
        double manhattan = 1.0 - (DistanceMetrics.manhattan(reference, other) / maxManhattan);

        return (euclidean + manhattan) / 2.0;
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

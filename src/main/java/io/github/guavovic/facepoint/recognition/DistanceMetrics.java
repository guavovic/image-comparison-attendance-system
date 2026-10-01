package io.github.guavovic.facepoint.recognition;

final class DistanceMetrics {

    private DistanceMetrics() {
    }

    static double euclidean(double[] reference, double[] current) {
        double sumOfSquares = 0;

        for (int i = 0; i < reference.length; i++) {
            double difference = reference[i] - current[i];
            sumOfSquares += difference * difference;
        }

        return Math.sqrt(sumOfSquares);
    }

    static double manhattan(double[] reference, double[] current) {
        double sum = 0;

        for (int i = 0; i < reference.length; i++) {
            sum += Math.abs(reference[i] - current[i]);
        }

        return sum;
    }
}

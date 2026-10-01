package io.github.guavovic.facepoint.recognition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceMetricsTest {

    private static final double[] A = { 1, 0, 0 };
    private static final double[] B = { 0, 1, 0 };

    @Test
    @DisplayName("histogramas iguais ficam a distância zero")
    void identicalHistogramsHaveZeroDistance() {
        assertEquals(0.0, DistanceMetrics.euclidean(A, A));
        assertEquals(0.0, DistanceMetrics.manhattan(A, A));
    }

    @Test
    @DisplayName("histogramas sem nada em comum chegam ao máximo: raiz de 2 e 2")
    void disjointHistogramsReachTheMaximum() {
        assertEquals(Math.sqrt(2), DistanceMetrics.euclidean(A, B), 1e-12);
        assertEquals(2.0, DistanceMetrics.manhattan(A, B), 1e-12);
    }

    @Test
    @DisplayName("a distância não depende da ordem")
    void distanceIsSymmetric() {
        double[] c = { 0.5, 0.25, 0.25 };

        assertEquals(DistanceMetrics.euclidean(A, c), DistanceMetrics.euclidean(c, A), 1e-12);
        assertEquals(DistanceMetrics.manhattan(A, c), DistanceMetrics.manhattan(c, A), 1e-12);
    }

    @Test
    @DisplayName("calcula um caso conhecido")
    void computesAKnownCase() {
        double[] c = { 0.5, 0.25, 0.25 };

        assertEquals(Math.sqrt(0.25 + 0.0625 + 0.0625), DistanceMetrics.euclidean(A, c), 1e-12);
        assertEquals(1.0, DistanceMetrics.manhattan(A, c), 1e-12);
    }
}

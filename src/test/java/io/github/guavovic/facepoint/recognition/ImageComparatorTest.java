package io.github.guavovic.facepoint.recognition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.guavovic.facepoint.TestSupport;

class ImageComparatorTest {

    private final ImageComparator comparator = new ImageComparator();

    @Test
    @DisplayName("imagem igual a ela mesma tira nota máxima em tudo")
    void identicalImagesScoreOne() {
        BufferedImage image = TestSupport.halves(Color.RED, Color.BLUE, 50);

        Comparison comparison = comparator.compare(image, image);

        assertEquals(1.0, comparison.score(), 1e-12);
        assertEquals(1.0, comparison.histogram(), 1e-12);
        assertEquals(1.0, comparison.distance(), 1e-12);
    }

    @Test
    @DisplayName("preto contra branco tira nota zero")
    void blackAgainstWhiteScoresZero() {
        Comparison comparison = comparator.compare(TestSupport.solid(Color.BLACK, 50), TestSupport.solid(Color.WHITE, 50));

        assertEquals(0.0, comparison.score(), 1e-12);
        assertEquals(0.0, comparison.histogram(), 1e-12);
    }

    @Test
    @DisplayName("a nota é a semelhança pixel a pixel")
    void scoreIsThePixelSimilarity() {
        Comparison comparison = comparator.compare(TestSupport.halves(Color.RED, Color.BLUE, 50),
                TestSupport.solid(Color.RED, 50));

        assertEquals(comparison.pixel(), comparison.score());
    }

    @Test
    @DisplayName("a nota não depende de qual imagem é a referência")
    void scoreIsSymmetric() {
        BufferedImage a = TestSupport.halves(Color.RED, Color.BLUE, 50);
        BufferedImage b = TestSupport.halves(Color.RED, Color.GREEN, 50);

        assertEquals(comparator.compare(a, b).score(), comparator.compare(b, a).score(), 1e-12);
    }

    @Test
    @DisplayName("a mesma imagem em tamanhos diferentes continua parecida")
    void sizeDoesNotMatter() {
        Comparison comparison = comparator.compare(TestSupport.halves(Color.RED, Color.BLUE, 50),
                TestSupport.halves(Color.RED, Color.BLUE, 300));

        assertTrue(comparison.score() > 0.97, "nota " + comparison.score());
    }

    @Test
    @DisplayName("todas as notas ficam entre 0 e 1")
    void allScoresAreBetweenZeroAndOne() {
        Color[] colors = { Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };

        for (Color first : colors) {
            for (Color second : colors) {
                Comparison comparison = comparator.compare(TestSupport.halves(first, second, 40),
                        TestSupport.halves(second, Color.GRAY, 40));

                assertBetweenZeroAndOne(comparison.score());
                assertBetweenZeroAndOne(comparison.pixel());
                assertBetweenZeroAndOne(comparison.histogram());
                assertBetweenZeroAndOne(comparison.distance());
            }
        }
    }

    @Test
    @DisplayName("o histograma olha as três cores, não só o vermelho")
    void histogramLooksAtAllThreeColors() {
        Comparison comparison = comparator.compare(TestSupport.solid(new Color(255, 0, 0), 50),
                TestSupport.solid(new Color(255, 255, 0), 50));

        assertEquals(2.0 / 3.0, comparison.histogram(), 1e-12);
    }

    @Test
    @DisplayName("as mesmas cores em outro lugar enganam o histograma, mas não a nota")
    void sameColorsInOtherPlacesFoolOnlyTheHistogram() {
        Comparison comparison = comparator.compare(TestSupport.halves(Color.RED, Color.BLUE, 50),
                TestSupport.halves(Color.BLUE, Color.RED, 50));

        assertEquals(1.0, comparison.histogram(), 1e-12);
        assertEquals(1.0, comparison.distance(), 1e-12);
        assertTrue(comparison.score() < 0.5, "nota " + comparison.score());
    }

    private static void assertBetweenZeroAndOne(double value) {
        assertTrue(value >= 0.0 && value <= 1.0, "valor fora de 0 a 1: " + value);
    }
}

package io.github.guavovic.pontofacial.recognition;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.guavovic.pontofacial.TestSupport;

class ColorHistogramTest {

    @Test
    @DisplayName("tem um histograma para cada uma das três cores")
    void hasOneHistogramPerChannel() {
        double[][] histogram = ColorHistogram.of(TestSupport.solid(Color.RED, 10));

        assertEquals(3, histogram.length);
        assertEquals(256, histogram[0].length);
    }

    @Test
    @DisplayName("conta cada canal no nível certo")
    void countsEachChannelAtTheRightLevel() {
        double[][] histogram = ColorHistogram.of(TestSupport.solid(new Color(255, 128, 0), 10));

        assertEquals(1.0, histogram[0][255]);
        assertEquals(1.0, histogram[1][128]);
        assertEquals(1.0, histogram[2][0]);
    }

    @Test
    @DisplayName("normaliza: cada canal soma 1, seja qual for o tamanho")
    void eachChannelSumsToOne() {
        double[][] histogram = ColorHistogram.of(TestSupport.halves(Color.RED, Color.BLUE, 37));

        for (double[] channel : histogram) {
            double sum = 0;
            for (double value : channel) {
                sum += value;
            }
            assertEquals(1.0, sum, 1e-12);
        }
    }

    @Test
    @DisplayName("a mesma imagem em tamanhos diferentes dá o mesmo histograma")
    void sizeDoesNotChangeTheHistogram() {
        double[][] small = ColorHistogram.of(TestSupport.halves(Color.RED, Color.BLUE, 10));
        double[][] large = ColorHistogram.of(TestSupport.halves(Color.RED, Color.BLUE, 100));

        for (int channel = 0; channel < small.length; channel++) {
            assertArrayEquals(small[channel], large[channel], 1e-12);
        }
    }
}

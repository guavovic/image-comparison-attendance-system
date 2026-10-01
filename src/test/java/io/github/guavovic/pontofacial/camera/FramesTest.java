package io.github.guavovic.pontofacial.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FramesTest {

    @Test
    @DisplayName("quadro largo vira um quadrado do tamanho da altura")
    void wideFrameBecomesASquare() {
        BufferedImage image = Frames.centerSquare(8, 4, new byte[8 * 4 * 3]);

        assertEquals(4, image.getWidth());
        assertEquals(4, image.getHeight());
    }

    @Test
    @DisplayName("quadro alto vira um quadrado do tamanho da largura")
    void tallFrameBecomesASquare() {
        BufferedImage image = Frames.centerSquare(4, 8, new byte[4 * 8 * 3]);

        assertEquals(4, image.getWidth());
        assertEquals(4, image.getHeight());
    }

    @Test
    @DisplayName("corta pelo centro: tira as laterais iguais")
    void cropsFromTheCenter() {
        int width = 8;
        int height = 4;
        byte[] bgr = new byte[width * height * 3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bgr[(y * width + x) * 3 + 2] = (byte) (x * 10);
            }
        }

        BufferedImage image = Frames.centerSquare(width, height, bgr);

        assertEquals(20, red(image, 0, 0));
        assertEquals(50, red(image, 3, 3));
    }

    @Test
    @DisplayName("os bytes vêm em azul, verde e vermelho")
    void bytesAreBlueGreenRed() {
        byte[] bgr = { 10, 20, 30 };

        int color = Frames.centerSquare(1, 1, bgr).getRGB(0, 0);

        assertEquals(30, color >> 16 & 0xFF);
        assertEquals(20, color >> 8 & 0xFF);
        assertEquals(10, color & 0xFF);
    }

    @Test
    @DisplayName("quadro já quadrado não perde nada")
    void squareFrameIsKept() {
        byte[] bgr = new byte[3 * 3 * 3];
        for (int i = 0; i < bgr.length; i++) {
            bgr[i] = (byte) (i * 7);
        }

        BufferedImage image = Frames.centerSquare(3, 3, bgr);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int offset = (y * 3 + x) * 3;
                int expected = (bgr[offset + 2] & 0xFF) << 16 | (bgr[offset + 1] & 0xFF) << 8 | (bgr[offset] & 0xFF);
                assertEquals(expected, image.getRGB(x, y) & 0xFFFFFF, "pixel " + x + "," + y);
            }
        }
    }

    @Test
    @DisplayName("tamanho de bytes que não bate com a imagem dá erro")
    void wrongSizeFails() {
        assertThrows(IllegalArgumentException.class, () -> Frames.centerSquare(4, 4, new byte[10]));
    }

    private static int red(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >> 16 & 0xFF;
    }
}

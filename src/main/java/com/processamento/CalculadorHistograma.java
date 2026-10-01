package com.processamento;

import java.awt.image.BufferedImage;

public class CalculadorHistograma {

    public static int[] calcularHistograma(BufferedImage imagem) {
        int[] histograma = new int[256];

        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                int cor = imagem.getRGB(x, y);
                int valor = (cor >> 16) & 0xFF; // Componente vermelho (Red)
                histograma[valor]++;
            }
        }

        return histograma;
    }
}

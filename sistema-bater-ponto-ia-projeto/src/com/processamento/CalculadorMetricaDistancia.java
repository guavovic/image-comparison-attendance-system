package com.processamento;

public class CalculadorMetricaDistancia {

    public static double calcularDistanciaEuclidiana(int[] histogramaReferencia, int[] histogramaAtual) {
        double somaDiferencasQuadradas = 0;

        for (int i = 0; i < histogramaReferencia.length; i++) {
            double diferenca = histogramaReferencia[i] - histogramaAtual[i];
            somaDiferencasQuadradas += diferenca * diferenca;
        }

        return Math.sqrt(somaDiferencasQuadradas);
    }

    public static double calcularDistanciaManhattan(int[] histogramaReferencia, int[] histogramaAtual) {
        double somaDiferencasAbsolutas = 0;

        for (int i = 0; i < histogramaReferencia.length; i++) {
            somaDiferencasAbsolutas += Math.abs(histogramaReferencia[i] - histogramaAtual[i]);
        }

        return somaDiferencasAbsolutas;
    }
}


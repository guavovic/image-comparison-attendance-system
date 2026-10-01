package io.github.guavovic.facepoint.recognition;

public record Comparison(double score, double pixel, double histogram, double distance) {
}

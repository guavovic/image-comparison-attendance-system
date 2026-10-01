package io.github.guavovic.pontofacial;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.imageio.ImageIO;

import io.github.guavovic.pontofacial.storage.Database;

public final class TestSupport {

    public static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 1, 8, 30, 15);

    private TestSupport() {
    }

    public static Clock clock() {
        return Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static Database database(Path folder) {
        return Database.open(folder.resolve("pontofacial.db"));
    }

    public static BufferedImage solid(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, size, size);
        graphics.dispose();
        return image;
    }

    public static BufferedImage halves(Color left, Color right, int size) {
        BufferedImage image = solid(left, size);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(right);
        graphics.fillRect(size / 2, 0, size - size / 2, size);
        graphics.dispose();
        return image;
    }

    public static Path writePng(Path folder, String name, BufferedImage image) throws IOException {
        Path file = folder.resolve(name);
        Files.createDirectories(folder);
        ImageIO.write(image, "png", file.toFile());
        return file;
    }

    public static BufferedImage sampleImage(String folder, String name) throws IOException {
        try (InputStream content = open(folder, name)) {
            return ImageIO.read(content);
        }
    }

    public static Path samplePhoto(Path target, String folder, String name) throws IOException {
        Path file = target.resolve(name);
        Files.createDirectories(target);
        try (InputStream content = open(folder, name)) {
            Files.copy(content, file);
        }
        return file;
    }

    private static InputStream open(String folder, String name) throws IOException {
        String path = "/samples/" + folder + "/" + name;
        InputStream content = TestSupport.class.getResourceAsStream(path);
        if (content == null) {
            throw new IOException("Recurso de teste não encontrado: " + path);
        }
        return content;
    }
}

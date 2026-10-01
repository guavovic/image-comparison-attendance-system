package io.github.guavovic.pontofacial.config;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DataDirectory {

    public static final String PROPERTY = "pontofacial.data";

    private final Path root;

    public DataDirectory(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    public static DataDirectory resolve() {
        String override = System.getProperty(PROPERTY);
        if (override != null && !override.isBlank()) {
            return new DataDirectory(Path.of(override));
        }
        return new DataDirectory(applicationFolder().resolve("data"));
    }

    private static Path applicationFolder() {
        try {
            Path location = Path.of(DataDirectory.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(location)) {
                return location.getParent();
            }
        } catch (URISyntaxException | SecurityException e) {
            // Sem a localização do programa, usa a pasta de onde ele foi aberto.
        }
        return Path.of("").toAbsolutePath();
    }

    public Path root() {
        return root;
    }

    public Path database() {
        return root.resolve("pontofacial.db");
    }

    public Path photos() {
        return root.resolve("photos");
    }

    public Path testPhotos() {
        return root.resolve("test-photos");
    }
}

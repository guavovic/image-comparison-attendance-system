package io.github.guavovic.facepoint.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DataDirectoryTest {

    private final String original = System.getProperty(DataDirectory.PROPERTY);

    @AfterEach
    void restore() {
        if (original == null) {
            System.clearProperty(DataDirectory.PROPERTY);
        } else {
            System.setProperty(DataDirectory.PROPERTY, original);
        }
    }

    @Test
    @DisplayName("monta os caminhos de dentro da pasta de dados")
    void buildsPathsInsideTheRoot(@TempDir Path root) {
        DataDirectory data = new DataDirectory(root);

        assertEquals(root.toAbsolutePath().normalize(), data.root());
        assertEquals(data.root().resolve("facepoint.db"), data.database());
        assertEquals(data.root().resolve("photos"), data.photos());
        assertEquals(data.root().resolve("test-photos"), data.testPhotos());
    }

    @Test
    @DisplayName("-Dfacepoint.data troca a pasta")
    void propertyOverridesTheFolder(@TempDir Path root) {
        System.setProperty(DataDirectory.PROPERTY, root.resolve("meus dados").toString());

        assertEquals(root.resolve("meus dados").toAbsolutePath().normalize(), DataDirectory.resolve().root());
    }

    @Test
    @DisplayName("sem a propriedade, usa a pasta data/ de onde o programa roda")
    void defaultsToADataFolder() {
        System.clearProperty(DataDirectory.PROPERTY);

        Path root = DataDirectory.resolve().root();

        assertEquals("data", root.getFileName().toString());
        assertNull(System.getProperty(DataDirectory.PROPERTY));
    }

    @Test
    @DisplayName("propriedade em branco é ignorada")
    void blankPropertyIsIgnored() {
        System.setProperty(DataDirectory.PROPERTY, "  ");

        assertEquals("data", DataDirectory.resolve().root().getFileName().toString());
    }
}

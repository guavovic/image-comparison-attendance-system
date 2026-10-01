package io.github.guavovic.pontofacial.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.pontofacial.domain.Employee;

class PhotoStoreTest {

    @TempDir
    Path folder;

    private PhotoStore store;
    private Path source;
    private final Employee ana = new Employee(1, "Ana", "Manhã", "Atendente");
    private final Employee bruno = new Employee(2, "Bruno", "Tarde", "Estoquista");

    @BeforeEach
    void setUp() throws IOException {
        store = new PhotoStore(folder.resolve("photos"));
        source = Files.createDirectories(folder.resolve("origem"));
    }

    @Test
    @DisplayName("quem não tem pasta não tem fotos")
    void noFolderMeansNoPhotos() {
        assertTrue(store.photosOf(ana).isEmpty());
    }

    @Test
    @DisplayName("copia a foto para a pasta do funcionário e mantém a original")
    void copiesThePhotoIntoTheEmployeeFolder() throws IOException {
        Path original = Files.writeString(source.resolve("rosto.png"), "x");

        Path copy = store.add(ana, original);

        assertTrue(Files.exists(original));
        assertEquals(folder.resolve("photos/1/rosto.png"), copy);
        assertEquals(List.of(copy), store.photosOf(ana));
        assertTrue(store.photosOf(bruno).isEmpty());
    }

    @Test
    @DisplayName("foto com o mesmo nome não sobrescreve a anterior")
    void sameNameDoesNotOverwrite() throws IOException {
        Path original = source.resolve("rosto.png");

        Files.writeString(original, "um");
        store.add(ana, original);
        Files.writeString(original, "dois");
        store.add(ana, original);

        assertEquals("um", Files.readString(folder.resolve("photos/1/rosto.png")));
        assertEquals("dois", Files.readString(folder.resolve("photos/1/2-rosto.png")));
        assertEquals(2, store.photosOf(ana).size());
    }

    @Test
    @DisplayName("só lista imagens, em ordem de nome")
    void listsOnlyImagesInNameOrder() throws IOException {
        store.save(ana, "b.jpg", new ByteArrayInputStream(new byte[] { 1 }));
        store.save(ana, "a.PNG", new ByteArrayInputStream(new byte[] { 1 }));
        store.save(ana, "notas.txt", new ByteArrayInputStream(new byte[] { 1 }));

        List<String> names = store.photosOf(ana).stream().map(photo -> photo.getFileName().toString()).toList();

        assertEquals(List.of("a.PNG", "b.jpg"), names);
    }

    @Test
    @DisplayName("reconhece as extensões de imagem sem se importar com maiúsculas")
    void recognizesImageExtensions() {
        assertTrue(PhotoStore.isImage(Path.of("a.png")));
        assertTrue(PhotoStore.isImage(Path.of("a.JPG")));
        assertTrue(PhotoStore.isImage(Path.of("a.jpeg")));
        assertFalse(PhotoStore.isImage(Path.of("a.txt")));
        assertFalse(PhotoStore.isImage(Path.of("png")));
    }

    @Test
    @DisplayName("apagar tudo remove só a pasta daquele funcionário")
    void deleteAllRemovesOnlyThatFolder() throws IOException {
        store.save(ana, "a.png", new ByteArrayInputStream(new byte[] { 1 }));
        store.save(bruno, "b.png", new ByteArrayInputStream(new byte[] { 1 }));

        store.deleteAll(ana);

        assertFalse(Files.exists(folder.resolve("photos/1")));
        assertEquals(1, store.photosOf(bruno).size());
    }

    @Test
    @DisplayName("apagar quem não tem pasta não dá erro")
    void deletingWithoutAFolderIsFine() {
        store.deleteAll(ana);

        assertTrue(store.photosOf(ana).isEmpty());
    }
}

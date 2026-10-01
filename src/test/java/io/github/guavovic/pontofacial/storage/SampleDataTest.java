package io.github.guavovic.pontofacial.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.pontofacial.TestSupport;
import io.github.guavovic.pontofacial.domain.Employee;

class SampleDataTest {

    @TempDir
    Path folder;

    private EmployeeRepository employees;
    private PhotoStore photos;
    private Path testPhotos;

    @BeforeEach
    void setUp() {
        employees = new EmployeeRepository(TestSupport.database(folder));
        photos = new PhotoStore(folder.resolve("photos"));
        testPhotos = folder.resolve("test-photos");
    }

    @Test
    @DisplayName("cadastra os cinco funcionários de exemplo, cada um com a sua foto")
    void seedsTheFiveEmployees() {
        SampleData.seedIfEmpty(employees, photos, testPhotos);

        List<Employee> all = employees.findAll();
        assertEquals(5, all.size());
        for (Employee employee : all) {
            assertEquals(1, photos.photosOf(employee).size(), employee.name());
        }
    }

    @Test
    @DisplayName("copia as fotos de teste, inclusive a do rosto desconhecido")
    void copiesTheTestPhotos() throws IOException {
        SampleData.seedIfEmpty(employees, photos, testPhotos);

        try (Stream<Path> files = Files.list(testPhotos)) {
            List<String> names = files.map(file -> file.getFileName().toString()).sorted().toList();
            assertEquals(6, names.size());
            assertTrue(names.contains("desconhecido.jpg"));
        }
    }

    @Test
    @DisplayName("não repete os exemplos quando já existe funcionário")
    void doesNothingWhenThereAreEmployees() {
        employees.add("Quem já estava", "Noite", "Vigia");

        SampleData.seedIfEmpty(employees, photos, testPhotos);

        assertEquals(1, employees.findAll().size());
        assertTrue(Files.notExists(testPhotos));
    }

    @Test
    @DisplayName("rodar duas vezes não duplica nada")
    void runningTwiceDoesNotDuplicate() {
        SampleData.seedIfEmpty(employees, photos, testPhotos);
        SampleData.seedIfEmpty(employees, photos, testPhotos);

        assertEquals(5, employees.findAll().size());
    }
}

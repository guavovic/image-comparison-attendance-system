package io.github.guavovic.facepoint.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.github.guavovic.facepoint.domain.Employee;

public final class SampleData {

    private record Sample(String slug, String name, String shift, String role) {
    }

    private static final List<Sample> EMPLOYEES = List.of(
            new Sample("ana-souza", "Ana Souza", "Manhã", "Atendente"),
            new Sample("bruno-lima", "Bruno Lima", "Tarde", "Estoquista"),
            new Sample("carla-mendes", "Carla Mendes", "Manhã", "Gerente"),
            new Sample("diego-rocha", "Diego Rocha", "Noite", "Segurança"),
            new Sample("elisa-martins", "Elisa Martins", "Tarde", "Caixa"));

    private static final List<String> TEST_PHOTOS = List.of(
            "ana-souza.jpg", "bruno-lima.jpg", "carla-mendes.jpg", "diego-rocha.jpg", "elisa-martins.jpg",
            "desconhecido.jpg");

    private SampleData() {
    }

    public static void seedIfEmpty(EmployeeRepository employees, PhotoStore photos, Path testPhotos) {
        if (!employees.isEmpty()) {
            return;
        }
        for (Sample sample : EMPLOYEES) {
            Employee employee = employees.add(sample.name(), sample.shift(), sample.role());
            String fileName = sample.slug() + ".png";
            try (InputStream content = resource("photos/" + fileName)) {
                photos.save(employee, fileName, content);
            } catch (IOException e) {
                throw new StorageException("Não foi possível copiar a foto de exemplo " + fileName, e);
            }
        }
        copyTestPhotos(testPhotos);
    }

    private static void copyTestPhotos(Path folder) {
        try {
            Files.createDirectories(folder);
            for (String fileName : TEST_PHOTOS) {
                Path target = folder.resolve(fileName);
                if (Files.notExists(target)) {
                    try (InputStream content = resource("test-photos/" + fileName)) {
                        Files.copy(content, target);
                    }
                }
            }
        } catch (IOException e) {
            throw new StorageException("Não foi possível copiar as fotos de teste", e);
        }
    }

    private static InputStream resource(String path) throws IOException {
        InputStream content = SampleData.class.getResourceAsStream("/samples/" + path);
        if (content == null) {
            throw new IOException("Recurso não encontrado: /samples/" + path);
        }
        return content;
    }
}

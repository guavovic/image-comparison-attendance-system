package io.github.guavovic.pontofacial.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import io.github.guavovic.pontofacial.domain.Employee;

public final class PhotoStore {

    public static final Set<String> EXTENSIONS = Set.of("png", "jpg", "jpeg", "bmp", "gif");

    private final Path root;

    public PhotoStore(Path root) {
        this.root = root;
    }

    public List<Path> photosOf(Employee employee) {
        Path folder = folderOf(employee);
        if (!Files.isDirectory(folder)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(folder)) {
            return files.filter(Files::isRegularFile).filter(PhotoStore::isImage).sorted().toList();
        } catch (IOException e) {
            throw new StorageException("Não foi possível ler as fotos de " + employee.name(), e);
        }
    }

    public Path save(Employee employee, String fileName, InputStream content) {
        Path target = folderOf(employee).resolve(fileName);
        try {
            Files.createDirectories(target.getParent());
            Files.copy(content, target);
            return target;
        } catch (IOException e) {
            throw new StorageException("Não foi possível salvar a foto de " + employee.name(), e);
        }
    }

    public Path add(Employee employee, Path source) {
        Path folder = folderOf(employee);
        try {
            Files.createDirectories(folder);
            Path target = folder.resolve(source.getFileName().toString());
            for (int copy = 2; Files.exists(target); copy++) {
                target = folder.resolve(copy + "-" + source.getFileName());
            }
            return Files.copy(source, target);
        } catch (IOException e) {
            throw new StorageException("Não foi possível copiar a foto de " + employee.name(), e);
        }
    }

    public void deleteAll(Employee employee) {
        Path folder = folderOf(employee);
        if (!Files.isDirectory(folder)) {
            return;
        }
        try (Stream<Path> files = Files.walk(folder)) {
            for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        } catch (IOException e) {
            throw new StorageException("Não foi possível apagar as fotos de " + employee.name(), e);
        }
    }

    public static boolean isImage(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 && EXTENSIONS.contains(name.substring(dot + 1).toLowerCase(Locale.ROOT));
    }

    private Path folderOf(Employee employee) {
        return root.resolve(Long.toString(employee.id()));
    }
}

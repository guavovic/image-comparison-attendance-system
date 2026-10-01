package io.github.guavovic.pontofacial.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.storage.EmployeeRepository;
import io.github.guavovic.pontofacial.storage.PhotoStore;

public final class EmployeeService {

    private final EmployeeRepository employees;
    private final PhotoStore photos;

    public EmployeeService(EmployeeRepository employees, PhotoStore photos) {
        this.employees = employees;
        this.photos = photos;
    }

    public List<Employee> list() {
        return employees.findAll();
    }

    public int photoCount(Employee employee) {
        return photos.photosOf(employee).size();
    }

    public Optional<Path> firstPhoto(Employee employee) {
        return photos.photosOf(employee).stream().findFirst();
    }

    public Employee register(String name, String shift, String role, List<Path> newPhotos) {
        String cleanName = required(name, "o nome");
        String cleanShift = required(shift, "o turno");
        String cleanRole = required(role, "a função");
        if (newPhotos.isEmpty()) {
            throw new ValidationException("Escolha pelo menos uma foto: sem ela o funcionário não é reconhecido.");
        }
        checkImages(newPhotos);

        Employee employee = employees.add(cleanName, cleanShift, cleanRole);
        newPhotos.forEach(photo -> photos.add(employee, photo));
        return employee;
    }

    public Employee update(Employee employee, String name, String shift, String role, List<Path> newPhotos) {
        Employee changed = new Employee(employee.id(), required(name, "o nome"), required(shift, "o turno"),
                required(role, "a função"));
        checkImages(newPhotos);

        employees.update(changed);
        newPhotos.forEach(photo -> photos.add(changed, photo));
        return changed;
    }

    public void remove(Employee employee) {
        employees.delete(employee.id());
        photos.deleteAll(employee);
    }

    private static String required(String value, String what) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Informe " + what + ".");
        }
        return value.strip();
    }

    private static void checkImages(List<Path> files) {
        for (Path file : files) {
            try {
                if (ImageIO.read(file.toFile()) == null) {
                    throw new ValidationException("O arquivo " + file.getFileName() + " não é uma imagem válida.");
                }
            } catch (IOException e) {
                throw new ValidationException("Não foi possível ler o arquivo " + file.getFileName() + ".");
            }
        }
    }
}

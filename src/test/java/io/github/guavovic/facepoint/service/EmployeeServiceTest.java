package io.github.guavovic.facepoint.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.facepoint.TestSupport;
import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.storage.EmployeeRepository;
import io.github.guavovic.facepoint.storage.PhotoStore;

class EmployeeServiceTest {

    @TempDir
    Path folder;

    private EmployeeService service;
    private Path photo;
    private Path otherPhoto;

    @BeforeEach
    void setUp() throws IOException {
        service = new EmployeeService(new EmployeeRepository(TestSupport.database(folder)),
                new PhotoStore(folder.resolve("photos")));
        photo = TestSupport.samplePhoto(folder.resolve("origem"), "photos", "ana-souza.png");
        otherPhoto = TestSupport.samplePhoto(folder.resolve("origem"), "photos", "bruno-lima.png");
    }

    @Test
    @DisplayName("cadastra com nome sem espaços sobrando e guarda a foto")
    void registersAndKeepsThePhoto() {
        Employee employee = service.register("  Maria Teste ", " Manhã", "Caixa ", List.of(photo));

        assertEquals("Maria Teste", employee.name());
        assertEquals("Manhã", employee.shift());
        assertEquals("Caixa", employee.role());
        assertEquals(1, service.photoCount(employee));
        assertEquals(List.of(employee), service.list());
    }

    @Test
    @DisplayName("devolve a primeira foto do funcionário, ou nada se não tiver")
    void firstPhoto() {
        Employee employee = service.register("Maria", "Manhã", "Caixa", List.of(photo));

        assertEquals(folder.resolve("photos").resolve(Long.toString(employee.id())).resolve("ana-souza.png"),
                service.firstPhoto(employee).orElseThrow());
        assertTrue(service.firstPhoto(new Employee(999, "Ninguém", "Noite", "Vigia")).isEmpty());
    }

    @Test
    @DisplayName("recusa nome, turno e função vazios")
    void refusesBlankFields() {
        ValidationException name = assertThrows(ValidationException.class,
                () -> service.register(" ", "Manhã", "Caixa", List.of(photo)));
        ValidationException shift = assertThrows(ValidationException.class,
                () -> service.register("Maria", "", "Caixa", List.of(photo)));
        ValidationException role = assertThrows(ValidationException.class,
                () -> service.register("Maria", "Manhã", null, List.of(photo)));

        assertEquals("Informe o nome.", name.getMessage());
        assertEquals("Informe o turno.", shift.getMessage());
        assertEquals("Informe a função.", role.getMessage());
        assertTrue(service.list().isEmpty());
    }

    @Test
    @DisplayName("recusa cadastro sem foto")
    void refusesWithoutPhoto() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> service.register("Maria", "Manhã", "Caixa", List.of()));

        assertTrue(error.getMessage().contains("pelo menos uma foto"));
        assertTrue(service.list().isEmpty());
    }

    @Test
    @DisplayName("recusa arquivo que não é imagem e não grava nada")
    void refusesFilesThatAreNotImages() throws IOException {
        Path fake = Files.writeString(folder.resolve("falsa.png"), "oi");

        ValidationException error = assertThrows(ValidationException.class,
                () -> service.register("Maria", "Manhã", "Caixa", List.of(photo, fake)));

        assertTrue(error.getMessage().contains("falsa.png"));
        assertTrue(service.list().isEmpty());
        assertFalse(Files.exists(folder.resolve("photos")));
    }

    @Test
    @DisplayName("edita os dados e junta fotos novas às que já tinha")
    void updatesDataAndAddsPhotos() {
        Employee employee = service.register("Maria", "Manhã", "Caixa", List.of(photo));

        Employee changed = service.update(employee, "Maria Silva", "Noite", "Gerente", List.of(otherPhoto));

        assertEquals(new Employee(employee.id(), "Maria Silva", "Noite", "Gerente"), changed);
        assertEquals(List.of(changed), service.list());
        assertEquals(2, service.photoCount(changed));
    }

    @Test
    @DisplayName("editar com dado inválido não muda nada")
    void invalidUpdateChangesNothing() {
        Employee employee = service.register("Maria", "Manhã", "Caixa", List.of(photo));

        assertThrows(ValidationException.class, () -> service.update(employee, "", "Noite", "Gerente", List.of(otherPhoto)));

        assertEquals(List.of(employee), service.list());
        assertEquals(1, service.photoCount(employee));
    }

    @Test
    @DisplayName("remove o funcionário e a pasta de fotos dele")
    void removesTheEmployeeAndThePhotos() {
        Employee maria = service.register("Maria", "Manhã", "Caixa", List.of(photo));
        Employee joao = service.register("João", "Tarde", "Estoque", List.of(otherPhoto));

        service.remove(maria);

        assertEquals(List.of(joao), service.list());
        assertFalse(Files.exists(folder.resolve("photos").resolve(Long.toString(maria.id()))));
        assertEquals(1, service.photoCount(joao));
    }
}

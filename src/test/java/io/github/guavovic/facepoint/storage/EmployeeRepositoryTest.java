package io.github.guavovic.facepoint.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.facepoint.TestSupport;
import io.github.guavovic.facepoint.domain.Employee;

class EmployeeRepositoryTest {

    @TempDir
    Path folder;

    private EmployeeRepository employees;

    @BeforeEach
    void setUp() {
        employees = new EmployeeRepository(TestSupport.database(folder));
    }

    @Test
    @DisplayName("começa vazio")
    void startsEmpty() {
        assertTrue(employees.isEmpty());
        assertTrue(employees.findAll().isEmpty());
    }

    @Test
    @DisplayName("cadastra com ID gerado e acha pelo ID")
    void addsWithAGeneratedId() {
        Employee ana = employees.add("Ana Souza", "Manhã", "Atendente");
        Employee bruno = employees.add("Bruno Lima", "Tarde", "Estoquista");

        assertTrue(ana.id() > 0 && bruno.id() > ana.id());
        assertEquals(ana, employees.findById(ana.id()).orElseThrow());
        assertFalse(employees.isEmpty());
    }

    @Test
    @DisplayName("quem não existe volta vazio")
    void unknownEmployeeIsEmpty() {
        assertTrue(employees.findById(999).isEmpty());
    }

    @Test
    @DisplayName("lista em ordem alfabética")
    void listsAlphabetically() {
        employees.add("Carla", "Manhã", "Gerente");
        employees.add("Ana", "Manhã", "Atendente");
        employees.add("Bruno", "Tarde", "Estoquista");

        List<String> names = employees.findAll().stream().map(Employee::name).toList();

        assertEquals(List.of("Ana", "Bruno", "Carla"), names);
    }

    @Test
    @DisplayName("atualiza só o funcionário certo")
    void updatesOnlyThatEmployee() {
        Employee ana = employees.add("Ana", "Manhã", "Atendente");
        Employee bruno = employees.add("Bruno", "Tarde", "Estoquista");

        employees.update(new Employee(ana.id(), "Ana Souza", "Noite", "Gerente"));

        assertEquals(new Employee(ana.id(), "Ana Souza", "Noite", "Gerente"), employees.findById(ana.id()).orElseThrow());
        assertEquals(bruno, employees.findById(bruno.id()).orElseThrow());
    }

    @Test
    @DisplayName("remove só o funcionário certo")
    void deletesOnlyThatEmployee() {
        Employee ana = employees.add("Ana", "Manhã", "Atendente");
        Employee bruno = employees.add("Bruno", "Tarde", "Estoquista");

        employees.delete(ana.id());

        assertEquals(List.of(bruno), employees.findAll());
    }

    @Test
    @DisplayName("aceita acento e aspas nos dados sem quebrar o SQL")
    void acceptsAccentsAndQuotes() {
        Employee employee = employees.add("João d'Ávila \"Jr\"", "Manhã", "Atendente; caixa");

        assertEquals(employee, employees.findById(employee.id()).orElseThrow());
    }
}

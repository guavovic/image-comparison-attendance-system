package io.github.guavovic.facepoint.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.facepoint.TestSupport;
import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;

class AttendanceRepositoryTest {

    @TempDir
    Path folder;

    private AttendanceRepository attendance;
    private Employee ana;
    private Employee bruno;

    @BeforeEach
    void setUp() {
        Database database = TestSupport.database(folder);
        EmployeeRepository employees = new EmployeeRepository(database);
        attendance = new AttendanceRepository(database);
        ana = employees.add("Ana", "Manhã", "Atendente");
        bruno = employees.add("Bruno", "Tarde", "Estoquista");
    }

    @Test
    @DisplayName("guarda funcionário, data e hora e nota")
    void storesEmployeeTimeAndSimilarity() {
        LocalDateTime time = LocalDateTime.of(2026, 10, 1, 8, 30, 15);

        AttendanceRecord saved = attendance.add(ana, time, 0.9321);

        AttendanceRecord read = attendance.findAll().get(0);
        assertEquals(saved, read);
        assertEquals(ana, read.employee());
        assertEquals(time, read.recordedAt());
        assertEquals(0.9321, read.similarity(), 1e-12);
    }

    @Test
    @DisplayName("lista em ordem de data e hora")
    void listsInChronologicalOrder() {
        attendance.add(bruno, LocalDateTime.of(2026, 10, 1, 17, 0), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 10, 1, 8, 0), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 9, 30, 8, 0), 0.95);

        List<LocalDateTime> times = attendance.findAll().stream().map(AttendanceRecord::recordedAt).toList();

        assertEquals(times.stream().sorted().toList(), times);
    }

    @Test
    @DisplayName("filtra por funcionário")
    void filtersByEmployee() {
        attendance.add(ana, LocalDateTime.of(2026, 10, 1, 8, 0), 0.95);
        attendance.add(bruno, LocalDateTime.of(2026, 10, 1, 9, 0), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 10, 2, 8, 0), 0.95);

        assertEquals(2, attendance.findByEmployee(ana).size());
        assertEquals(1, attendance.findByEmployee(bruno).size());
        assertTrue(attendance.findByEmployee(ana).stream().allMatch(record -> record.employee().equals(ana)));
    }

    @Test
    @DisplayName("filtra por período: o início conta e o fim não")
    void filtersByPeriod() {
        attendance.add(ana, LocalDateTime.of(2026, 9, 30, 23, 59, 59), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 10, 1, 0, 0, 0), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 10, 1, 23, 59, 59), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 10, 2, 0, 0, 0), 0.95);

        List<AttendanceRecord> inside = attendance.find(null, LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 10, 2, 0, 0));

        assertEquals(2, inside.size());
        assertEquals(1, attendance.find(null, LocalDateTime.of(2026, 10, 2, 0, 0), null).size());
        assertEquals(1, attendance.find(null, null, LocalDateTime.of(2026, 10, 1, 0, 0)).size());
    }

    @Test
    @DisplayName("combina funcionário e período")
    void combinesEmployeeAndPeriod() {
        attendance.add(ana, LocalDateTime.of(2026, 10, 1, 8, 0), 0.95);
        attendance.add(bruno, LocalDateTime.of(2026, 10, 1, 9, 0), 0.95);
        attendance.add(ana, LocalDateTime.of(2026, 10, 5, 8, 0), 0.95);

        List<AttendanceRecord> found = attendance.find(ana.id(), LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 10, 2, 0, 0));

        assertEquals(1, found.size());
        assertEquals(ana, found.get(0).employee());
    }

    @Test
    @DisplayName("remove só o registro escolhido")
    void deletesOnlyTheChosenRecord() {
        AttendanceRecord first = attendance.add(ana, LocalDateTime.of(2026, 10, 1, 8, 0), 0.95);
        AttendanceRecord second = attendance.add(ana, LocalDateTime.of(2026, 10, 1, 9, 0), 0.95);

        attendance.delete(first.id());

        assertEquals(List.of(second), attendance.findAll());
    }
}

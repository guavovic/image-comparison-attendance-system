package io.github.guavovic.pontofacial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.pontofacial.TestSupport;
import io.github.guavovic.pontofacial.domain.AttendanceRecord;
import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.recognition.ImageComparator;
import io.github.guavovic.pontofacial.service.AttendanceService.PunchResult;
import io.github.guavovic.pontofacial.storage.AttendanceRepository;
import io.github.guavovic.pontofacial.storage.Database;
import io.github.guavovic.pontofacial.storage.EmployeeRepository;
import io.github.guavovic.pontofacial.storage.NoticeRepository;
import io.github.guavovic.pontofacial.storage.PhotoStore;
import io.github.guavovic.pontofacial.storage.SampleData;

class AttendanceServiceTest {

    @TempDir
    Path folder;

    private EmployeeRepository employees;
    private AttendanceService service;
    private Path testPhotos;

    @BeforeEach
    void setUp() {
        Database database = TestSupport.database(folder);
        employees = new EmployeeRepository(database);
        PhotoStore photos = new PhotoStore(folder.resolve("photos"));
        testPhotos = folder.resolve("test-photos");
        SampleData.seedIfEmpty(employees, photos, testPhotos);
        service = new AttendanceService(employees, new AttendanceRepository(database), new NoticeRepository(database),
                photos, new ImageComparator(), TestSupport.clock());
    }

    @Test
    @DisplayName("registra o ponto de quem foi reconhecido, com a hora do relógio")
    void recordsTheRecognizedEmployee() throws IOException {
        PunchResult result = service.punch(testPhotos.resolve("bruno-lima.jpg"));

        AttendanceRecord record = result.record().orElseThrow();
        assertEquals("Bruno Lima", record.employee().name());
        assertEquals(TestSupport.NOW, record.recordedAt());
        assertTrue(record.similarity() >= ImageComparator.THRESHOLD);
        assertEquals(List.of(record), service.allRecords());
    }

    @Test
    @DisplayName("registra um ponto só, para o mais parecido, mesmo comparando com todos")
    void recordsOnlyTheBestMatch() throws IOException {
        PunchResult result = service.punch(testPhotos.resolve("elisa-martins.jpg"));

        assertEquals(5, result.scores().size());
        assertEquals(1, service.allRecords().size());
        assertEquals("Elisa Martins", service.allRecords().get(0).employee().name());
    }

    @Test
    @DisplayName("bater de novo registra outro ponto")
    void punchingAgainRecordsAnotherOne() throws IOException {
        service.punch(testPhotos.resolve("ana-souza.jpg"));
        service.punch(testPhotos.resolve("ana-souza.jpg"));

        assertEquals(2, service.allRecords().size());
    }

    @Test
    @DisplayName("rosto desconhecido não registra ponto e deixa um aviso")
    void unknownFaceLeavesANotice() throws IOException {
        PunchResult result = service.punch(testPhotos.resolve("desconhecido.jpg"));

        assertTrue(result.record().isEmpty());
        assertTrue(service.allRecords().isEmpty());
        assertEquals(1, service.notices().size());
        String message = service.notices().get(0).message();
        assertTrue(message.contains("desconhecido.jpg") && message.contains("não reconhecida"), message);
        assertTrue(message.contains("Bruno Lima") && message.contains("89,31%"), message);
        assertEquals(TestSupport.NOW, service.notices().get(0).createdAt());
    }

    @Test
    @DisplayName("sem nenhum funcionário com foto, avisa e não registra")
    void withoutEmployeesItOnlyWarns() throws IOException {
        for (Employee employee : employees.findAll()) {
            employees.delete(employee.id());
        }

        PunchResult result = service.punch(testPhotos.resolve("bruno-lima.jpg"));

        assertTrue(result.record().isEmpty());
        assertTrue(result.scores().isEmpty());
        assertTrue(service.notices().get(0).message().contains("não há funcionário"));
    }

    @Test
    @DisplayName("arquivo que não é imagem dá erro e não grava nada")
    void invalidFileFailsWithoutSideEffects() throws IOException {
        Path notAnImage = Files.writeString(folder.resolve("texto.png"), "isto não é uma imagem");

        IOException error = assertThrows(IOException.class, () -> service.punch(notAnImage));

        assertTrue(error.getMessage().contains("texto.png"));
        assertTrue(service.allRecords().isEmpty());
        assertTrue(service.notices().isEmpty());
    }

    @Test
    @DisplayName("filtra os pontos por funcionário e por período, com o último dia inteiro")
    void filtersRecords() throws IOException {
        service.punch(testPhotos.resolve("ana-souza.jpg"));
        service.punch(testPhotos.resolve("bruno-lima.jpg"));
        Employee ana = employees.findAll().get(0);
        LocalDate day = TestSupport.NOW.toLocalDate();

        assertEquals(2, service.records(null, day, day).size());
        assertEquals(1, service.records(ana, day, day).size());
        assertTrue(service.records(null, day.plusDays(1), null).isEmpty());
        assertTrue(service.records(null, null, day.minusDays(1)).isEmpty());
        assertEquals(2, service.records(null, null, null).size());
    }

    @Test
    @DisplayName("remove um registro e limpa os avisos")
    void deletesARecordAndClearsNotices() throws IOException {
        service.punch(testPhotos.resolve("ana-souza.jpg"));
        service.punch(testPhotos.resolve("desconhecido.jpg"));

        service.deleteRecord(service.allRecords().get(0));
        service.clearNotices();

        assertTrue(service.allRecords().isEmpty());
        assertTrue(service.notices().isEmpty());
    }
}

package io.github.guavovic.facepoint.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.storage.StorageException;

class ReportServiceTest {

    @TempDir
    Path folder;

    private final ReportService reports = new ReportService();

    @Test
    @DisplayName("relatório de pontos: cabeçalho, uma linha por ponto, data, hora e nota com vírgula")
    void attendanceReport() throws IOException {
        Employee ana = new Employee(1, "Ana Souza", "Manhã", "Atendente");
        List<AttendanceRecord> records = List.of(
                new AttendanceRecord(1, ana, LocalDateTime.of(2026, 10, 1, 8, 30, 15), 0.9323),
                new AttendanceRecord(2, ana, LocalDateTime.of(2026, 10, 2, 17, 5, 9), 1.0));
        Path file = folder.resolve("pontos.csv");

        reports.attendance(records, file);

        assertEquals("﻿ID;Nome;Turno;Função;Data;Hora;Nota\r\n"
                + "1;Ana Souza;Manhã;Atendente;01/10/2026;08:30:15;93,23%\r\n"
                + "1;Ana Souza;Manhã;Atendente;02/10/2026;17:05:09;100,00%\r\n", read(file));
    }

    @Test
    @DisplayName("relatório de funcionários")
    void employeesReport() throws IOException {
        Path file = folder.resolve("funcionarios.csv");

        reports.employees(List.of(new Employee(1, "Ana", "Manhã", "Caixa"), new Employee(2, "Bruno", "Noite", "Vigia")),
                file);

        assertEquals("﻿ID;Nome;Turno;Função\r\n1;Ana;Manhã;Caixa\r\n2;Bruno;Noite;Vigia\r\n", read(file));
    }

    @Test
    @DisplayName("sem dados, grava só o cabeçalho")
    void emptyReportHasOnlyTheHeader() throws IOException {
        Path file = folder.resolve("vazio.csv");

        reports.employees(List.of(), file);

        assertEquals("﻿ID;Nome;Turno;Função\r\n", read(file));
    }

    @Test
    @DisplayName("protege ponto e vírgula, aspas e quebra de linha nos dados")
    void escapesSpecialCharacters() throws IOException {
        Path file = folder.resolve("especiais.csv");

        reports.employees(List.of(new Employee(1, "Silva; \"Jr\"", "Manhã", "linha1\nlinha2")), file);

        assertEquals("﻿ID;Nome;Turno;Função\r\n1;\"Silva; \"\"Jr\"\"\";Manhã;\"linha1\nlinha2\"\r\n", read(file));
    }

    @Test
    @DisplayName("pasta que não existe dá erro claro")
    void missingFolderFails() {
        Path file = folder.resolve("nao-existe/relatorio.csv");

        StorageException error = assertThrows(StorageException.class, () -> reports.employees(List.of(), file));

        assertEquals(true, error.getMessage().contains("relatorio.csv"));
    }

    private static String read(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8);
    }
}

package io.github.guavovic.facepoint.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.storage.StorageException;

public final class ReportService {

    private static final String SEPARATOR = ";";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    public void attendance(List<AttendanceRecord> records, Path target) {
        List<List<String>> rows = records.stream()
                .map(record -> List.of(Long.toString(record.employee().id()), record.employee().name(),
                        record.employee().shift(), record.employee().role(), record.recordedAt().format(DATE),
                        record.recordedAt().format(TIME), String.format(LOCALE, "%.2f%%", record.similarity() * 100)))
                .toList();
        write(target, List.of("ID", "Nome", "Turno", "Função", "Data", "Hora", "Nota"), rows);
    }

    public void employees(List<Employee> employees, Path target) {
        List<List<String>> rows = employees.stream()
                .map(employee -> List.of(Long.toString(employee.id()), employee.name(), employee.shift(),
                        employee.role()))
                .toList();
        write(target, List.of("ID", "Nome", "Turno", "Função"), rows);
    }

    private static void write(Path target, List<String> header, List<List<String>> rows) {
        try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            writer.write('﻿');
            writer.write(line(header));
            for (List<String> row : rows) {
                writer.write(line(row));
            }
        } catch (IOException e) {
            throw new StorageException("Não foi possível gravar o relatório em " + target, e);
        }
    }

    private static String line(List<String> cells) {
        return cells.stream().map(ReportService::escape).collect(Collectors.joining(SEPARATOR)) + "\r\n";
    }

    private static String escape(String cell) {
        if (cell.contains(SEPARATOR) || cell.contains("\"") || cell.contains("\n")) {
            return '"' + cell.replace("\"", "\"\"") + '"';
        }
        return cell;
    }
}

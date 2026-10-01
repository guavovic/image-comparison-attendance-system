package io.github.guavovic.facepoint.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.facepoint.TestSupport;
import io.github.guavovic.facepoint.domain.Employee;

class DatabaseTest {

    @TempDir
    Path folder;

    @Test
    @DisplayName("cria a pasta e o banco que ainda não existem")
    void createsTheFolderAndTheFile() {
        Path file = folder.resolve("dados/novos/facepoint.db");

        Database.open(file);

        assertTrue(Files.isRegularFile(file));
    }

    @Test
    @DisplayName("deixa o banco na versão mais nova do esquema")
    void migratesToTheLatestVersion() throws SQLException {
        Database database = TestSupport.database(folder);

        assertEquals(2, version(database));
        for (String table : new String[] { "employees", "attendance_records", "notices" }) {
            assertTrue(tableExists(database, table), "falta a tabela " + table);
        }
    }

    @Test
    @DisplayName("abrir de novo não refaz nem apaga nada")
    void reopeningKeepsTheData() throws SQLException {
        Database first = TestSupport.database(folder);
        new EmployeeRepository(first).add("Ana", "Manhã", "Caixa");

        Database second = TestSupport.database(folder);

        assertEquals(2, version(second));
        assertEquals(1, new EmployeeRepository(second).findAll().size());
    }

    @Test
    @DisplayName("atualiza um banco antigo, só com a primeira versão, sem perder dados")
    void upgradesAnOldDatabase() throws SQLException {
        Path file = folder.resolve("antigo.db");
        try (Connection connection = java.sql.DriverManager.getConnection("jdbc:sqlite:" + file);
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE employees (id INTEGER PRIMARY KEY, name TEXT NOT NULL, shift TEXT NOT NULL, role TEXT NOT NULL)");
            statement.execute("CREATE TABLE attendance_records (id INTEGER PRIMARY KEY, employee_id INTEGER NOT NULL REFERENCES employees (id) ON DELETE CASCADE, recorded_at TEXT NOT NULL, similarity REAL NOT NULL)");
            statement.execute("INSERT INTO employees (name, shift, role) VALUES ('Antiga', 'Noite', 'Vigia')");
            statement.execute("PRAGMA user_version = 1");
        }

        Database database = Database.open(file);

        assertEquals(2, version(database));
        assertTrue(tableExists(database, "notices"));
        assertEquals("Antiga", new EmployeeRepository(database).findAll().get(0).name());
    }

    @Test
    @DisplayName("apagar o funcionário apaga os pontos dele")
    void deletingAnEmployeeDeletesTheirRecords() {
        Database database = TestSupport.database(folder);
        EmployeeRepository employees = new EmployeeRepository(database);
        AttendanceRepository attendance = new AttendanceRepository(database);
        Employee ana = employees.add("Ana", "Manhã", "Caixa");
        Employee bruno = employees.add("Bruno", "Tarde", "Estoque");
        attendance.add(ana, LocalDateTime.of(2026, 10, 1, 8, 0), 0.95);
        attendance.add(bruno, LocalDateTime.of(2026, 10, 1, 9, 0), 0.95);

        employees.delete(ana.id());

        assertEquals(1, attendance.findAll().size());
        assertEquals("Bruno", attendance.findAll().get(0).employee().name());
    }

    private static int version(Database database) throws SQLException {
        try (Connection connection = database.connect();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("PRAGMA user_version")) {
            return result.getInt(1);
        }
    }

    private static boolean tableExists(Database database, String table) throws SQLException {
        try (Connection connection = database.connect();
                ResultSet result = connection.createStatement()
                        .executeQuery("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '" + table + "'")) {
            return result.next();
        }
    }
}

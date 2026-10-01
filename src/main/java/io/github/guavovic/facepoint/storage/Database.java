package io.github.guavovic.facepoint.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public final class Database {

    private static final List<String> MIGRATIONS = List.of(
            """
            CREATE TABLE employees (
                id    INTEGER PRIMARY KEY,
                name  TEXT NOT NULL,
                shift TEXT NOT NULL,
                role  TEXT NOT NULL
            );
            CREATE TABLE attendance_records (
                id          INTEGER PRIMARY KEY,
                employee_id INTEGER NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
                recorded_at TEXT NOT NULL,
                similarity  REAL NOT NULL
            );
            CREATE INDEX idx_attendance_records_employee ON attendance_records (employee_id, recorded_at);
            """);

    private final String url;

    public Database(Path file) {
        this.url = "jdbc:sqlite:" + file.toAbsolutePath();
    }

    public static Database open(Path file) {
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
        } catch (IOException e) {
            throw new StorageException("Não foi possível criar a pasta " + file.getParent(), e);
        }
        Database database = new Database(file);
        database.migrate();
        return database;
    }

    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void migrate() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            int version;
            try (ResultSet result = statement.executeQuery("PRAGMA user_version")) {
                version = result.getInt(1);
            }
            for (int next = version; next < MIGRATIONS.size(); next++) {
                connection.setAutoCommit(false);
                for (String sql : MIGRATIONS.get(next).split(";")) {
                    if (!sql.isBlank()) {
                        statement.execute(sql);
                    }
                }
                statement.execute("PRAGMA user_version = " + (next + 1));
                connection.commit();
            }
        } catch (SQLException e) {
            throw new StorageException("Não foi possível preparar o banco de dados", e);
        }
    }
}

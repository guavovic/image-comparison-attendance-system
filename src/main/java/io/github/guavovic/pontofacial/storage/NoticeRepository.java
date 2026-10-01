package io.github.guavovic.pontofacial.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.guavovic.pontofacial.domain.Notice;

public final class NoticeRepository {

    private final Database database;

    public NoticeRepository(Database database) {
        this.database = database;
    }

    public Notice add(LocalDateTime createdAt, String message) {
        String sql = "INSERT INTO notices (created_at, message) VALUES (?, ?)";
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, createdAt.toString());
            statement.setString(2, message);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return new Notice(keys.getLong(1), createdAt, message);
            }
        } catch (SQLException e) {
            throw new StorageException("Não foi possível registrar o aviso", e);
        }
    }

    public List<Notice> findAll() {
        String sql = "SELECT id, created_at, message FROM notices ORDER BY created_at DESC, id DESC";
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            List<Notice> notices = new ArrayList<>();
            while (result.next()) {
                notices.add(new Notice(result.getLong("id"), LocalDateTime.parse(result.getString("created_at")),
                        result.getString("message")));
            }
            return notices;
        } catch (SQLException e) {
            throw new StorageException("Não foi possível listar os avisos", e);
        }
    }

    public void deleteAll() {
        try (Connection connection = database.connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM notices");
        } catch (SQLException e) {
            throw new StorageException("Não foi possível limpar os avisos", e);
        }
    }
}

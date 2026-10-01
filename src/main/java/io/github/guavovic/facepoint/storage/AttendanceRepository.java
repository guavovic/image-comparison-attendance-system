package io.github.guavovic.facepoint.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;

public final class AttendanceRepository {

    private static final String SELECT = """
            SELECT r.id AS record_id, r.recorded_at, r.similarity, e.id, e.name, e.shift, e.role
            FROM attendance_records r
            JOIN employees e ON e.id = r.employee_id
            """;

    private final Database database;

    public AttendanceRepository(Database database) {
        this.database = database;
    }

    public AttendanceRecord add(Employee employee, LocalDateTime recordedAt, double similarity) {
        String sql = "INSERT INTO attendance_records (employee_id, recorded_at, similarity) VALUES (?, ?, ?)";
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, employee.id());
            statement.setString(2, recordedAt.toString());
            statement.setDouble(3, similarity);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return new AttendanceRecord(keys.getLong(1), employee, recordedAt, similarity);
            }
        } catch (SQLException e) {
            throw new StorageException("Não foi possível registrar o ponto", e);
        }
    }

    public List<AttendanceRecord> findByEmployee(Employee employee) {
        return find(employee.id(), null, null);
    }

    public List<AttendanceRecord> findAll() {
        return find(null, null, null);
    }

    public List<AttendanceRecord> find(Long employeeId, LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> parameters = new ArrayList<>();
        if (employeeId != null) {
            sql.append("AND e.id = ? ");
            parameters.add(employeeId);
        }
        if (from != null) {
            sql.append("AND r.recorded_at >= ? ");
            parameters.add(from.toString());
        }
        if (to != null) {
            sql.append("AND r.recorded_at < ? ");
            parameters.add(to.toString());
        }
        sql.append("ORDER BY r.recorded_at, r.id");

        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            return read(statement);
        } catch (SQLException e) {
            throw new StorageException("Não foi possível listar os pontos", e);
        }
    }

    public void delete(long id) {
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM attendance_records WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Não foi possível remover o registro", e);
        }
    }

    private static List<AttendanceRecord> read(PreparedStatement statement) throws SQLException {
        try (ResultSet result = statement.executeQuery()) {
            List<AttendanceRecord> records = new ArrayList<>();
            while (result.next()) {
                records.add(new AttendanceRecord(result.getLong("record_id"), EmployeeRepository.map(result),
                        LocalDateTime.parse(result.getString("recorded_at")), result.getDouble("similarity")));
            }
            return records;
        }
    }
}

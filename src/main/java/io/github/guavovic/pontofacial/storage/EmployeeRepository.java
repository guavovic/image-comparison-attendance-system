package io.github.guavovic.pontofacial.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.guavovic.pontofacial.domain.Employee;

public final class EmployeeRepository {

    private final Database database;

    public EmployeeRepository(Database database) {
        this.database = database;
    }

    public Employee add(String name, String shift, String role) {
        String sql = "INSERT INTO employees (name, shift, role) VALUES (?, ?, ?)";
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, shift);
            statement.setString(3, role);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return new Employee(keys.getLong(1), name, shift, role);
            }
        } catch (SQLException e) {
            throw new StorageException("Não foi possível cadastrar o funcionário", e);
        }
    }

    public void update(Employee employee) {
        String sql = "UPDATE employees SET name = ?, shift = ?, role = ? WHERE id = ?";
        try (Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.name());
            statement.setString(2, employee.shift());
            statement.setString(3, employee.role());
            statement.setLong(4, employee.id());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Não foi possível atualizar o funcionário", e);
        }
    }

    public void delete(long id) {
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM employees WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Não foi possível remover o funcionário", e);
        }
    }

    public Optional<Employee> findById(long id) {
        String sql = "SELECT id, name, shift, role FROM employees WHERE id = ?";
        try (Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new StorageException("Não foi possível buscar o funcionário", e);
        }
    }

    public List<Employee> findAll() {
        String sql = "SELECT id, name, shift, role FROM employees ORDER BY name";
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            List<Employee> employees = new ArrayList<>();
            while (result.next()) {
                employees.add(map(result));
            }
            return employees;
        } catch (SQLException e) {
            throw new StorageException("Não foi possível listar os funcionários", e);
        }
    }

    public boolean isEmpty() {
        try (Connection connection = database.connect();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT NOT EXISTS (SELECT 1 FROM employees)")) {
            return result.getBoolean(1);
        } catch (SQLException e) {
            throw new StorageException("Não foi possível consultar os funcionários", e);
        }
    }

    static Employee map(ResultSet result) throws SQLException {
        return new Employee(result.getLong("id"), result.getString("name"), result.getString("shift"),
                result.getString("role"));
    }
}

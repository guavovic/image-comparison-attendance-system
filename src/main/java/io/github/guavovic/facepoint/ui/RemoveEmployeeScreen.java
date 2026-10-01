package io.github.guavovic.facepoint.ui;

import java.awt.Container;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.service.EmployeeService;
import io.github.guavovic.facepoint.storage.StorageException;

final class RemoveEmployeeScreen {

    private final EmployeeService employees;
    private final JFrame frame;
    private final JComboBox<Employee> combo;

    RemoveEmployeeScreen(EmployeeService employees) {
        this.employees = employees;
        this.combo = Ui.employeeCombo(employees.list(), 140, 80, 170);
        this.frame = Ui.frame("Remover Funcionario", 351, 243, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    boolean hasEmployees() {
        return combo.getItemCount() > 0;
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        Ui.addLogo(content);
        Ui.addBars(content, 335, 16, 182, 23);

        content.add(Ui.fieldLabel("Funcionário:", 30, 80, 100));
        content.add(combo);
        content.add(Ui.button("Remover", 70, 135, 87, 23, e -> remove()));
        content.add(Ui.button("Cancelar", 177, 135, 87, 23, e -> frame.dispose()));
    }

    private void remove() {
        Employee employee = (Employee) combo.getSelectedItem();
        if (employee == null) {
            return;
        }
        if (!Ui.confirm(frame, "Remover " + employee.name() + "? As fotos e os pontos dele também serão apagados.")) {
            return;
        }
        try {
            employees.remove(employee);
            Ui.showInfo(frame, employee.name() + " removido.");
            frame.dispose();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

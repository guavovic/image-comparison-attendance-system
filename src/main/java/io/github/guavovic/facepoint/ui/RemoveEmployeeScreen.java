package io.github.guavovic.facepoint.ui;

import javax.swing.JButton;
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
        this.combo = Ui.employeeCombo(employees.list());

        Form form = new Form().row("Funcionário", combo);
        JButton remove = Ui.button("Remover", e -> remove());

        frame = Ui.screen("FacePoint - Remover funcionário", "FacePoint", "Remover funcionário", null, form,
                Ui.actions(null, Ui.button("Cancelar", e -> frame().dispose()), remove), JFrame.DISPOSE_ON_CLOSE);
    }

    boolean hasEmployees() {
        return combo.getItemCount() > 0;
    }

    void open() {
        frame.setVisible(true);
    }

    private JFrame frame() {
        return frame;
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

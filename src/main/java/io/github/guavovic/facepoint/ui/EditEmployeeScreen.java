package io.github.guavovic.facepoint.ui;

import java.awt.Container;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.service.EmployeeService;
import io.github.guavovic.facepoint.service.ValidationException;
import io.github.guavovic.facepoint.storage.StorageException;

final class EditEmployeeScreen {

    private final EmployeeService employees;
    private final JFrame frame;
    private final JComboBox<Employee> combo;
    private final JTextField name = Ui.textField(95, 120, 138);
    private final JTextField shift = Ui.textField(95, 160, 138);
    private final JTextField role = Ui.textField(95, 200, 138);
    private final JLabel photoLabel = new JLabel();
    private final List<Path> newPhotos = new ArrayList<>();

    EditEmployeeScreen(EmployeeService employees) {
        this.employees = employees;
        this.combo = Ui.employeeCombo(employees.list(), 95, 80, 138);
        this.frame = Ui.frame("Editar Funcionario", 281, 400, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
        load();
    }

    boolean hasEmployees() {
        return combo.getItemCount() > 0;
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        Ui.addLogo(content);
        Ui.addBars(content, 266, 16, 338, 23);

        content.add(Ui.fieldLabel("Quem:", 30, 80, 55));
        content.add(combo);
        combo.addActionListener(e -> load());
        content.add(Ui.fieldLabel("Nome:", 30, 120, 55));
        content.add(name);
        content.add(Ui.fieldLabel("Turno:", 30, 160, 55));
        content.add(shift);
        content.add(Ui.fieldLabel("Função:", 30, 200, 55));
        content.add(role);

        content.add(Ui.button("Adicionar fotos", 30, 240, 110, 23, e -> addPhotos()));
        photoLabel.setFont(Ui.BUTTON_FONT);
        photoLabel.setBounds(150, 240, 110, 23);
        content.add(photoLabel);

        content.add(Ui.button("Salvar", 40, 295, 87, 23, e -> save()));
        content.add(Ui.button("Cancelar", 136, 295, 87, 23, e -> frame.dispose()));
    }

    private void load() {
        Employee employee = (Employee) combo.getSelectedItem();
        newPhotos.clear();
        if (employee == null) {
            return;
        }
        name.setText(employee.name());
        shift.setText(employee.shift());
        role.setText(employee.role());
        showPhotoCount(employee);
    }

    private void addPhotos() {
        newPhotos.addAll(Ui.chooseImages(frame, null));
        showPhotoCount((Employee) combo.getSelectedItem());
    }

    private void showPhotoCount(Employee employee) {
        int saved = employee == null ? 0 : employees.photoCount(employee);
        String text = saved + (saved == 1 ? " foto" : " fotos");
        photoLabel.setText(newPhotos.isEmpty() ? text : text + " + " + newPhotos.size() + " nova(s)");
    }

    private void save() {
        Employee employee = (Employee) combo.getSelectedItem();
        if (employee == null) {
            return;
        }
        try {
            employees.update(employee, name.getText(), shift.getText(), role.getText(), List.copyOf(newPhotos));
            Ui.showInfo(frame, "Dados de " + name.getText().strip() + " salvos.");
            frame.dispose();
        } catch (ValidationException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

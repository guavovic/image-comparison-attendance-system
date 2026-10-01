package io.github.guavovic.pontofacial.ui;

import java.awt.FlowLayout;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.service.EmployeeService;
import io.github.guavovic.pontofacial.service.ValidationException;
import io.github.guavovic.pontofacial.storage.StorageException;

final class EditEmployeeScreen {

    private final EmployeeService employees;
    private final JFrame frame;
    private final JComboBox<Employee> combo;
    private final JTextField name = Ui.field(20);
    private final JComboBox<String> shift = Ui.shiftCombo();
    private final JTextField role = Ui.field(20);
    private final JLabel photoLabel = new JLabel();
    private final List<Path> newPhotos = new ArrayList<>();

    EditEmployeeScreen(EmployeeService employees) {
        this.employees = employees;
        this.combo = Ui.employeeCombo(employees.list());
        combo.addActionListener(e -> load());

        JPanel photoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        photoRow.add(Ui.button("Adicionar fotos", e -> addPhotos()));
        photoRow.add(photoLabel);

        Form form = new Form().row("Funcionário", combo).row("Nome", name).row("Turno", shift).row("Função", role)
                .row("Fotos", photoRow);
        JButton save = Ui.button("Salvar", e -> save());

        frame = Ui.screen("Ponto Facial - Editar funcionário", "Ponto Facial", "Editar funcionário", null, form,
                Ui.actions(null, Ui.button("Cancelar", e -> frame().dispose()), save), JFrame.DISPOSE_ON_CLOSE);
        Ui.primary(frame, save);
        load();
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

    private void load() {
        Employee employee = (Employee) combo.getSelectedItem();
        newPhotos.clear();
        if (employee == null) {
            return;
        }
        name.setText(employee.name());
        shift.setSelectedItem(employee.shift());
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
            employees.update(employee, name.getText(), (String) shift.getSelectedItem(), role.getText(),
                    List.copyOf(newPhotos));
            Ui.showInfo(frame, "Dados de " + name.getText().strip() + " salvos.");
            frame.dispose();
        } catch (ValidationException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

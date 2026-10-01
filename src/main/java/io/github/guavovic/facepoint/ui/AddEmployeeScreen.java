package io.github.guavovic.facepoint.ui;

import java.awt.Container;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.service.EmployeeService;
import io.github.guavovic.facepoint.service.ValidationException;
import io.github.guavovic.facepoint.storage.StorageException;

final class AddEmployeeScreen {

    private final EmployeeService employees;
    private final JFrame frame;
    private final JTextField name = Ui.textField(95, 80, 138);
    private final JTextField shift = Ui.textField(95, 120, 138);
    private final JTextField role = Ui.textField(95, 160, 138);
    private final JLabel photoLabel = new JLabel("Nenhuma foto");
    private List<Path> photos = List.of();

    AddEmployeeScreen(EmployeeService employees) {
        this.employees = employees;
        this.frame = Ui.frame("Adicionar Funcionario", 281, 350, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        Ui.addLogo(content);
        Ui.addBars(content, 266, 16, 288, 23);

        content.add(Ui.fieldLabel("Nome:", 30, 80, 55));
        content.add(name);
        content.add(Ui.fieldLabel("Turno:", 30, 120, 55));
        content.add(shift);
        content.add(Ui.fieldLabel("Função:", 30, 160, 55));
        content.add(role);

        content.add(Ui.button("Escolher fotos", 30, 200, 110, 23, e -> choosePhotos()));
        photoLabel.setFont(Ui.BUTTON_FONT);
        photoLabel.setBounds(150, 200, 100, 23);
        content.add(photoLabel);

        content.add(Ui.button("Adicionar", 40, 245, 87, 23, e -> add()));
        content.add(Ui.button("Cancelar", 136, 245, 87, 23, e -> frame.dispose()));
    }

    private void choosePhotos() {
        List<Path> chosen = Ui.chooseImages(frame, null);
        if (!chosen.isEmpty()) {
            photos = chosen;
            photoLabel.setText(chosen.size() == 1 ? "1 foto" : chosen.size() + " fotos");
        }
    }

    private void add() {
        try {
            Employee employee = employees.register(name.getText(), shift.getText(), role.getText(), photos);
            Ui.showInfo(frame, employee.name() + " cadastrado com o ID " + employee.id() + ".");
            frame.dispose();
        } catch (ValidationException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

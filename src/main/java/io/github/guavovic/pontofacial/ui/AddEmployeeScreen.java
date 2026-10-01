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

final class AddEmployeeScreen {

    private final EmployeeService employees;
    private final JFrame frame;
    private final JTextField name = Ui.field(20);
    private final JComboBox<String> shift = Ui.shiftCombo();
    private final JTextField role = Ui.field(20);
    private final JLabel photoLabel = new JLabel("Nenhuma foto");
    private final List<Path> photos = new ArrayList<>();

    AddEmployeeScreen(EmployeeService employees) {
        this.employees = employees;

        JPanel photoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        photoRow.add(Ui.button("Escolher fotos", e -> choosePhotos()));
        photoRow.add(Ui.button("Tirar foto", e -> takePhoto()));
        photoRow.add(photoLabel);

        Form form = new Form().row("Nome", name).row("Turno", shift).row("Função", role).row("Fotos", photoRow);
        JButton add = Ui.button("Adicionar", e -> add());

        frame = Ui.screen("Ponto Facial - Adicionar funcionário", "Ponto Facial", "Adicionar funcionário", null, form,
                Ui.actions(null, Ui.button("Cancelar", e -> frame().dispose()), add), JFrame.DISPOSE_ON_CLOSE);
        Ui.primary(frame, add);
    }

    void open() {
        frame.setVisible(true);
    }

    private JFrame frame() {
        return frame;
    }

    private void choosePhotos() {
        photos.addAll(Ui.chooseImages(frame, null));
        showPhotoCount();
    }

    private void takePhoto() {
        Path photo = Ui.takePhoto(frame);
        if (photo != null) {
            photos.add(photo);
            showPhotoCount();
        }
    }

    private void showPhotoCount() {
        photoLabel.setText(photos.isEmpty() ? "Nenhuma foto" : photos.size() == 1 ? "1 foto" : photos.size() + " fotos");
    }

    private void add() {
        try {
            Employee employee = employees.register(name.getText(), (String) shift.getSelectedItem(), role.getText(),
                    List.copyOf(photos));
            Ui.showInfo(frame, employee.name() + " cadastrado com o ID " + employee.id() + ".");
            frame.dispose();
        } catch (ValidationException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

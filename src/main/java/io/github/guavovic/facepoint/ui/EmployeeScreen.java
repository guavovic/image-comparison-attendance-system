package io.github.guavovic.facepoint.ui;

import java.awt.Color;
import java.awt.Container;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.service.AttendanceService;
import io.github.guavovic.facepoint.service.AttendanceService.PhotoScore;
import io.github.guavovic.facepoint.service.AttendanceService.PunchResult;
import io.github.guavovic.facepoint.storage.PhotoStore;
import io.github.guavovic.facepoint.storage.StorageException;

public final class EmployeeScreen {

    private final AttendanceService attendance;
    private final Path testPhotos;
    private final JFrame frame;
    private final JTextArea output = new JTextArea();
    private final JLabel nameLabel = infoLabel(89);
    private final JLabel shiftLabel = infoLabel(128);
    private final JLabel roleLabel = infoLabel(167);
    private final JLabel idLabel = infoLabel(206);
    private Employee current;

    public EmployeeScreen(AttendanceService attendance, Path testPhotos) {
        this.attendance = attendance;
        this.testPhotos = testPhotos;
        this.frame = Ui.frame("Tela do Funcionário", 638, 350, JFrame.EXIT_ON_CLOSE);
        build(frame.getContentPane());
        show(null);
    }

    public void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        output.setForeground(Color.WHITE);
        output.setBackground(Color.BLACK);
        output.setEditable(false);
        output.setText("\n Clique em Bater Ponto e escolha a foto.\n");
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBounds(289, 92, 317, 175);
        content.add(scroll);

        content.add(Ui.clock(475, 286));
        content.add(Ui.button("SAIR", 540, 40, 60, 23, e -> System.exit(0)));
        content.add(Ui.button("Registros", 420, 40, 89, 23, e -> openRecords()));
        content.add(Ui.button("Bater Ponto", 306, 40, 104, 23, e -> punch()));

        content.add(nameLabel);
        Ui.addLogo(content);
        Ui.addBars(content, 622, 49, 278, 33);
        content.add(shiftLabel);
        content.add(roleLabel);
        content.add(idLabel);
    }

    private void punch() {
        JFileChooser chooser = new JFileChooser(testPhotos.toFile());
        chooser.setDialogTitle("Escolha a foto para bater o ponto");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens", PhotoStore.EXTENSIONS.toArray(String[]::new)));
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path photo = chooser.getSelectedFile().toPath();
        output.append("\n Foto: " + photo.getFileName() + "\n");
        output.append("\n ----------------------------------------\n");

        try {
            PunchResult result = attendance.punch(photo);
            for (PhotoScore score : result.scores()) {
                output.append(String.format(Ui.LOCALE, "%n %s: %.2f%%%n", score.employee().name(),
                        score.similarity() * 100));
            }
            if (result.record().isEmpty()) {
                output.append("\n Funcionário não reconhecido.\n Ponto não registrado.\n");
            } else {
                AttendanceRecord record = result.record().get();
                output.append("\n  ( OK - Ponto registrado: " + record.employee().name() + " )\n");
                output.append("\n =================================");
                output.append("\n  Validação de ponto finalizada!");
                output.append("\n =================================\n");
                show(record.employee());
            }
        } catch (IOException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
        output.setCaretPosition(output.getDocument().getLength());
    }

    private void openRecords() {
        if (current == null) {
            JOptionPane.showMessageDialog(frame, "Bata o ponto primeiro para ver os seus registros.", "FacePoint",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            RecordsScreen.forEmployee(current, attendance.recordsOf(current)).open();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }

    private void show(Employee employee) {
        current = employee;
        nameLabel.setText("Nome: " + (employee == null ? "" : employee.name()));
        shiftLabel.setText("Turno: " + (employee == null ? "" : employee.shift()));
        roleLabel.setText("Função: " + (employee == null ? "" : employee.role()));
        idLabel.setText("ID: " + (employee == null ? "" : employee.id()));
    }

    private static JLabel infoLabel(int y) {
        JLabel label = new JLabel();
        label.setForeground(Color.BLACK);
        label.setFont(Ui.LABEL_FONT);
        label.setBounds(20, y, 259, 28);
        return label;
    }
}

package io.github.guavovic.pontofacial.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatClientProperties;

import io.github.guavovic.pontofacial.domain.AttendanceRecord;
import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.service.AttendanceService.PhotoScore;
import io.github.guavovic.pontofacial.service.AttendanceService.PunchResult;
import io.github.guavovic.pontofacial.service.Services;
import io.github.guavovic.pontofacial.storage.PhotoStore;
import io.github.guavovic.pontofacial.storage.StorageException;

public final class EmployeeScreen {

    private static final int PHOTO_SIZE = 160;

    private final Services services;
    private final Path testPhotos;
    private final JFrame frame;
    private final JTextArea output = new JTextArea(12, 34);
    private final JLabel photo = new JLabel("Sem foto", SwingConstants.CENTER);
    private final JLabel name = new JLabel();
    private final JLabel shift = new JLabel();
    private final JLabel role = new JLabel();
    private final JLabel id = new JLabel();
    private Employee current;

    public EmployeeScreen(Services services, Path testPhotos) {
        this.services = services;
        this.testPhotos = testPhotos;

        JButton punch = Ui.button("Bater ponto", e -> punch());
        JButton records = Ui.button("Meus registros", e -> openRecords());
        JButton exit = Ui.button("Sair", e -> System.exit(0));

        frame = Ui.screen("Ponto Facial", "Ponto Facial", "Registro de ponto", Ui.clock(), body(),
                Ui.actions(exit, records, punch), JFrame.EXIT_ON_CLOSE);
        Ui.primary(frame, punch);
        show(null);
    }

    public void open() {
        frame.setVisible(true);
    }

    private JPanel body() {
        photo.setPreferredSize(new Dimension(PHOTO_SIZE, PHOTO_SIZE));
        photo.setMaximumSize(new Dimension(PHOTO_SIZE, PHOTO_SIZE));
        photo.setAlignmentX(0);
        photo.setForeground(Color.GRAY);
        photo.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        name.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        name.setAlignmentX(0);
        for (JLabel label : new JLabel[] { shift, role, id }) {
            label.setAlignmentX(0);
        }

        JPanel employee = new JPanel();
        employee.setLayout(new BoxLayout(employee, BoxLayout.Y_AXIS));
        employee.setPreferredSize(new Dimension(PHOTO_SIZE + 20, 290));
        employee.add(photo);
        employee.add(Box.createVerticalStrut(12));
        employee.add(name);
        employee.add(Box.createVerticalStrut(4));
        employee.add(shift);
        employee.add(role);
        employee.add(id);
        employee.add(Box.createVerticalGlue());

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        output.setMargin(new Insets(8, 10, 8, 10));
        output.setText("Clique em Bater ponto e escolha a foto.\n");

        JPanel body = new JPanel(new BorderLayout(24, 0));
        body.add(employee, BorderLayout.WEST);
        body.add(new JScrollPane(output), BorderLayout.CENTER);
        return body;
    }

    private void punch() {
        JFileChooser chooser = new JFileChooser(testPhotos.toFile());
        chooser.setDialogTitle("Escolha a foto para bater o ponto");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens", PhotoStore.EXTENSIONS.toArray(String[]::new)));
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path file = chooser.getSelectedFile().toPath();
        output.setText("Foto: " + file.getFileName() + "\n\n");

        try {
            PunchResult result = services.attendance().punch(file);
            for (PhotoScore score : result.scores()) {
                output.append(String.format(Ui.LOCALE, "%-24s %6.2f%%%n", score.employee().name(),
                        score.similarity() * 100));
            }
            if (result.record().isEmpty()) {
                output.append("\nFuncionário não reconhecido.\nPonto não registrado.\n");
                show(null);
            } else {
                AttendanceRecord record = result.record().get();
                output.append("\nPonto registrado: " + record.employee().name() + "\n");
                show(record.employee());
            }
        } catch (IOException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
        output.setCaretPosition(0);
    }

    private void openRecords() {
        if (current == null) {
            Ui.showInfo(frame, "Bata o ponto primeiro para ver os seus registros.");
            return;
        }
        try {
            RecordsScreen.forEmployee(current, services.attendance()).open();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }

    private void show(Employee employee) {
        current = employee;
        if (employee == null) {
            name.setText("Ninguém identificado");
            name.setForeground(Color.GRAY);
            shift.setText(" ");
            role.setText(" ");
            id.setText(" ");
            photo.setIcon(null);
            photo.setText("Sem foto");
            return;
        }
        name.setText(employee.name());
        name.setForeground(UIManager.getColor("Label.foreground"));
        shift.setText("Turno: " + employee.shift());
        role.setText("Função: " + employee.role());
        id.setText("ID: " + employee.id());

        var icon = services.employees().firstPhoto(employee).map(file -> Ui.thumbnail(file, PHOTO_SIZE - 2))
                .orElse(null);
        photo.setIcon(icon);
        photo.setText(icon == null ? "Sem foto" : null);
    }
}

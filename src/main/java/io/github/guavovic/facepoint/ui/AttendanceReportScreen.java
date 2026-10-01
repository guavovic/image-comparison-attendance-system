package io.github.guavovic.facepoint.ui;

import java.awt.Container;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.service.Services;
import io.github.guavovic.facepoint.storage.StorageException;

final class AttendanceReportScreen {

    private final Services services;
    private final JFrame frame;
    private final JComboBox<Employee> combo;
    private final JTextField from = Ui.textField(95, 120, 138);
    private final JTextField to = Ui.textField(95, 160, 138);

    AttendanceReportScreen(Services services) {
        this.services = services;
        List<Employee> everyone = new ArrayList<>();
        everyone.add(null);
        everyone.addAll(services.employees().list());
        this.combo = Ui.employeeCombo(everyone, 95, 80, 138);
        this.frame = Ui.frame("Relatório", 281, 330, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        Ui.addLogo(content);
        Ui.addBars(content, 266, 16, 268, 23);

        content.add(Ui.fieldLabel("Quem:", 30, 80, 55));
        content.add(combo);
        content.add(Ui.fieldLabel("De:", 30, 120, 55));
        content.add(from);
        content.add(Ui.fieldLabel("Até:", 30, 160, 55));
        content.add(to);
        JLabel hint = new JLabel("Datas: dd/mm/aaaa. Vazio = tudo.");
        hint.setFont(Ui.BUTTON_FONT);
        hint.setBounds(30, 190, 230, 20);
        content.add(hint);
        content.add(Ui.button("Gerar", 40, 225, 87, 23, e -> generate()));
        content.add(Ui.button("Cancelar", 136, 225, 87, 23, e -> frame.dispose()));
    }

    private void generate() {
        try {
            LocalDate start = Ui.parseDate(from.getText());
            LocalDate end = Ui.parseDate(to.getText());
            if (start != null && end != null && end.isBefore(start)) {
                Ui.showError(frame, "A data final é anterior à inicial.");
                return;
            }
            List<AttendanceRecord> records = services.attendance().records((Employee) combo.getSelectedItem(), start,
                    end);
            if (records.isEmpty()) {
                Ui.showInfo(frame, "Nenhum ponto nesse filtro.");
                return;
            }
            Path target = Ui.chooseCsv(frame, "pontos.csv");
            if (target == null) {
                return;
            }
            services.reports().attendance(records, target);
            Ui.showInfo(frame, records.size() + " ponto(s) gravados em " + target.getFileName() + ".");
            frame.dispose();
        } catch (IllegalArgumentException | StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

package io.github.guavovic.pontofacial.ui;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

import io.github.guavovic.pontofacial.domain.AttendanceRecord;
import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.service.Services;
import io.github.guavovic.pontofacial.storage.StorageException;

final class AttendanceReportScreen {

    private final Services services;
    private final JFrame frame;
    private final JComboBox<Employee> combo;
    private final JTextField from = Ui.field(10);
    private final JTextField to = Ui.field(10);

    AttendanceReportScreen(Services services) {
        this.services = services;

        List<Employee> everyone = new ArrayList<>();
        everyone.add(null);
        everyone.addAll(services.employees().list());
        this.combo = Ui.employeeCombo(everyone);

        from.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/mm/aaaa (vazio = desde o começo)");
        to.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/mm/aaaa (vazio = até hoje)");

        Form form = new Form().row("Funcionário", combo).row("De", from).row("Até", to);
        JButton generate = Ui.button("Gerar", e -> generate());

        frame = Ui.screen("Ponto Facial - Relatório de pontos", "Ponto Facial", "Relatório de pontos", null, form,
                Ui.actions(null, Ui.button("Cancelar", e -> frame().dispose()), generate), JFrame.DISPOSE_ON_CLOSE);
        Ui.primary(frame, generate);
    }

    void open() {
        frame.setVisible(true);
    }

    private JFrame frame() {
        return frame;
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

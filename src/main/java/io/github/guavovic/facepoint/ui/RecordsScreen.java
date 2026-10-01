package io.github.guavovic.facepoint.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;

final class RecordsScreen {

    private final JFrame frame;
    private final JList<String> list = new JList<>();
    private final JTextField search = new JTextField(20);
    private final List<String> lines = new ArrayList<>();

    private RecordsScreen(String title, List<AttendanceRecord> records, Function<AttendanceRecord, String> describe) {
        for (int i = 0; i < records.size(); i++) {
            lines.add((i + 1) + "   " + describe.apply(records.get(i)));
        }

        frame = new JFrame(" " + title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refresh();
            }
        });

        JButton reverse = new JButton("Inverter Ordem");
        reverse.addActionListener(e -> {
            Collections.reverse(lines);
            refresh();
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(search);
        searchPanel.add(reverse);

        frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
        refresh();
    }

    static RecordsScreen forEmployee(Employee employee, List<AttendanceRecord> records) {
        return new RecordsScreen("Registros de " + employee.name(), records,
                record -> record.recordedAt().format(Ui.DATE_TIME));
    }

    static RecordsScreen forEveryone(List<AttendanceRecord> records) {
        return new RecordsScreen("Registros de ponto", records,
                record -> record.recordedAt().format(Ui.DATE_TIME) + "   " + record.employee().name());
    }

    void open() {
        frame.setVisible(true);
    }

    private void refresh() {
        String term = search.getText().toLowerCase(Ui.LOCALE);
        list.setListData(lines.stream()
                .filter(line -> line.toLowerCase(Ui.LOCALE).contains(term))
                .toArray(String[]::new));
    }
}

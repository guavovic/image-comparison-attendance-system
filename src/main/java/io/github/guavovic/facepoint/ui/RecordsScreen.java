package io.github.guavovic.facepoint.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
import io.github.guavovic.facepoint.service.AttendanceService;
import io.github.guavovic.facepoint.storage.StorageException;

final class RecordsScreen {

    private final JFrame frame;
    private final JList<String> list = new JList<>();
    private final JTextField search = new JTextField(20);
    private final Supplier<List<AttendanceRecord>> loader;
    private final Function<AttendanceRecord, String> describe;
    private final Consumer<AttendanceRecord> deleter;
    private List<AttendanceRecord> records = new ArrayList<>();
    private List<AttendanceRecord> visible = List.of();
    private boolean reversed;

    private RecordsScreen(String title, Supplier<List<AttendanceRecord>> loader,
            Function<AttendanceRecord, String> describe, Consumer<AttendanceRecord> deleter) {
        this.loader = loader;
        this.describe = describe;
        this.deleter = deleter;

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
            reversed = !reversed;
            refresh();
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(search);
        searchPanel.add(reverse);

        frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);

        if (deleter != null) {
            JButton delete = new JButton("Remover registro selecionado");
            delete.addActionListener(e -> deleteSelected());
            JPanel bottom = new JPanel();
            bottom.add(delete);
            frame.getContentPane().add(bottom, BorderLayout.SOUTH);
        }

        reload();
    }

    static RecordsScreen forEmployee(Employee employee, AttendanceService attendance) {
        return new RecordsScreen("Registros de " + employee.name(), () -> attendance.recordsOf(employee),
                record -> record.recordedAt().format(Ui.DATE_TIME), null);
    }

    static RecordsScreen forEveryone(AttendanceService attendance) {
        return new RecordsScreen("Registros de ponto", attendance::allRecords,
                record -> record.recordedAt().format(Ui.DATE_TIME) + "   " + record.employee().name(),
                attendance::deleteRecord);
    }

    void open() {
        frame.setVisible(true);
    }

    private void reload() {
        records = new ArrayList<>(loader.get());
        refresh();
    }

    private void refresh() {
        String term = search.getText().toLowerCase(Ui.LOCALE);
        List<AttendanceRecord> shown = new ArrayList<>(records);
        if (reversed) {
            Collections.reverse(shown);
        }
        shown = shown.stream().filter(record -> describe.apply(record).toLowerCase(Ui.LOCALE).contains(term))
                .toList();
        visible = shown;
        String[] lines = new String[shown.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = (i + 1) + "   " + describe.apply(shown.get(i));
        }
        list.setListData(lines);
    }

    private void deleteSelected() {
        int index = list.getSelectedIndex();
        if (index < 0) {
            Ui.showInfo(frame, "Selecione um registro na lista.");
            return;
        }
        AttendanceRecord record = visible.get(index);
        if (!Ui.confirm(frame, "Remover o ponto de " + record.employee().name() + " em "
                + record.recordedAt().format(Ui.DATE_TIME) + "?")) {
            return;
        }
        try {
            deleter.accept(record);
            reload();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

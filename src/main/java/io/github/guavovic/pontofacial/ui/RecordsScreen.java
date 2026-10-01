package io.github.guavovic.pontofacial.ui;

import java.awt.BorderLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.FlatClientProperties;

import io.github.guavovic.pontofacial.domain.AttendanceRecord;
import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.service.AttendanceService;
import io.github.guavovic.pontofacial.storage.StorageException;

final class RecordsScreen {

    private final JFrame frame;
    private final JTable table = new JTable();
    private final JTextField search = Ui.field(20);
    private final Supplier<List<AttendanceRecord>> loader;
    private final Consumer<AttendanceRecord> deleter;
    private final boolean showEmployee;
    private final RecordsModel model = new RecordsModel();
    private final TableRowSorter<RecordsModel> sorter = new TableRowSorter<>(model);

    private RecordsScreen(String title, String subtitle, boolean showEmployee,
            Supplier<List<AttendanceRecord>> loader, Consumer<AttendanceRecord> deleter) {
        this.showEmployee = showEmployee;
        this.loader = loader;
        this.deleter = deleter;

        table.setModel(model);
        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setDefaultRenderer(LocalDateTime.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : ((LocalDateTime) value).format(Ui.DATE_TIME));
            }
        });
        table.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : String.format(Ui.LOCALE, "%.2f%%", (Double) value * 100));
            }
        });
        sorter.setSortKeys(List.of(new TableRowSorter.SortKey(0, SortOrder.DESCENDING)));

        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar por nome ou data");
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.add(search, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        body.setPreferredSize(new java.awt.Dimension(560, 380));

        JPanel actions;
        JButton close = Ui.button("Fechar", e -> frame().dispose());
        if (deleter != null) {
            actions = Ui.actions(null, Ui.button("Remover registro selecionado", e -> deleteSelected()), close);
        } else {
            actions = Ui.actions(null, close);
        }

        frame = Ui.screen("Ponto Facial - " + title, "Ponto Facial", subtitle, null, body, actions,
                JFrame.DISPOSE_ON_CLOSE);
        reload();
    }

    static RecordsScreen forEmployee(Employee employee, AttendanceService attendance) {
        return new RecordsScreen("Registros de " + employee.name(), "Registros de " + employee.name(), false,
                () -> attendance.recordsOf(employee), null);
    }

    static RecordsScreen forEveryone(AttendanceService attendance) {
        return new RecordsScreen("Registros de ponto", "Registros de ponto", true, attendance::allRecords,
                attendance::deleteRecord);
    }

    void open() {
        frame.setVisible(true);
    }

    private JFrame frame() {
        return frame;
    }

    private void reload() {
        model.set(loader.get());
    }

    private void applyFilter() {
        String term = search.getText().strip().toLowerCase(Ui.LOCALE);
        sorter.setRowFilter(term.isEmpty() ? null : new RowFilter<RecordsModel, Integer>() {
            @Override
            public boolean include(Entry<? extends RecordsModel, ? extends Integer> entry) {
                AttendanceRecord record = model.records.get(entry.getIdentifier());
                String text = record.recordedAt().format(Ui.DATE_TIME) + " " + record.employee().name();
                return text.toLowerCase(Ui.LOCALE).contains(term);
            }
        });
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Ui.showInfo(frame, "Selecione um registro na tabela.");
            return;
        }
        AttendanceRecord record = model.records.get(table.convertRowIndexToModel(row));
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

    private final class RecordsModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private transient List<AttendanceRecord> records = new ArrayList<>();

        void set(List<AttendanceRecord> newRecords) {
            records = new ArrayList<>(newRecords);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return records.size();
        }

        @Override
        public int getColumnCount() {
            return showEmployee ? 3 : 2;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> "Data e hora";
                case 1 -> showEmployee ? "Funcionário" : "Nota";
                default -> "Nota";
            };
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return switch (column) {
                case 0 -> LocalDateTime.class;
                case 1 -> showEmployee ? String.class : Double.class;
                default -> Double.class;
            };
        }

        @Override
        public Object getValueAt(int row, int column) {
            AttendanceRecord record = records.get(row);
            return switch (column) {
                case 0 -> record.recordedAt();
                case 1 -> showEmployee ? record.employee().name() : record.similarity();
                default -> record.similarity();
            };
        }
    }
}

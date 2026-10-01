package io.github.guavovic.facepoint.ui;

import java.awt.Dimension;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.guavovic.facepoint.domain.Notice;
import io.github.guavovic.facepoint.service.AttendanceService;
import io.github.guavovic.facepoint.storage.StorageException;

final class NoticesScreen {

    private final AttendanceService attendance;
    private final JFrame frame;
    private final NoticesModel model = new NoticesModel();

    NoticesScreen(AttendanceService attendance) {
        this.attendance = attendance;

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(0).setMaxWidth(190);
        table.getColumnModel().getColumn(1).setPreferredWidth(480);
        table.setDefaultRenderer(LocalDateTime.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : ((LocalDateTime) value).format(Ui.DATE_TIME));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(700, 340));

        frame = Ui.screen("FacePoint - Avisos", "FacePoint", "Fotos que não foram reconhecidas", null, scroll,
                Ui.actions(null, Ui.button("Limpar avisos", e -> clear()), Ui.button("Fechar", e -> frame().dispose())),
                JFrame.DISPOSE_ON_CLOSE);
        model.set(attendance.notices());
    }

    void open() {
        frame.setVisible(true);
    }

    private JFrame frame() {
        return frame;
    }

    private void clear() {
        if (attendance.notices().isEmpty() || !Ui.confirm(frame, "Apagar todos os avisos?")) {
            return;
        }
        try {
            attendance.clearNotices();
            model.set(attendance.notices());
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }

    private static final class NoticesModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private transient List<Notice> notices = List.of();

        void set(List<Notice> newNotices) {
            notices = newNotices;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return notices.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Data e hora" : "Aviso";
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 0 ? LocalDateTime.class : String.class;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Notice notice = notices.get(row);
            return column == 0 ? notice.createdAt() : notice.message();
        }
    }
}

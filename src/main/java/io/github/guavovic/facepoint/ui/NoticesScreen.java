package io.github.guavovic.facepoint.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import io.github.guavovic.facepoint.domain.Notice;
import io.github.guavovic.facepoint.service.AttendanceService;
import io.github.guavovic.facepoint.storage.StorageException;

final class NoticesScreen {

    private final AttendanceService attendance;
    private final JFrame frame;
    private final JList<String> list = new JList<>();

    NoticesScreen(AttendanceService attendance) {
        this.attendance = attendance;

        frame = new JFrame(" Lista de Avisos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 400);
        frame.setLocationRelativeTo(null);

        JButton clear = new JButton("Limpar avisos");
        clear.addActionListener(e -> clear());
        JPanel bottom = new JPanel();
        bottom.add(clear);

        frame.getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
        frame.getContentPane().add(bottom, BorderLayout.SOUTH);
        reload();
    }

    void open() {
        frame.setVisible(true);
    }

    private void reload() {
        List<Notice> notices = attendance.notices();
        if (notices.isEmpty()) {
            list.setListData(new String[] { "  Nenhum aviso. Fotos não reconhecidas aparecem aqui." });
            return;
        }
        list.setListData(notices.stream()
                .map(notice -> "  " + notice.createdAt().format(Ui.DATE_TIME) + "   " + notice.message())
                .toArray(String[]::new));
    }

    private void clear() {
        if (attendance.notices().isEmpty() || !Ui.confirm(frame, "Apagar todos os avisos?")) {
            return;
        }
        try {
            attendance.clearNotices();
            reload();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

package io.github.guavovic.facepoint.ui;

import java.awt.Container;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

import io.github.guavovic.facepoint.service.Services;
import io.github.guavovic.facepoint.storage.StorageException;

public final class ManagementScreen {

    private final Services services;
    private final JFrame frame;

    public ManagementScreen(Services services) {
        this.services = services;
        this.frame = Ui.frame("Tela de Gerenciamento", 816, 472, JFrame.EXIT_ON_CLOSE);
        build(frame.getContentPane());
    }

    public void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        content.add(Ui.clock(660, 408));
        content.add(Ui.button("SAIR", 710, 40, 60, 23, e -> System.exit(0)));
        content.add(Ui.button("Gerenciamento de Pontos", 514, 40, 166, 23, e -> guard(() -> RecordsScreen.forEveryone(services.attendance()).open())));
        content.add(Ui.button("Gerenciamento de Acessos", 330, 40, 174, 23, e -> new AccessManagementScreen(services).open()));
        content.add(Ui.button("Lista de Avisos", 206, 40, 114, 23, e -> guard(() -> new NoticesScreen(services.attendance()).open())));
        content.add(Ui.button("Gerar Relatorio", 82, 40, 114, 23, e -> guard(() -> new AttendanceReportScreen(services).open())));
        Ui.addLogo(content);
        Ui.addBars(content, 800, 49, 400, 33);
        addCenterLogo(content, 330);
    }

    private void guard(Runnable action) {
        try {
            action.run();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }

    static void addCenterLogo(Container content, int x) {
        Ui.addLogo(content, 26, x, 193, 136, 23, x + 2, 186, 40);
        JLabel underline = new JLabel("_____________");
        underline.setFont(new Font("Tahoma", Font.BOLD, 16));
        underline.setBounds(x, 205, 136, 23);
        content.add(underline);
    }
}

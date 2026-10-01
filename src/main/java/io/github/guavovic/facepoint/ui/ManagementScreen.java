package io.github.guavovic.facepoint.ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import io.github.guavovic.facepoint.service.Services;
import io.github.guavovic.facepoint.storage.StorageException;

public final class ManagementScreen {

    private final Services services;
    private final JFrame frame;

    public ManagementScreen(Services services) {
        this.services = services;

        JPanel tiles = new JPanel(new GridLayout(2, 2, 14, 14));
        tiles.add(Ui.tile("Pontos", "Ver e remover os registros", e -> guard(() -> RecordsScreen.forEveryone(services.attendance()).open())));
        tiles.add(Ui.tile("Funcionários", "Adicionar, editar e remover", e -> new EmployeesScreen(services).open()));
        tiles.add(Ui.tile("Avisos", "Fotos que não foram reconhecidas", e -> guard(() -> new NoticesScreen(services.attendance()).open())));
        tiles.add(Ui.tile("Relatório", "Pontos em planilha CSV", e -> guard(() -> new AttendanceReportScreen(services).open())));

        frame = Ui.screen("FacePoint - Gerenciamento", "FacePoint", "Gerenciamento", Ui.clock(), tiles,
                Ui.actions(Ui.button("Sair", e -> System.exit(0))), JFrame.EXIT_ON_CLOSE);
    }

    public void open() {
        frame.setVisible(true);
    }

    private void guard(Runnable action) {
        try {
            action.run();
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

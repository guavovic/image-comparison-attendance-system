package io.github.guavovic.pontofacial.ui;

import java.awt.GridLayout;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

import io.github.guavovic.pontofacial.service.Services;
import io.github.guavovic.pontofacial.storage.StorageException;

final class EmployeesScreen {

    private final Services services;
    private final JFrame frame;

    EmployeesScreen(Services services) {
        this.services = services;

        JPanel tiles = new JPanel(new GridLayout(2, 2, 14, 14));
        tiles.add(Ui.tile("Adicionar", "Cadastrar com as fotos", e -> new AddEmployeeScreen(services.employees()).open()));
        tiles.add(Ui.tile("Editar", "Mudar dados e juntar fotos", e -> edit()));
        tiles.add(Ui.tile("Remover", "Apaga também fotos e pontos", e -> remove()));
        tiles.add(Ui.tile("Relatório", "Lista de funcionários em CSV", e -> report()));

        frame = Ui.screen("Ponto Facial - Funcionários", "Ponto Facial", "Funcionários", Ui.clock(), tiles,
                Ui.actions(null, Ui.button("Voltar", e -> frame().dispose())), JFrame.DISPOSE_ON_CLOSE);
    }

    void open() {
        frame.setVisible(true);
    }

    private JFrame frame() {
        return frame;
    }

    private void edit() {
        EditEmployeeScreen screen = new EditEmployeeScreen(services.employees());
        if (screen.hasEmployees()) {
            screen.open();
        } else {
            Ui.showInfo(frame, "Não há funcionários cadastrados.");
        }
    }

    private void remove() {
        RemoveEmployeeScreen screen = new RemoveEmployeeScreen(services.employees());
        if (screen.hasEmployees()) {
            screen.open();
        } else {
            Ui.showInfo(frame, "Não há funcionários cadastrados.");
        }
    }

    private void report() {
        try {
            Path target = Ui.chooseCsv(frame, "funcionarios.csv");
            if (target != null) {
                services.reports().employees(services.employees().list(), target);
                Ui.showInfo(frame, "Relatório gravado em " + target.getFileName() + ".");
            }
        } catch (StorageException e) {
            Ui.showError(frame, e.getMessage());
        }
    }
}

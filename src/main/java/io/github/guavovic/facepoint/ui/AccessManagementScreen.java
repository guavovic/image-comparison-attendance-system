package io.github.guavovic.facepoint.ui;

import java.awt.Container;
import java.nio.file.Path;

import javax.swing.JFrame;

import io.github.guavovic.facepoint.service.Services;
import io.github.guavovic.facepoint.storage.StorageException;

final class AccessManagementScreen {

    private final Services services;
    private final JFrame frame;

    AccessManagementScreen(Services services) {
        this.services = services;
        this.frame = Ui.frame("Tela de Gerenciamento de Acesso", 786, 472, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        content.add(Ui.clock(620, 408));
        content.add(Ui.button("Voltar", 687, 40, 67, 23, e -> frame.dispose()));
        content.add(Ui.button("Remover Usuário", 542, 40, 124, 23, e -> remove()));
        content.add(Ui.button("Gerar Relatório", 414, 40, 118, 23, e -> report()));
        content.add(Ui.button("Editar Usuário", 294, 40, 111, 23, e -> edit()));
        content.add(Ui.button("Adicionar Usuário", 154, 40, 130, 23, e -> new AddEmployeeScreen(services.employees()).open()));
        Ui.addLogo(content);
        Ui.addBars(content, 771, 49, 400, 33);
        ManagementScreen.addCenterLogo(content, 317);
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

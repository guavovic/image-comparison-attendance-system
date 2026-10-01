package io.github.guavovic.facepoint.ui;

import java.awt.Container;

import javax.swing.JFrame;

final class AccessManagementScreen {

    private final JFrame frame;

    AccessManagementScreen() {
        frame = Ui.frame("Tela de Gerenciamento de Acesso", 786, 472, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        content.add(Ui.clock(620, 408));
        content.add(Ui.button("Voltar", 687, 40, 67, 23, e -> frame.dispose()));
        content.add(Ui.button("Remover Usuário", 542, 40, 124, 23, e -> new RemoveEmployeeScreen().open()));
        content.add(Ui.button("Gerar Relatório", 414, 40, 118, 23, null));
        content.add(Ui.button("Editar Usuário", 294, 40, 111, 23, null));
        content.add(Ui.button("Adicionar Usuário", 154, 40, 130, 23, e -> new AddEmployeeScreen().open()));
        Ui.addLogo(content);
        Ui.addBars(content, 771, 49, 400, 33);
        ManagementScreen.addCenterLogo(content, 317);
    }
}

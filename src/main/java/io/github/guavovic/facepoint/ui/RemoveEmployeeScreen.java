package io.github.guavovic.facepoint.ui;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

final class RemoveEmployeeScreen {

    private final JFrame frame;

    RemoveEmployeeScreen() {
        frame = Ui.frame("Remover Funcionario", 351, 243, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        Ui.addLogo(content);
        Ui.addBars(content, 335, 16, 182, 23);
        content.add(Ui.button("Remover", 70, 135, 87, 23, null));

        JTextField id = new JTextField(10);
        id.setBounds(172, 80, 132, 25);
        content.add(id);

        JLabel label = new JLabel("ID do funcionário:");
        label.setFont(Ui.FIELD_LABEL_FONT);
        label.setBounds(30, 80, 132, 20);
        content.add(label);

        content.add(Ui.button("Cancelar", 177, 135, 87, 23, e -> frame.dispose()));
    }
}

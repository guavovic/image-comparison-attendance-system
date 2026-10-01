package io.github.guavovic.facepoint.ui;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

final class AddEmployeeScreen {

    private final JFrame frame;

    AddEmployeeScreen() {
        frame = Ui.frame("Adicionar Funcionario", 281, 350, JFrame.DISPOSE_ON_CLOSE);
        build(frame.getContentPane());
    }

    void open() {
        frame.setVisible(true);
    }

    private void build(Container content) {
        Ui.addLogo(content);
        Ui.addBars(content, 266, 16, 288, 23);
        content.add(Ui.button("Adicionar", 40, 245, 87, 23, null));
        addField(content, "Nome:", 80, 80);
        addField(content, "ID:", 123, 120);
        addField(content, "Turno:", 160, 158);
        addField(content, "Função:", 195, 194);
        content.add(Ui.button("Cancelar", 136, 245, 87, 23, e -> frame.dispose()));
    }

    private static void addField(Container content, String text, int labelY, int fieldY) {
        JLabel label = new JLabel(text);
        label.setFont(Ui.FIELD_LABEL_FONT);
        label.setBounds(30, labelY, 55, 20);
        content.add(label);

        JTextField field = new JTextField(10);
        field.setBounds(95, fieldY, 138, 25);
        content.add(field);
    }
}

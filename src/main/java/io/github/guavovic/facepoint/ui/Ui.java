package io.github.guavovic.facepoint.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

final class Ui {

    static final Color BAR = new Color(74, 149, 149);
    static final Color TOOLBAR = new Color(106, 181, 181);
    static final Color LOGO = new Color(0, 128, 64);
    static final Color LOGO_SHADOW = new Color(154, 214, 181);

    static final Font BUTTON_FONT = new Font("Tahoma", Font.BOLD, 10);
    static final Font LABEL_FONT = new Font("Tahoma", Font.BOLD, 12);
    static final Font FIELD_LABEL_FONT = new Font("Tahoma", Font.BOLD, 14);

    static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");

    private Ui() {
    }

    static JFrame frame(String title, int width, int height, int closeOperation) {
        JFrame frame = new JFrame(" " + title);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(closeOperation);
        frame.getContentPane().setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        return frame;
    }

    static void addLogo(Container container) {
        addLogo(container, 16, 20, 5, 93, 23, 23, 7, 23);
    }

    static void addLogo(Container container, int size, int x, int y, int width, int height, int logoX, int logoY,
            int logoHeight) {
        JLabel shadow = new JLabel("FacePoint");
        shadow.setForeground(LOGO_SHADOW);
        shadow.setFont(new Font("Tahoma", Font.BOLD, size));
        shadow.setBounds(x, y, width, height);
        container.add(shadow);

        JLabel logo = new JLabel("FacePoint");
        logo.setForeground(LOGO);
        logo.setFont(new Font("Tahoma", Font.BOLD, size));
        logo.setBounds(logoX, logoY, width, logoHeight);
        container.add(logo);
    }

    static void addBars(Container container, int width, int toolbarHeight, int footerY, int footerHeight) {
        container.add(panel(BAR, 0, 0, width, 33));
        container.add(panel(BAR, 0, footerY, width, footerHeight));
        container.add(panel(TOOLBAR, 0, 29, width, toolbarHeight));
    }

    static JButton button(String text, int x, int y, int width, int height, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBounds(x, y, width, height);
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    static JLabel clock(int x, int y) {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setBounds(x, y, 124, 17);
        Timer timer = new Timer(1000, e -> label.setText(LocalDateTime.now().format(DATE_TIME)));
        timer.setInitialDelay(0);
        timer.start();
        return label;
    }

    static void showError(Container parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "FacePoint", JOptionPane.ERROR_MESSAGE);
    }

    private static JPanel panel(Color color, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setBounds(x, y, width, height);
        return panel;
    }
}

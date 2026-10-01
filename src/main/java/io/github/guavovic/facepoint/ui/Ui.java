package io.github.guavovic.facepoint.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.storage.PhotoStore;

final class Ui {

    static final Color BAR = new Color(74, 149, 149);
    static final Color TOOLBAR = new Color(106, 181, 181);
    static final Color LOGO = new Color(0, 128, 64);
    static final Color LOGO_SHADOW = new Color(154, 214, 181);

    static final Font BUTTON_FONT = new Font("Tahoma", Font.BOLD, 10);
    static final Font LABEL_FONT = new Font("Tahoma", Font.BOLD, 12);
    static final Font FIELD_LABEL_FONT = new Font("Tahoma", Font.BOLD, 14);

    static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

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

    static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "FacePoint", JOptionPane.ERROR_MESSAGE);
    }

    static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "FacePoint", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "FacePoint", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    static JLabel fieldLabel(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setFont(FIELD_LABEL_FONT);
        label.setBounds(x, y, width, 20);
        return label;
    }

    static JTextField textField(int x, int y, int width) {
        JTextField field = new JTextField(10);
        field.setBounds(x, y, width, 25);
        return field;
    }

    static JComboBox<Employee> employeeCombo(List<Employee> employees, int x, int y, int width) {
        JComboBox<Employee> combo = new JComboBox<>(employees.toArray(Employee[]::new));
        combo.setBounds(x, y, width, 25);
        combo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected,
                    boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Employee employee) {
                    setText(employee.name() + " (ID " + employee.id() + ")");
                } else if (value == null) {
                    setText("Todos");
                }
                return this;
            }
        });
        return combo;
    }

    static List<Path> chooseImages(Component parent, Path startFolder) {
        JFileChooser chooser = new JFileChooser(startFolder == null ? null : startFolder.toFile());
        chooser.setDialogTitle("Escolha as fotos");
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens", PhotoStore.EXTENSIONS.toArray(String[]::new)));
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return List.of();
        }
        return Arrays.stream(chooser.getSelectedFiles()).map(java.io.File::toPath).toList();
    }

    static Path chooseCsv(Component parent, String suggestedName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatório");
        chooser.setSelectedFile(new java.io.File(suggestedName));
        chooser.setFileFilter(new FileNameExtensionFilter("Planilha CSV", "csv"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        Path path = chooser.getSelectedFile().toPath();
        if (!path.getFileName().toString().toLowerCase(LOCALE).endsWith(".csv")) {
            path = path.resolveSibling(path.getFileName() + ".csv");
        }
        if (path.toFile().exists() && !confirm(parent, "O arquivo " + path.getFileName() + " já existe. Substituir?")) {
            return null;
        }
        return path;
    }

    static LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(text.strip(), DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + text.strip() + ". Use o formato dd/mm/aaaa.");
        }
    }

    private static JPanel panel(Color color, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setBounds(x, y, width, height);
        return panel;
    }
}

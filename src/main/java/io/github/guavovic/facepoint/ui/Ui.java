package io.github.guavovic.facepoint.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatClientProperties;

import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.storage.PhotoStore;

final class Ui {

    private static final int MIN_WIDTH = 420;

    static final Color ACCENT = new Color(0x2F8F8F);

    static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");

    static final List<String> SHIFTS = List.of("Manhã", "Tarde", "Noite");

    private Ui() {
    }

    static JFrame screen(String title, String heading, String subtitle, JComponent east, JComponent body,
            JComponent actions, int closeOperation) {
        JPanel root = new JPanel(new BorderLayout());
        root.add(header(heading, subtitle, east), BorderLayout.NORTH);

        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        root.add(body, BorderLayout.CENTER);

        if (actions != null) {
            actions.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
            root.add(actions, BorderLayout.SOUTH);
        }

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(closeOperation);
        frame.setContentPane(root);
        frame.pack();
        frame.setSize(Math.max(frame.getWidth(), MIN_WIDTH), frame.getHeight());
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private static JPanel header(String heading, String subtitle, JComponent east) {
        JLabel title = new JLabel(heading);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(subtitle);
        sub.setForeground(new Color(0xD9EFEF));

        JPanel texts = new JPanel(new BorderLayout());
        texts.setOpaque(false);
        texts.add(title, BorderLayout.NORTH);
        texts.add(sub, BorderLayout.SOUTH);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        header.add(texts, BorderLayout.WEST);
        if (east != null) {
            east.setForeground(Color.WHITE);
            header.add(east, BorderLayout.EAST);
        }
        return header;
    }

    static JPanel actions(JComponent left, JComponent... right) {
        JPanel panel = new JPanel(new BorderLayout());
        if (left != null) {
            panel.add(left, BorderLayout.WEST);
        }
        JPanel group = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        for (JComponent component : right) {
            group.add(component);
        }
        panel.add(group, BorderLayout.EAST);
        return panel;
    }

    static JButton button(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    static JButton primary(JFrame frame, JButton button) {
        frame.getRootPane().setDefaultButton(button);
        return button;
    }

    static JButton tile(String title, String description, ActionListener action) {
        JButton button = new JButton("<html><b>" + title + "</b><br><span style='color:gray'>" + description
                + "</span></html>");
        button.setHorizontalAlignment(JButton.LEFT);
        button.setMargin(new Insets(14, 16, 14, 16));
        button.setPreferredSize(new Dimension(230, 76));
        button.addActionListener(action);
        return button;
    }

    static JLabel clock() {
        JLabel label = new JLabel();
        Timer timer = new Timer(1000, e -> label.setText(LocalDateTime.now().format(DATE_TIME)));
        timer.setInitialDelay(0);
        timer.start();
        label.addHierarchyListener(e -> {
            boolean changed = (e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0;
            if (changed && !label.isDisplayable()) {
                timer.stop();
            }
        });
        return label;
    }

    static JTextField field(int columns) {
        return new JTextField(columns);
    }

    static JComboBox<String> shiftCombo() {
        JComboBox<String> combo = new JComboBox<>(SHIFTS.toArray(String[]::new));
        combo.setEditable(true);
        return combo;
    }

    static JComboBox<Employee> employeeCombo(List<Employee> employees) {
        JComboBox<Employee> combo = new JComboBox<>(employees.toArray(Employee[]::new));
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

    static ImageIcon thumbnail(Path photo, int size) {
        try {
            BufferedImage image = ImageIO.read(photo.toFile());
            if (image == null) {
                return null;
            }
            return new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            return null;
        }
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

    static List<Path> chooseImages(Component parent, Path startFolder) {
        JFileChooser chooser = new JFileChooser(startFolder == null ? null : startFolder.toFile());
        chooser.setDialogTitle("Escolha as fotos");
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens", PhotoStore.EXTENSIONS.toArray(String[]::new)));
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return List.of();
        }
        return Arrays.stream(chooser.getSelectedFiles()).map(File::toPath).toList();
    }

    static Path chooseCsv(Component parent, String suggestedName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatório");
        chooser.setSelectedFile(new File(suggestedName));
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
}

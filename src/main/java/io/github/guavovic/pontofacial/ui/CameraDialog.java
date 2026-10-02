package io.github.guavovic.pontofacial.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import io.github.guavovic.pontofacial.camera.Camera;
import io.github.guavovic.pontofacial.camera.CameraException;

final class CameraDialog {

    private static final int PREVIEW_SIZE = 400;

    private final JDialog dialog;
    private final Preview preview = new Preview();
    private final JLabel status = new JLabel("Procurando câmeras...", SwingConstants.CENTER);
    private final JComboBox<Camera.Info> cameras = new JComboBox<>();
    private final JButton shoot = new JButton("Tirar foto");
    private final AtomicReference<BufferedImage> latest = new AtomicReference<>();
    private volatile int requestedIndex = -1;
    private volatile boolean running = true;
    private volatile boolean autoPicking = true;
    private volatile List<Camera.Info> found = List.of();
    private BufferedImage result;

    private CameraDialog(Component parent) {
        dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Ponto Facial - Câmera",
                JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        cameras.addActionListener(e -> {
            Camera.Info selected = (Camera.Info) cameras.getSelectedItem();
            if (selected != null) {
                latest.set(null);
                preview.show(null);
                status.setText("Abrindo a câmera...");
                shoot.setEnabled(false);
                requestedIndex = selected.index();
            }
        });

        shoot.setEnabled(false);
        shoot.addActionListener(e -> {
            result = latest.get();
            dialog.dispose();
        });
        JButton cancel = new JButton("Cancelar");
        cancel.addActionListener(e -> dialog.dispose());

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.add(new JLabel("Câmera"), BorderLayout.WEST);
        top.add(cameras, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(status, BorderLayout.CENTER);
        JPanel buttons = Ui.actions(null, cancel, shoot);
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        bottom.add(buttons, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        content.add(top, BorderLayout.NORTH);
        content.add(preview, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(shoot);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                running = false;
            }
        });
    }

    static BufferedImage capture(Component parent) {
        CameraDialog camera = new CameraDialog(parent);
        Thread worker = new Thread(camera::work, "camera");
        worker.setDaemon(true);
        worker.start();
        camera.dialog.setVisible(true);
        return camera.result;
    }

    private void work() {
        try {
            found = Camera.list();
            if (found.isEmpty()) {
                SwingUtilities.invokeLater(() -> status.setText("Nenhuma câmera real encontrada."));
                return;
            }
            SwingUtilities.invokeLater(() -> {
                found.forEach(cameras::addItem);
                cameras.setSelectedIndex(0);
            });
            while (running) {
                int index = requestedIndex;
                if (index < 0) {
                    Thread.sleep(50);
                    continue;
                }
                stream(index);
            }
        } catch (CameraException e) {
            SwingUtilities.invokeLater(() -> status.setText(e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void stream(int index) throws InterruptedException {
        try (Camera camera = Camera.open(index)) {
            boolean first = true;
            while (running && requestedIndex == index) {
                BufferedImage frame = camera.read();
                if (frame == null) {
                    Thread.sleep(30);
                    continue;
                }
                latest.set(frame);
                boolean showStatus = first;
                first = false;
                SwingUtilities.invokeLater(() -> {
                    preview.show(frame);
                    shoot.setEnabled(true);
                    if (showStatus) {
                        autoPicking = false;
                        status.setText("Centralize o rosto no oval e clique em Tirar foto.");
                    }
                });
                Thread.sleep(25);
            }
        } catch (CameraException e) {
            requestedIndex = -1;
            failed(index);
        }
    }

    private void failed(int index) {
        List<Camera.Info> cameraList = found;
        int position = 0;
        while (position < cameraList.size() && cameraList.get(position).index() != index) {
            position++;
        }
        String name = position < cameraList.size() ? cameraList.get(position).name() : "a câmera";
        int next = position + 1;
        if (autoPicking && next < cameraList.size()) {
            SwingUtilities.invokeLater(() -> {
                status.setText("Não foi possível abrir " + name + ". Tentando a próxima câmera...");
                cameras.setSelectedIndex(next);
            });
        } else {
            SwingUtilities.invokeLater(
                    () -> status.setText("Não foi possível abrir " + name + ". Escolha outra câmera na lista."));
        }
    }

    private static final class Preview extends JPanel {

        private static final long serialVersionUID = 1L;

        private transient BufferedImage image;

        Preview() {
            setPreferredSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE));
            setBackground(new Color(0x20, 0x20, 0x20));
            setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));
        }

        void show(BufferedImage frame) {
            image = frame;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g.create();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (image != null) {
                graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
            graphics.setColor(new Color(255, 255, 255, 210));
            graphics.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                    new float[] { 10f, 8f }, 0f));
            int width = (int) (getWidth() * 0.55);
            int height = (int) (getHeight() * 0.78);
            graphics.drawOval((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
            graphics.dispose();
        }
    }
}

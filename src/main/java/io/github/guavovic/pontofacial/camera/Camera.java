package io.github.guavovic.pontofacial.camera;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Locale;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public final class Camera implements AutoCloseable {

    public record Info(int index, String name) {

        @Override
        public String toString() {
            return name;
        }
    }

    private static final int BACKEND = Videoio.CAP_DSHOW;

    private static boolean loaded;

    private final VideoCapture capture;
    private final Mat frame = new Mat();

    private Camera(VideoCapture capture) {
        this.capture = capture;
    }

    public static List<Info> list() {
        requireWindows();
        return DirectShowDevices.list().stream().filter(info -> !DirectShowDevices.isVirtual(info.name())).toList();
    }

    public static Camera open(int index) {
        load();
        VideoCapture capture = new VideoCapture(index, BACKEND);
        if (!capture.isOpened()) {
            capture.release();
            throw new CameraException("Não foi possível abrir a câmera.");
        }
        return new Camera(capture);
    }

    public BufferedImage read() {
        if (!capture.read(frame) || frame.empty() || frame.channels() != 3) {
            return null;
        }
        int width = frame.cols();
        int height = frame.rows();
        byte[] bgr = new byte[width * height * 3];
        frame.get(0, 0, bgr);
        return Frames.centerSquare(width, height, bgr);
    }

    @Override
    public void close() {
        capture.release();
        frame.release();
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        requireWindows();
        try {
            nu.pattern.OpenCV.loadLocally();
        } catch (UnsatisfiedLinkError | RuntimeException e) {
            throw new CameraException("Não foi possível carregar o suporte à câmera.", e);
        }
        loaded = true;
    }

    private static void requireWindows() {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("windows")) {
            throw new CameraException("A câmera só funciona no Windows.");
        }
    }
}

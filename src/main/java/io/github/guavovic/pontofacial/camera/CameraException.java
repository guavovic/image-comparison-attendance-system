package io.github.guavovic.pontofacial.camera;

public class CameraException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CameraException(String message) {
        super(message);
    }

    public CameraException(String message, Throwable cause) {
        super(message, cause);
    }
}

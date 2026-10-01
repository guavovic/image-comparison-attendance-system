package io.github.guavovic.pontofacial.camera;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public final class Frames {

    private Frames() {
    }

    public static BufferedImage centerSquare(int width, int height, byte[] bgr) {
        if (bgr.length != width * height * 3) {
            throw new IllegalArgumentException("O quadro tem " + bgr.length + " bytes, mas " + width + "x" + height
                    + " precisa de " + width * height * 3);
        }

        int side = Math.min(width, height);
        int left = (width - side) / 2;
        int top = (height - side) / 2;

        BufferedImage image = new BufferedImage(side, side, BufferedImage.TYPE_3BYTE_BGR);
        byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < side; row++) {
            System.arraycopy(bgr, ((top + row) * width + left) * 3, target, row * side * 3, side * 3);
        }
        return image;
    }
}

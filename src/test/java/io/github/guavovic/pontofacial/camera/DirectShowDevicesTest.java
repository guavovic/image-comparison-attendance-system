package io.github.guavovic.pontofacial.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DirectShowDevicesTest {

    @Test
    @DisplayName("lê o índice e o nome de cada linha, na ordem")
    void parsesIndexAndName() {
        String output = "0\tACER HD User Facing\r\n1\tHD Pro Webcam C920\r\n2\tCHRONOS A05S (Câmera Virtual do Windows)\r\n";

        List<Camera.Info> cameras = DirectShowDevices.parse(output);

        assertEquals(List.of(new Camera.Info(0, "ACER HD User Facing"), new Camera.Info(1, "HD Pro Webcam C920"),
                new Camera.Info(2, "CHRONOS A05S (Câmera Virtual do Windows)")), cameras);
    }

    @Test
    @DisplayName("ignora linhas em branco e o que não é dispositivo")
    void ignoresOtherLines() {
        String output = "\n AVISO: algo do PowerShell\n0\tWebcam\n\nsem tab\nx\tnome sem número\n";

        assertEquals(List.of(new Camera.Info(0, "Webcam")), DirectShowDevices.parse(output));
    }

    @Test
    @DisplayName("saída vazia dá lista vazia")
    void emptyOutputIsEmptyList() {
        assertTrue(DirectShowDevices.parse("").isEmpty());
    }

    @Test
    @DisplayName("o nome da câmera é o texto que aparece na lista")
    void infoPrintsItsName() {
        assertEquals("HD Pro Webcam C920", new Camera.Info(1, "HD Pro Webcam C920").toString());
    }

    @Test
    @DisplayName("câmeras virtuais são reconhecidas pelo nome")
    void recognizesVirtualCameras() {
        assertTrue(DirectShowDevices.isVirtual("CHRONOS A05S (Câmera Virtual do Windows)"));
        assertTrue(DirectShowDevices.isVirtual("OBS Virtual Camera"));
        assertTrue(DirectShowDevices.isVirtual("Meta Quest 3"));
    }

    @Test
    @DisplayName("webcams de verdade não são tratadas como virtuais")
    void realCamerasAreNotVirtual() {
        assertFalse(DirectShowDevices.isVirtual("HD Pro Webcam C920"));
        assertFalse(DirectShowDevices.isVirtual("ACER HD User Facing"));
        assertFalse(DirectShowDevices.isVirtual("Integrated Camera"));
    }
}

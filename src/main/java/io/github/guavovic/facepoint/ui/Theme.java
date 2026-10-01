package io.github.guavovic.facepoint.ui;

import java.util.Map;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public final class Theme {

    private Theme() {
    }

    public static void install() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#2F8F8F"));
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }
}

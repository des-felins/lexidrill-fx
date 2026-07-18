package dev.cyberjar.springfx.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class JavaFxResourceHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("fxml/*");
        hints.resources().registerPattern("styles/styles.css");
        hints.resources().registerPattern("graphics/*");
    }
}
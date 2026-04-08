package com.baoths;

/**
 * Launcher class to workaround the JavaFX 11+ runtime component missing error
 * when running from a fat jar.
 * This class must NOT extend javafx.application.Application.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}

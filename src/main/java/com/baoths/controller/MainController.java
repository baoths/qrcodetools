package com.baoths.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Main controller for the application root layout.
 * Handles theme toggling and global status updates.
 */
public class MainController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML private TabPane tabPane;
    @FXML private ToggleButton btnThemeToggle;
    @FXML private Label lblStatus;
    @FXML private Label lblAppTitle;

    private boolean isDarkTheme = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("MainController initialized.");
    }

    /**
     * Toggles between dark and light theme.
     */
    @FXML
    private void handleThemeToggle() {
        Scene scene = btnThemeToggle.getScene();
        if (scene == null) {
            return;
        }

        isDarkTheme = !isDarkTheme;

        if (isDarkTheme) {
            scene.getRoot().getStyleClass().remove("light-theme");
            btnThemeToggle.setText("\uD83C\uDF19 Tối");
            btnThemeToggle.setSelected(false);
        } else {
            scene.getRoot().getStyleClass().add("light-theme");
            btnThemeToggle.setText("☀ Sáng");
            btnThemeToggle.setSelected(true);
        }

        LOGGER.info("Theme toggled to: " + (isDarkTheme ? "Dark" : "Light"));
    }

    /**
     * Updates the global status bar text.
     *
     * @param message status message
     */
    public void setStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }
}

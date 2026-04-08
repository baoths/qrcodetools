package com.baoths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * QRCodeTools - JavaFX QR Code Utility Application.
 * Entry point for the application.
 */
public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("main"), 1100, 720);
        scene.getStylesheets().add(
                getClass().getResource("css/styles.css").toExternalForm()
        );

        stage.setTitle("QRCodeTools - Tiện ích QR Code");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();

        LOGGER.info("QRCodeTools started.");
    }

    /**
     * Changes the root of the current scene.
     *
     * @param fxml the FXML file name (without extension)
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Returns the primary stage reference.
     *
     * @return the primary Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Loads an FXML file from the fxml/ resource directory.
     *
     * @param fxml the FXML file name (without extension)
     * @return the loaded Parent node
     * @throws IOException if the FXML file cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("fxml/" + fxml + ".fxml")
        );
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
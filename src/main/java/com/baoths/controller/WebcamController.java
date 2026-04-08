package com.baoths.controller;

import com.baoths.exception.QRCodeException;
import com.baoths.model.QRCodeData;
import com.baoths.model.QRCodeType;
import com.baoths.service.ContentFormatterService;
import com.baoths.service.HistoryService;
import com.baoths.service.WebcamService;
import com.baoths.util.ClipboardUtils;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Controller for the Webcam QR scanning tab.
 * Handles camera start/stop, live preview, and real-time QR detection.
 */
public class WebcamController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(WebcamController.class.getName());

    @FXML private StackPane pnlWebcamView;
    @FXML private ImageView imgWebcam;
    @FXML private Label lblWebcamPlaceholder;
    @FXML private Label lblWebcamStatus;
    @FXML private Label lblScanCount;
    @FXML private Button btnStartCamera;
    @FXML private Button btnStopCamera;
    @FXML private TextArea txaWebcamResult;
    @FXML private Button btnCopyWebcamResult;

    private final WebcamService webcamService = new WebcamService();
    private final ContentFormatterService formatterService = new ContentFormatterService();
    private final HistoryService historyService = HistoryService.getInstance();

    private final Set<String> scannedCodes = new HashSet<>();
    private int scanCount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("WebcamController initialized.");
    }

    /**
     * Starts the webcam capture.
     */
    @FXML
    private void handleStartCamera() {
        lblWebcamStatus.setText("Đang khởi động webcam...");
        lblWebcamStatus.getStyleClass().removeAll("label-success", "label-error");

        btnStartCamera.setDisable(true);
        btnStopCamera.setDisable(false);
        lblWebcamPlaceholder.setVisible(false);

        // Start webcam on background
        new Thread(() -> {
            try {
                webcamService.startCapture(
                        // Frame callback — update ImageView
                        frame -> Platform.runLater(() -> {
                            imgWebcam.setImage(SwingFXUtils.toFXImage(frame, null));
                        }),
                        // Decoded callback — show result
                        decodedContent -> Platform.runLater(() -> {
                            if (!scannedCodes.contains(decodedContent)) {
                                scannedCodes.add(decodedContent);
                                scanCount++;

                                QRCodeType type = formatterService.detectType(decodedContent);
                                String entry = "[" + type.getDisplayName() + "] " + decodedContent + "\n";
                                txaWebcamResult.appendText(entry);

                                lblScanCount.setText("Đã quét: " + scanCount + " mã");
                                btnCopyWebcamResult.setDisable(false);

                                // Add to history
                                historyService.addEntry(new QRCodeData(
                                        decodedContent, type, "DECODED", 0, 0));

                                lblWebcamStatus.setText("✓ Phát hiện mã QR!");
                                lblWebcamStatus.getStyleClass().add("label-success");
                            }
                        })
                );

                Platform.runLater(() -> {
                    lblWebcamStatus.setText("Webcam đang hoạt động. Hướng camera vào mã QR...");
                });

            } catch (QRCodeException e) {
                Platform.runLater(() -> {
                    lblWebcamStatus.setText("✗ " + e.getMessage());
                    lblWebcamStatus.getStyleClass().add("label-error");
                    btnStartCamera.setDisable(false);
                    btnStopCamera.setDisable(true);
                    lblWebcamPlaceholder.setVisible(true);
                });
            }
        }).start();
    }

    /**
     * Stops the webcam capture.
     */
    @FXML
    private void handleStopCamera() {
        webcamService.stopCapture();
        btnStartCamera.setDisable(false);
        btnStopCamera.setDisable(true);
        lblWebcamPlaceholder.setVisible(true);
        lblWebcamStatus.setText("Webcam đã dừng.");
        imgWebcam.setImage(null);
    }

    /**
     * Copies all webcam scan results to clipboard.
     */
    @FXML
    private void handleCopyWebcamResult() {
        String content = txaWebcamResult.getText();
        if (content != null && !content.isEmpty()) {
            ClipboardUtils.copyToClipboard(content);
            lblWebcamStatus.setText("✓ Đã copy kết quả vào clipboard.");
        }
    }

    /**
     * Clears webcam scan results.
     */
    @FXML
    private void handleClearWebcamResult() {
        txaWebcamResult.setText("");
        scannedCodes.clear();
        scanCount = 0;
        lblScanCount.setText("Đã quét: 0 mã");
        btnCopyWebcamResult.setDisable(true);
    }
}

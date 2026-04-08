package com.baoths.controller;

import com.baoths.App;
import com.baoths.exception.QRCodeException;
import com.baoths.model.QRCodeConfig;
import com.baoths.service.BatchGeneratorService;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for the Batch QR Code Generator tab.
 * Handles multi-line input, batch generation, and folder save.
 */
public class BatchController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(BatchController.class.getName());

    @FXML private TextArea txaBatchInput;
    @FXML private Label lblLineCount;
    @FXML private ComboBox<String> cmbBatchSize;
    @FXML private ComboBox<String> cmbBatchErrorCorrection;
    @FXML private ComboBox<String> cmbBatchFormat;
    @FXML private Button btnBatchGenerate;
    @FXML private Button btnBatchSave;
    @FXML private ProgressBar prgBatch;
    @FXML private Label lblBatchProgress;
    @FXML private TextArea txaBatchLog;

    private final BatchGeneratorService batchService = new BatchGeneratorService();
    private List<BufferedImage> generatedImages = new ArrayList<>();
    private List<String> contentLines = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate combos
        cmbBatchSize.getItems().addAll("Nhỏ (200px)", "Vừa (400px)", "Lớn (600px)");
        cmbBatchSize.setValue("Vừa (400px)");

        cmbBatchErrorCorrection.getItems().addAll("L - Thấp", "M - Trung bình", "Q - Khá", "H - Cao");
        cmbBatchErrorCorrection.setValue("M - Trung bình");

        cmbBatchFormat.getItems().addAll("PNG", "JPG");
        cmbBatchFormat.setValue("PNG");

        // Monitor line count
        txaBatchInput.textProperty().addListener((obs, oldVal, newVal) -> {
            long lines = newVal.isEmpty() ? 0 :
                    Arrays.stream(newVal.split("\\n")).filter(l -> !l.trim().isEmpty()).count();
            lblLineCount.setText(lines + " dòng");
        });

        LOGGER.info("BatchController initialized.");
    }

    /**
     * Handles batch generate button click.
     */
    @FXML
    private void handleBatchGenerate() {
        String input = txaBatchInput.getText();
        if (input == null || input.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập nội dung.");
            return;
        }

        contentLines = Arrays.stream(input.split("\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (contentLines.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không có nội dung hợp lệ.");
            return;
        }

        QRCodeConfig config = buildBatchConfig();
        int total = contentLines.size();

        btnBatchGenerate.setDisable(true);
        btnBatchSave.setDisable(true);
        prgBatch.setProgress(0);
        txaBatchLog.setText("");
        lblBatchProgress.setText("Đang tạo 0/" + total + "...");

        Task<List<BufferedImage>> batchTask = new Task<List<BufferedImage>>() {
            @Override
            protected List<BufferedImage> call() throws Exception {
                List<BufferedImage> results = new ArrayList<>();
                for (int i = 0; i < contentLines.size(); i++) {
                    String content = contentLines.get(i);
                    BufferedImage img = batchService.generateBatch(
                            java.util.Collections.singletonList(content), config).get(0);
                    results.add(img);

                    int current = i + 1;
                    Platform.runLater(() -> {
                        prgBatch.setProgress((double) current / total);
                        lblBatchProgress.setText("Đang tạo " + current + "/" + total + "...");
                        txaBatchLog.appendText("✓ [" + current + "] " + content + "\n");
                    });
                }
                return results;
            }
        };

        batchTask.setOnSucceeded(event -> {
            generatedImages = batchTask.getValue();
            prgBatch.setProgress(1.0);
            lblBatchProgress.setText("✓ Hoàn thành! " + generatedImages.size() + " mã QR đã tạo.");
            btnBatchGenerate.setDisable(false);
            btnBatchSave.setDisable(false);
            txaBatchLog.appendText("\n=== Hoàn thành ===\n");
        });

        batchTask.setOnFailed(event -> {
            Throwable ex = batchTask.getException();
            lblBatchProgress.setText("✗ Lỗi: " + ex.getMessage());
            btnBatchGenerate.setDisable(false);
            txaBatchLog.appendText("✗ Lỗi: " + ex.getMessage() + "\n");
        });

        new Thread(batchTask).start();
    }

    /**
     * Handles saving batch results to a folder.
     */
    @FXML
    private void handleBatchSave() {
        if (generatedImages.isEmpty()) {
            return;
        }

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Chọn thư mục lưu");

        File folder = dirChooser.showDialog(App.getPrimaryStage());
        if (folder != null) {
            try {
                String format = cmbBatchFormat.getValue().toLowerCase();
                List<String> names = new ArrayList<>();
                for (int i = 0; i < contentLines.size(); i++) {
                    names.add("qrcode_" + (i + 1));
                }

                batchService.saveBatchToFolder(generatedImages, names, folder, format);
                lblBatchProgress.setText("✓ Đã lưu " + generatedImages.size() + " file vào " + folder.getName());
                txaBatchLog.appendText("✓ Đã lưu vào: " + folder.getAbsolutePath() + "\n");
            } catch (QRCodeException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu: " + e.getMessage());
            }
        }
    }

    /**
     * Builds QRCodeConfig from batch configuration fields.
     */
    private QRCodeConfig buildBatchConfig() {
        QRCodeConfig config = new QRCodeConfig();

        String sizeStr = cmbBatchSize.getValue();
        if (sizeStr.contains("200")) {
            config.setSize(QRCodeConfig.SIZE_SMALL);
        } else if (sizeStr.contains("600")) {
            config.setSize(QRCodeConfig.SIZE_LARGE);
        } else {
            config.setSize(QRCodeConfig.SIZE_MEDIUM);
        }

        String ecStr = cmbBatchErrorCorrection.getValue();
        if (ecStr.startsWith("L")) {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.L);
        } else if (ecStr.startsWith("Q")) {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.Q);
        } else if (ecStr.startsWith("H")) {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.H);
        } else {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.M);
        }

        return config;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

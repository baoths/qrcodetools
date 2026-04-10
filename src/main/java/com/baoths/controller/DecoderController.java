package com.baoths.controller;

import com.baoths.App;
import com.baoths.model.QRCodeData;
import com.baoths.model.QRCodeType;
import com.baoths.service.ContentFormatterService;
import com.baoths.service.HistoryService;
import com.baoths.service.QRCodeDecoderService;
import com.baoths.util.ClipboardUtils;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller for the QR Code Decoder tab.
 * Handles file selection, drag & drop, and QR decoding.
 */
public class DecoderController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DecoderController.class.getName());

    @FXML private StackPane pnlDropZone;
    @FXML private StackPane pnlImagePreview;
    @FXML private ImageView imgPreview;
    @FXML private TextArea txaDecodedContent;
    @FXML private Label lblDecodedType;
    @FXML private Label lblDecodeStatus;
    @FXML private Button btnCopyResult;
    @FXML private Button btnClearResult;
    @FXML private Button btnChooseFile;
    @FXML private Button btnPasteImage;

    private final QRCodeDecoderService decoderService = new QRCodeDecoderService();
    private final ContentFormatterService formatterService = new ContentFormatterService();
    private final HistoryService historyService = HistoryService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup drag & drop on the drop zone
        pnlDropZone.setOnDragOver(this::handleDragOver);
        pnlDropZone.setOnDragDropped(this::handleDragDropped);
        pnlDropZone.setOnDragEntered(e -> pnlDropZone.getStyleClass().add("drop-zone-active"));
        pnlDropZone.setOnDragExited(e -> pnlDropZone.getStyleClass().remove("drop-zone-active"));

        LOGGER.info("DecoderController initialized.");
    }

    /**
     * Handles drag over event — accepts files.
     */
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != pnlDropZone && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    /**
     * Handles drag drop event — decodes the dropped image file.
     */
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            if (!files.isEmpty()) {
                decodeFile(files.get(0));
                success = true;
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Handles file chooser button click.
     */
    @FXML
    private void handleChooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh QR Code");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        File file = chooser.showOpenDialog(App.getPrimaryStage());
        if (file != null) {
            decodeFile(file);
        }
    }

    /**
     * Decodes a QR code from an image file on a background thread.
     */
    private void decodeFile(File file) {
        // Show image preview
        try {
            Image img = new Image(file.toURI().toString());
            imgPreview.setImage(img);
            pnlImagePreview.setVisible(true);
            pnlImagePreview.setManaged(true);
        } catch (Exception e) {
            LOGGER.warning("Failed to preview image: " + e.getMessage());
        }

        lblDecodeStatus.setText("Đang giải mã...");
        lblDecodeStatus.getStyleClass().removeAll("label-success", "label-error");

        Task<String> decodeTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return decoderService.decodeFromFile(file);
            }
        };

        decodeTask.setOnSucceeded(event -> {
            String content = decodeTask.getValue();
            txaDecodedContent.setText(content);

            QRCodeType type = formatterService.detectType(content);
            lblDecodedType.setText("Loại: " + type.getDisplayName());

            lblDecodeStatus.setText("✓ Giải mã thành công!");
            lblDecodeStatus.getStyleClass().add("label-success");

            btnCopyResult.setDisable(false);
            btnClearResult.setDisable(false);

            // Add to history
            historyService.addEntry(new QRCodeData(content, type, "DECODED", 0, 0));
        });

        decodeTask.setOnFailed(event -> {
            Throwable ex = decodeTask.getException();
            lblDecodeStatus.setText("✗ " + ex.getMessage());
            lblDecodeStatus.getStyleClass().add("label-error");
            txaDecodedContent.setText("");
        });

        new Thread(decodeTask).start();
    }

    /**
     * Decodes QR from image currently stored in system clipboard.
     */
    @FXML
    private void handlePasteImageFromClipboard() {
        Image clipboardImage = ClipboardUtils.getImageFromClipboard();
        if (clipboardImage == null) {
            lblDecodeStatus.setText("Clipboard không có ảnh QR.");
            lblDecodeStatus.getStyleClass().remove("label-success");
            lblDecodeStatus.getStyleClass().add("label-error");
            return;
        }

        decodeFxImage(clipboardImage);
    }

    private void decodeFxImage(Image fxImage) {
        imgPreview.setImage(fxImage);
        pnlImagePreview.setVisible(true);
        pnlImagePreview.setManaged(true);

        lblDecodeStatus.setText("Đang giải mã...");
        lblDecodeStatus.getStyleClass().removeAll("label-success", "label-error");

        Task<String> decodeTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                BufferedImage image = SwingFXUtils.fromFXImage(fxImage, null);
                return decoderService.decodeFromImage(image);
            }
        };

        decodeTask.setOnSucceeded(event -> {
            String content = decodeTask.getValue();
            txaDecodedContent.setText(content);

            QRCodeType type = formatterService.detectType(content);
            lblDecodedType.setText("Loại: " + type.getDisplayName());

            lblDecodeStatus.setText("✓ Giải mã thành công!");
            lblDecodeStatus.getStyleClass().add("label-success");

            btnCopyResult.setDisable(false);
            btnClearResult.setDisable(false);

            historyService.addEntry(new QRCodeData(content, type, "DECODED", 0, 0));
        });

        decodeTask.setOnFailed(event -> {
            Throwable ex = decodeTask.getException();
            lblDecodeStatus.setText("✗ " + ex.getMessage());
            lblDecodeStatus.getStyleClass().add("label-error");
            txaDecodedContent.setText("");
        });

        new Thread(decodeTask).start();
    }

    /**
     * Copies the decoded content to clipboard.
     */
    @FXML
    private void handleCopyResult() {
        String content = txaDecodedContent.getText();
        if (content != null && !content.isEmpty()) {
            ClipboardUtils.copyToClipboard(content);
            lblDecodeStatus.setText("✓ Đã copy vào clipboard.");
        }
    }

    /**
     * Clears the decoded result.
     */
    @FXML
    private void handleClearResult() {
        txaDecodedContent.setText("");
        lblDecodedType.setText("");
        lblDecodeStatus.setText("");
        imgPreview.setImage(null);
        pnlImagePreview.setVisible(false);
        pnlImagePreview.setManaged(false);
        btnCopyResult.setDisable(true);
        btnClearResult.setDisable(true);
    }
}

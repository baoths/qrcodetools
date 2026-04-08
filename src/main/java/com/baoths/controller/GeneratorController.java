package com.baoths.controller;

import com.baoths.App;
import com.baoths.exception.QRCodeException;
import com.baoths.model.ModuleShape;
import com.baoths.model.QRCodeConfig;
import com.baoths.model.QRCodeData;
import com.baoths.model.QRCodeType;
import com.baoths.service.ContentFormatterService;
import com.baoths.service.HistoryService;
import com.baoths.service.QRCodeGeneratorService;
import com.baoths.util.ClipboardUtils;
import com.baoths.util.FileUtils;
import com.baoths.util.PrintUtils;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller for the QR Code Generator tab.
 * Handles content input, customization, QR generation, and export actions.
 */
public class GeneratorController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(GeneratorController.class.getName());

    // --- QR Type & Content ---
    @FXML private ComboBox<QRCodeType> cmbQRType;
    @FXML private TextArea txaContent;

    // --- WiFi fields ---
    @FXML private VBox pnlWifi;
    @FXML private TextField txtWifiSSID;
    @FXML private TextField txtWifiPassword;
    @FXML private ComboBox<String> cmbWifiEncryption;

    // --- VCard fields ---
    @FXML private VBox pnlVCard;
    @FXML private TextField txtVCardName;
    @FXML private TextField txtVCardPhone;
    @FXML private TextField txtVCardEmail;
    @FXML private TextField txtVCardOrg;

    // --- Email fields ---
    @FXML private VBox pnlEmail;
    @FXML private TextField txtEmailAddr;
    @FXML private TextField txtEmailSubject;
    @FXML private TextField txtEmailBody;

    // --- Phone fields ---
    @FXML private VBox pnlPhone;
    @FXML private TextField txtPhone;

    // --- Maps fields ---
    @FXML private VBox pnlMaps;
    @FXML private TextField txtMapsLat;
    @FXML private TextField txtMapsLng;

    // --- Customization ---
    @FXML private ComboBox<String> cmbSize;
    @FXML private ComboBox<String> cmbErrorCorrection;
    @FXML private ComboBox<ModuleShape> cmbModuleShape;
    @FXML private ColorPicker cpkForeground;
    @FXML private ColorPicker cpkBackground;
    @FXML private CheckBox chkGradient;
    @FXML private HBox pnlGradient;
    @FXML private ColorPicker cpkGradientStart;
    @FXML private ColorPicker cpkGradientEnd;

    // --- Logo ---
    @FXML private Button btnChooseLogo;
    @FXML private Label lblLogoName;
    @FXML private Button btnClearLogo;

    // --- Actions ---
    @FXML private Button btnGenerate;
    @FXML private Button btnSave;
    @FXML private Button btnPrint;
    @FXML private Button btnCopyImage;
    @FXML private Button btnPasteClipboard;

    // --- Preview ---
    @FXML private StackPane pnlQRPreview;
    @FXML private ImageView imgQRCode;
    @FXML private Label lblGenerateStatus;

    // --- Services ---
    private final QRCodeGeneratorService generatorService = new QRCodeGeneratorService();
    private final ContentFormatterService formatterService = new ContentFormatterService();
    private final HistoryService historyService = HistoryService.getInstance();

    // --- State ---
    private BufferedImage currentQRImage;
    private File selectedLogoFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate QR type combo
        cmbQRType.getItems().addAll(QRCodeType.values());
        cmbQRType.setValue(QRCodeType.TEXT);
        cmbQRType.setOnAction(e -> handleTypeChange());

        // Populate size combo
        cmbSize.getItems().addAll("Nhỏ (200px)", "Vừa (400px)", "Lớn (600px)");
        cmbSize.setValue("Vừa (400px)");

        // Populate error correction combo
        cmbErrorCorrection.getItems().addAll("L - Thấp (7%)", "M - Trung bình (15%)", "Q - Khá (25%)", "H - Cao (30%)");
        cmbErrorCorrection.setValue("M - Trung bình (15%)");

        // Populate module shape combo
        cmbModuleShape.getItems().addAll(ModuleShape.values());
        cmbModuleShape.setValue(ModuleShape.SQUARE);

        // Populate WiFi encryption combo
        cmbWifiEncryption.getItems().addAll("WPA", "WEP", "nopass");
        cmbWifiEncryption.setValue("WPA");

        // Default colors
        cpkForeground.setValue(Color.BLACK);
        cpkBackground.setValue(Color.WHITE);
        cpkGradientStart.setValue(Color.web("#7c5cff"));
        cpkGradientEnd.setValue(Color.web("#34d399"));

        LOGGER.info("GeneratorController initialized.");
    }

    /**
     * Handles QR type selection change — shows/hides relevant input fields.
     */
    private void handleTypeChange() {
        QRCodeType type = cmbQRType.getValue();

        // Hide all special panels
        txaContent.setVisible(true);
        txaContent.setManaged(true);
        pnlWifi.setVisible(false);
        pnlWifi.setManaged(false);
        pnlVCard.setVisible(false);
        pnlVCard.setManaged(false);
        pnlEmail.setVisible(false);
        pnlEmail.setManaged(false);
        pnlPhone.setVisible(false);
        pnlPhone.setManaged(false);
        pnlMaps.setVisible(false);
        pnlMaps.setManaged(false);

        switch (type) {
            case WIFI:
                txaContent.setVisible(false);
                txaContent.setManaged(false);
                pnlWifi.setVisible(true);
                pnlWifi.setManaged(true);
                break;
            case VCARD:
                txaContent.setVisible(false);
                txaContent.setManaged(false);
                pnlVCard.setVisible(true);
                pnlVCard.setManaged(true);
                break;
            case EMAIL:
                txaContent.setVisible(false);
                txaContent.setManaged(false);
                pnlEmail.setVisible(true);
                pnlEmail.setManaged(true);
                break;
            case PHONE:
                txaContent.setVisible(false);
                txaContent.setManaged(false);
                pnlPhone.setVisible(true);
                pnlPhone.setManaged(true);
                break;
            case MAPS:
                txaContent.setVisible(false);
                txaContent.setManaged(false);
                pnlMaps.setVisible(true);
                pnlMaps.setManaged(true);
                break;
            case URL:
                txaContent.setPromptText("Nhập URL (vd: https://example.com)");
                break;
            case TEXT:
            default:
                txaContent.setPromptText("Nhập nội dung mã QR...");
                break;
        }
    }

    /**
     * Builds the final content string based on the selected QR type.
     */
    private String buildContent() {
        QRCodeType type = cmbQRType.getValue();
        switch (type) {
            case WIFI:
                return formatterService.formatWifi(
                        txtWifiSSID.getText(), txtWifiPassword.getText(),
                        cmbWifiEncryption.getValue());
            case VCARD:
                return formatterService.formatVCard(
                        txtVCardName.getText(), txtVCardPhone.getText(),
                        txtVCardEmail.getText(), txtVCardOrg.getText());
            case EMAIL:
                return formatterService.formatEmail(
                        txtEmailAddr.getText(), txtEmailSubject.getText(),
                        txtEmailBody.getText());
            case PHONE:
                return formatterService.formatPhone(txtPhone.getText());
            case MAPS:
                return formatterService.formatMaps(txtMapsLat.getText(), txtMapsLng.getText());
            case URL:
                return formatterService.formatUrl(txaContent.getText());
            case TEXT:
            default:
                return txaContent.getText();
        }
    }

    /**
     * Builds the QRCodeConfig from the current UI selections.
     */
    private QRCodeConfig buildConfig() {
        QRCodeConfig config = new QRCodeConfig();

        // Size
        String sizeStr = cmbSize.getValue();
        if (sizeStr.contains("200")) {
            config.setSize(QRCodeConfig.SIZE_SMALL);
        } else if (sizeStr.contains("600")) {
            config.setSize(QRCodeConfig.SIZE_LARGE);
        } else {
            config.setSize(QRCodeConfig.SIZE_MEDIUM);
        }

        // Error correction
        String ecStr = cmbErrorCorrection.getValue();
        if (ecStr.startsWith("L")) {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.L);
        } else if (ecStr.startsWith("Q")) {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.Q);
        } else if (ecStr.startsWith("H")) {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.H);
        } else {
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.M);
        }

        // Module shape
        config.setModuleShape(cmbModuleShape.getValue());

        // Colors
        config.setForegroundColor(toArgbInt(cpkForeground.getValue()));
        config.setBackgroundColor(toArgbInt(cpkBackground.getValue()));

        // Gradient
        config.setUseGradient(chkGradient.isSelected());
        config.setGradientStartColor(toArgbInt(cpkGradientStart.getValue()));
        config.setGradientEndColor(toArgbInt(cpkGradientEnd.getValue()));

        // Logo
        config.setLogoFile(selectedLogoFile);

        return config;
    }

    /**
     * Handles the Generate button click — creates QR code on background thread.
     */
    @FXML
    private void handleGenerate() {
        String content = buildContent();
        if (content == null || content.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập nội dung để tạo mã QR.");
            return;
        }

        QRCodeConfig config = buildConfig();
        lblGenerateStatus.setText("Đang tạo mã QR...");
        lblGenerateStatus.getStyleClass().removeAll("label-success", "label-error");

        Task<BufferedImage> generateTask = new Task<BufferedImage>() {
            @Override
            protected BufferedImage call() throws Exception {
                return generatorService.generateQRCode(content, config);
            }
        };

        generateTask.setOnSucceeded(event -> {
            currentQRImage = generateTask.getValue();
            imgQRCode.setImage(SwingFXUtils.toFXImage(currentQRImage, null));

            lblGenerateStatus.setText("✓ Tạo mã QR thành công!");
            lblGenerateStatus.getStyleClass().add("label-success");

            btnSave.setDisable(false);
            btnPrint.setDisable(false);
            btnCopyImage.setDisable(false);

            // Add to history
            historyService.addEntry(new QRCodeData(
                    content, cmbQRType.getValue(), "GENERATED",
                    config.getSize(), config.getSize()));
        });

        generateTask.setOnFailed(event -> {
            Throwable ex = generateTask.getException();
            lblGenerateStatus.setText("✗ Lỗi: " + ex.getMessage());
            lblGenerateStatus.getStyleClass().add("label-error");
            LOGGER.severe("Generate failed: " + ex.getMessage());
        });

        new Thread(generateTask).start();
    }

    /**
     * Handles the Save button click — exports QR to PNG/JPG file.
     */
    @FXML
    private void handleSave() {
        if (currentQRImage == null) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu mã QR");
        chooser.setInitialFileName("qrcode.png");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg", "*.jpeg")
        );

        File file = chooser.showSaveDialog(App.getPrimaryStage());
        if (file != null) {
            try {
                String format = file.getName().toLowerCase().endsWith(".jpg") ? "jpg" : "png";
                FileUtils.saveImage(currentQRImage, file, format);
                lblGenerateStatus.setText("✓ Đã lưu: " + file.getName());
                lblGenerateStatus.getStyleClass().removeAll("label-error");
                lblGenerateStatus.getStyleClass().add("label-success");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu file: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the Print button click.
     */
    @FXML
    private void handlePrint() {
        if (currentQRImage == null) {
            return;
        }
        javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(currentQRImage, null);
        boolean success = PrintUtils.printImage(fxImage, App.getPrimaryStage());
        if (success) {
            lblGenerateStatus.setText("✓ Đã gửi lệnh in.");
        }
    }

    /**
     * Handles copying the QR image to clipboard.
     */
    @FXML
    private void handleCopyImage() {
        if (currentQRImage == null) {
            return;
        }
        javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(currentQRImage, null);
        ClipboardUtils.copyImageToClipboard(fxImage);
        lblGenerateStatus.setText("✓ Đã copy ảnh vào clipboard.");
    }

    /**
     * Handles pasting content from clipboard into the input field.
     */
    @FXML
    private void handlePasteClipboard() {
        String text = ClipboardUtils.getFromClipboard();
        if (!text.isEmpty()) {
            txaContent.setText(text);
            lblGenerateStatus.setText("Đã dán nội dung từ clipboard.");
        } else {
            lblGenerateStatus.setText("Clipboard trống.");
        }
    }

    /**
     * Handles gradient toggle checkbox.
     */
    @FXML
    private void handleGradientToggle() {
        boolean show = chkGradient.isSelected();
        pnlGradient.setVisible(show);
        pnlGradient.setManaged(show);
    }

    /**
     * Handles choosing a logo file.
     */
    @FXML
    private void handleChooseLogo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn logo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = chooser.showOpenDialog(App.getPrimaryStage());
        if (file != null) {
            selectedLogoFile = file;
            lblLogoName.setText(file.getName());
            btnClearLogo.setVisible(true);
            btnClearLogo.setManaged(true);
        }
    }

    /**
     * Handles clearing the selected logo.
     */
    @FXML
    private void handleClearLogo() {
        selectedLogoFile = null;
        lblLogoName.setText("Chưa chọn");
        btnClearLogo.setVisible(false);
        btnClearLogo.setManaged(false);
    }

    /**
     * Converts a JavaFX Color to an ARGB int.
     */
    private int toArgbInt(Color color) {
        int a = (int) (color.getOpacity() * 255);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Shows an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

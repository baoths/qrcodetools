package com.baoths.controller;

import com.baoths.App;
import com.baoths.model.QRCodeData;
import com.baoths.service.HistoryService;
import com.baoths.util.ClipboardUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller for the History tab.
 * Displays QR code history in a TableView with export options.
 */
public class HistoryController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HistoryController.class.getName());

    @FXML private TableView<QRCodeData> tblHistory;
    @FXML private TableColumn<QRCodeData, String> colTime;
    @FXML private TableColumn<QRCodeData, String> colAction;
    @FXML private TableColumn<QRCodeData, String> colType;
    @FXML private TableColumn<QRCodeData, String> colContent;
    @FXML private TableColumn<QRCodeData, String> colSize;
    @FXML private Label lblHistoryCount;
    @FXML private Button btnExportJson;
    @FXML private Button btnExportTxt;
    @FXML private Button btnClearHistory;

    private final HistoryService historyService = HistoryService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        colTime.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedTime()));
        colAction.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAction()));
        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().getDisplayName()));
        colContent.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTruncatedContent()));
        colSize.setCellValueFactory(data -> {
            QRCodeData d = data.getValue();
            String size = d.getWidth() > 0 ? d.getWidth() + "x" + d.getHeight() : "—";
            return new SimpleStringProperty(size);
        });

        // Bind table to history list
        tblHistory.setItems(historyService.getHistory());

        // Update count label when history changes
        historyService.getHistory().addListener(
                (javafx.collections.ListChangeListener<QRCodeData>) c ->
                        lblHistoryCount.setText(historyService.getHistory().size() + " mục")
        );

        // Double-click to copy content
        tblHistory.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                QRCodeData selected = tblHistory.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ClipboardUtils.copyToClipboard(selected.getContent());
                    showAlert(Alert.AlertType.INFORMATION, "Đã copy",
                            "Nội dung đã được copy vào clipboard.");
                }
            }
        });

        LOGGER.info("HistoryController initialized.");
    }

    /**
     * Exports history to JSON file.
     */
    @FXML
    private void handleExportJson() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất lịch sử ra JSON");
        chooser.setInitialFileName("qr_history.json");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = chooser.showSaveDialog(App.getPrimaryStage());
        if (file != null) {
            try {
                historyService.exportToJson(file);
                showAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Đã xuất " + historyService.getHistory().size() + " mục ra " + file.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xuất JSON: " + e.getMessage());
            }
        }
    }

    /**
     * Exports history to TXT file.
     */
    @FXML
    private void handleExportTxt() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất lịch sử ra TXT");
        chooser.setInitialFileName("qr_history.txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = chooser.showSaveDialog(App.getPrimaryStage());
        if (file != null) {
            try {
                historyService.exportToTxt(file);
                showAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Đã xuất " + historyService.getHistory().size() + " mục ra " + file.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xuất TXT: " + e.getMessage());
            }
        }
    }

    /**
     * Clears all history entries.
     */
    @FXML
    private void handleClearHistory() {
        if (historyService.getHistory().isEmpty()) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Xoá toàn bộ lịch sử phiên làm việc?");
        confirm.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isDefaultButton()) {
                historyService.clearHistory();
                lblHistoryCount.setText("0 mục");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

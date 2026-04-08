package com.baoths.service;

import com.baoths.model.QRCodeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing QR code history within the current session.
 * Provides observable list for UI binding and export capabilities.
 */
public class HistoryService {

    private static final Logger LOGGER = Logger.getLogger(HistoryService.class.getName());
    private static final HistoryService INSTANCE = new HistoryService();

    private final ObservableList<QRCodeData> history;

    private HistoryService() {
        this.history = FXCollections.observableArrayList();
    }

    /**
     * Returns the singleton instance.
     *
     * @return HistoryService singleton
     */
    public static HistoryService getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the observable history list for UI binding.
     *
     * @return ObservableList of QRCodeData
     */
    public ObservableList<QRCodeData> getHistory() {
        return history;
    }

    /**
     * Adds a new entry to the history.
     *
     * @param entry the QRCodeData to add
     */
    public void addEntry(QRCodeData entry) {
        history.add(0, entry); // Add to beginning (newest first)
        LOGGER.info("History entry added: " + entry.getAction() + " - " + entry.getTruncatedContent());
    }

    /**
     * Clears all history entries.
     */
    public void clearHistory() {
        history.clear();
        LOGGER.info("History cleared.");
    }

    /**
     * Exports the history to a JSON file.
     *
     * @param file the output JSON file
     * @throws IOException if writing fails
     */
    public void exportToJson(File file) throws IOException {
        LOGGER.info("Exporting history to JSON: " + file.getAbsolutePath());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Map<String, String>> entries = new ArrayList<>();
        for (QRCodeData data : history) {
            Map<String, String> entry = new HashMap<>();
            entry.put("time", data.getFormattedTime());
            entry.put("action", data.getAction());
            entry.put("type", data.getType().name());
            entry.put("content", data.getContent());
            entry.put("size", data.getWidth() + "x" + data.getHeight());
            entries.add(entry);
        }

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(entries, writer);
        }
        LOGGER.info("JSON export completed. " + entries.size() + " entries.");
    }

    /**
     * Exports the history to a plain text file.
     *
     * @param file the output text file
     * @throws IOException if writing fails
     */
    public void exportToTxt(File file) throws IOException {
        LOGGER.info("Exporting history to TXT: " + file.getAbsolutePath());

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("=== QRCodeTools - Lịch sử phiên làm việc ===");
            writer.println();

            for (int i = 0; i < history.size(); i++) {
                QRCodeData data = history.get(i);
                writer.println("--- Entry #" + (i + 1) + " ---");
                writer.println("Thời gian: " + data.getFormattedTime());
                writer.println("Hành động: " + data.getAction());
                writer.println("Loại:      " + data.getType().getDisplayName());
                writer.println("Kích thước:" + data.getWidth() + "x" + data.getHeight());
                writer.println("Nội dung:  " + data.getContent());
                writer.println();
            }

            writer.println("=== Tổng cộng: " + history.size() + " mục ===");
        }
        LOGGER.info("TXT export completed. " + history.size() + " entries.");
    }
}

package com.baoths.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * POJO representing a QR code history entry.
 * Stores content, type, creation time, and action performed.
 */
public class QRCodeData {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String content;
    private QRCodeType type;
    private LocalDateTime createdAt;
    private String action; // "GENERATED" or "DECODED"
    private int width;
    private int height;

    /**
     * Creates a new QRCodeData entry.
     *
     * @param content the QR code content
     * @param type    the type of QR code content
     * @param action  the action performed ("GENERATED" or "DECODED")
     * @param width   the width of the QR code
     * @param height  the height of the QR code
     */
    public QRCodeData(String content, QRCodeType type, String action, int width, int height) {
        this.content = content;
        this.type = type;
        this.action = action;
        this.width = width;
        this.height = height;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QRCodeType getType() {
        return type;
    }

    public void setType(QRCodeType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns a formatted string of the creation time.
     *
     * @return formatted date-time string
     */
    public String getFormattedTime() {
        return createdAt.format(FORMATTER);
    }

    /**
     * Returns a truncated version of the content for display in tables.
     *
     * @return truncated content (max 60 chars)
     */
    public String getTruncatedContent() {
        if (content == null) {
            return "";
        }
        return content.length() > 60 ? content.substring(0, 57) + "..." : content;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s",
                getFormattedTime(), action, type.getDisplayName(), getTruncatedContent());
    }
}

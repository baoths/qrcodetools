package com.baoths.util;

import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.logging.Logger;

/**
 * Utility class for clipboard operations (copy/paste text and images).
 */
public final class ClipboardUtils {

    private static final Logger LOGGER = Logger.getLogger(ClipboardUtils.class.getName());

    private ClipboardUtils() {
        // Utility class — no instantiation
    }

    /**
     * Copies a text string to the system clipboard.
     *
     * @param text the text to copy
     */
    public static void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        LOGGER.info("Text copied to clipboard. Length: " + text.length());
    }

    /**
     * Returns the current text content of the system clipboard.
     *
     * @return the clipboard text, or empty string if none
     */
    public static String getFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String text = clipboard.getString();
            LOGGER.info("Text retrieved from clipboard. Length: " + text.length());
            return text;
        }
        return "";
    }

    /**
     * Copies a JavaFX Image to the system clipboard.
     *
     * @param image the JavaFX Image to copy
     */
    public static void copyImageToClipboard(Image image) {
        ClipboardContent content = new ClipboardContent();
        content.putImage(image);
        Clipboard.getSystemClipboard().setContent(content);
        LOGGER.info("Image copied to clipboard.");
    }

    /**
     * Returns the current image from the system clipboard.
     *
     * @return clipboard image, or null if clipboard does not contain an image
     */
    public static Image getImageFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            LOGGER.info("Image retrieved from clipboard.");
            return clipboard.getImage();
        }
        return null;
    }
}

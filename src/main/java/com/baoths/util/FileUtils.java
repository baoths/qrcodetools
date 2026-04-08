package com.baoths.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for file I/O operations (image read/write).
 */
public final class FileUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {
        // Utility class — no instantiation
    }

    /**
     * Saves a BufferedImage to a file in the specified format.
     *
     * @param image  the image to save
     * @param file   the destination file
     * @param format the image format (e.g., "png", "jpg")
     * @throws IOException if saving fails
     */
    public static void saveImage(BufferedImage image, File file, String format) throws IOException {
        if (image == null) {
            throw new IOException("Ảnh không hợp lệ (null).");
        }

        // For JPEG, convert ARGB to RGB (JPEG doesn't support alpha)
        BufferedImage outputImage = image;
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            outputImage.createGraphics().drawImage(image, 0, 0, java.awt.Color.WHITE, null);
        }

        boolean success = ImageIO.write(outputImage, format.toUpperCase(), file);
        if (!success) {
            throw new IOException("Định dạng ảnh không được hỗ trợ: " + format);
        }
        LOGGER.info("Image saved: " + file.getAbsolutePath());
    }

    /**
     * Loads a BufferedImage from a file.
     *
     * @param file the image file to read
     * @return the loaded BufferedImage
     * @throws IOException if reading fails
     */
    public static BufferedImage loadImage(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File không tồn tại: " + (file != null ? file.getAbsolutePath() : "null"));
        }

        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("Không thể đọc file ảnh: " + file.getName());
        }
        LOGGER.info("Image loaded: " + file.getAbsolutePath());
        return image;
    }
}

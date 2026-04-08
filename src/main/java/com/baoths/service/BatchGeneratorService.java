package com.baoths.service;

import com.baoths.exception.QRCodeException;
import com.baoths.model.QRCodeConfig;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Service for batch generating multiple QR codes from a list of content strings.
 */
public class BatchGeneratorService {

    private static final Logger LOGGER = Logger.getLogger(BatchGeneratorService.class.getName());

    private final QRCodeGeneratorService generatorService;

    /**
     * Creates a new BatchGeneratorService.
     */
    public BatchGeneratorService() {
        this.generatorService = new QRCodeGeneratorService();
    }

    /**
     * Generates multiple QR codes from a list of content strings.
     *
     * @param contents list of content strings to encode
     * @param config   the QR code configuration to use for all
     * @return list of generated BufferedImage objects
     * @throws QRCodeException if any generation fails
     */
    public List<BufferedImage> generateBatch(List<String> contents, QRCodeConfig config)
            throws QRCodeException {
        if (contents == null || contents.isEmpty()) {
            throw new QRCodeException("Danh sách nội dung không được để trống.");
        }

        LOGGER.info("Starting batch generation for " + contents.size() + " items.");
        List<BufferedImage> results = new ArrayList<>();

        for (int i = 0; i < contents.size(); i++) {
            String content = contents.get(i).trim();
            if (!content.isEmpty()) {
                BufferedImage image = generatorService.generateQRCode(content, config);
                results.add(image);
                LOGGER.info("Generated batch item " + (i + 1) + "/" + contents.size());
            }
        }

        return results;
    }

    /**
     * Saves a batch of QR code images to a folder.
     *
     * @param images list of BufferedImage QR codes
     * @param names  list of filenames (without extension)
     * @param folder the destination folder
     * @param format the image format ("png" or "jpg")
     * @throws QRCodeException if saving fails
     */
    public void saveBatchToFolder(List<BufferedImage> images, List<String> names,
                                  File folder, String format) throws QRCodeException {
        if (!folder.exists() && !folder.mkdirs()) {
            throw new QRCodeException("Không thể tạo thư mục: " + folder.getAbsolutePath());
        }

        LOGGER.info("Saving batch of " + images.size() + " to: " + folder.getAbsolutePath());

        for (int i = 0; i < images.size(); i++) {
            String fileName = (i < names.size() && !names.get(i).isEmpty())
                    ? sanitizeFileName(names.get(i))
                    : "qrcode_" + (i + 1);
            File outputFile = new File(folder, fileName + "." + format);

            try {
                ImageIO.write(images.get(i), format.toUpperCase(), outputFile);
                LOGGER.info("Saved: " + outputFile.getName());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to save batch item " + i, e);
                throw new QRCodeException("Lỗi lưu file: " + outputFile.getName(), e);
            }
        }
    }

    /**
     * Sanitizes a filename by removing invalid characters.
     */
    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._\\-]", "_")
                   .substring(0, Math.min(50, name.length()));
    }
}

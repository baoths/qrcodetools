package com.baoths.service;

import com.baoths.exception.QRCodeException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for decoding QR codes from image files and BufferedImage objects.
 */
public class QRCodeDecoderService {

    private static final Logger LOGGER = Logger.getLogger(QRCodeDecoderService.class.getName());

    /**
     * Decodes a QR code from an image file.
     *
     * @param imageFile the image file containing a QR code
     * @return the decoded content string
     * @throws QRCodeException if the file cannot be read or no QR code is found
     */
    public String decodeFromFile(File imageFile) throws QRCodeException {
        if (imageFile == null || !imageFile.exists()) {
            throw new QRCodeException("File ảnh không tồn tại.");
        }

        LOGGER.info("Decoding QR from file: " + imageFile.getName());

        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new QRCodeException("Không thể đọc file ảnh: " + imageFile.getName());
            }
            return decodeFromImage(image);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read image file", e);
            throw new QRCodeException("Lỗi đọc file ảnh: " + e.getMessage(), e);
        }
    }

    /**
     * Decodes a QR code from a BufferedImage.
     *
     * @param image the BufferedImage containing a QR code
     * @return the decoded content string
     * @throws QRCodeException if no QR code is found in the image
     */
    public String decodeFromImage(BufferedImage image) throws QRCodeException {
        if (image == null) {
            throw new QRCodeException("Ảnh không hợp lệ.");
        }

        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            String content = result.getText();
            LOGGER.info("QR code decoded successfully. Content length: " + content.length());
            return content;
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "No QR code found in image");
            throw new QRCodeException("Không tìm thấy mã QR trong ảnh.", e);
        }
    }
}

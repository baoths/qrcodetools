package com.baoths.service;

import com.baoths.exception.QRCodeException;
import com.baoths.model.ModuleShape;
import com.baoths.model.QRCodeConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for generating QR codes with customization options.
 * Supports custom colors, gradients, module shapes, and logo overlay.
 */
public class QRCodeGeneratorService {

    private static final Logger LOGGER = Logger.getLogger(QRCodeGeneratorService.class.getName());

    /**
     * Generates a QR code image from content and configuration.
     *
     * @param content the content to encode
     * @param config  the QR code visual configuration
     * @return BufferedImage containing the QR code
     * @throws QRCodeException if generation fails
     */
    public BufferedImage generateQRCode(String content, QRCodeConfig config) throws QRCodeException {
        if (content == null || content.trim().isEmpty()) {
            throw new QRCodeException("Nội dung không được để trống.");
        }

        LOGGER.info("Generating QR code: " + content.substring(0, Math.min(50, content.length())));

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, config.getErrorCorrectionLevel());
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE,
                    config.getSize(), config.getSize(), hints);

            BufferedImage qrImage = applyCustomization(bitMatrix, config);

            // Add logo if specified
            if (config.getLogoFile() != null && config.getLogoFile().exists()) {
                qrImage = addLogo(qrImage, config.getLogoFile());
            }

            return qrImage;

        } catch (WriterException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate QR code", e);
            throw new QRCodeException("Không thể tạo mã QR: " + e.getMessage(), e);
        }
    }

    /**
     * Applies visual customization (colors, gradient, module shape) to a BitMatrix.
     *
     * @param matrix the ZXing BitMatrix
     * @param config the visual configuration
     * @return customized BufferedImage
     */
    private BufferedImage applyCustomization(BitMatrix matrix, QRCodeConfig config) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2d.setColor(new Color(config.getBackgroundColor(), true));
        g2d.fillRect(0, 0, width, height);

        // Determine foreground paint (solid or gradient)
        if (config.isUseGradient()) {
            g2d.setPaint(new GradientPaint(
                    0, 0, new Color(config.getGradientStartColor(), true),
                    width, height, new Color(config.getGradientEndColor(), true)
            ));
        } else {
            g2d.setColor(new Color(config.getForegroundColor(), true));
        }

        // Calculate module size
        // Find actual data bounds in the BitMatrix
        int moduleSize = 1;
        for (int x = 0; x < width; x++) {
            if (matrix.get(x, height / 2) != matrix.get(0, height / 2)) {
                moduleSize = x;
                break;
            }
        }
        if (moduleSize < 1) {
            moduleSize = 1;
        }

        // Draw modules according to shape
        for (int y = 0; y < height; y += moduleSize) {
            for (int x = 0; x < width; x += moduleSize) {
                if (matrix.get(x, y)) {
                    drawModule(g2d, x, y, moduleSize, config.getModuleShape());
                }
            }
        }

        g2d.dispose();
        return image;
    }

    /**
     * Draws a single QR module at the specified position with the given shape.
     */
    private void drawModule(Graphics2D g2d, int x, int y, int size, ModuleShape shape) {
        switch (shape) {
            case CIRCLE:
                g2d.fill(new Ellipse2D.Double(x, y, size, size));
                break;
            case DIAMOND:
                Path2D diamond = new Path2D.Double();
                double cx = x + size / 2.0;
                double cy = y + size / 2.0;
                double half = size / 2.0;
                diamond.moveTo(cx, y);
                diamond.lineTo(x + size, cy);
                diamond.lineTo(cx, y + size);
                diamond.lineTo(x, cy);
                diamond.closePath();
                g2d.fill(diamond);
                break;
            case SQUARE:
            default:
                g2d.fillRect(x, y, size, size);
                break;
        }
    }

    /**
     * Overlays a logo image in the center of the QR code.
     *
     * @param qrImage  the QR code image
     * @param logoFile the logo image file
     * @return BufferedImage with logo overlay
     * @throws QRCodeException if logo cannot be loaded
     */
    private BufferedImage addLogo(BufferedImage qrImage, File logoFile) throws QRCodeException {
        try {
            BufferedImage logo = ImageIO.read(logoFile);
            if (logo == null) {
                throw new QRCodeException("Không thể đọc file logo: " + logoFile.getName());
            }

            int qrWidth = qrImage.getWidth();
            int qrHeight = qrImage.getHeight();

            // Logo occupies ~20% of QR code
            int logoWidth = qrWidth / 5;
            int logoHeight = qrHeight / 5;

            // Center position
            int x = (qrWidth - logoWidth) / 2;
            int y = (qrHeight - logoHeight) / 2;

            Graphics2D g2d = qrImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Draw white background behind logo for readability
            int padding = 4;
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x - padding, y - padding,
                    logoWidth + 2 * padding, logoHeight + 2 * padding, 8, 8);

            // Draw logo
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.drawImage(logo, x, y, logoWidth, logoHeight, null);
            g2d.dispose();

            LOGGER.info("Logo added to QR code successfully.");
            return qrImage;

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to add logo", e);
            throw new QRCodeException("Không thể thêm logo: " + e.getMessage(), e);
        }
    }
}

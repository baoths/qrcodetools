package com.baoths.service;

import com.baoths.exception.QRCodeException;
import com.baoths.model.ModuleShape;
import com.baoths.model.QRCodeConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

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
    private static final double MIN_CONTRAST_RATIO = 3.5;
    private static final double MIN_LOGO_SCALE = 0.08;

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

        if (config == null) {
            throw new QRCodeException("Cấu hình QR không hợp lệ.");
        }

        validateSafety(config);

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
                qrImage = addLogo(qrImage, config);
            }

            return qrImage;

        } catch (WriterException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate QR code", e);
            throw new QRCodeException("Không thể tạo mã QR: " + e.getMessage(), e);
        }
    }

    /**
     * Validates visual safety constraints so generated QR remains scannable.
     */
    private void validateSafety(QRCodeConfig config) throws QRCodeException {
        Color background = new Color(config.getBackgroundColor(), true);

        if (config.isUseGradient()) {
            Color start = new Color(config.getGradientStartColor(), true);
            Color end = new Color(config.getGradientEndColor(), true);

            double startContrast = contrastRatio(start, background);
            double endContrast = contrastRatio(end, background);
            if (startContrast < MIN_CONTRAST_RATIO || endContrast < MIN_CONTRAST_RATIO) {
                throw new QRCodeException(String.format(
                        "Độ tương phản quá thấp (min %.1f). Vui lòng chọn màu QR đậm hơn hoặc nền sáng hơn.",
                        MIN_CONTRAST_RATIO));
            }
        } else {
            Color foreground = new Color(config.getForegroundColor(), true);
            double contrast = contrastRatio(foreground, background);
            if (contrast < MIN_CONTRAST_RATIO) {
                throw new QRCodeException(String.format(
                        "Độ tương phản quá thấp (%.2f < %.1f). Vui lòng tăng tương phản giữa mã QR và nền.",
                        contrast, MIN_CONTRAST_RATIO));
            }
        }

        if (config.getLogoFile() != null && config.getLogoFile().exists()) {
            if (config.getErrorCorrectionLevel() == ErrorCorrectionLevel.L
                    || config.getErrorCorrectionLevel() == ErrorCorrectionLevel.M) {
                throw new QRCodeException("Khi dùng logo, hãy chọn mức sửa lỗi Q hoặc H để mã dễ quét hơn.");
            }

            if (config.getLogoScale() > QRCodeConfig.MAX_SAFE_LOGO_SCALE) {
                throw new QRCodeException(String.format(
                        "Logo quá to. Kích thước logo tối đa là %.0f%% chiều rộng mã QR.",
                        QRCodeConfig.MAX_SAFE_LOGO_SCALE * 100));
            }
        }
    }

    private double contrastRatio(Color color1, Color color2) {
        double l1 = relativeLuminance(color1);
        double l2 = relativeLuminance(color2);
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    private double relativeLuminance(Color color) {
        double r = linearize(color.getRed() / 255.0);
        double g = linearize(color.getGreen() / 255.0);
        double b = linearize(color.getBlue() / 255.0);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    private double linearize(double channel) {
        return channel <= 0.04045 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
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
        // Keep module edges crisp to maximize scanner compatibility.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

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

        // Draw every set pixel from the matrix.
        // Do not infer module size from transitions because it can corrupt placement.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    drawModule(g2d, x, y, 1, config.getModuleShape());
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
    private BufferedImage addLogo(BufferedImage qrImage, QRCodeConfig config) throws QRCodeException {
        try {
            File logoFile = config.getLogoFile();
            BufferedImage logo = ImageIO.read(logoFile);
            if (logo == null) {
                throw new QRCodeException("Không thể đọc file logo: " + logoFile.getName());
            }

            int qrWidth = qrImage.getWidth();
            int qrHeight = qrImage.getHeight();

            double safeLogoScale = Math.max(MIN_LOGO_SCALE,
                    Math.min(config.getLogoScale(), QRCodeConfig.MAX_SAFE_LOGO_SCALE));
            int logoWidth = Math.max(1, (int) Math.round(qrWidth * safeLogoScale));
            int logoHeight = Math.max(1, (int) Math.round(qrHeight * safeLogoScale));

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

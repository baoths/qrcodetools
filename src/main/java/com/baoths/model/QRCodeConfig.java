package com.baoths.model;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;

/**
 * Configuration POJO for QR code generation customization.
 * Holds all visual and encoding parameters.
 */
public class QRCodeConfig {

    /** Default QR code sizes in pixels. */
    public static final int SIZE_SMALL = 200;
    public static final int SIZE_MEDIUM = 400;
    public static final int SIZE_LARGE = 600;

    private int foregroundColor;
    private int backgroundColor;
    private boolean useGradient;
    private int gradientStartColor;
    private int gradientEndColor;
    private ModuleShape moduleShape;
    private ErrorCorrectionLevel errorCorrectionLevel;
    private File logoFile;
    private int size;

    /**
     * Creates a default QRCodeConfig with standard settings.
     */
    public QRCodeConfig() {
        this.foregroundColor = 0xFF000000;   // Black
        this.backgroundColor = 0xFFFFFFFF;   // White
        this.useGradient = false;
        this.gradientStartColor = 0xFF000000;
        this.gradientEndColor = 0xFF0000FF;
        this.moduleShape = ModuleShape.SQUARE;
        this.errorCorrectionLevel = ErrorCorrectionLevel.M;
        this.logoFile = null;
        this.size = SIZE_MEDIUM;
    }

    // --- Getters & Setters ---

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isUseGradient() {
        return useGradient;
    }

    public void setUseGradient(boolean useGradient) {
        this.useGradient = useGradient;
    }

    public int getGradientStartColor() {
        return gradientStartColor;
    }

    public void setGradientStartColor(int gradientStartColor) {
        this.gradientStartColor = gradientStartColor;
    }

    public int getGradientEndColor() {
        return gradientEndColor;
    }

    public void setGradientEndColor(int gradientEndColor) {
        this.gradientEndColor = gradientEndColor;
    }

    public ModuleShape getModuleShape() {
        return moduleShape;
    }

    public void setModuleShape(ModuleShape moduleShape) {
        this.moduleShape = moduleShape;
    }

    public ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    public void setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    public File getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(File logoFile) {
        this.logoFile = logoFile;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

package com.baoths.service;

import com.baoths.exception.QRCodeException;
import com.baoths.model.ModuleShape;
import com.baoths.model.QRCodeConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QRCodeGeneratorServiceTest {

    private final QRCodeGeneratorService generatorService = new QRCodeGeneratorService();
    private final QRCodeDecoderService decoderService = new QRCodeDecoderService();

    @Test
    void generateDefaultQr_canBeDecodedBack() throws QRCodeException {
        String content = "https://example.com/test?x=1&y=2";
        QRCodeConfig config = new QRCodeConfig();

        BufferedImage image = generatorService.generateQRCode(content, config);

        assertNotNull(image);
        assertEquals(content, decoderService.decodeFromImage(image));
    }

    @Test
    void generateStyledQr_canStillBeDecodedBack() throws QRCodeException {
        String content = "Xin chào QRCodeTools";
        QRCodeConfig config = new QRCodeConfig();
        config.setUseGradient(true);
        config.setModuleShape(ModuleShape.CIRCLE);
        config.setSize(QRCodeConfig.SIZE_LARGE);

        BufferedImage image = generatorService.generateQRCode(content, config);

        assertNotNull(image);
        assertEquals(content, decoderService.decodeFromImage(image));
    }

    @Test
    void generateQr_withLowContrast_throwsException() {
        QRCodeConfig config = new QRCodeConfig();
        config.setForegroundColor(0xFF777777);
        config.setBackgroundColor(0xFF888888);

        assertThrows(QRCodeException.class,
                () -> generatorService.generateQRCode("contrast-check", config));
    }

    @Test
    void generateQr_withLogoAndLowErrorCorrection_throwsException() throws IOException {
        QRCodeConfig config = new QRCodeConfig();
        config.setErrorCorrectionLevel(ErrorCorrectionLevel.M);

        File tempLogo = File.createTempFile("qr-logo", ".png");
        tempLogo.deleteOnExit();

        BufferedImage logoImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(logoImage, "png", tempLogo);
        config.setLogoFile(tempLogo);

        assertThrows(QRCodeException.class,
                () -> generatorService.generateQRCode("logo-safety-check", config));
    }
}

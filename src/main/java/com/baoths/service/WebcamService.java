package com.baoths.service;

import com.baoths.exception.QRCodeException;
import com.github.sarxos.webcam.Webcam;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for webcam access and live QR code scanning.
 * Uses the sarxos webcam-capture library.
 */
public class WebcamService {

    private static final Logger LOGGER = Logger.getLogger(WebcamService.class.getName());
    private static final Dimension CAPTURE_SIZE = new Dimension(640, 480);

    private Webcam webcam;
    private Thread captureThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final QRCodeDecoderService decoderService;

    /**
     * Creates a new WebcamService.
     */
    public WebcamService() {
        this.decoderService = new QRCodeDecoderService();
    }

    /**
     * Checks if a webcam is available on the system.
     *
     * @return true if at least one webcam is detected
     */
    public boolean isWebcamAvailable() {
        try {
            Webcam cam = Webcam.getDefault();
            return cam != null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Webcam check failed", e);
            return false;
        }
    }

    /**
     * Starts capturing frames from the default webcam.
     * Each frame is passed to the frameCallback.
     * When a QR code is detected, the decodedCallback receives the content.
     *
     * @param frameCallback   callback for each captured frame
     * @param decodedCallback callback when a QR code is decoded (may be null)
     * @throws QRCodeException if webcam cannot be opened
     */
    public void startCapture(Consumer<BufferedImage> frameCallback,
                             Consumer<String> decodedCallback) throws QRCodeException {
        if (running.get()) {
            LOGGER.warning("Webcam capture already running.");
            return;
        }

        try {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                throw new QRCodeException("Không tìm thấy webcam.");
            }

            webcam.setViewSize(CAPTURE_SIZE);
            webcam.open();
            running.set(true);

            LOGGER.info("Webcam opened: " + webcam.getName());

            captureThread = new Thread(() -> {
                int frameCount = 0;
                while (running.get() && webcam.isOpen()) {
                    try {
                        BufferedImage frame = webcam.getImage();
                        if (frame != null) {
                            frameCallback.accept(frame);

                            // Try to decode every 5th frame for performance
                            if (decodedCallback != null && frameCount % 5 == 0) {
                                try {
                                    String result = decoderService.decodeFromImage(frame);
                                    if (result != null && !result.isEmpty()) {
                                        decodedCallback.accept(result);
                                    }
                                } catch (QRCodeException ignored) {
                                    // No QR code in frame — normal behavior
                                }
                            }
                            frameCount++;
                        }
                        Thread.sleep(33); // ~30 FPS
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Frame capture error", e);
                    }
                }
                LOGGER.info("Webcam capture thread stopped.");
            }, "WebcamCaptureThread");

            captureThread.setDaemon(true);
            captureThread.start();

        } catch (Exception e) {
            running.set(false);
            if (!(e instanceof QRCodeException)) {
                throw new QRCodeException("Không thể mở webcam: " + e.getMessage(), e);
            }
            throw (QRCodeException) e;
        }
    }

    /**
     * Stops the webcam capture and releases resources.
     */
    public void stopCapture() {
        running.set(false);

        if (captureThread != null) {
            captureThread.interrupt();
            captureThread = null;
        }

        if (webcam != null && webcam.isOpen()) {
            webcam.close();
            LOGGER.info("Webcam closed.");
        }
    }

    /**
     * Returns whether the webcam is currently capturing.
     *
     * @return true if capture is active
     */
    public boolean isRunning() {
        return running.get();
    }
}

package com.baoths.exception;

/**
 * Custom exception for QR code generation and decoding errors.
 */
public class QRCodeException extends Exception {

    /**
     * Creates a new QRCodeException with a message.
     *
     * @param message the error message
     */
    public QRCodeException(String message) {
        super(message);
    }

    /**
     * Creates a new QRCodeException with a message and cause.
     *
     * @param message the error message
     * @param cause   the underlying cause
     */
    public QRCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}

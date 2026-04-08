package com.baoths.service;

import com.baoths.model.QRCodeType;

import java.util.logging.Logger;

/**
 * Service for formatting content strings into QR-code-compatible formats.
 * Supports WiFi, VCard, Email (mailto), Phone (tel), Maps (geo), and URL.
 */
public class ContentFormatterService {

    private static final Logger LOGGER = Logger.getLogger(ContentFormatterService.class.getName());

    /**
     * Formats WiFi credentials into a QR-compatible string.
     *
     * @param ssid       the WiFi network name
     * @param password   the WiFi password
     * @param encryption the encryption type (WPA, WEP, or nopass)
     * @return formatted WiFi QR string
     */
    public String formatWifi(String ssid, String password, String encryption) {
        LOGGER.info("Formatting WiFi QR for SSID: " + ssid);
        return String.format("WIFI:T:%s;S:%s;P:%s;;", encryption, ssid, password);
    }

    /**
     * Formats contact information into a VCard string.
     *
     * @param name  full name
     * @param phone phone number
     * @param email email address
     * @param org   organization name
     * @return formatted VCard string
     */
    public String formatVCard(String name, String phone, String email, String org) {
        LOGGER.info("Formatting VCard for: " + name);
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCARD\n");
        sb.append("VERSION:3.0\n");
        sb.append("FN:").append(name).append("\n");
        if (phone != null && !phone.isEmpty()) {
            sb.append("TEL:").append(phone).append("\n");
        }
        if (email != null && !email.isEmpty()) {
            sb.append("EMAIL:").append(email).append("\n");
        }
        if (org != null && !org.isEmpty()) {
            sb.append("ORG:").append(org).append("\n");
        }
        sb.append("END:VCARD");
        return sb.toString();
    }

    /**
     * Formats an email address into a mailto URI.
     *
     * @param email   the recipient email address
     * @param subject the email subject (optional)
     * @param body    the email body (optional)
     * @return formatted mailto URI
     */
    public String formatEmail(String email, String subject, String body) {
        LOGGER.info("Formatting email QR for: " + email);
        StringBuilder sb = new StringBuilder("mailto:");
        sb.append(email);
        boolean hasParams = false;
        if (subject != null && !subject.isEmpty()) {
            sb.append("?subject=").append(encodeURIComponent(subject));
            hasParams = true;
        }
        if (body != null && !body.isEmpty()) {
            sb.append(hasParams ? "&" : "?");
            sb.append("body=").append(encodeURIComponent(body));
        }
        return sb.toString();
    }

    /**
     * Formats a phone number into a tel URI.
     *
     * @param phone the phone number
     * @return formatted tel URI
     */
    public String formatPhone(String phone) {
        LOGGER.info("Formatting phone QR for: " + phone);
        return "tel:" + phone.replaceAll("[\\s\\-()]", "");
    }

    /**
     * Formats GPS coordinates into a geo URI for Google Maps.
     *
     * @param latitude  the latitude coordinate
     * @param longitude the longitude coordinate
     * @return formatted geo URI
     */
    public String formatMaps(String latitude, String longitude) {
        LOGGER.info("Formatting Maps QR for: " + latitude + ", " + longitude);
        return String.format("geo:%s,%s", latitude, longitude);
    }

    /**
     * Validates and formats a URL, adding http:// prefix if missing.
     *
     * @param url the URL to format
     * @return formatted URL with protocol
     */
    public String formatUrl(String url) {
        LOGGER.info("Formatting URL QR");
        if (url == null || url.trim().isEmpty()) {
            return url;
        }
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    /**
     * Auto-detects the QR code type from the content string.
     *
     * @param content the content to analyze
     * @return the detected QRCodeType
     */
    public QRCodeType detectType(String content) {
        if (content == null || content.trim().isEmpty()) {
            return QRCodeType.TEXT;
        }
        String lower = content.trim().toLowerCase();
        if (lower.startsWith("wifi:")) {
            return QRCodeType.WIFI;
        }
        if (lower.startsWith("begin:vcard")) {
            return QRCodeType.VCARD;
        }
        if (lower.startsWith("mailto:")) {
            return QRCodeType.EMAIL;
        }
        if (lower.startsWith("tel:")) {
            return QRCodeType.PHONE;
        }
        if (lower.startsWith("geo:")) {
            return QRCodeType.MAPS;
        }
        if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("www.")) {
            return QRCodeType.URL;
        }
        return QRCodeType.TEXT;
    }

    /**
     * Simple URL encoding for query parameters.
     */
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20");
        } catch (java.io.UnsupportedEncodingException e) {
            return value;
        }
    }
}

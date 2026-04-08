package com.baoths.model;

/**
 * Enum representing the types of QR code content.
 */
public enum QRCodeType {
    TEXT("Văn bản"),
    URL("Đường dẫn URL"),
    EMAIL("Email"),
    PHONE("Số điện thoại"),
    WIFI("Wi-Fi"),
    VCARD("Danh thiếp (VCard)"),
    MAPS("Google Maps");

    private final String displayName;

    QRCodeType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the Vietnamese display name for UI rendering.
     *
     * @return display name in Vietnamese
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

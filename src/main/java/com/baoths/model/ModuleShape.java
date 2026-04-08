package com.baoths.model;

/**
 * Enum representing the shape of QR code modules (the small squares/dots).
 */
public enum ModuleShape {
    SQUARE("Vuông"),
    CIRCLE("Tròn"),
    DIAMOND("Kim cương");

    private final String displayName;

    ModuleShape(String displayName) {
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

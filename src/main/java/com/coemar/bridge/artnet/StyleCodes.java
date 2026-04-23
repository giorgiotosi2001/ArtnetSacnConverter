package com.coemar.bridge.artnet;

public enum StyleCodes {

    StNode(0x00),
    StController(0x01),
    StMedia(0x02),
    StRoute(0x03),
    StBackup(0x04),
    StConfig(0x05),
    StVisual(0x06);

    private final int value;

    StyleCodes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static StyleCodes fromValue(int value) {
        for (StyleCodes style : values()) {
            if (style.value == value) {
                return style;
            }
        }
        return null;
    }
}
package com.coemar.bridge.model.artnet;

public final class ArtPollStatus1 {

    public enum IndicatorState {
        UNKNOWN, LOCATE_IDENTIFY, MUTE, NORMAL
    }

    public enum PortAddressAuthority {
        UNKNOWN, FRONT_PANEL, NETWORK, UNUSED
    }

    private final IndicatorState indicatorState;
    private final PortAddressAuthority portAddressAuthority;
    private final boolean bootedFromRom;
    private final boolean rdmCapable;
    private final boolean ubeaPresent;

    public ArtPollStatus1(byte status) {
        int value = status & 0xFF;

        // bit 7-6
        int indicatorBits = (value >> 6) & 0b11;
        this.indicatorState = switch (indicatorBits) {
            case 0b00 -> IndicatorState.UNKNOWN;
            case 0b01 -> IndicatorState.LOCATE_IDENTIFY;
            case 0b10 -> IndicatorState.MUTE;
            case 0b11 -> IndicatorState.NORMAL;
            default -> IndicatorState.UNKNOWN;
        };

        // bit 5-4
        int authorityBits = (value >> 4) & 0b11;
        this.portAddressAuthority = switch (authorityBits) {
            case 0b00 -> PortAddressAuthority.UNKNOWN;
            case 0b01 -> PortAddressAuthority.FRONT_PANEL;
            case 0b10 -> PortAddressAuthority.NETWORK;
            case 0b11 -> PortAddressAuthority.UNUSED;
            default -> PortAddressAuthority.UNKNOWN;
        };

        // bit 2
        this.bootedFromRom = ((value >> 2) & 1) == 1;

        // bit 1
        this.rdmCapable = ((value >> 1) & 1) == 1;

        // bit 0
        this.ubeaPresent = (value & 1) == 1;
    }
}
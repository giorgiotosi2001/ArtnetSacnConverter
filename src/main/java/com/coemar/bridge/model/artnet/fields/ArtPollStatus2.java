package com.coemar.bridge.model.artnet.fields;

public final class ArtPollStatus2 {

    private final boolean rdmControlSupported;        // bit 7
    private final boolean outputSwitchSupported;      // bit 6
    private final boolean squawking;                  // bit 5
    private final boolean artNetSacnSwitch;           // bit 4
    private final boolean portAddress15Bit;           // bit 3
    private final boolean dhcpCapable;                // bit 2
    private final boolean dhcpConfigured;             // bit 1
    private final boolean webBrowserConfigSupported;  // bit 0

    public ArtPollStatus2(byte value) {
        int v = value & 0xFF;

        this.rdmControlSupported       = ((v >> 7) & 1) == 1;
        this.outputSwitchSupported     = ((v >> 6) & 1) == 1;
        this.squawking                 = ((v >> 5) & 1) == 1;
        this.artNetSacnSwitch          = ((v >> 4) & 1) == 1;
        this.portAddress15Bit          = ((v >> 3) & 1) == 1;
        this.dhcpCapable               = ((v >> 2) & 1) == 1;
        this.dhcpConfigured            = ((v >> 1) & 1) == 1;
        this.webBrowserConfigSupported = (v & 1) == 1;
    }

    public boolean isRdmControlSupported() {
        return rdmControlSupported;
    }

    public boolean isOutputSwitchSupported() {
        return outputSwitchSupported;
    }

    public boolean isSquawking() {
        return squawking;
    }

    public boolean isArtNetSacnSwitch() {
        return artNetSacnSwitch;
    }

    public boolean isPortAddress15Bit() {
        return portAddress15Bit;
    }

    public boolean isDhcpCapable() {
        return dhcpCapable;
    }

    public boolean isDhcpConfigured() {
        return dhcpConfigured;
    }

    public boolean isWebBrowserConfigSupported() {
        return webBrowserConfigSupported;
    }
}
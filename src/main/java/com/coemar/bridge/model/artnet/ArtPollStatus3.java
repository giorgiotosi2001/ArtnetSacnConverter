package com.coemar.bridge.model.artnet;

public final class ArtPollStatus3 {

    public enum FailsafeState {
        HOLD_LAST_STATE,
        ALL_OUTPUTS_ZERO,
        ALL_OUTPUTS_FULL,
        PLAYBACK_SCENE
    }

    private final FailsafeState failsafeState;          // bit 7-6
    private final boolean supportsFailsafe;             // bit 5
    private final boolean supportsLlrp;                 // bit 4
    private final boolean supportsPortSwitching;        // bit 3
    private final boolean supportsRdmNet;               // bit 2
    private final boolean supportsBackgroundQueue;      // bit 1
    private final boolean backgroundDiscoverySwitch;    // bit 0

    public ArtPollStatus3(byte value) {
        int v = value & 0xFF;

        // bit 7-6
        int fs = (v >> 6) & 0b11;
        this.failsafeState = switch (fs) {
            case 0b00 -> FailsafeState.HOLD_LAST_STATE;
            case 0b01 -> FailsafeState.ALL_OUTPUTS_ZERO;
            case 0b10 -> FailsafeState.ALL_OUTPUTS_FULL;
            case 0b11 -> FailsafeState.PLAYBACK_SCENE;
            default -> FailsafeState.HOLD_LAST_STATE;
        };

        this.supportsFailsafe          = ((v >> 5) & 1) == 1;
        this.supportsLlrp              = ((v >> 4) & 1) == 1;
        this.supportsPortSwitching     = ((v >> 3) & 1) == 1;
        this.supportsRdmNet            = ((v >> 2) & 1) == 1;
        this.supportsBackgroundQueue   = ((v >> 1) & 1) == 1;
        this.backgroundDiscoverySwitch = (v & 1) == 1;
    }

    public FailsafeState getFailsafeState() {
        return failsafeState;
    }

    public boolean isSupportsFailsafe() {
        return supportsFailsafe;
    }

    public boolean isSupportsLlrp() {
        return supportsLlrp;
    }

    public boolean isSupportsPortSwitching() {
        return supportsPortSwitching;
    }

    public boolean isSupportsRdmNet() {
        return supportsRdmNet;
    }

    public boolean isSupportsBackgroundQueue() {
        return supportsBackgroundQueue;
    }

    public boolean isBackgroundDiscoverySwitch() {
        return backgroundDiscoverySwitch;
    }
}
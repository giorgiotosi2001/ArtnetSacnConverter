package com.coemar.bridge.model.artnet.fields;

public final class ArtPollFlags {
    private final boolean targetedMode;
    private final boolean vlcTrasmission;
    private final boolean diagnosticsEnabled;
    private final boolean diagnosticsUnicast;
    private final boolean sendReplyOnChange;

    public ArtPollFlags(byte flags) {
        this.sendReplyOnChange   = ((flags >> 1) & 1) == 1;
        this.diagnosticsEnabled  = ((flags >> 2) & 1) == 1;
        this.diagnosticsUnicast  = ((flags >> 3) & 1) == 1;
        this.vlcTrasmission      = ((flags >> 4) & 1) == 1;
        this.targetedMode        = ((flags >> 5) & 1) == 1;
    }
}

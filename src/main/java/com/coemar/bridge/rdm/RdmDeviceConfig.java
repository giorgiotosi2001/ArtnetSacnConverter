package com.coemar.bridge.rdm;

public class RdmDeviceConfig {

    private final byte[] sourceUid;
    private final RdmTimingConfig timingConfig;

    public RdmDeviceConfig(byte[] sourceUid) {
        this(sourceUid, RdmTimingConfig.defaults());
    }

    public RdmDeviceConfig(byte[] sourceUid, RdmTimingConfig timingConfig) {
        if (sourceUid == null || sourceUid.length != 6) {
            throw new IllegalArgumentException("sourceUid must contain exactly 6 bytes");
        }
        if (timingConfig == null) {
            throw new IllegalArgumentException("timingConfig must not be null");
        }

        this.sourceUid = sourceUid.clone();
        this.timingConfig = timingConfig;
    }

    public byte[] getSourceUid() {
        return sourceUid.clone();
    }

    public RdmTimingConfig getTimingConfig() {
        return timingConfig;
    }
}

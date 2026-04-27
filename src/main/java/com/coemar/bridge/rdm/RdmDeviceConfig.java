package com.coemar.bridge.rdm;

public class RdmDeviceConfig {

    private final String portName;
    private final byte[] sourceUid;
    private final int discoveryTimeoutMs;
    private final int ackTimeoutMs;
    private final RdmTimingConfig timingConfig;

    public RdmDeviceConfig(String portName,
                           byte[] sourceUid,
                           int discoveryTimeoutMs,
                           int ackTimeoutMs) {
        this(portName, sourceUid, discoveryTimeoutMs, ackTimeoutMs,
                RdmTimingConfig.defaults(discoveryTimeoutMs, ackTimeoutMs));
    }

    public RdmDeviceConfig(String portName,
                           byte[] sourceUid,
                           int discoveryTimeoutMs,
                           int ackTimeoutMs,
                           RdmTimingConfig timingConfig) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("portName must not be blank");
        }
        if (sourceUid == null || sourceUid.length != 6) {
            throw new IllegalArgumentException("sourceUid must contain exactly 6 bytes");
        }
        if (discoveryTimeoutMs <= 0) {
            throw new IllegalArgumentException("discoveryTimeoutMs must be positive");
        }
        if (ackTimeoutMs <= 0) {
            throw new IllegalArgumentException("ackTimeoutMs must be positive");
        }
        if (timingConfig == null) {
            throw new IllegalArgumentException("timingConfig must not be null");
        }

        this.portName = portName;
        this.sourceUid = sourceUid.clone();
        this.discoveryTimeoutMs = discoveryTimeoutMs;
        this.ackTimeoutMs = ackTimeoutMs;
        this.timingConfig = timingConfig;
    }

    public String getPortName() {
        return portName;
    }

    public byte[] getSourceUid() {
        return sourceUid.clone();
    }

    public int getDiscoveryTimeoutMs() {
        return discoveryTimeoutMs;
    }

    public int getAckTimeoutMs() {
        return ackTimeoutMs;
    }

    public RdmTimingConfig getTimingConfig() {
        return timingConfig;
    }
}

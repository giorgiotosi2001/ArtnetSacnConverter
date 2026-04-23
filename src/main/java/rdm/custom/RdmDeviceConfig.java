package rdm.custom;

public class RdmDeviceConfig {

    private final String portName;
    private final byte[] sourceUid;
    private final int discoveryTimeoutMs;
    private final int ackTimeoutMs;

    public RdmDeviceConfig(String portName,
                           byte[] sourceUid,
                           int discoveryTimeoutMs,
                           int ackTimeoutMs) {
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

        this.portName = portName;
        this.sourceUid = sourceUid.clone();
        this.discoveryTimeoutMs = discoveryTimeoutMs;
        this.ackTimeoutMs = ackTimeoutMs;
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
}
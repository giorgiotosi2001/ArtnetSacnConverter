package com.coemar.bridge.rdm;

public class RdmDiscoveredDevice {

    private final String portName;
    private final RdmUid uid;

    public RdmDiscoveredDevice(String portName, RdmUid uid) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("portName must not be blank");
        }
        if (uid == null) {
            throw new IllegalArgumentException("uid must not be null");
        }
        this.portName = portName;
        this.uid = uid;
    }

    public String getPortName() {
        return portName;
    }

    public RdmUid getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "RdmDiscoveredDevice{" +
                "portName='" + portName + '\'' +
                ", uid=" + uid +
                '}';
    }
}

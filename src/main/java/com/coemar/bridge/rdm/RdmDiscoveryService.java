package com.coemar.bridge.rdm;

import com.coemar.bridge.serial.JSerialCommAdapter;
import com.coemar.bridge.serial.SerialPortAdapter;
import com.coemar.bridge.serial.SerialPortScanner;

import java.util.ArrayList;
import java.util.List;

public class RdmDiscoveryService {

    private final byte[] sourceUid;
    private final RdmTimingConfig timingConfig;

    public RdmDiscoveryService(byte[] sourceUid) {
        this(sourceUid, RdmTimingConfig.defaults());
    }

    public RdmDiscoveryService(byte[] sourceUid, RdmTimingConfig timingConfig) {
        if (sourceUid == null || sourceUid.length != 6) {
            throw new IllegalArgumentException("sourceUid must contain exactly 6 bytes");
        }
        if (timingConfig == null) {
            throw new IllegalArgumentException("timingConfig must not be null");
        }
        this.sourceUid = sourceUid.clone();
        this.timingConfig = timingConfig;
    }

    public List<RdmDiscoveredDevice> discoverAllPorts() {
        return discoverPorts(SerialPortScanner.listPortNames());
    }

    public List<RdmDiscoveredDevice> discoverPorts(List<String> portNames) {
        if (portNames == null) {
            throw new IllegalArgumentException("portNames must not be null");
        }

        List<RdmDiscoveredDevice> devices = new ArrayList<>();

        for (String portName : portNames) {
            if (portName == null || portName.isBlank()) {
                continue;
            }
            devices.addAll(discoverPort(portName));
        }

        return devices;
    }

    public List<RdmDiscoveredDevice> discoverPort(String portName) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("portName must not be blank");
        }

        SerialPortAdapter serialPort = new JSerialCommAdapter(portName, timingConfig);
        RdmDeviceConfig config = new RdmDeviceConfig(sourceUid, timingConfig);
        RdmSerialDmxOutput rdmOutput = new RdmSerialDmxOutput(serialPort, config);

        try {
            if (!rdmOutput.connect()) {
                return List.of();
            }
            return rdmOutput.discoverDevicesWithPort();
        } finally {
            rdmOutput.disconnect();
        }
    }
}

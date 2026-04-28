package com.coemar.bridge.rdm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RdmDeviceRegistry {

    private final Map<String, Map<RdmUid, RdmDiscoveredDevice>> devicesByPort = new LinkedHashMap<>();

    public synchronized void replacePortDevices(String portName, List<RdmUid> uids) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("portName must not be blank");
        }
        if (uids == null) {
            throw new IllegalArgumentException("uids must not be null");
        }

        Map<RdmUid, RdmDiscoveredDevice> devices = new LinkedHashMap<>();
        for (RdmUid uid : uids) {
            if (uid == null) {
                continue;
            }
            devices.put(uid, new RdmDiscoveredDevice(portName, uid));
        }

        devicesByPort.put(portName, devices);
    }

    public synchronized void replaceAll(List<RdmDiscoveredDevice> devices) {
        if (devices == null) {
            throw new IllegalArgumentException("devices must not be null");
        }

        devicesByPort.clear();
        for (RdmDiscoveredDevice device : devices) {
            if (device == null) {
                continue;
            }
            devicesByPort
                    .computeIfAbsent(device.getPortName(), ignored -> new LinkedHashMap<>())
                    .put(device.getUid(), device);
        }
    }

    public synchronized List<RdmDiscoveredDevice> getAll() {
        List<RdmDiscoveredDevice> devices = new ArrayList<>();
        for (Map<RdmUid, RdmDiscoveredDevice> portDevices : devicesByPort.values()) {
            devices.addAll(portDevices.values());
        }
        return Collections.unmodifiableList(devices);
    }

    public synchronized List<RdmDiscoveredDevice> getByPort(String portName) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("portName must not be blank");
        }

        Map<RdmUid, RdmDiscoveredDevice> portDevices = devicesByPort.get(portName);
        if (portDevices == null) {
            return List.of();
        }
        return Collections.unmodifiableList(new ArrayList<>(portDevices.values()));
    }

    public synchronized boolean contains(String portName, RdmUid uid) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("portName must not be blank");
        }
        if (uid == null) {
            throw new IllegalArgumentException("uid must not be null");
        }

        Map<RdmUid, RdmDiscoveredDevice> portDevices = devicesByPort.get(portName);
        return portDevices != null && portDevices.containsKey(uid);
    }

    public synchronized void clear() {
        devicesByPort.clear();
    }
}

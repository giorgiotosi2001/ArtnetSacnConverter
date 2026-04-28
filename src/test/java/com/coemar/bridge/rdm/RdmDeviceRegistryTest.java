package com.coemar.bridge.rdm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RdmDeviceRegistryTest {

    @Test
    void replacePortDevicesStoresDevicesForOnePort() {
        RdmDeviceRegistry registry = new RdmDeviceRegistry();
        RdmUid uid = RdmUid.fromLong(1);

        registry.replacePortDevices("ttyUSB0", List.of(uid));

        assertTrue(registry.contains("ttyUSB0", uid));
        assertEquals(1, registry.getByPort("ttyUSB0").size());
        assertEquals("ttyUSB0", registry.getAll().get(0).getPortName());
        assertEquals(uid, registry.getAll().get(0).getUid());
    }

    @Test
    void replacePortDevicesOnlyReplacesThatPort() {
        RdmDeviceRegistry registry = new RdmDeviceRegistry();
        RdmUid first = RdmUid.fromLong(1);
        RdmUid second = RdmUid.fromLong(2);

        registry.replacePortDevices("ttyUSB0", List.of(first));
        registry.replacePortDevices("ttyUSB1", List.of(second));
        registry.replacePortDevices("ttyUSB0", List.of());

        assertEquals(0, registry.getByPort("ttyUSB0").size());
        assertEquals(1, registry.getByPort("ttyUSB1").size());
        assertTrue(registry.contains("ttyUSB1", second));
    }

    @Test
    void replaceAllReplacesWholeRegistry() {
        RdmDeviceRegistry registry = new RdmDeviceRegistry();
        RdmUid oldUid = RdmUid.fromLong(1);
        RdmUid newUid = RdmUid.fromLong(2);

        registry.replacePortDevices("ttyUSB0", List.of(oldUid));
        registry.replaceAll(List.of(new RdmDiscoveredDevice("ttyUSB1", newUid)));

        assertEquals(1, registry.getAll().size());
        assertEquals("ttyUSB1", registry.getAll().get(0).getPortName());
        assertEquals(newUid, registry.getAll().get(0).getUid());
    }
}

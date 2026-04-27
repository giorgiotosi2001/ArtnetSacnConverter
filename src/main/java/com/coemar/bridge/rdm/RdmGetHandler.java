package com.coemar.bridge.rdm;

import java.nio.charset.StandardCharsets;

import static com.coemar.bridge.rdm.RdmConstants.DEVICE_INFO;
import static com.coemar.bridge.rdm.RdmConstants.DEVICE_MODEL_DESCRIPTION;
import static com.coemar.bridge.rdm.RdmConstants.SOFTWARE_VERSION_ID;
import static com.coemar.bridge.util.BinaryParseUtils.require;
import static com.coemar.bridge.util.BinaryParseUtils.u16be;
import static com.coemar.bridge.util.BinaryParseUtils.u32be;

public class RdmGetHandler {


    GetResult handleGet(RdmFrame frame) {
        return switch (frame.getParameterId()) {
            case DEVICE_INFO -> // DEVICE_INFO
                    handleDeviceInfo(frame);
            case DEVICE_MODEL_DESCRIPTION -> // DEVICE_MODEL_DESCRIPTION
                    handleDeviceModelDescription(frame);
            case SOFTWARE_VERSION_ID -> // SOFTWARE_VERSION_ID
                    handleSoftwareVersionId(frame);
            default -> throw new IllegalArgumentException("PID non supportato");
        };
    }

    private SoftwareVersion handleSoftwareVersionId(RdmFrame frame) {
        byte[] data = frame.getParameterData();
        require(data.length == 4, "SOFTWARE_VERSION_ID richiede 4 byte");
        long softwareVersionId = u32be(data, 0);
        return new SoftwareVersion(Long.toUnsignedString(softwareVersionId));
    }

    private ModelDescription handleDeviceModelDescription(RdmFrame frame) {
        byte[] data = frame.getParameterData();
        return new ModelDescription(new String(data, StandardCharsets.US_ASCII).trim());
    }

    private DeviceInfo handleDeviceInfo(RdmFrame frame) {
        byte[] data = frame.getParameterData();
        require(data.length == 19, "DEVICE_INFO richiede 19 byte");

        return new DeviceInfo(
                u16be(data, 0),
                u16be(data, 2),
                u16be(data, 4),
                u32be(data, 6),
                u16be(data, 10),
                data[12] & 0xFF,
                data[13] & 0xFF,
                u16be(data, 14),
                u16be(data, 16),
                data[18] & 0xFF
        );
    }


}

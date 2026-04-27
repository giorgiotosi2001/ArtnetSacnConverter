package com.coemar.bridge.rdm;

import static com.coemar.bridge.rdm.RdmConstants.*;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU16BE;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU32BE;

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
    }

    private ModelDescription handleDeviceModelDescription(RdmFrame frame) {
    }

    private DeviceInfo handleDeviceInfo(RdmFrame frame) {

    }


}

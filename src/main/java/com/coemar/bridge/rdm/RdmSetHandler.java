package com.coemar.bridge.rdm;

import static com.coemar.bridge.rdm.RdmConstants.FAN_SPEED_CONTROL_PID;

public class RdmSetHandler {

    void handleSet(RdmFrame frame) {

        switch (frame.getParameterId()) {

            case FAN_SPEED_CONTROL_PID: // FAN_SPEED_CONTROL_PID
                handleFanSpeedControl(frame);
                break;

            default:
                throw new IllegalArgumentException("PID non supportato");
        }
    }


    private void handleFanSpeedControl(RdmFrame frame) {
    }

}

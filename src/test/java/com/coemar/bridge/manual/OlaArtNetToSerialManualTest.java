package com.coemar.bridge.manual;

import com.coemar.bridge.OlaArtNetToSerialBridge;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class OlaArtNetToSerialManualTest {

    @Test
    @Disabled("Manual hardware test: requires OLA, UDP 6454, /dev/ttyS0 and a connected DMX device")
    void runOlaArtNetToSerialBridge() {
        /*
         * Manual test chain:
         * OLA web UI: http://localhost:9090
         * OLA output protocol: Art-Net
         * Art-Net UDP port: 6454
         * Target universe: 1
         * Serial output: /dev/ttyS0
         *
         * Run only from the Ubuntu VM when the serial adapter and fixture are connected.
         * TODO: Later move serial port, universe and refresh period to configuration.
         */
        new OlaArtNetToSerialBridge("/dev/ttyS0", 1, 30).run();
    }
}

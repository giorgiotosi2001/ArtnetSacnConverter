package com.coemar.bridge.model.artnet.fields;

public final class GoodOutputB {

    public static final class OutputStatus {
        private final boolean rdmDisabled;          // bit 7 (1 = disabled)
        private final boolean continuousOutput;     // bit 6 (1 = continuous, 0 = delta)
        private final boolean discoveryNotRunning;  // bit 5 (1 = NOT running)
        private final boolean backgroundDisabled;   // bit 4 (1 = disabled)

        public OutputStatus(byte value) {
            int v = value & 0xFF;

            this.rdmDisabled         = ((v >> 7) & 1) == 1;
            this.continuousOutput    = ((v >> 6) & 1) == 1;
            this.discoveryNotRunning = ((v >> 5) & 1) == 1;
            this.backgroundDisabled  = ((v >> 4) & 1) == 1;
        }

        public boolean isRdmDisabled() {
            return rdmDisabled;
        }

        public boolean isContinuousOutput() {
            return continuousOutput;
        }

        public boolean isDiscoveryNotRunning() {
            return discoveryNotRunning;
        }

        public boolean isBackgroundDisabled() {
            return backgroundDisabled;
        }
    }

    private final OutputStatus[] outputs = new OutputStatus[4];

    public GoodOutputB(byte[] goodOutputB) {
        for (int i = 0; i < 4; i++) {
            outputs[i] = new OutputStatus(goodOutputB[i]);
        }
    }

    public OutputStatus getOutput(int index) {
        return outputs[index];
    }

    public OutputStatus[] getOutputs() {
        return outputs;
    }
}
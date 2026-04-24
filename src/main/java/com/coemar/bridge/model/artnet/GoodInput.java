package com.coemar.bridge.model.artnet;

public final class GoodInput {

    public static final class InputStatus {
        private final boolean dataReceived;
        private final boolean dmxTestPackets;
        private final boolean dmxSipPackets;
        private final boolean dmxTextPackets;
        private final boolean inputDisabled;
        private final boolean receiveErrorsDetected;
        private final boolean convertToSACN;

        public InputStatus(byte value) {
            int v = value & 0xFF;

            this.dataReceived          = ((v >> 7) & 1) == 1;
            this.dmxTestPackets        = ((v >> 6) & 1) == 1;
            this.dmxSipPackets         = ((v >> 5) & 1) == 1;
            this.dmxTextPackets        = ((v >> 4) & 1) == 1;
            this.inputDisabled         = ((v >> 3) & 1) == 1;
            this.receiveErrorsDetected = ((v >> 2) & 1) == 1;
            this.convertToSACN         = (v & 1) == 1;
        }

        public boolean isDataReceived() {
            return dataReceived;
        }

        public boolean hasDmxTestPackets() {
            return dmxTestPackets;
        }

        public boolean hasDmxSipPackets() {
            return dmxSipPackets;
        }

        public boolean hasDmxTextPackets() {
            return dmxTextPackets;
        }

        public boolean isInputDisabled() {
            return inputDisabled;
        }

        public boolean hasReceiveErrorsDetected() {
            return receiveErrorsDetected;
        }

        public boolean isConvertToSACN() {
            return convertToSACN;
        }
    }

    private final InputStatus[] inputs = new InputStatus[4];

    public GoodInput(byte[] goodInput) {
        for (int i = 0; i < 4; i++) {
            inputs[i] = new InputStatus(goodInput[i]);
        }
    }

    public InputStatus getInput(int index) {
        return inputs[index];
    }

    public InputStatus[] getInputs() {
        return inputs;
    }
}
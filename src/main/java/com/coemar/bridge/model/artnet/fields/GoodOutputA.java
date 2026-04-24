package com.coemar.bridge.model.artnet.fields;

public final class GoodOutputA {

    public static final class OutputStatus {
        private final boolean dataBeingOutput;      // bit 7
        private final boolean dmxTestPackets;       // bit 6
        private final boolean dmxSipPackets;        // bit 5
        private final boolean dmxTextPackets;       // bit 4
        private final boolean mergingData;          // bit 3
        private final boolean shortDetected;        // bit 2
        private final boolean mergeModeLtp;         // bit 1
        private final boolean convertFromSACN;      // bit 0

        public OutputStatus(byte value) {
            int v = value & 0xFF;

            this.dataBeingOutput = ((v >> 7) & 1) == 1;
            this.dmxTestPackets  = ((v >> 6) & 1) == 1;
            this.dmxSipPackets   = ((v >> 5) & 1) == 1;
            this.dmxTextPackets  = ((v >> 4) & 1) == 1;
            this.mergingData     = ((v >> 3) & 1) == 1;
            this.shortDetected   = ((v >> 2) & 1) == 1;
            this.mergeModeLtp    = ((v >> 1) & 1) == 1;
            this.convertFromSACN = (v & 1) == 1;
        }

        public boolean isDataBeingOutput() {
            return dataBeingOutput;
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

        public boolean isMergingData() {
            return mergingData;
        }

        public boolean isShortDetected() {
            return shortDetected;
        }

        public boolean isMergeModeLtp() {
            return mergeModeLtp;
        }

        public boolean isConvertFromSACN() {
            return convertFromSACN;
        }
    }

    private final OutputStatus[] outputs = new OutputStatus[4];

    public GoodOutputA(byte[] goodOutputA) {
        for (int i = 0; i < 4; i++) {
            outputs[i] = new OutputStatus(goodOutputA[i]);
        }
    }

    public OutputStatus getOutput(int index) {
        return outputs[index];
    }

    public OutputStatus[] getOutputs() {
        return outputs;
    }
}
package com.coemar.bridge.model.artnet;

public final class PortTypes {

    public enum Protocol {
        DMX512,
        MIDI,
        AVAB,
        CMX,
        ADB_62_5,
        ART_NET,
        DALI,
        UNKNOWN
    }

    public static final class PortType {
        private final boolean canOutput;
        private final boolean canInput;
        private final Protocol protocol;

        public PortType(byte value) {
            int v = value & 0xFF;

            // bit 7
            this.canOutput = ((v >> 7) & 1) == 1;

            // bit 6
            this.canInput = ((v >> 6) & 1) == 1;

            // bit 5-0
            int protocolBits = v & 0b0011_1111;
            this.protocol = mapProtocol(protocolBits);
        }

        private Protocol mapProtocol(int value) {
            return switch (value) {
                case 0x00 -> Protocol.DMX512;
                case 0x01 -> Protocol.MIDI;
                case 0x02 -> Protocol.AVAB;
                case 0x03 -> Protocol.CMX;
                case 0x04 -> Protocol.ADB_62_5;
                case 0x05 -> Protocol.ART_NET;
                case 0x06 -> Protocol.DALI;
                default -> Protocol.UNKNOWN;
            };
        }

        public boolean canOutput() {
            return canOutput;
        }

        public boolean canInput() {
            return canInput;
        }

        public Protocol getProtocol() {
            return protocol;
        }
    }

    private final PortType[] ports = new PortType[4];

    public PortTypes(byte[] portTypes) {
        for (int i = 0; i < 4; i++) {
            ports[i] = new PortType(portTypes[i]);
        }
    }

    public PortType getPort(int index) {
        return ports[index];
    }

    public PortType[] getPorts() {
        return ports;
    }
}
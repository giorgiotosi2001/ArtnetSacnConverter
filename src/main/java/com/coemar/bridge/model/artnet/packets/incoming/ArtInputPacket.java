package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

public class ArtInputPacket implements Packet {

    private static final int INPUT_ARRAY_LENGTH = 4;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int bindIndex;
    private final int numPorts;
    private final byte[] input;

    public ArtInputPacket(int opCode, int protocolVersion, int filler1, int bindIndex, int numPorts, byte[] input) {
        if (numPorts < 0 || numPorts > INPUT_ARRAY_LENGTH) {
            throw new IllegalArgumentException("numPorts fuori range: " + numPorts);
        }

        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.bindIndex = bindIndex;
        this.numPorts = numPorts;
        this.input = copyFixedLengthBytes(input, INPUT_ARRAY_LENGTH, "input");
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getFiller1() {
        return filler1;
    }

    public int getBindIndex() {
        return bindIndex;
    }

    public int getNumPorts() {
        return numPorts;
    }

    public byte[] getInput() {
        return Arrays.copyOf(input, input.length);
    }

    public boolean isInputDisabled(int index) {
        return (getInputStatusByte(index) & 0x01) != 0;
    }

    public boolean isInputEnabled(int index) {
        return !isInputDisabled(index);
    }

    public boolean hasReservedBitsSet(int index) {
        return (getInputStatusByte(index) & 0xFE) != 0;
    }

    public int getConfiguredInputCount() {
        return numPorts;
    }

    private byte getInputStatusByte(int index) {
        if (index < 0 || index >= input.length) {
            throw new IndexOutOfBoundsException("Input index fuori range: " + index);
        }
        return input[index];
    }

    private static byte[] copyFixedLengthBytes(byte[] value, int expectedLength, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " non puo essere null");
        }
        if (value.length != expectedLength) {
            throw new IllegalArgumentException(fieldName + " deve essere lungo " + expectedLength + " byte");
        }
        return Arrays.copyOf(value, value.length);
    }

    private static String formatUnsignedByteArray(byte[] value) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < value.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(value[i] & 0xFF);
        }
        return builder.append(']').toString();
    }

    @Override
    public String toString() {
        return "ArtInputPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", bindIndex=" + bindIndex +
                ", numPorts=" + numPorts +
                ", input=" + formatUnsignedByteArray(input) +
                '}';
    }
}

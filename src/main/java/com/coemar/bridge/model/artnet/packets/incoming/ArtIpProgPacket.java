package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

public class ArtIpProgPacket implements Packet {

    private static final int IPV4_LENGTH = 4;
    private static final int SPARE_LENGTH = 4;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int filler2;
    private final int command;
    private final int filler4;
    private final byte[] progIpAddress;
    private final byte[] progSubnetMask;
    private final int progPort;
    private final byte[] progDefaultGateway;
    private final byte[] spare;

    public ArtIpProgPacket(int opCode, int protocolVersion, int filler1, int filler2, int command, int filler4,
                           byte[] progIpAddress, byte[] progSubnetMask, int progPort, byte[] progDefaultGateway,
                           byte[] spare) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.filler2 = filler2;
        this.command = command;
        this.filler4 = filler4;
        this.progIpAddress = copyFixedLengthBytes(progIpAddress, IPV4_LENGTH, "progIpAddress");
        this.progSubnetMask = copyFixedLengthBytes(progSubnetMask, IPV4_LENGTH, "progSubnetMask");
        this.progPort = progPort;
        this.progDefaultGateway = copyFixedLengthBytes(progDefaultGateway, IPV4_LENGTH, "progDefaultGateway");
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
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

    public int getFiller2() {
        return filler2;
    }

    public int getCommand() {
        return command;
    }

    public int getFiller4() {
        return filler4;
    }

    public byte[] getProgIpAddress() {
        return Arrays.copyOf(progIpAddress, progIpAddress.length);
    }

    public byte[] getProgSubnetMask() {
        return Arrays.copyOf(progSubnetMask, progSubnetMask.length);
    }

    public int getProgPort() {
        return progPort;
    }

    public byte[] getProgDefaultGateway() {
        return Arrays.copyOf(progDefaultGateway, progDefaultGateway.length);
    }

    public byte[] getSpare() {
        return Arrays.copyOf(spare, spare.length);
    }

    public boolean isInquiryOnly() {
        return (command & 0xFF) == 0;
    }

    public boolean isEnableProgramming() {
        return isBitSet(7);
    }

    public boolean isEnableDhcp() {
        return isBitSet(6);
    }

    public boolean isProgramDefaultGateway() {
        return isBitSet(4);
    }

    public boolean isSetToDefault() {
        return isBitSet(3);
    }

    public boolean isProgramIpAddress() {
        return isBitSet(2);
    }

    public boolean isProgramSubnetMask() {
        return isBitSet(1);
    }

    public boolean isProgramPort() {
        return isBitSet(0);
    }

    private boolean isBitSet(int bitIndex) {
        return ((command >> bitIndex) & 1) == 1;
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

    private static String formatIpv4(byte[] value) {
        return (value[0] & 0xFF) + "."
                + (value[1] & 0xFF) + "."
                + (value[2] & 0xFF) + "."
                + (value[3] & 0xFF);
    }

    @Override
    public String toString() {
        return "ArtIpProgPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", filler2=" + filler2 +
                ", command=" + command +
                ", inquiryOnly=" + isInquiryOnly() +
                ", enableProgramming=" + isEnableProgramming() +
                ", enableDhcp=" + isEnableDhcp() +
                ", programDefaultGateway=" + isProgramDefaultGateway() +
                ", setToDefault=" + isSetToDefault() +
                ", programIpAddress=" + isProgramIpAddress() +
                ", programSubnetMask=" + isProgramSubnetMask() +
                ", programPort=" + isProgramPort() +
                ", filler4=" + filler4 +
                ", progIpAddress=" + formatIpv4(progIpAddress) +
                ", progSubnetMask=" + formatIpv4(progSubnetMask) +
                ", progPort=" + progPort +
                ", progDefaultGateway=" + formatIpv4(progDefaultGateway) +
                ", spare=" + Arrays.toString(spare) +
                '}';
    }
}

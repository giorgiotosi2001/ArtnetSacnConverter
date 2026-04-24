package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

public class ArtFirmwareMasterPacket implements Packet {

    private static final int SPARE_LENGTH = 20;
    private static final int DATA_LENGTH = 512;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int filler2;
    private final int type;
    private final int blockId;
    private final long firmwareLength;
    private final byte[] spare;
    private final byte[] data;

    public ArtFirmwareMasterPacket(int opCode, int protocolVersion, int filler1, int filler2, int type, int blockId,
                                   long firmwareLength, byte[] spare, byte[] data) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.filler2 = filler2;
        this.type = type;
        this.blockId = blockId;
        this.firmwareLength = firmwareLength;
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
        this.data = copyFixedLengthBytes(data, DATA_LENGTH, "data");
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

    public int getType() {
        return type;
    }

    public Type getPacketType() {
        return Type.fromValue(type);
    }

    public int getBlockId() {
        return blockId;
    }

    public long getFirmwareLength() {
        return firmwareLength;
    }

    public byte[] getSpare() {
        return Arrays.copyOf(spare, spare.length);
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public boolean isFirmwareUpload() {
        Type packetType = getPacketType();
        return packetType == Type.FirmFirst || packetType == Type.FirmCont || packetType == Type.FirmLast;
    }

    public boolean isUbeaUpload() {
        Type packetType = getPacketType();
        return packetType == Type.UbeaFirst || packetType == Type.UbeaCont || packetType == Type.UbeaLast;
    }

    public boolean isFirstPacket() {
        Type packetType = getPacketType();
        return packetType == Type.FirmFirst || packetType == Type.UbeaFirst;
    }

    public boolean isContinuationPacket() {
        Type packetType = getPacketType();
        return packetType == Type.FirmCont || packetType == Type.UbeaCont;
    }

    public boolean isLastPacket() {
        Type packetType = getPacketType();
        return packetType == Type.FirmLast || packetType == Type.UbeaLast;
    }

    public int getDataWord(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= DATA_LENGTH / 2) {
            throw new IndexOutOfBoundsException("Word index fuori range: " + wordIndex);
        }

        int offset = wordIndex * 2;
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
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

    @Override
    public String toString() {
        return "ArtFirmwareMasterPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", filler2=" + filler2 +
                ", type=" + type +
                ", packetType=" + getPacketType() +
                ", blockId=" + blockId +
                ", firmwareLength=" + firmwareLength +
                ", firmwareUpload=" + isFirmwareUpload() +
                ", ubeaUpload=" + isUbeaUpload() +
                ", firstPacket=" + isFirstPacket() +
                ", continuationPacket=" + isContinuationPacket() +
                ", lastPacket=" + isLastPacket() +
                '}';
    }

    public enum Type {
        FirmFirst(0x00),
        FirmCont(0x01),
        FirmLast(0x02),
        UbeaFirst(0x03),
        UbeaCont(0x04),
        UbeaLast(0x05),
        Unknown(-1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type fromValue(int value) {
            for (Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return Unknown;
        }
    }
}

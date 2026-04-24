package com.coemar.bridge.model.artnet.packets.incoming;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ArtVlcPacket extends ArtNzsPacket {

    private static final int VLC_START_CODE = 0x09;
    private static final int VLC_MAGIC_MAN_ID_HI = 0x41;
    private static final int VLC_MAGIC_MAN_ID_LO = 0x4C;
    private static final int VLC_MAGIC_SUB_CODE = 0x45;
    private static final int VLC_HEADER_LENGTH = 22;
    private static final int MAX_VLC_PAYLOAD_LENGTH = 480;

    private final int flags;
    private final int transactionNumber;
    private final int slotAddress;
    private final int payloadCount;
    private final int payloadChecksum;
    private final int spare1;
    private final int vlcDepth;
    private final int vlcFrequency;
    private final int vlcModulationType;
    private final int payloadLanguage;
    private final int beaconRepeatFrequency;
    private final byte[] payload;

    public ArtVlcPacket(int opCode, int protocolVersion, int sequence, int startCode, int subUni, int net,
                        int portAddress, int length, byte[] data, int flags, int transactionNumber, int slotAddress,
                        int payloadCount, int payloadChecksum, int spare1, int vlcDepth, int vlcFrequency,
                        int vlcModulationType, int payloadLanguage, int beaconRepeatFrequency, byte[] payload) {
        super(opCode, protocolVersion, sequence, startCode, subUni, net, portAddress, length, data);

        if (startCode != VLC_START_CODE) {
            throw new IllegalArgumentException("StartCode ArtVlc non valido");
        }
        if (data.length < VLC_HEADER_LENGTH) {
            throw new IllegalArgumentException("Header ArtVlc incompleto");
        }
        if ((data[0] & 0xFF) != VLC_MAGIC_MAN_ID_HI || (data[1] & 0xFF) != VLC_MAGIC_MAN_ID_LO || (data[2] & 0xFF) != VLC_MAGIC_SUB_CODE) {
            throw new IllegalArgumentException("Magic number ArtVlc non validi");
        }
        if (payload == null) {
            throw new IllegalArgumentException("payload non puo essere null");
        }
        if (payloadCount != payload.length) {
            throw new IllegalArgumentException("payloadCount e payload.length non coincidono");
        }
        if (payloadCount < 0 || payloadCount > MAX_VLC_PAYLOAD_LENGTH) {
            throw new IllegalArgumentException("payloadCount fuori range per ArtVlc");
        }
        if (length != VLC_HEADER_LENGTH + payloadCount) {
            throw new IllegalArgumentException("Length ArtVlc non coerente con payloadCount");
        }

        this.flags = flags;
        this.transactionNumber = transactionNumber;
        this.slotAddress = slotAddress;
        this.payloadCount = payloadCount;
        this.payloadChecksum = payloadChecksum;
        this.spare1 = spare1;
        this.vlcDepth = vlcDepth;
        this.vlcFrequency = vlcFrequency;
        this.vlcModulationType = vlcModulationType;
        this.payloadLanguage = payloadLanguage;
        this.beaconRepeatFrequency = beaconRepeatFrequency;
        this.payload = Arrays.copyOf(payload, payload.length);
    }

    public int getFlags() {
        return flags;
    }

    public boolean isIeeePayload() {
        return ((flags >> 7) & 1) == 1;
    }

    public boolean isReplyPacket() {
        return ((flags >> 6) & 1) == 1;
    }

    public boolean isBeaconRepeatEnabled() {
        return ((flags >> 5) & 1) == 1;
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public boolean isFirstPacketInTransaction() {
        return transactionNumber == 0x0000;
    }

    public boolean isFinalPacketInTransaction() {
        return transactionNumber == 0xFFFF;
    }

    public int getSlotAddress() {
        return slotAddress;
    }

    public boolean isBroadcastToAllSlots() {
        return slotAddress == 0x0000;
    }

    public int getPayloadCount() {
        return payloadCount;
    }

    public int getPayloadChecksum() {
        return payloadChecksum;
    }

    public boolean isPayloadChecksumValid() {
        return computeUnsignedAdditiveChecksum(payload) == payloadChecksum;
    }

    public int getSpare1() {
        return spare1;
    }

    public int getVlcDepth() {
        return vlcDepth;
    }

    public int getVlcFrequency() {
        return vlcFrequency;
    }

    public int getVlcModulationType() {
        return vlcModulationType;
    }

    public int getPayloadLanguage() {
        return payloadLanguage;
    }

    public PayloadLanguage getPayloadLanguageType() {
        return PayloadLanguage.fromValue(payloadLanguage);
    }

    public int getBeaconRepeatFrequency() {
        return beaconRepeatFrequency;
    }

    public byte[] getPayload() {
        return Arrays.copyOf(payload, payload.length);
    }

    public String getPayloadAsAsciiString() {
        int end = 0;
        while (end < payload.length && payload[end] != 0) {
            end++;
        }
        return new String(payload, 0, end, StandardCharsets.US_ASCII);
    }

    private static int computeUnsignedAdditiveChecksum(byte[] data) {
        int checksum = 0;
        for (byte b : data) {
            checksum = (checksum + (b & 0xFF)) & 0xFFFF;
        }
        return checksum;
    }

    @Override
    public String toString() {
        return "ArtVlcPacket{" +
                "opCode=" + getOpCode() +
                ", protocolVersion=" + getProtocolVersion() +
                ", sequence=" + getSequence() +
                ", startCode=" + getStartCode() +
                ", subUni=" + getSubUni() +
                ", net=" + getNet() +
                ", portAddress=" + getPortAddress() +
                ", length=" + getLength() +
                ", flags=0x" + Integer.toHexString(flags) +
                ", ieeePayload=" + isIeeePayload() +
                ", replyPacket=" + isReplyPacket() +
                ", beaconRepeatEnabled=" + isBeaconRepeatEnabled() +
                ", transactionNumber=" + transactionNumber +
                ", slotAddress=" + slotAddress +
                ", payloadCount=" + payloadCount +
                ", payloadChecksum=0x" + Integer.toHexString(payloadChecksum) +
                ", payloadChecksumValid=" + isPayloadChecksumValid() +
                ", spare1=" + spare1 +
                ", vlcDepth=" + vlcDepth +
                ", vlcFrequency=" + vlcFrequency +
                ", vlcModulationType=" + vlcModulationType +
                ", payloadLanguage=0x" + Integer.toHexString(payloadLanguage) +
                ", payloadLanguageType=" + getPayloadLanguageType() +
                ", beaconRepeatFrequency=" + beaconRepeatFrequency +
                '}';
    }

    public enum PayloadLanguage {
        BeaconUrl(0x0000),
        BeaconText(0x0001),
        BeaconLocationId(0x0002),
        Unknown(-1);

        private final int value;

        PayloadLanguage(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PayloadLanguage fromValue(int value) {
            for (PayloadLanguage language : values()) {
                if (language.value == value) {
                    return language;
                }
            }
            return Unknown;
        }
    }
}

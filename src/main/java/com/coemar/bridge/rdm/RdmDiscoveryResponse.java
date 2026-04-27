package com.coemar.bridge.rdm;

import java.util.Optional;

public final class RdmDiscoveryResponse {

    private static final int PREAMBLE_BYTE = 0xFE;
    private static final int SEPARATOR_BYTE = 0xAA;
    private static final int ENCODED_UID_LENGTH = 12;
    private static final int ENCODED_CHECKSUM_LENGTH = 4;

    private final RdmUid uid;
    private final int checksum;

    private RdmDiscoveryResponse(RdmUid uid, int checksum) {
        this.uid = uid;
        this.checksum = checksum;
    }

    public static Optional<RdmDiscoveryResponse> parse(byte[] data) {
        if (data == null || data.length < 1 + ENCODED_UID_LENGTH + ENCODED_CHECKSUM_LENGTH) {
            return Optional.empty();
        }

        for (int separatorIndex = 0; separatorIndex < data.length; separatorIndex++) {
            if ((data[separatorIndex] & 0xFF) != SEPARATOR_BYTE) {
                continue;
            }
            if (!hasPreambleBefore(data, separatorIndex)) {
                continue;
            }
            int encodedStart = separatorIndex + 1;
            int encodedEnd = encodedStart + ENCODED_UID_LENGTH + ENCODED_CHECKSUM_LENGTH;
            if (encodedEnd > data.length) {
                continue;
            }

            byte[] uidBytes = decodeMaskedBytes(data, encodedStart, RdmUid.LENGTH);
            if (uidBytes == null) {
                continue;
            }

            byte[] checksumBytes = decodeMaskedBytes(data, encodedStart + ENCODED_UID_LENGTH, 2);
            if (checksumBytes == null) {
                continue;
            }

            int expectedChecksum = checksum(uidBytes);
            int actualChecksum = ((checksumBytes[0] & 0xFF) << 8) | (checksumBytes[1] & 0xFF);
            if (expectedChecksum == actualChecksum) {
                return Optional.of(new RdmDiscoveryResponse(RdmUid.of(uidBytes), actualChecksum));
            }
        }

        return Optional.empty();
    }

    public RdmUid getUid() {
        return uid;
    }

    public int getChecksum() {
        return checksum;
    }

    private static boolean hasPreambleBefore(byte[] data, int separatorIndex) {
        if (separatorIndex == 0) {
            return false;
        }
        for (int i = 0; i < separatorIndex; i++) {
            if ((data[i] & 0xFF) == PREAMBLE_BYTE) {
                return true;
            }
        }
        return false;
    }

    private static byte[] decodeMaskedBytes(byte[] data, int offset, int decodedLength) {
        byte[] decoded = new byte[decodedLength];
        for (int i = 0; i < decodedLength; i++) {
            int first = data[offset + (i * 2)] & 0xFF;
            int second = data[offset + (i * 2) + 1] & 0xFF;

            if ((first & 0xAA) != 0xAA || (second & 0x55) != 0x55) {
                return null;
            }

            decoded[i] = (byte) ((first & 0x55) | (second & 0xAA));
        }
        return decoded;
    }

    private static int checksum(byte[] uidBytes) {
        int checksum = 0;
        for (byte uidByte : uidBytes) {
            checksum = (checksum + (uidByte & 0xFF)) & 0xFFFF;
        }
        return checksum;
    }
}

package com.coemar.bridge.util;

import java.nio.charset.StandardCharsets;

public final class BinarySerializeUtils {

    private BinarySerializeUtils() {
    }

    public static int writeU8(byte[] target, int offset, int value) {
        target[offset] = (byte) value;
        return offset + 1;
    }

    public static int writeU16LE(byte[] target, int offset, int value) {
        target[offset] = (byte) (value & 0xFF);
        target[offset + 1] = (byte) ((value >>> 8) & 0xFF);
        return offset + 2;
    }

    public static int writeU16BE(byte[] target, int offset, int value) {
        target[offset] = (byte) ((value >>> 8) & 0xFF);
        target[offset + 1] = (byte) (value & 0xFF);
        return offset + 2;
    }

    public static int writeU32BE(byte[] target, int offset, int value) {
        target[offset] = (byte) ((value >>> 24) & 0xFF);
        target[offset + 1] = (byte) ((value >>> 16) & 0xFF);
        target[offset + 2] = (byte) ((value >>> 8) & 0xFF);
        target[offset + 3] = (byte) (value & 0xFF);
        return offset + 4;
    }

    public static int writeBytes(byte[] target, int offset, byte[] value) {
        System.arraycopy(value, 0, target, offset, value.length);
        return offset + value.length;
    }

    public static int writeIntArray(byte[] target, int offset, int[] values, int expectedLength) {
        for (int i = 0; i < expectedLength; i++) {
            target[offset + i] = (byte) ((values != null && i < values.length) ? values[i] : 0);
        }
        return offset + expectedLength;
    }

    public static int writeFixedAscii(byte[] target, int offset, String value, int fieldLength) {
        byte[] encoded = value == null ? new byte[0] : value.getBytes(StandardCharsets.US_ASCII);
        int copyLength = Math.min(encoded.length, Math.max(fieldLength - 1, 0));

        System.arraycopy(encoded, 0, target, offset, copyLength);

        for (int i = copyLength; i < fieldLength; i++) {
            target[offset + i] = 0;
        }

        return offset + fieldLength;
    }

    public static int writeZeroBytes(byte[] target, int offset, int count) {
        for (int i = 0; i < count; i++) {
            target[offset + i] = 0;
        }
        return offset + count;
    }

    public static int writePackedUnsignedInt(byte[] target, int offset, int value, int byteCount) {
        long unsignedValue = value & 0xFFFF_FFFFL;

        for (int i = 0; i < byteCount; i++) {
            int shift = (byteCount - 1 - i) * 8;
            target[offset + i] = (byte) (shift >= 32 ? 0 : (unsignedValue >>> shift));
        }

        return offset + byteCount;
    }
}

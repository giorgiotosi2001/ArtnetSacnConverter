package com.coemar.bridge.util;

import java.nio.charset.StandardCharsets;

public final class BinaryParseUtils {

    private BinaryParseUtils() {
    }

    public static int u8(byte[] data, int off) {
        return data[off] & 0xFF;
    }

    public static int u16be(byte[] data, int off) {
        return ((data[off] & 0xFF) << 8) | (data[off + 1] & 0xFF);
    }

    public static int u16le(byte[] data, int off) {
        return (data[off] & 0xFF) | ((data[off + 1] & 0xFF) << 8);
    }

    public static long u32be(byte[] data, int off) {
        return ((long) (data[off] & 0xFF) << 24)
                | ((long) (data[off + 1] & 0xFF) << 16)
                | ((long) (data[off + 2] & 0xFF) << 8)
                | (data[off + 3] & 0xFFL);
    }

    public static long u32le(byte[] data, int off) {
        return (data[off] & 0xFFL)
                | ((long) (data[off + 1] & 0xFF) << 8)
                | ((long) (data[off + 2] & 0xFF) << 16)
                | ((long) (data[off + 3] & 0xFF) << 24);
    }

    public static String readNullTerminatedUtf8(byte[] data, int offset, int maxLen) {
        int end = offset;
        int limit = offset + maxLen;
        while (end < limit && data[end] != 0) {
            end++;
        }
        return new String(data, offset, end - offset, StandardCharsets.UTF_8);
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}

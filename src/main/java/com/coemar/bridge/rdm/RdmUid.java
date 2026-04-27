package com.coemar.bridge.rdm;

import java.util.Arrays;

public final class RdmUid implements Comparable<RdmUid> {

    public static final int LENGTH = 6;
    public static final RdmUid MIN = new RdmUid(new byte[]{0, 0, 0, 0, 0, 0});
    public static final RdmUid MAX = new RdmUid(new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    });

    private static final long MAX_VALUE = 0xFFFF_FFFF_FFFFL;

    private final byte[] bytes;

    public RdmUid(byte[] bytes) {
        if (bytes == null || bytes.length != LENGTH) {
            throw new IllegalArgumentException("UID must contain exactly 6 bytes");
        }
        this.bytes = Arrays.copyOf(bytes, LENGTH);
    }

    public static RdmUid of(byte[] bytes) {
        return new RdmUid(bytes);
    }

    public static RdmUid fromLong(long value) {
        if (value < 0 || value > MAX_VALUE) {
            throw new IllegalArgumentException("UID value fuori range");
        }
        byte[] bytes = new byte[LENGTH];
        for (int i = LENGTH - 1; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        return new RdmUid(bytes);
    }

    public long toLong() {
        long value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFFL);
        }
        return value;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, LENGTH);
    }

    public RdmUid next() {
        long value = toLong();
        if (value == MAX_VALUE) {
            throw new IllegalStateException("UID massimo non incrementabile");
        }
        return fromLong(value + 1);
    }

    public static RdmUid midpoint(RdmUid lower, RdmUid upper) {
        long low = lower.toLong();
        long high = upper.toLong();
        return fromLong(low + ((high - low) >>> 1));
    }

    @Override
    public int compareTo(RdmUid other) {
        return Long.compareUnsigned(toLong(), other.toLong());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RdmUid rdmUid && Arrays.equals(bytes, rdmUid.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(17);
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                builder.append(':');
            }
            builder.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return builder.toString();
    }
}

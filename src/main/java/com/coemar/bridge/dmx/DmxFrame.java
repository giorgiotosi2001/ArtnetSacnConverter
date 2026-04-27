package com.coemar.bridge.dmx;

import com.coemar.bridge.model.LightingPacket;

import java.util.Arrays;

public class DmxFrame implements LightingPacket {

    private final DmxSourceProtocol sourceProtocol;
    private final int universe;
    private final int sequence;
    private final int priority;
    private final int startCode;
    private final byte[] slots;

    public DmxFrame(int channelCount) {
        this(DmxSourceProtocol.LOCAL, 0, 0, 0, DmxConstants.START_CODE_DMX, new byte[channelCount]);
    }

    public DmxFrame(DmxSourceProtocol sourceProtocol,
                    int universe,
                    int sequence,
                    int priority,
                    int startCode,
                    byte[] slots) {
        if (sourceProtocol == null) {
            throw new IllegalArgumentException("sourceProtocol must not be null");
        }
        validateStartCode(startCode);
        validateSlots(slots);

        this.sourceProtocol = sourceProtocol;
        this.universe = universe;
        this.sequence = sequence & 0xFF;
        this.priority = priority;
        this.startCode = startCode;
        this.slots = Arrays.copyOf(slots, slots.length);
    }

    public static DmxFrame standard(DmxSourceProtocol sourceProtocol, int universe, int sequence, int priority, byte[] slots) {
        return new DmxFrame(sourceProtocol, universe, sequence, priority, DmxConstants.START_CODE_DMX, slots);
    }

    public DmxSourceProtocol getSourceProtocol() {
        return sourceProtocol;
    }

    public int getUniverse() {
        return universe;
    }

    public int getSequence() {
        return sequence;
    }

    public int getPriority() {
        return priority;
    }

    public int getStartCode() {
        return startCode;
    }

    public boolean isStandardDmx() {
        return startCode == DmxConstants.START_CODE_DMX;
    }

    public int getChannelCount() {
        return slots.length;
    }

    public void setChannelValue(int channel, int value) {
        validateChannel(channel);
        validateValue(value);
        slots[channel] = (byte) value;
    }

    public int getChannelValue(int channel) {
        validateChannel(channel);
        return slots[channel] & 0xFF;
    }

    public int[] getValues() {
        int[] values = new int[slots.length];
        for (int i = 0; i < slots.length; i++) {
            values[i] = slots[i] & 0xFF;
        }
        return values;
    }

    public byte[] getSlots() {
        return Arrays.copyOf(slots, slots.length);
    }

    public byte[] toWireBytes() {
        byte[] data = new byte[slots.length + 1];
        data[0] = (byte) startCode;
        System.arraycopy(slots, 0, data, 1, slots.length);
        return data;
    }

    private void validateChannel(int channel) {
        if (channel < 0 || channel >= slots.length) {
            throw new IllegalArgumentException("Invalid DMX channel: " + channel);
        }
    }

    private static void validateStartCode(int startCode) {
        if (startCode < DmxConstants.MIN_SLOT_VALUE || startCode > DmxConstants.MAX_SLOT_VALUE) {
            throw new IllegalArgumentException("Invalid DMX start code: " + startCode);
        }
    }

    private static void validateSlots(byte[] slots) {
        if (slots == null) {
            throw new IllegalArgumentException("slots must not be null");
        }
        if (slots.length < DmxConstants.MIN_SLOT_COUNT || slots.length > DmxConstants.MAX_SLOT_COUNT) {
            throw new IllegalArgumentException("DMX slot count fuori range: " + slots.length);
        }
    }

    private static void validateValue(int value) {
        if (value < DmxConstants.MIN_SLOT_VALUE || value > DmxConstants.MAX_SLOT_VALUE) {
            throw new IllegalArgumentException("Invalid DMX value: " + value);
        }
    }

    @Override
    public String toString() {
        return "DmxFrame{" +
                "sourceProtocol=" + sourceProtocol +
                ", universe=" + universe +
                ", sequence=" + sequence +
                ", priority=" + priority +
                ", startCode=" + startCode +
                ", channelCount=" + slots.length +
                '}';
    }
}

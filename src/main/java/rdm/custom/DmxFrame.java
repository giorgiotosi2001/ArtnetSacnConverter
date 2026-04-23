package rdm.custom;

import java.util.Arrays;

public class DmxFrame {

    private final int[] channels;

    public DmxFrame(int channelCount) {
        if (channelCount <= 0) {
            throw new IllegalArgumentException("Channel count must be positive");
        }
        this.channels = new int[channelCount];
    }

    public void setChannelValue(int channel, int value) {
        validateChannel(channel);
        validateValue(value);
        channels[channel] = value;
    }

    public int getChannelValue(int channel) {
        validateChannel(channel);
        return channels[channel];
    }

    public int[] getValues() {
        return Arrays.copyOf(channels, channels.length);
    }

    private void validateChannel(int channel) {
        if (channel < 0 || channel >= channels.length) {
            throw new IllegalArgumentException("Invalid channel: " + channel);
        }
    }

    private void validateValue(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Invalid DMX value: " + value);
        }
    }
}
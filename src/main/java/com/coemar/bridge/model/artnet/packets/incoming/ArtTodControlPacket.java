package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * <p>Comando di controllo discovery/TOD per una porta RDM di output gateway.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> controller -&gt; output gateway in unicast o broadcast; input gateway puo usare directed broadcast.</li>
 *   <li><b>Risposta attesa:</b> {@code ArtTodDataPacket}.</li>
 *   <li><b>Serve per:</b> flush TOD, fine discovery corrente, enable/disable incremental discovery.</li>
 *   <li><b>Note:</b> agisce su un Port-Address preciso identificato da Net + Address.</li>
 * </ul>
 */
public class ArtTodControlPacket implements Packet {

    private static final int SPARE_LENGTH = 7;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int filler2;
    private final byte[] spare;
    private final int net;
    private final int command;
    private final int address;

    public ArtTodControlPacket(int opCode, int protocolVersion, int filler1, int filler2, byte[] spare, int net,
                               int command, int address) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.filler2 = filler2;
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
        this.net = net;
        this.command = command;
        this.address = address;
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

    public byte[] getSpare() {
        return Arrays.copyOf(spare, spare.length);
    }

    public int getNet() {
        return net;
    }

    public int getNetSwitchValue() {
        return net & 0x7F;
    }

    public int getCommand() {
        return command;
    }

    public Command getCommandType() {
        return Command.fromValue(command);
    }

    public boolean isFlushRequest() {
        return getCommandType() == Command.AtcFlush;
    }

    public boolean isEndRequest() {
        return getCommandType() == Command.AtcEnd;
    }

    public boolean isIncrementalDiscoveryEnableRequest() {
        return getCommandType() == Command.AtcIncOn;
    }

    public boolean isIncrementalDiscoveryDisableRequest() {
        return getCommandType() == Command.AtcIncOff;
    }

    public int getAddress() {
        return address;
    }

    public int getPortAddress() {
        return ((net & 0x7F) << 8) | (address & 0xFF);
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
        return "ArtTodControlPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", filler2=" + filler2 +
                ", spare=" + formatUnsignedByteArray(spare) +
                ", net=" + (net & 0xFF) +
                ", netSwitchValue=" + getNetSwitchValue() +
                ", command=" + command +
                ", commandType=" + getCommandType() +
                ", address=" + (address & 0xFF) +
                ", portAddress=" + getPortAddress() +
                '}';
    }

    public enum Command {
        AtcNone(0x00),
        AtcFlush(0x01),
        AtcEnd(0x02),
        AtcIncOn(0x03),
        AtcIncOff(0x04),
        Unknown(-1);

        private final int value;

        Command(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Command fromValue(int value) {
            for (Command command : values()) {
                if (command.value == value) {
                    return command;
                }
            }
            return Unknown;
        }
    }
}

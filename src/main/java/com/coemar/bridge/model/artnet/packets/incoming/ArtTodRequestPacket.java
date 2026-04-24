package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * <p>Richiesta della Table of Devices (TOD) RDM di uno o piu output gateway.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> controller -&gt; gateway in unicast o broadcast; input gateway puo usare directed broadcast.</li>
 *   <li><b>Risposta attesa:</b> {@code ArtTodDataPacket}.</li>
 *   <li><b>Serve per:</b> ottenere gli UID RDM scoperti su specifici Port-Address.</li>
 *   <li><b>Note:</b> non forza una full discovery; chiede il TOD gia mantenuto dal gateway.</li>
 * </ul>
 */
public class ArtTodRequestPacket implements Packet {

    private static final int SPARE_LENGTH = 7;
    private static final int ADDRESS_LENGTH = 32;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int filler2;
    private final byte[] spare;
    private final int net;
    private final int command;
    private final int addCount;
    private final byte[] address;

    public ArtTodRequestPacket(int opCode, int protocolVersion, int filler1, int filler2, byte[] spare, int net,
                               int command, int addCount, byte[] address) {
        if (addCount < 0 || addCount > ADDRESS_LENGTH) {
            throw new IllegalArgumentException("addCount fuori range: " + addCount);
        }

        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.filler2 = filler2;
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
        this.net = net;
        this.command = command;
        this.addCount = addCount;
        this.address = copyFixedLengthBytes(address, ADDRESS_LENGTH, "address");
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

    public boolean isTodFullRequest() {
        return getCommandType() == Command.TodFull;
    }

    public int getAddCount() {
        return addCount;
    }

    public byte[] getAddress() {
        return Arrays.copyOf(address, address.length);
    }

    public int getAddressLowByte(int index) {
        if (index < 0 || index >= addCount) {
            throw new IndexOutOfBoundsException("Address index fuori range: " + index);
        }
        return address[index] & 0xFF;
    }

    public int getPortAddress(int index) {
        return ((net & 0x7F) << 8) | getAddressLowByte(index);
    }

    public int[] getRequestedPortAddresses() {
        int[] requestedPortAddresses = new int[addCount];
        for (int i = 0; i < addCount; i++) {
            requestedPortAddresses[i] = getPortAddress(i);
        }
        return requestedPortAddresses;
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
        return "ArtTodRequestPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", filler2=" + filler2 +
                ", net=" + (net & 0xFF) +
                ", netSwitchValue=" + getNetSwitchValue() +
                ", command=" + command +
                ", commandType=" + getCommandType() +
                ", addCount=" + addCount +
                ", address=" + formatUnsignedByteArray(address) +
                '}';
    }

    public enum Command {
        TodFull(0x00),
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

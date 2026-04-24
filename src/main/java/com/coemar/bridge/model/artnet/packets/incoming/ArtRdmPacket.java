package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * <p>Trasporto Art-Net di messaggi RDM non di discovery.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> bidirezionale tra controller, gateway e nodi proxy, in unicast.</li>
 *   <li><b>Risposta attesa:</b> nessuna risposta Art-Net dedicata; l'eventuale risposta RDM torna come un altro {@code ArtRdmPacket}.</li>
 *   <li><b>Serve per:</b> inoltrare il pacchetto RDM raw verso il Port-Address corretto, con informazioni sulla coda FIFO gateway.</li>
 *   <li><b>Note:</b> il payload RDM esclude il DMX start code {@code 0xCC}.</li>
 * </ul>
 */
public class ArtRdmPacket implements Packet {

    private static final int SPARE_LENGTH = 5;

    private final int opCode;
    private final int protocolVersion;
    private final int rdmVersion;
    private final int filler2;
    private final byte[] spare;
    private final int fifoAvail;
    private final int fifoMax;
    private final int net;
    private final int command;
    private final int address;
    private final byte[] rdmPacket;

    public ArtRdmPacket(int opCode, int protocolVersion, int rdmVersion, int filler2, byte[] spare, int fifoAvail,
                        int fifoMax, int net, int command, int address, byte[] rdmPacket) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.rdmVersion = rdmVersion;
        this.filler2 = filler2;
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
        this.fifoAvail = fifoAvail;
        this.fifoMax = fifoMax;
        this.net = net;
        this.command = command;
        this.address = address;
        this.rdmPacket = copyBytes(rdmPacket, "rdmPacket");
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getRdmVersion() {
        return rdmVersion;
    }

    public boolean isRdmDraftV10() {
        return rdmVersion == 0x00;
    }

    public boolean isRdmStandardV10() {
        return rdmVersion == 0x01;
    }

    public int getFiller2() {
        return filler2;
    }

    public byte[] getSpare() {
        return Arrays.copyOf(spare, spare.length);
    }

    public int getFifoAvail() {
        return fifoAvail;
    }

    public int getFifoMax() {
        return fifoMax;
    }

    public boolean isQueueImplemented() {
        return fifoMax > 0;
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

    public boolean isProcessCommand() {
        return getCommandType() == Command.ArProcess;
    }

    public int getAddress() {
        return address;
    }

    public int getPortAddress() {
        return ((net & 0x7F) << 8) | (address & 0xFF);
    }

    public byte[] getRdmPacket() {
        return Arrays.copyOf(rdmPacket, rdmPacket.length);
    }

    public boolean hasRdmPayload() {
        return rdmPacket.length > 0;
    }

    public boolean hasValidRdmSubStartCode() {
        return hasRdmPayload() && (rdmPacket[0] & 0xFF) == 0x01;
    }

    public int getRdmMessageLength() {
        if (rdmPacket.length < 2) {
            throw new IllegalStateException("RDM packet troppo corto per leggere il message length");
        }
        return rdmPacket[1] & 0xFF;
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

    private static byte[] copyBytes(byte[] value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " non puo essere null");
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
        return "ArtRdmPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", rdmVersion=" + rdmVersion +
                ", filler2=" + filler2 +
                ", spare=" + formatUnsignedByteArray(spare) +
                ", fifoAvail=" + fifoAvail +
                ", fifoMax=" + fifoMax +
                ", queueImplemented=" + isQueueImplemented() +
                ", net=" + (net & 0xFF) +
                ", netSwitchValue=" + getNetSwitchValue() +
                ", command=" + command +
                ", commandType=" + getCommandType() +
                ", address=" + (address & 0xFF) +
                ", portAddress=" + getPortAddress() +
                ", rdmPacketLength=" + rdmPacket.length +
                '}';
    }

    public enum Command {
        ArProcess(0x00),
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

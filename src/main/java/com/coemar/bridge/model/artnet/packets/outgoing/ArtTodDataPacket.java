package com.coemar.bridge.model.artnet.packets.outgoing;

import com.coemar.bridge.artnet.codes.ArtNetOpCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.coemar.bridge.util.BinarySerializeUtils.writeBytes;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU16BE;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU16LE;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU8;
import static com.coemar.bridge.util.BinarySerializeUtils.writeZeroBytes;

public class ArtTodDataPacket {

    private static final int UID_LENGTH = 6;
    private static final int HEADER_LENGTH = 28;
    private static final int MAX_UID_COUNT = 200;
    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    private final ArtNetOpCode opCode = ArtNetOpCode.OpTodData;
    private final int protocolVersion;
    private final int rdmVersion;
    private final int port;
    private final int bindIndex;
    private final int net;
    private final int commandResponse;
    private final int address;
    private final int uidTotal;
    private final int blockCount;
    private final byte[][] tod;

    public ArtTodDataPacket(int protocolVersion, int rdmVersion, int port, int bindIndex, int net,
                            int commandResponse, int address, int uidTotal, int blockCount, byte[][] tod) {
        this.protocolVersion = protocolVersion;
        this.rdmVersion = rdmVersion;
        this.port = validateRange(port, 1, 4, "port");
        this.bindIndex = validateRange(bindIndex, 1, 255, "bindIndex");
        this.net = validateRange(net, 0, 0x7F, "net");
        this.commandResponse = commandResponse & 0xFF;
        this.address = validateRange(address, 0, 0xFF, "address");
        this.uidTotal = validateRange(uidTotal, 0, 0xFFFF, "uidTotal");
        this.blockCount = validateRange(blockCount, 0, 0xFF, "blockCount");
        this.tod = copyTod(tod);

        if (this.uidTotal < this.tod.length) {
            throw new IllegalArgumentException("uidTotal non puo essere minore di uidCount");
        }
    }

    public ArtTodDataPacket(int protocolVersion, int rdmVersion, int port, int bindIndex, int net,
                            CommandResponse commandResponse, int address, int uidTotal, int blockCount, byte[][] tod) {
        this(protocolVersion, rdmVersion, port, bindIndex, net,
                commandResponse == null ? CommandResponse.Unknown.getValue() : commandResponse.getValue(),
                address, uidTotal, blockCount, tod);
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getRdmVersion() {
        return rdmVersion;
    }

    public int getPort() {
        return port;
    }

    public int getBindIndex() {
        return bindIndex;
    }

    public int getNet() {
        return net;
    }

    public int getCommandResponse() {
        return commandResponse;
    }

    public CommandResponse getCommandResponseType() {
        return CommandResponse.fromValue(commandResponse);
    }

    public int getAddress() {
        return address;
    }

    public int getUidTotal() {
        return uidTotal;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public int getUidCount() {
        return tod.length;
    }

    public int getPortAddress() {
        return ((net & 0x7F) << 8) | (address & 0xFF);
    }

    public boolean isTodFullResponse() {
        return getCommandResponseType() == CommandResponse.TodFull;
    }

    public boolean isTodNakResponse() {
        return getCommandResponseType() == CommandResponse.TodNak;
    }

    public byte[][] getTod() {
        return copyTod(tod);
    }

    public byte[] serializeArtTodDataPacket() {
        byte[] encodedTod = encodeTod();
        byte[] packet = new byte[HEADER_LENGTH + encodedTod.length];
        int offset = 0;

        offset = writeBytes(packet, offset, ARTNET_ID);
        offset = writeU16LE(packet, offset, opCode.getValue());
        offset = writeU16BE(packet, offset, protocolVersion);
        offset = writeU8(packet, offset, rdmVersion);
        offset = writeU8(packet, offset, port);
        offset = writeZeroBytes(packet, offset, 6);
        offset = writeU8(packet, offset, bindIndex);
        offset = writeU8(packet, offset, net);
        offset = writeU8(packet, offset, encodeCommandResponse());
        offset = writeU8(packet, offset, encodeAddress());
        offset = writeU16BE(packet, offset, encodeUidTotal());
        offset = writeU8(packet, offset, encodeBlockCount());
        offset = writeU8(packet, offset, encodeUidCount());
        offset = writeBytes(packet, offset, encodedTod);

        if (offset != packet.length) {
            throw new IllegalStateException("Lunghezza ArtTodData non valida: " + offset);
        }

        return packet;
    }

    public static byte[] encodeUid(long uidValue) {
        if (uidValue < 0 || uidValue > 0xFFFF_FFFFFFFFL) {
            throw new IllegalArgumentException("uidValue fuori range per UID RDM a 48 bit");
        }

        return new byte[]{
                (byte) ((uidValue >>> 40) & 0xFF),
                (byte) ((uidValue >>> 32) & 0xFF),
                (byte) ((uidValue >>> 24) & 0xFF),
                (byte) ((uidValue >>> 16) & 0xFF),
                (byte) ((uidValue >>> 8) & 0xFF),
                (byte) (uidValue & 0xFF)
        };
    }

    public static byte[] encodeUid(int manufacturerId, long deviceId) {
        if (manufacturerId < 0 || manufacturerId > 0xFFFF) {
            throw new IllegalArgumentException("manufacturerId fuori range");
        }
        if (deviceId < 0 || deviceId > 0xFFFF_FFFFL) {
            throw new IllegalArgumentException("deviceId fuori range");
        }

        return new byte[]{
                (byte) ((manufacturerId >>> 8) & 0xFF),
                (byte) (manufacturerId & 0xFF),
                (byte) ((deviceId >>> 24) & 0xFF),
                (byte) ((deviceId >>> 16) & 0xFF),
                (byte) ((deviceId >>> 8) & 0xFF),
                (byte) (deviceId & 0xFF)
        };
    }

    public static byte[][] encodeUidList(long... uidValues) {
        if (uidValues == null) {
            throw new IllegalArgumentException("uidValues non puo essere null");
        }

        byte[][] encoded = new byte[uidValues.length][];
        for (int i = 0; i < uidValues.length; i++) {
            encoded[i] = encodeUid(uidValues[i]);
        }
        return encoded;
    }

    private int encodeCommandResponse() {
        return commandResponse & 0xFF;
    }

    private int encodeAddress() {
        return address & 0xFF;
    }

    private int encodeUidTotal() {
        return uidTotal & 0xFFFF;
    }

    private int encodeBlockCount() {
        return blockCount & 0xFF;
    }

    private int encodeUidCount() {
        return tod.length;
    }

    private byte[] encodeTod() {
        byte[] encoded = new byte[tod.length * UID_LENGTH];
        int offset = 0;

        for (byte[] uid : tod) {
            System.arraycopy(uid, 0, encoded, offset, UID_LENGTH);
            offset += UID_LENGTH;
        }

        return encoded;
    }

    private static byte[][] copyTod(byte[][] tod) {
        if (tod == null) {
            throw new IllegalArgumentException("tod non puo essere null");
        }
        if (tod.length > MAX_UID_COUNT) {
            throw new IllegalArgumentException("tod non puo contenere piu di " + MAX_UID_COUNT + " UID");
        }

        byte[][] copy = new byte[tod.length][];
        for (int i = 0; i < tod.length; i++) {
            if (tod[i] == null) {
                throw new IllegalArgumentException("tod[" + i + "] non puo essere null");
            }
            if (tod[i].length != UID_LENGTH) {
                throw new IllegalArgumentException("tod[" + i + "] deve essere lungo " + UID_LENGTH + " byte");
            }
            copy[i] = Arrays.copyOf(tod[i], tod[i].length);
        }
        return copy;
    }

    private static int validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " fuori range: " + value);
        }
        return value;
    }

    @Override
    public String toString() {
        return "ArtTodDataPacket{" +
                "protocolVersion=" + protocolVersion +
                ", rdmVersion=" + rdmVersion +
                ", port=" + port +
                ", bindIndex=" + bindIndex +
                ", net=" + net +
                ", commandResponse=0x" + Integer.toHexString(commandResponse & 0xFF) +
                ", commandResponseType=" + getCommandResponseType() +
                ", address=" + address +
                ", portAddress=" + getPortAddress() +
                ", uidTotal=" + uidTotal +
                ", blockCount=" + blockCount +
                ", uidCount=" + getUidCount() +
                '}';
    }

    public enum CommandResponse {
        TodFull(0x00),
        TodNak(0xFF),
        Unknown(-1);

        private final int value;

        CommandResponse(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static CommandResponse fromValue(int value) {
            for (CommandResponse commandResponse : values()) {
                if (commandResponse.value == value) {
                    return commandResponse;
                }
            }
            return Unknown;
        }
    }
}

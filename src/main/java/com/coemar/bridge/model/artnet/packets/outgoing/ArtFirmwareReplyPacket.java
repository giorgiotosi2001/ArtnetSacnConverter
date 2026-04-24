package com.coemar.bridge.model.artnet.packets.outgoing;

import com.coemar.bridge.artnet.ArtNetOpCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.coemar.bridge.util.BinarySerializeUtils.writeBytes;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU16BE;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU16LE;
import static com.coemar.bridge.util.BinarySerializeUtils.writeU8;
import static com.coemar.bridge.util.BinarySerializeUtils.writeZeroBytes;

/**
 * Risposta di acknowledge a un blocco {@code ArtFirmwareMasterPacket}.
 *
 * - Direzione tipica: nodo -> controller, in unicast.
 * - Risponde a: {@code ArtFirmwareMasterPacket}.
 * - Serve per: segnalare blocco ricevuto, upload completato o errore di firmware/UBEA.
 * - Note: dopo un esito positivo il controller puo inviare il blocco successivo.
 */
public class ArtFirmwareReplyPacket {

    private static final int SPARE_LENGTH = 21;
    private static final int PACKET_LENGTH = 36;
    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    private final ArtNetOpCode opCode = ArtNetOpCode.OpFirmwareReply;
    private final int protocolVersion;
    private final int type;
    private final byte[] spare;

    public ArtFirmwareReplyPacket(int protocolVersion, int type) {
        this(protocolVersion, type, new byte[SPARE_LENGTH]);
    }

    public ArtFirmwareReplyPacket(int protocolVersion, Type type) {
        this(protocolVersion, type == null ? Type.Unknown.getValue() : type.getValue(), new byte[SPARE_LENGTH]);
    }

    public ArtFirmwareReplyPacket(int protocolVersion, int type, byte[] spare) {
        this.protocolVersion = protocolVersion;
        this.type = type;
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
    }

    public ArtFirmwareReplyPacket(int protocolVersion, Type type, byte[] spare) {
        this(protocolVersion, type == null ? Type.Unknown.getValue() : type.getValue(), spare);
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getType() {
        return type;
    }

    public Type getReplyType() {
        return Type.fromValue(type);
    }

    public boolean isBlockGood() {
        return type == Type.FirmBlockGood.getValue();
    }

    public boolean isAllGood() {
        return type == Type.FirmAllGood.getValue();
    }

    public boolean isFailure() {
        return type == Type.FirmFail.getValue();
    }

    public byte[] getSpare() {
        return Arrays.copyOf(spare, spare.length);
    }

    public byte[] serializeArtFirmwareReplyPacket() {
        byte[] packet = new byte[PACKET_LENGTH];
        int offset = 0;

        offset = writeBytes(packet, offset, ARTNET_ID);
        offset = writeU16LE(packet, offset, opCode.getValue());
        offset = writeU16BE(packet, offset, protocolVersion);
        offset = writeZeroBytes(packet, offset, 2);
        offset = writeU8(packet, offset, encodeType());
        offset = writeBytes(packet, offset, encodeSpare());

        if (offset != packet.length) {
            throw new IllegalStateException("Lunghezza ArtFirmwareReply non valida: " + offset);
        }

        return packet;
    }

    private int encodeType() {
        return type & 0xFF;
    }

    private byte[] encodeSpare() {
        return spare;
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

    @Override
    public String toString() {
        return "ArtFirmwareReplyPacket{" +
                "protocolVersion=" + protocolVersion +
                ", type=0x" + Integer.toHexString(type & 0xFF) +
                ", replyType=" + getReplyType() +
                ", blockGood=" + isBlockGood() +
                ", allGood=" + isAllGood() +
                ", failure=" + isFailure() +
                '}';
    }

    public enum Type {
        FirmBlockGood(0x00),
        FirmAllGood(0x01),
        FirmFail(0xFF),
        Unknown(-1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type fromValue(int value) {
            for (Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return Unknown;
        }
    }
}

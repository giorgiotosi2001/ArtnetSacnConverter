package com.coemar.bridge.model.artnet.packets.outgoing;

import com.coemar.bridge.artnet.ArtNetOpCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.coemar.bridge.util.BinarySerializeUtils.*;

/**
 * Risposta a una {@code ArtDataRequestPacket} con dati descrittivi del dispositivo.
 *
 * - Direzione tipica: nodo/media server -> controller che ha fatto la richiesta, in unicast.
 * - Risponde a: {@code ArtDataRequestPacket}.
 * - Serve per: restituire payload come URL, documentazione o dati vendor-specific.
 * - Note: il payload puo essere binario o stringa ASCII null-terminated, a seconda del request code.
 */
public class ArtDataReplyPacket {

    private static final int HEADER_LENGTH = 20;
    private static final int MAX_PAYLOAD_LENGTH = 513;
    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    private final ArtNetOpCode opCode = ArtNetOpCode.OpDataReply;
    private final int protocolVersion;
    private final int estaManufacturerCode;
    private final int oemCode;
    private final int requestCode;
    private final byte[] payload;

    public ArtDataReplyPacket(int protocolVersion, int estaManufacturerCode, int oemCode, int requestCode, byte[] payload) {
        this.protocolVersion = protocolVersion;
        this.estaManufacturerCode = estaManufacturerCode;
        this.oemCode = oemCode;
        this.requestCode = requestCode;
        this.payload = copyPayload(payload);
    }

    public ArtDataReplyPacket(int protocolVersion, int estaManufacturerCode, int oemCode, int requestCode, String payloadText) {
        this(protocolVersion, estaManufacturerCode, oemCode, requestCode, encodeNullTerminatedStringPayload(payloadText));
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getEstaManufacturerCode() {
        return estaManufacturerCode;
    }

    public int getOemCode() {
        return oemCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public byte[] getPayload() {
        return Arrays.copyOf(payload, payload.length);
    }

    public boolean isManufacturerSpecificRequest() {
        return requestCode >= 0x8000 && requestCode <= 0xFFFF;
    }

    public byte[] serializeArtDataReplyPacket() {
        byte[] packet = new byte[HEADER_LENGTH + payload.length];
        int offset = 0;

        offset = writeBytes(packet, offset, ARTNET_ID);
        offset = writeU16LE(packet, offset, opCode.getValue());
        offset = writeU16BE(packet, offset, protocolVersion);
        offset = writeU16BE(packet, offset, estaManufacturerCode);
        offset = writeU16BE(packet, offset, oemCode);
        offset = writeU16BE(packet, offset, encodeRequestCode());
        offset = writeU16BE(packet, offset, encodePayloadLength());
        offset = writeBytes(packet, offset, encodePayload());

        if (offset != packet.length) {
            throw new IllegalStateException("Lunghezza ArtDataReply non valida: " + offset);
        }

        return packet;
    }

    public static byte[] encodeNullTerminatedStringPayload(String payloadText) {
        if (payloadText == null) {
            throw new IllegalArgumentException("payloadText non puo essere null");
        }

        byte[] encoded = payloadText.getBytes(StandardCharsets.US_ASCII);
        if (encoded.length >= MAX_PAYLOAD_LENGTH) {
            throw new IllegalArgumentException("payloadText troppo lungo per ArtDataReply");
        }

        byte[] nullTerminated = Arrays.copyOf(encoded, encoded.length + 1);
        nullTerminated[nullTerminated.length - 1] = 0;
        return nullTerminated;
    }

    private int encodeRequestCode() {
        return requestCode & 0xFFFF;
    }

    private int encodePayloadLength() {
        return payload.length;
    }

    private byte[] encodePayload() {
        return payload;
    }

    private static byte[] copyPayload(byte[] payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload non puo essere null");
        }
        if (payload.length > MAX_PAYLOAD_LENGTH) {
            throw new IllegalArgumentException("payload troppo lungo per ArtDataReply");
        }
        return Arrays.copyOf(payload, payload.length);
    }

    @Override
    public String toString() {
        return "ArtDataReplyPacket{" +
                "protocolVersion=" + protocolVersion +
                ", estaManufacturerCode=" + estaManufacturerCode +
                ", oemCode=" + oemCode +
                ", requestCode=0x" + Integer.toHexString(requestCode) +
                ", payloadLength=" + payload.length +
                ", manufacturerSpecificRequest=" + isManufacturerSpecificRequest() +
                '}';
    }
}

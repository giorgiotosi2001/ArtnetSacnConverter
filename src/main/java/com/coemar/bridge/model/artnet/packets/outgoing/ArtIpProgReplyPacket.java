package com.coemar.bridge.model.artnet.packets.outgoing;

import com.coemar.bridge.artnet.ArtNetOpCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.coemar.bridge.util.BinarySerializeUtils.*;

/**
 * Risposta allo stato di programmazione IP remota del nodo.
 *
 * - Direzione tipica: nodo -> controller che ha inviato la richiesta, in unicast.
 * - Risponde a: {@code ArtIpProgPacket}.
 * - Serve per: restituire IP, mask, gateway, porta e stato DHCP correnti/programmatici.
 * - Note: e un packet di esito/stato; non trasporta DMX.
 */
public class ArtIpProgReplyPacket {

    private static final int IPV4_LENGTH = 4;
    private static final int PACKET_LENGTH = 34;
    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    private final ArtNetOpCode opCode = ArtNetOpCode.OpIpProgReply;
    private final int protocolVersion;
    private final byte[] progIpAddress;
    private final byte[] progSubnetMask;
    private final int progPort;
    private final boolean dhcpEnabled;
    private final byte[] progDefaultGateway;

    public ArtIpProgReplyPacket(int protocolVersion, byte[] progIpAddress, byte[] progSubnetMask, int progPort,
                                boolean dhcpEnabled, byte[] progDefaultGateway) {
        this.protocolVersion = protocolVersion;
        this.progIpAddress = copyFixedLengthBytes(progIpAddress, IPV4_LENGTH, "progIpAddress");
        this.progSubnetMask = copyFixedLengthBytes(progSubnetMask, IPV4_LENGTH, "progSubnetMask");
        this.progPort = progPort;
        this.dhcpEnabled = dhcpEnabled;
        this.progDefaultGateway = copyFixedLengthBytes(progDefaultGateway, IPV4_LENGTH, "progDefaultGateway");
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public byte[] getProgIpAddress() {
        return Arrays.copyOf(progIpAddress, progIpAddress.length);
    }

    public byte[] getProgSubnetMask() {
        return Arrays.copyOf(progSubnetMask, progSubnetMask.length);
    }

    public int getProgPort() {
        return progPort;
    }

    public boolean isDhcpEnabled() {
        return dhcpEnabled;
    }

    public byte[] getProgDefaultGateway() {
        return Arrays.copyOf(progDefaultGateway, progDefaultGateway.length);
    }

    public byte[] serializeArtIpProgReplyPacket() {
        byte[] packet = new byte[PACKET_LENGTH];
        int offset = 0;

        offset = writeBytes(packet, offset, ARTNET_ID);
        offset = writeU16LE(packet, offset, opCode.getValue());
        offset = writeU16BE(packet, offset, protocolVersion);
        offset = writeZeroBytes(packet, offset, 4);
        offset = writeBytes(packet, offset, progIpAddress);
        offset = writeBytes(packet, offset, progSubnetMask);
        offset = writeU16BE(packet, offset, progPort);
        offset = writeU8(packet, offset, encodeStatus());
        offset = writeU8(packet, offset, 0);
        offset = writeBytes(packet, offset, progDefaultGateway);
        offset = writeZeroBytes(packet, offset, 2);

        if (offset != packet.length) {
            throw new IllegalStateException("Lunghezza ArtIpProgReply non valida: " + offset);
        }

        return packet;
    }

    private int encodeStatus() {
        int status = 0;
        if (dhcpEnabled) {
            status |= 1 << 6;
        }
        return status;
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

    private static String formatIpv4(byte[] value) {
        return (value[0] & 0xFF) + "."
                + (value[1] & 0xFF) + "."
                + (value[2] & 0xFF) + "."
                + (value[3] & 0xFF);
    }

    @Override
    public String toString() {
        return "ArtIpProgReplyPacket{" +
                "protocolVersion=" + protocolVersion +
                ", progIpAddress=" + formatIpv4(progIpAddress) +
                ", progSubnetMask=" + formatIpv4(progSubnetMask) +
                ", progPort=" + progPort +
                ", dhcpEnabled=" + dhcpEnabled +
                ", progDefaultGateway=" + formatIpv4(progDefaultGateway) +
                '}';
    }
}

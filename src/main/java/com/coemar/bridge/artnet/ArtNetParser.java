package com.coemar.bridge.artnet;

import com.coemar.bridge.artnet.codes.ArtNetOpCode;
import com.coemar.bridge.model.Packet;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDmxPacket;
import com.coemar.bridge.model.artnet.fields.ArtPollFlags;
import com.coemar.bridge.model.artnet.packets.incoming.ArtPollPacket;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.coemar.bridge.util.BinaryParseUtils.*;

public class ArtNetParser {

    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    public static boolean looksLikeArtNet(byte[] data, int length) {
        if (length < 10) return false;
        return Arrays.equals(Arrays.copyOfRange(data, 0, 8), ARTNET_ID);
    }

    public static Packet parse(byte[] data, int length) {

        require(length >= 10, "Pacchetto Art-Net troppo corto");

        ArtNetOpCode opCode = ArtNetOpCode.fromValue(u16le(data, 8));

        switch (Objects.requireNonNull(opCode)) {

            case OpOutput:
                return parseArtDmx(data, length);
            case OpPoll:
                return parseArtPollPacket(data, length);
            case OpPollReply:
            case OpDiagData:
                break;
            default:
                break;
        }

        throw new IllegalArgumentException("OpCode Art-Net non supportato: 0x" + Integer.toHexString(opCode.getValue()));

    }

    private static ArtPollPacket parseArtPollPacket(byte[] data, int length) {
        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        ArtPollFlags flags = new ArtPollFlags(((byte) u8(data, 12)));
        int diagPriority = u8(data, 13);
        int targetPortAddressTop = u16be(data, 14);
        int targetPortAddressBottom = u16be(data, 16);
        int estaManufacturerCode = u16be(data, 18);
        int oemCode = u16be(data, 20);

        return new ArtPollPacket(opCode, protocolVersion, flags, diagPriority, targetPortAddressTop, targetPortAddressBottom, estaManufacturerCode, oemCode);
    }

    public static ArtDmxPacket parseArtDmx(byte[] data, int lengthData) {

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int sequence = u8(data, 12);
        int physical = u8(data, 13);
        int subUni = u8(data, 14);
        int net = u8(data, 15);
        int portAddress = ((net & 0x7F) << 8) | subUni;
        int length = u16be(data, 16);

        require(length >= 2 && length <= 512, "Length ArtDmx fuori range");
        require(lengthData >= 18 + length, "Payload ArtDmx incompleto");

        byte[]dmxData = Arrays.copyOfRange(data, 18, 18 + length);

        return new ArtDmxPacket( opCode,  protocolVersion,  sequence,  physical,  subUni,  net,  portAddress,  length,  dmxData);
    }


}

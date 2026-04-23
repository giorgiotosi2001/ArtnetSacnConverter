package com.coemar.bridge.artnet;

import com.coemar.bridge.model.Packet;
import com.coemar.bridge.model.artnet.ArtDmxPacket;
import com.coemar.bridge.model.LightingPacket;
import com.coemar.bridge.parser.PacketParser;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ArtNetParser extends PacketParser {

    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    public static boolean looksLikeArtNet(byte[] data, int length) {
        if (length < 10) return false;
        return Arrays.equals(Arrays.copyOfRange(data, 0, 8), ARTNET_ID);
    }

    public static Packet parse(byte[] data, int length) {

        require(length >= 18, "Pacchetto Art-Net troppo corto");

        int opCode = u16le(data, 8);

        if (opCode == 0x5000) {
            return parseArtDmx(data, length);
        }

        throw new IllegalArgumentException("OpCode Art-Net non supportato: 0x" + Integer.toHexString(opCode));

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

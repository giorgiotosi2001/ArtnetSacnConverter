package com.coemar.bridge.artnet;

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

    public static LightingPacket parse(byte[] data, int length) {

        require(length >= 18, "Pacchetto Art-Net troppo corto");

        int opCode = u16le(data, 8);

        if (opCode == 0x5000) {
            return parseArtDmx(data, length);
        }

        throw new IllegalArgumentException("OpCode Art-Net non supportato: 0x" + Integer.toHexString(opCode));

    }

    public static ArtDmxPacket parseArtDmx(byte[] data, int length) {

        ArtDmxPacket p = new ArtDmxPacket();
        p.opCode = u16le(data, 8);
        p.protocolVersion = u16be(data, 10);
        p.sequence = u8(data, 12);
        p.physical = u8(data, 13);
        p.subUni = u8(data, 14);
        p.net = u8(data, 15);
        p.portAddress = ((p.net & 0x7F) << 8) | p.subUni;
        p.length = u16be(data, 16);

        require(p.length >= 2 && p.length <= 512, "Length ArtDmx fuori range");
        require(length >= 18 + p.length, "Payload ArtDmx incompleto");

        p.dmxData = Arrays.copyOfRange(data, 18, 18 + p.length);
        return p;
    }


}

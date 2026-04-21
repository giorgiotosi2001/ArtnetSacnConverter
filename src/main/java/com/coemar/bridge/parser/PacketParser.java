package com.coemar.bridge.parser;

import com.coemar.bridge.artnet.ArtNetParser;
import com.coemar.bridge.sacn.SacnParser;
import com.coemar.bridge.model.LightingPacket;

public class PacketParser {

    static LightingPacket parse(byte[] data, int length) {
        if (ArtNetParser.looksLikeArtNet(data, length)) {
            return ArtNetParser.parse(data, length);
        }
        if (SacnParser.looksLikeSacn(data, length)) {
            return SacnParser.parse(data, length);
        }
        throw new IllegalArgumentException("Pacchetto non riconosciuto");
    }

    public static int u8(byte[] data, int off) {
        return data[off] & 0xFF;
    }

    public static int u16be(byte[] data, int off) {
        return ((data[off] & 0xFF) << 8) | (data[off + 1] & 0xFF);
    }

    public static int u16le(byte[] data, int off) {
        return (data[off] & 0xFF) | ((data[off + 1] & 0xFF) << 8);
    }

    public static int u32be( byte[] data, int off) {
        return ((data[off] & 0xFF) << 24) | ((data[off + 1] & 0xFF) << 16) | ((data[off + 2] & 0xFF) << 8) | (data[off + 3] & 0xFF);
    }

    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}



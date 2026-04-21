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
}



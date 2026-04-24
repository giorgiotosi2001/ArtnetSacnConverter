package com.coemar.bridge;

import com.coemar.bridge.artnet.ArtNetParser;
import com.coemar.bridge.model.Packet;
import com.coemar.bridge.sacn.SacnParser;

public class PacketParser {

    static Packet parse(byte[] data, int length) {
        if (ArtNetParser.looksLikeArtNet(data, length)) {
            return ArtNetParser.parse(data, length);
        }
        if (SacnParser.looksLikeSacn(data, length)) {
            return SacnParser.parse(data, length);
        }
        throw new IllegalArgumentException("Pacchetto non riconosciuto");
    }
}



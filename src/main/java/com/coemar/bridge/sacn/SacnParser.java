package com.coemar.bridge.sacn;

import com.coemar.bridge.model.ArtDmxPacket;
import com.coemar.bridge.model.LightingPacket;
import com.coemar.bridge.model.SacnDataPacket;

public class SacnParser {
    public static LightingPacket parse(byte[] data, int length) {
        return new SacnDataPacket();
    }

    public static boolean looksLikeSacn(byte[] data, int length) {
        return false;
    }
}

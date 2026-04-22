package com.coemar.bridge.sacn;

import com.coemar.bridge.model.*;
import com.coemar.bridge.parser.PacketParser;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SacnParser extends PacketParser {

    private static final byte[] ACN_PID = new byte[]{
            0x41, 0x53, 0x43, 0x2d, 0x45, 0x31, 0x2e, 0x31, 0x37, 0x00, 0x00, 0x00
    };

    // ROOT VECTOR
    private static final long ROOT_VECTOR_DATA = 0x00000004L;
    private static final long ROOT_VECTOR_EXTENDED = 0x00000008L; // usato per sincronizzazione e discovery

    // FRAMING VECTOR
    private static final long FRAMING_VECTOR_DATA = 0x00000002L;
    private static final long FRAMING_VECTOR_EXTENDED_SYNC = 0x00000001L;
    private static final long FRAMING_VECTOR_EXTENDED_DISCOVERY = 0x00000002L;

    // VECTOR_UNIVERSE_DISCOVERY_UNIVERSE_LIST
    private static final long UNIVERSE_VECTOR_DISCOVERY_UNIVERSE_LIST = 0x00000001L;

    public static Packet parse(byte[] data, int length) {
        require(length >= 126, "Pacchetto sACN troppo corto");
        long rootVector = u32be(data, 18);
        long framingVector = u32be(data, 40);
        if (rootVector == ROOT_VECTOR_DATA) {
            // va bene

            if (framingVector == FRAMING_VECTOR_DATA) {
                return parseDataPacket(data, length);
            } else {
                throw new IllegalArgumentException("Pacchetto sACN con Root Vector o Framing Vector estesi non supportato");
            }
        } else{
            if (rootVector == ROOT_VECTOR_EXTENDED) {
                if (framingVector == FRAMING_VECTOR_EXTENDED_SYNC) {
                    return parseSyncPacket(data, length);
                } else if (framingVector == FRAMING_VECTOR_EXTENDED_DISCOVERY) {
                    long universeVector = u32be(data, 114);
                    if (universeVector == UNIVERSE_VECTOR_DISCOVERY_UNIVERSE_LIST) {
                        return parseDiscoveryPacket(data, length);
                    } else {
                        throw new IllegalArgumentException("Pacchetto sACN con Universe Vector non supportato: 0x" + Long.toHexString(universeVector));
                    }
                }
            }

        }
            throw new IllegalArgumentException("Pacchetto sACN con Root Vector o Framing Vector non supportato");

}

private static Packet parseDiscoveryPacket(byte[] data, int length) {
    return null; // TODO
}

private static Packet parseSyncPacket(byte[] data, int length) {
    SacnSyncPacket p = new SacnSyncPacket();

    p.preambleSize = u16be(data, 0);
    p.postambleSize = u16be(data, 2);
    p.rootVector = u32be(data, 18);
    p.cid = Arrays.copyOfRange(data, 22, 38);
    p.framingVector = u32be(data, 40);
    p.sequenceNumber = u8(data, 44);
    p.synchronizationAddress = u16be(data, 45);


    return p;
}

public static SacnDataPacket parseDataPacket(byte[] data, int length) {
    SacnDataPacket p = new SacnDataPacket();

    p.preambleSize = u16be(data, 0);
    p.postambleSize = u16be(data, 2);
    p.rootVector = u32be(data, 18);
    p.cid = Arrays.copyOfRange(data, 22, 38);

    p.framingVector = u32be(data, 40);
    p.sourceName = readNullTerminatedUtf8(data, 44, 64);
    p.priority = u8(data, 108);
    p.synchronizationAddress = u16be(data, 109);
    p.sequenceNumber = u8(data, 111);
    p.options = u8(data, 112);
    p.universe = u16be(data, 113);

    p.dmpVector = u8(data, 117);
    p.addressTypeAndDataType = u8(data, 118);
    p.firstPropertyAddress = u16be(data, 119);
    p.addressIncrement = u16be(data, 121);
    p.propertyValueCount = u16be(data, 123);

    require(p.universe >= 1 && p.universe <= 63999, "Universe sACN fuori range");
    require(p.firstPropertyAddress == 0x0000, "First Property Address non valido");
    require(p.addressIncrement == 0x0001, "Address Increment non valido");
    require(p.propertyValueCount >= 1 && p.propertyValueCount <= 513, "Property Value Count non valido");

    int valuesOffset = 125;
    require(length >= valuesOffset + p.propertyValueCount, "Payload DMP incompleto");

    p.startCode = u8(data, valuesOffset);
    p.dmxData = Arrays.copyOfRange(data, valuesOffset + 1, valuesOffset + p.propertyValueCount);

    return p;
}

static String readNullTerminatedUtf8(byte[] data, int offset, int maxLen) {
    int end = offset;
    int limit = offset + maxLen;
    while (end < limit && data[end] != 0) end++;
    return new String(data, offset, end - offset, StandardCharsets.UTF_8);
}

public static boolean looksLikeSacn(byte[] data, int length) {
    if (length < 38) return false;
    if (u16be(data, 0) != 0x0010) return false;
    if (u16be(data, 2) != 0x0000) return false;
    return Arrays.equals(Arrays.copyOfRange(data, 4, 16), ACN_PID);
}

}

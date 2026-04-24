package com.coemar.bridge.sacn;

import com.coemar.bridge.model.*;
import com.coemar.bridge.model.sacn.packets.SacnDataPacket;
import com.coemar.bridge.model.sacn.packets.SacnDiscoveryPacket;
import com.coemar.bridge.model.sacn.packets.SacnSyncPacket;

import java.util.Arrays;

import static com.coemar.bridge.util.BinaryParseUtils.*;

public class SacnParser {

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

    int preambleSize = u16be(data, 0);
    int postambleSize = u16be(data, 2);
    int rootFlagsAndLength = u16be(data, 16);
    int rootPduLength = rootFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)
    int rootVector = u32be(data, 18);
    byte[] cid = Arrays.copyOfRange(data, 22, 38);

    int framingFlagsAndLength = u16be(data, 38);
    int framingPduLength = framingFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)

    int framingVector = u32be(data, 40);
    String sourceName = readNullTerminatedUtf8(data, 44, 64);

    int discoveryFlagsAndLength = u16be(data, 112);
    int discoveryPduLength = discoveryFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)

    int discoveryVector = u32be(data, 114);
    int page = u8(data, 118);
    int lastPage = u8(data, 119);

    int universesCount = (length - 120) / 2;
    if(universesCount != (discoveryPduLength - 8) / 2) {
        throw new IllegalArgumentException("Pacchetto sACN con lunghezza Discovery PDU non coerente con il numero di universi");
    }
    int[] universes = new int[universesCount];
    for (int i = 0; i < universesCount; i++) {
        universes[i] = u16be(data, 120 + i * 2);
    }

    return new SacnDiscoveryPacket(rootFlagsAndLength, framingFlagsAndLength, discoveryFlagsAndLength, rootPduLength, framingPduLength, discoveryPduLength, preambleSize, postambleSize, cid, rootVector, framingVector, sourceName, discoveryVector, page, lastPage, universes);
}

private static Packet parseSyncPacket(byte[] data, int length) {


    int preambleSize = u16be(data, 0);
    int postambleSize = u16be(data, 2);
    int rootFlagsAndLength = u16be(data, 16);
    int rootPduLength = rootFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)
    long rootVector = u32be(data, 18);
    byte[] cid = Arrays.copyOfRange(data, 22, 38);
    int framingFlagsAndLength = u16be(data, 38);
    int framingPduLength = framingFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)
    long framingVector = u32be(data, 40);
    int sequenceNumber = u8(data, 44);
    int synchronizationAddress = u16be(data, 45);

    return new SacnSyncPacket(rootFlagsAndLength, framingFlagsAndLength, rootPduLength, framingPduLength, preambleSize, postambleSize, cid, rootVector, framingVector, sequenceNumber, synchronizationAddress);
}

public static SacnDataPacket parseDataPacket(byte[] data, int length) {

    int preambleSize = u16be(data, 0);
    int postambleSize = u16be(data, 2);
    int rootFlagsAndLength = u16be(data, 16);
    int rootPduLength = rootFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)
    long rootVector = u32be(data, 18);
    byte[] cid = Arrays.copyOfRange(data, 22, 38);

    int framingFlagsAndLength = u16be(data, 38);
    int framingPduLength = framingFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)
    long framingVector = u32be(data, 40);
    String sourceName = readNullTerminatedUtf8(data, 44, 64);
    int priority = u8(data, 108);
    int synchronizationAddress = u16be(data, 109);
    int sequenceNumber = u8(data, 111);
    int options = u8(data, 112);
    int universe = u16be(data, 113);

    int dmpFlagsAndLength = u16be(data, 115);
    int dmpPduLength = dmpFlagsAndLength & 0x0FFF; // 12 bit meno significativi (il resto sono flags)
    int dmpVector = u8(data, 117);
    int addressTypeAndDataType = u8(data, 118);
    int firstPropertyAddress = u16be(data, 119);
    int addressIncrement = u16be(data, 121);
    int propertyValueCount = u16be(data, 123);

    require(universe >= 1 && universe <= 63999, "Universe sACN fuori range");
    require(firstPropertyAddress == 0x0000, "First Property Address non valido");
    require(addressIncrement == 0x0001, "Address Increment non valido");
    require(propertyValueCount >= 1 && propertyValueCount <= 513, "Property Value Count non valido");

    int valuesOffset = 125;
    require(length >= valuesOffset + propertyValueCount, "Payload DMP incompleto");

    int startCode = u8(data, valuesOffset);
    byte[] dmxData = Arrays.copyOfRange(data, valuesOffset + 1, valuesOffset + propertyValueCount);

    return new SacnDataPacket(rootFlagsAndLength, framingFlagsAndLength, dmpFlagsAndLength, rootPduLength, framingPduLength, dmpPduLength, preambleSize, postambleSize, cid, rootVector, framingVector, sourceName, priority, synchronizationAddress, sequenceNumber, options, universe, dmpVector, addressTypeAndDataType, firstPropertyAddress, addressIncrement, propertyValueCount, startCode, dmxData);
}

public static boolean looksLikeSacn(byte[] data, int length) {
    if (length < 38) return false;
    if (u16be(data, 0) != 0x0010) return false;
    if (u16be(data, 2) != 0x0000) return false;
    return Arrays.equals(Arrays.copyOfRange(data, 4, 16), ACN_PID);
}

}

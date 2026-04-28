package com.coemar.bridge.rdm;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RdmDiscoveryResponseTest {

    @Test
    void parseValidDiscoveryResponse() {
        byte[] uid = new byte[]{
                0x43, 0x4D, 0x49, 0x4D, 0x4D, 0x49
        };
        byte[] raw = buildDiscoveryResponse(uid);

        Optional<RdmDiscoveryResponse> parsed = RdmDiscoveryResponse.parse(raw);

        assertTrue(parsed.isPresent());
        assertArrayEquals(uid, parsed.get().getUid().toByteArray());
        assertEquals(0x01BC, parsed.get().getChecksum());
    }

    @Test
    void parseCorruptedDiscoveryResponseFails() {
        byte[] raw = buildDiscoveryResponse(new byte[]{
                0x43, 0x4D, 0x49, 0x4D, 0x4D, 0x49
        });
        raw[9] = 0x00;

        Optional<RdmDiscoveryResponse> parsed = RdmDiscoveryResponse.parse(raw);

        assertTrue(parsed.isEmpty());
    }

    private static byte[] buildDiscoveryResponse(byte[] uid) {
        byte[] raw = new byte[24];
        for (int i = 0; i < 7; i++) {
            raw[i] = (byte) 0xFE;
        }
        raw[7] = (byte) 0xAA;

        encodeMaskedBytes(uid, raw, 8);

        int checksum = 0;
        for (byte uidByte : uid) {
            checksum = (checksum + (uidByte & 0xFF)) & 0xFFFF;
        }
        encodeMaskedBytes(new byte[]{
                (byte) ((checksum >>> 8) & 0xFF),
                (byte) (checksum & 0xFF)
        }, raw, 20);

        return raw;
    }

    private static void encodeMaskedBytes(byte[] source, byte[] target, int offset) {
        for (int i = 0; i < source.length; i++) {
            int value = source[i] & 0xFF;
            target[offset + (i * 2)] = (byte) (0xAA | value);
            target[offset + (i * 2) + 1] = (byte) (0x55 | value);
        }
    }
}

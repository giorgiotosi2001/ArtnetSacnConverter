package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * <p>Pacchetto trigger per macro, soft-key, show o funzioni vendor-specific.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> controller -&gt; dispositivi Art-Net, in broadcast o unicast.</li>
 *   <li><b>Risposta attesa:</b> nessuna risposta dedicata.</li>
 *   <li><b>Serve per:</b> invocare azioni rapide definite da OEM, key e subKey.</li>
 *   <li><b>Note:</b> il significato del payload dipende da OEM e tipo di trigger.</li>
 * </ul>
 */
public class ArtTriggerPacket implements Packet {

    private static final int DATA_LENGTH = 512;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int filler2;
    private final int oemCode;
    private final int key;
    private final int subKey;
    private final byte[] data;

    public ArtTriggerPacket(int opCode, int protocolVersion, int filler1, int filler2, int oemCode, int key,
                            int subKey, byte[] data) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.filler2 = filler2;
        this.oemCode = oemCode;
        this.key = key;
        this.subKey = subKey;
        this.data = copyFixedLengthBytes(data, DATA_LENGTH, "data");
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getFiller1() {
        return filler1;
    }

    public int getFiller2() {
        return filler2;
    }

    public int getOemCode() {
        return oemCode;
    }

    public boolean isStandardArtNetTrigger() {
        return oemCode == 0xFFFF;
    }

    public boolean isManufacturerSpecificTrigger() {
        return !isStandardArtNetTrigger();
    }

    public int getKey() {
        return key;
    }

    public KeyType getKeyType() {
        if (isManufacturerSpecificTrigger()) {
            return KeyType.ManufacturerSpecific;
        }
        return KeyType.fromValue(key);
    }

    public int getSubKey() {
        return subKey;
    }

    public char getSubKeyAsAsciiChar() {
        if (getKeyType() != KeyType.KeyAscii) {
            throw new IllegalStateException("SubKey ASCII valido solo per trigger standard con KeyAscii");
        }
        return (char) (subKey & 0xFF);
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    private static byte[] copyFixedLengthBytes(byte[] value, int expectedLength, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " non puo essere null");
        }
        if (value.length != expectedLength) {
            throw new IllegalArgumentException(fieldName + " deve essere lungo " + expectedLength + " byte");
        }
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public String toString() {
        return "ArtTriggerPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", filler2=" + filler2 +
                ", oemCode=0x" + Integer.toHexString(oemCode) +
                ", standardArtNetTrigger=" + isStandardArtNetTrigger() +
                ", manufacturerSpecificTrigger=" + isManufacturerSpecificTrigger() +
                ", key=" + key +
                ", keyType=" + getKeyType() +
                ", subKey=" + subKey +
                '}';
    }

    public enum KeyType {
        KeyAscii(0),
        KeyMacro(1),
        KeySoft(2),
        KeyShow(3),
        ManufacturerSpecific(-2),
        Undefined(-1);

        private final int value;

        KeyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static KeyType fromValue(int value) {
            for (KeyType keyType : values()) {
                if (keyType.value == value) {
                    return keyType;
                }
            }
            return Undefined;
        }
    }
}

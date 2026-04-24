package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * <p>Richiesta di dati descrittivi del dispositivo, ad esempio URL o informazioni prodotto.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> controller -&gt; nodo/media server, in unicast.</li>
 *   <li><b>Risposta attesa:</b> {@code ArtDataReplyPacket}.</li>
 *   <li><b>Serve per:</b> interrogare dati identificati da un request code della tabella Art-Net.</li>
 *   <li><b>Non fa:</b> non configura il nodo e non trasporta DMX/RDM.</li>
 * </ul>
 */
public class ArtDataRequestPacket implements Packet {

    private static final int SPARE_LENGTH = 22;

    private final int opCode;
    private final int protocolVersion;
    private final int estaManufacturerCode;
    private final int oemCode;
    private final int requestCode;
    private final byte[] spare;

    public ArtDataRequestPacket(int opCode, int protocolVersion, int estaManufacturerCode, int oemCode, int requestCode,
                                byte[] spare) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.estaManufacturerCode = estaManufacturerCode;
        this.oemCode = oemCode;
        this.requestCode = requestCode;
        this.spare = copyFixedLengthBytes(spare, SPARE_LENGTH, "spare");
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getEstaManufacturerCode() {
        return estaManufacturerCode;
    }

    public int getOemCode() {
        return oemCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public byte[] getSpare() {
        return Arrays.copyOf(spare, spare.length);
    }

    public DataRequestCode getRequestType() {
        return DataRequestCode.fromValue(requestCode);
    }

    public boolean isManufacturerSpecificRequest() {
        return requestCode >= 0x8000 && requestCode <= 0xFFFF;
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
        return "ArtDataRequest{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", estaManufacturerCode=" + estaManufacturerCode +
                ", oemCode=" + oemCode +
                ", requestCode=0x" + Integer.toHexString(requestCode) +
                ", requestType=" + getRequestType() +
                ", manufacturerSpecificRequest=" + isManufacturerSpecificRequest() +
                '}';
    }

    public enum DataRequestCode {
        DrPoll(0x0000),
        DrUrlProduct(0x0001),
        DrUrlUserGuide(0x0002),
        DrUrlSupport(0x0003),
        DrUrlPersUdr(0x0004),
        DrUrlPersGdtf(0x0005),
        DrManSpec(-1),
        Unknown(-2);

        private final int value;

        DataRequestCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static DataRequestCode fromValue(int value) {
            if (value >= 0x8000 && value <= 0xFFFF) {
                return DrManSpec;
            }

            for (DataRequestCode code : values()) {
                if (code.value == value) {
                    return code;
                }
            }
            return Unknown;
        }
    }
}

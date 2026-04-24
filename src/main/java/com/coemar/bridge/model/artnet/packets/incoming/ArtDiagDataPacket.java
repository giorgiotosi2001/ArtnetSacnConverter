package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Messaggio di diagnostica testuale destinato a tool o controller Art-Net.
 *
 * - Direzione tipica: nodo/controller/media server -> destinazione definita dalla strategia di {@code ArtPollPacket}.
 * - Risposta attesa: nessuna risposta dedicata.
 * - Serve per: inviare testo diagnostico con priorita e porta logica associata.
 * - Non fa: non trasporta livelli DMX e non cambia configurazioni del nodo.
 */
public class ArtDiagDataPacket implements Packet {

    private static final int MAX_DATA_LENGTH = 512;

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int diagPriority;
    private final int logicalPort;
    private final int filler3;
    private final int textLength;
    private final byte[] data;
    private final String diagnosticText;

    public ArtDiagDataPacket(int opCode, int protocolVersion, int filler1, int diagPriority, int logicalPort,
                             int filler3, int textLength, byte[] data) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.diagPriority = diagPriority;
        this.logicalPort = logicalPort;
        this.filler3 = filler3;
        this.data = copyData(data);
        this.textLength = textLength;
        this.diagnosticText = decodeDiagnosticText(this.data);
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

    public int getDiagPriority() {
        return diagPriority;
    }

    public PriorityCode getPriorityCode() {
        return PriorityCode.fromValue(diagPriority);
    }

    public int getLogicalPort() {
        return logicalPort;
    }

    public boolean isGeneralMessage() {
        return logicalPort == 0;
    }

    public int getFiller3() {
        return filler3;
    }

    public int getTextLength() {
        return textLength;
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public String getDiagnosticText() {
        return diagnosticText;
    }

    private static byte[] copyData(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("data non puo essere null");
        }
        if (value.length > MAX_DATA_LENGTH) {
            throw new IllegalArgumentException("data troppo lunga per ArtDiagData");
        }
        return Arrays.copyOf(value, value.length);
    }

    private static String decodeDiagnosticText(byte[] value) {
        int end = 0;
        while (end < value.length && value[end] != 0) {
            end++;
        }
        return new String(value, 0, end, StandardCharsets.US_ASCII);
    }

    @Override
    public String toString() {
        return "ArtDiagDataPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", diagPriority=0x" + Integer.toHexString(diagPriority) +
                ", priorityCode=" + getPriorityCode() +
                ", logicalPort=" + logicalPort +
                ", generalMessage=" + isGeneralMessage() +
                ", filler3=" + filler3 +
                ", textLength=" + textLength +
                ", diagnosticText='" + diagnosticText + '\'' +
                '}';
    }

    public enum PriorityCode {
        DpLow(0x10),
        DpMed(0x40),
        DpHigh(0x80),
        DpCritical(0xE0),
        DpVolatile(0xF0),
        Unknown(-1);

        private final int value;

        PriorityCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PriorityCode fromValue(int value) {
            for (PriorityCode code : values()) {
                if (code.value == value) {
                    return code;
                }
            }
            return Unknown;
        }
    }
}

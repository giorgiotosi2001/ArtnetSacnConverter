package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.LightingPacket;

import java.util.Arrays;

/**
 * <p>Pacchetto dati DMX con start code non zero, escluso RDM.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> controller/nodo -&gt; nodo/controller, solo in unicast.</li>
 *   <li><b>Risposta attesa:</b> nessuna risposta dedicata.</li>
 *   <li><b>Serve per:</b> trasportare alternate start code mantenendo lo stesso modello di routing di {@code ArtDmxPacket}.</li>
 *   <li><b>Non fa:</b> non accetta start code {@code 0x00} e non trasporta RDM.</li>
 * </ul>
 */
public class ArtNzsPacket implements LightingPacket {

    private final int opCode;
    private final int protocolVersion;
    private final int sequence;
    private final int startCode;
    private final int subUni;
    private final int net;
    private final int portAddress;
    private final int length;
    private final byte[] data;

    public ArtNzsPacket(int opCode, int protocolVersion, int sequence, int startCode, int subUni, int net,
                        int portAddress, int length, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data non puo essere null");
        }
        if (length != data.length) {
            throw new IllegalArgumentException("length e data.length non coincidono");
        }

        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.sequence = sequence;
        this.startCode = startCode;
        this.subUni = subUni;
        this.net = net;
        this.portAddress = portAddress;
        this.length = length;
        this.data = Arrays.copyOf(data, data.length);
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getSequence() {
        return sequence;
    }

    public int getStartCode() {
        return startCode;
    }

    public int getSubUni() {
        return subUni;
    }

    public int getNet() {
        return net;
    }

    public int getPortAddress() {
        return portAddress;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public String toString() {
        return "ArtNzsPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", sequence=" + sequence +
                ", startCode=" + startCode +
                ", subUni=" + subUni +
                ", net=" + net +
                ", portAddress=" + portAddress +
                ", length=" + length +
                ", data=" + formatBytes(data) +
                '}';
    }

    private static String formatBytes(byte[] value) {
        if (value == null) {
            return "null";
        }

        StringBuilder builder = new StringBuilder();
        for (byte b : value) {
            builder.append(b & 0xFF).append(" ");
        }
        return builder.toString();
    }
}

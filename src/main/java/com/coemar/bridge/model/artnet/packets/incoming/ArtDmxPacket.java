package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.LightingPacket;

import java.util.Arrays;

/**
 * Pacchetto dati DMX512 standard per il trasferimento dei livelli di un universo.
 *
 * - Direzione tipica: controller/nodo -> nodo/controller, solo in unicast verso i subscriber dell'universo.
 * - Risposta attesa: nessuna; il dato viene usato direttamente in output o bufferizzato per {@code ArtSyncPacket}.
 * - Serve per: trasportare frame DMX512 con sequence, physical port e Port-Address.
 * - Non fa: non e consentito in broadcast e non e un pacchetto di configurazione.
 */
public class ArtDmxPacket implements LightingPacket {

    private final int opCode;

    private final int protocolVersion;

    private final int sequence;

    private final int physical;

    private final int subUni;

    private final int net;

    private final int portAddress; // net + subuni(subnet(4bit alti) + universe(4 bit bassi))
    private final int length;

    private final byte[] dmxData;

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getSequence() {
        return sequence;
    }

    public int getPhysical() {
        return physical;
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

    public byte[] getDmxData() {
        return Arrays.copyOf(dmxData, dmxData.length);
    }

    public ArtDmxPacket(int opCode, int protocolVersion, int sequence, int physical, int subUni, int net, int portAddress, int length, byte[] dmxData) {
        if (dmxData == null) {
            throw new IllegalArgumentException("dmxData non puo essere null");
        }
        if (length != dmxData.length) {
            throw new IllegalArgumentException("length e dmxData.length non coincidono");
        }

        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.sequence = sequence;
        this.physical = physical;
        this.subUni = subUni;
        this.net = net;
        this.portAddress = portAddress;
        this.length = length;
        this.dmxData = Arrays.copyOf(dmxData, dmxData.length);
    }

    @Override
    public String toString() {
        return "ArtDmxPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", sequence=" + sequence +
                ", physical=" + physical +
                ", subUni=" + subUni +
                ", net=" + net +
                ", portAddress=" + portAddress +
                ", length=" + length +
                ", dmxData=" + scrivoInDmxIByte(dmxData) +
                '}';
    }

    public String scrivoInDmxIByte(byte[] dmxData) {
        if (dmxData == null) return "null";

        StringBuilder s = new StringBuilder();

        for (byte b : dmxData) {
            s.append(b & 0xFF).append(" ");
        }

        return s.toString();
    }
}

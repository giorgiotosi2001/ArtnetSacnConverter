package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

/**
 * Trigger di sincronizzazione per emettere insieme ArtDmx gia ricevuti.
 *
 * - Direzione tipica: controller -> nodi, in directed broadcast.
 * - Risposta attesa: nessuna risposta dedicata.
 * - Serve per: forzare l'output sincrono di piu universi in applicazioni video o media-wall.
 * - Note: mette il nodo in modalita sincrona; senza nuovi sync il nodo torna poi in modalita non sincrona.
 */
public class ArtSyncPacket implements Packet {

    private final int opCode;
    private final int protocolVersion;
    private final int aux1;
    private final int aux2;

    public ArtSyncPacket(int opCode, int protocolVersion, int aux1, int aux2) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.aux1 = aux1;
        this.aux2 = aux2;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getAux1() {
        return aux1;
    }

    public int getAux2() {
        return aux2;
    }

    public boolean isAuxDataZeroed() {
        return aux1 == 0 && aux2 == 0;
    }

    @Override
    public String toString() {
        return "ArtSyncPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", aux1=" + aux1 +
                ", aux2=" + aux2 +
                ", auxDataZeroed=" + isAuxDataZeroed() +
                '}';
    }
}

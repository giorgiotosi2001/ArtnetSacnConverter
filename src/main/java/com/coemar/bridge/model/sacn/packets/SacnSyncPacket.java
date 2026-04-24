package com.coemar.bridge.model.sacn.packets;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * Pacchetto sACN di sincronizzazione universi, privo di payload DMX.
 *
 * - Direzione tipica: source -> receiver in multicast o unicast.
 * - Risposta attesa: nessuna risposta dedicata.
 * - Serve per: triggerare l'aggiornamento simultaneo dei {@code SacnDataPacket} associati a un synchronization address.
 * - Note: trasporta solo informazioni di sync; l'universo di sincronizzazione puo esistere anche come normale universo dati.
 */
public class SacnSyncPacket implements Packet {

    private final int rootFlagsAndLength;
    private final int framingFlagsAndLength;
    private final int rootPduLength;
    private final int framingPduLength;
    private final int preambleSize;
    private final int postambleSize;
    private final byte[] cid;                 // 16 byte
    private final long rootVector;
    private final long framingVector;
    private final int sequenceNumber;
    private final int synchronizationAddress;

    public SacnSyncPacket(int rootFlagsAndLength, int framingFlagsAndLength, int rootPduLength, int framingPduLength, int preambleSize, int postambleSize, byte[] cid, long rootVector, long framingVector, int sequenceNumber, int synchronizationAddress) {
        this.rootFlagsAndLength = rootFlagsAndLength;
        this.framingFlagsAndLength = framingFlagsAndLength;
        this.rootPduLength = rootPduLength;
        this.framingPduLength = framingPduLength;
        this.preambleSize = preambleSize;
        this.postambleSize = postambleSize;
        this.cid = cid;
        this.rootVector = rootVector;
        this.framingVector = framingVector;
        this.sequenceNumber = sequenceNumber;
        this.synchronizationAddress = synchronizationAddress;
    }

    public int getRootFlagsAndLength() {
        return rootFlagsAndLength;
    }

    public int getFramingFlagsAndLength() {
        return framingFlagsAndLength;
    }

    public int getRootPduLength() {
        return rootPduLength;
    }

    public int getFramingPduLength() {
        return framingPduLength;
    }

    public int getPreambleSize() {
        return preambleSize;
    }

    public int getPostambleSize() {
        return postambleSize;
    }

    public byte[] getCid() {
        return Arrays.copyOf(cid, cid.length);
    }

    public long getRootVector() {
        return rootVector;
    }

    public long getFramingVector() {
        return framingVector;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getSynchronizationAddress() {
        return synchronizationAddress;
    }

    @Override
    public String toString() {
        return "SacnSyncPacket{" +
                "cid=" + Arrays.toString(cid) +
                ", rootVector=" + rootVector +
                ", framingVector=" + framingVector +
                ", sequenceNumber=" + sequenceNumber +
                ", synchronizationAddress=" + synchronizationAddress +
                '}';
    }
}

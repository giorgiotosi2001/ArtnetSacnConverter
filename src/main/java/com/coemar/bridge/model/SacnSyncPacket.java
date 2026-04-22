package com.coemar.bridge.model;

import java.util.Arrays;

public class SacnSyncPacket implements Packet{

    public int rootFlagsAndLength;
    public int framingFlagsAndLength;
    public int dmpFlagsAndLength;
    //-------------------------------------
    public int rootPduLength;
    public int framingPduLength;
    public int dmpPduLength;
    //-------------------------------------
    public int preambleSize;
    public int postambleSize;
    public byte[] cid;                 // 16 byte
    public long rootVector;


    public long framingVector;
    public int sequenceNumber;
    public int synchronizationAddress;

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

package com.coemar.bridge.model.sacn;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

public class SacnDiscoveryPacket implements Packet {

    public int rootFlagsAndLength;
    public int framingFlagsAndLength;
    public int discoveryFlagsAndLength;
    public int rootPduLength;
    public int framingPduLength;
    public int discoveryPduLength;
    public int preambleSize;
    public int postambleSize;
    public byte[] cid;                 // 16 byte
    public long rootVector;
    public long framingVector;
    public String sourceName;
    public int discoveryVector;
    public int page;
    public int lastPage;
    public int[] universes;

    @Override
    public String toString() {
        return "SacnDiscoveryPacket{" +
                "cid=" + Arrays.toString(cid) +
                ", rootVector=" + rootVector +
                ", framingVector=" + framingVector +
                ", sourceName='" + sourceName + '\'' +
                ", discoveryVector=" + discoveryVector +
                ", page=" + page +
                ", lastPage=" + lastPage +
                ", universes=" + Arrays.toString(universes) +
                '}';
    }
}

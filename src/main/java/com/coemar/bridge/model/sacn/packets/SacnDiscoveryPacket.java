package com.coemar.bridge.model.sacn.packets;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

public class SacnDiscoveryPacket implements Packet {

    private final int rootFlagsAndLength;
    private final int framingFlagsAndLength;
    private final int discoveryFlagsAndLength;
    private final int rootPduLength;
    private final int framingPduLength;
    private final int discoveryPduLength;
    private final int preambleSize;
    private final int postambleSize;
    private final byte[] cid;                 // 16 byte
    private final long rootVector;
    private final long framingVector;
    private final String sourceName;
    private final int discoveryVector;
    private final int page;
    private final int lastPage;
    private final int[] universes;

    public SacnDiscoveryPacket(int rootFlagsAndLength, int framingFlagsAndLength, int discoveryFlagsAndLength, int rootPduLength, int framingPduLength, int discoveryPduLength, int preambleSize, int postambleSize, byte[] cid, long rootVector, long framingVector, String sourceName, int discoveryVector, int page, int lastPage, int[] universes) {
        this.rootFlagsAndLength = rootFlagsAndLength;
        this.framingFlagsAndLength = framingFlagsAndLength;
        this.discoveryFlagsAndLength = discoveryFlagsAndLength;
        this.rootPduLength = rootPduLength;
        this.framingPduLength = framingPduLength;
        this.discoveryPduLength = discoveryPduLength;
        this.preambleSize = preambleSize;
        this.postambleSize = postambleSize;
        this.cid = cid;
        this.rootVector = rootVector;
        this.framingVector = framingVector;
        this.sourceName = sourceName;
        this.discoveryVector = discoveryVector;
        this.page = page;
        this.lastPage = lastPage;
        this.universes = universes;
    }

    public int getRootFlagsAndLength() {
        return rootFlagsAndLength;
    }

    public int getFramingFlagsAndLength() {
        return framingFlagsAndLength;
    }

    public int getDiscoveryFlagsAndLength() {
        return discoveryFlagsAndLength;
    }

    public int getRootPduLength() {
        return rootPduLength;
    }

    public int getFramingPduLength() {
        return framingPduLength;
    }

    public int getDiscoveryPduLength() {
        return discoveryPduLength;
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

    public String getSourceName() {
        return sourceName;
    }

    public int getDiscoveryVector() {
        return discoveryVector;
    }

    public int getPage() {
        return page;
    }

    public int getLastPage() {
        return lastPage;
    }

    public int[] getUniverses() {
        return Arrays.copyOf(universes, universes.length);
    }

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

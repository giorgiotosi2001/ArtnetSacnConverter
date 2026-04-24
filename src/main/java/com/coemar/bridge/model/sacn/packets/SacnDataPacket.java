package com.coemar.bridge.model.sacn.packets;

import com.coemar.bridge.model.LightingPacket;

import java.util.Arrays;

public class SacnDataPacket implements LightingPacket {
    private final int rootFlagsAndLength;
    private final int framingFlagsAndLength;
    private final int dmpFlagsAndLength;
    private final int rootPduLength;
    private final int framingPduLength;
    private final int dmpPduLength;
    private final int preambleSize;
    private final int postambleSize;
    private final byte[] cid;                 // 16 byte
    private final long rootVector;
    private final long framingVector;
    private final String sourceName;
    private final int priority;
    private final int synchronizationAddress;
    private final int sequenceNumber;
    private final int options;
    private final int universe;
    private final int dmpVector;
    private final int addressTypeAndDataType;
    private final int firstPropertyAddress;
    private final int addressIncrement;
    private final int propertyValueCount;
    private final int startCode;
    private final byte[] dmxData;

    public SacnDataPacket(int rootFlagsAndLength, int framingFlagsAndLength, int dmpFlagsAndLength, int rootPduLength, int framingPduLength, int dmpPduLength, int preambleSize, int postambleSize, byte[] cid, long rootVector, long framingVector, String sourceName, int priority, int synchronizationAddress, int sequenceNumber, int options, int universe, int dmpVector, int addressTypeAndDataType, int firstPropertyAddress, int addressIncrement, int propertyValueCount, int startCode, byte[] dmxData) {
        this.rootFlagsAndLength = rootFlagsAndLength;
        this.framingFlagsAndLength = framingFlagsAndLength;
        this.dmpFlagsAndLength = dmpFlagsAndLength;
        this.rootPduLength = rootPduLength;
        this.framingPduLength = framingPduLength;
        this.dmpPduLength = dmpPduLength;
        this.preambleSize = preambleSize;
        this.postambleSize = postambleSize;
        this.cid = cid;
        this.rootVector = rootVector;
        this.framingVector = framingVector;
        this.sourceName = sourceName;
        this.priority = priority;
        this.synchronizationAddress = synchronizationAddress;
        this.sequenceNumber = sequenceNumber;
        this.options = options;
        this.universe = universe;
        this.dmpVector = dmpVector;
        this.addressTypeAndDataType = addressTypeAndDataType;
        this.firstPropertyAddress = firstPropertyAddress;
        this.addressIncrement = addressIncrement;
        this.propertyValueCount = propertyValueCount;
        this.startCode = startCode;
        this.dmxData = dmxData;
    }

    public int getRootFlagsAndLength() {
        return rootFlagsAndLength;
    }

    public int getFramingFlagsAndLength() {
        return framingFlagsAndLength;
    }

    public int getDmpFlagsAndLength() {
        return dmpFlagsAndLength;
    }

    public int getRootPduLength() {
        return rootPduLength;
    }

    public int getFramingPduLength() {
        return framingPduLength;
    }

    public int getDmpPduLength() {
        return dmpPduLength;
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

    public int getPriority() {
        return priority;
    }

    public int getSynchronizationAddress() {
        return synchronizationAddress;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getOptions() {
        return options;
    }

    public int getUniverse() {
        return universe;
    }

    public int getDmpVector() {
        return dmpVector;
    }

    public int getAddressTypeAndDataType() {
        return addressTypeAndDataType;
    }

    public int getFirstPropertyAddress() {
        return firstPropertyAddress;
    }

    public int getAddressIncrement() {
        return addressIncrement;
    }

    public int getPropertyValueCount() {
        return propertyValueCount;
    }

    public int getStartCode() {
        return startCode;
    }

    public byte[] getDmxData() {
        return Arrays.copyOf(dmxData, dmxData.length);
    }

    @Override
    public String toString() {
        return "SacnDataPacket{" +
                "preambleSize=" + preambleSize +
                ", postambleSize=" + postambleSize +
                ", cid=" + scrivoInEsadecimaleIByte(cid) +
                ", rootVector=" + rootVector +
                ", framingVector=" + framingVector +
                ", sourceName='" + sourceName + '\'' +
                ", priority=" + priority +
                ", synchronizationAddress=" + synchronizationAddress +
                ", sequenceNumber=" + sequenceNumber +
                ", options=" + options +
                ", universe=" + universe +
                ", dmpVector=" + dmpVector +
                ", addressTypeAndDataType=" + addressTypeAndDataType +
                ", firstPropertyAddress=" + firstPropertyAddress +
                ", addressIncrement=" + addressIncrement +
                ", propertyValueCount=" + propertyValueCount +
                ", startCode=" + startCode +
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

    public String scrivoInEsadecimaleIByte(byte[] dmxData) {
        if (dmxData == null) return "null";

        StringBuilder s = new StringBuilder(dmxData.length * 3);

        for (byte b : dmxData) {
            int val = b & 0xFF;
            if (val < 16) s.append('0');
            s.append(Integer.toHexString(val).toUpperCase()).append(' ');
        }

        return s.toString().trim();
    }

}

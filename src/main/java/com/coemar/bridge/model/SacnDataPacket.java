package com.coemar.bridge.model;

import java.util.Arrays;

public class SacnDataPacket implements LightingPacket{
    public int preambleSize;
    public int postambleSize;
    public byte[] cid;                 // 16 byte
    public long rootVector;
    public long framingVector;
    public String sourceName;
    public int priority;
    public int synchronizationAddress;
    public int sequenceNumber;
    public int options;
    public int universe;
    public int dmpVector;
    public int addressTypeAndDataType;
    public int firstPropertyAddress;
    public int addressIncrement;
    public int propertyValueCount;
    public int startCode;
    public byte[] dmxData;

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

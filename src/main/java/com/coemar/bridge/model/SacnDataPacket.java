package com.coemar.bridge.model;

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
}

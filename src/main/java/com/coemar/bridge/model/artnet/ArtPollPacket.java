package com.coemar.bridge.model.artnet;

import com.coemar.bridge.model.Packet;

public class ArtPollPacket implements Packet{

    private final int opCode;

    private final int protocolVersion;

    private final ArtPollFlags flags;

    private final int diagPriority;

    private final int targetPortAddressTop;
    private final int targetPortAddressBottom;
    private final int estaManufacturerCode;
    private final int oemCode;

    public ArtPollPacket(int opCode, int protocolVersion, ArtPollFlags flags, int diagPriority, int targetPortAddressTop, int targetPortAddressBottom, int estaManufacturerCode, int oemCode) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.flags = flags;
        this.diagPriority = diagPriority;
        this.targetPortAddressTop = targetPortAddressTop;
        this.targetPortAddressBottom = targetPortAddressBottom;
        this.estaManufacturerCode = estaManufacturerCode;
        this.oemCode = oemCode;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public ArtPollFlags getFlags() {
        return flags;
    }

    public int getDiagPriority() {
        return diagPriority;
    }

    public int getTargetPortAddressTop() {
        return targetPortAddressTop;
    }

    public int getTargetPortAddressBottom() {
        return targetPortAddressBottom;
    }

    public int getEstaManufacturerCode() {
        return estaManufacturerCode;
    }

    public int getOemCode() {
        return oemCode;
    }

    @Override
    public String toString() {
        return "ArtPollPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", flags=" + flags +
                ", diagPriority=" + diagPriority +
                ", targetPortAddressTop=" + targetPortAddressTop +
                ", targetPortAddressBottom=" + targetPortAddressBottom +
                ", estaManufacturerCode=" + estaManufacturerCode +
                ", oemCode=" + oemCode +
                '}';
    }
}

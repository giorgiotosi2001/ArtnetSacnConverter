package com.coemar.bridge.model;

import java.util.Arrays;

public class ArtDmxPacket implements LightingPacket{

    public int opCode;

    public int protocolVersion;

    public int sequence;

    public int physical;

    public int subUni;

    public int net;

    public int portAddress; // net + subuni(subnet(4bit alti) + universe(4 bit bassi))
    public int length;

    public byte[] dmxData;

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
                ", dmxData=" + scrivoInEsadecimaleIByte(dmxData) +
                '}';
    }

    private String scrivoInEsadecimaleIByte(byte[] dmxData) {
        if (dmxData == null) return "null";

        StringBuilder sb = new StringBuilder(dmxData.length * 3);

        for (byte b : dmxData) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString().trim();
    }
}

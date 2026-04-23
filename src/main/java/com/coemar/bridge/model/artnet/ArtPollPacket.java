package com.coemar.bridge.model.artnet;

import com.coemar.bridge.model.Packet;

public class ArtPollPacket implements Packet{

    public int opCode;

    public int protocolVersion;

    public boolean[] flags;

    public int diagPriority;

}

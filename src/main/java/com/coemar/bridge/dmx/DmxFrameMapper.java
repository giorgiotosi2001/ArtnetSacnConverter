package com.coemar.bridge.dmx;

import com.coemar.bridge.model.artnet.packets.incoming.ArtDmxPacket;
import com.coemar.bridge.model.sacn.packets.SacnDataPacket;

public final class DmxFrameMapper {

    private DmxFrameMapper() {
    }

    public static DmxFrame fromArtNet(ArtDmxPacket packet) {
        if (packet == null) {
            throw new IllegalArgumentException("packet must not be null");
        }
        return DmxFrame.standard(
                DmxSourceProtocol.ARTNET,
                packet.getPortAddress(),
                packet.getSequence(),
                0,
                packet.getDmxData()
        );
    }

    public static DmxFrame fromSacn(SacnDataPacket packet) {
        if (packet == null) {
            throw new IllegalArgumentException("packet must not be null");
        }
        return new DmxFrame(
                DmxSourceProtocol.SACN,
                packet.getUniverse(),
                packet.getSequenceNumber(),
                packet.getPriority(),
                packet.getStartCode(),
                packet.getDmxData()
        );
    }
}

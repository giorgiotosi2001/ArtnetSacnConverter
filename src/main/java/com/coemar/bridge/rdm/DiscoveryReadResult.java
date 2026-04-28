package com.coemar.bridge.rdm;

import java.util.Arrays;

public class DiscoveryReadResult {

    private final DiscoveryReadResultType type;
    private final byte[] uid;

    private DiscoveryReadResult(DiscoveryReadResultType type, byte[] uid) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (type == DiscoveryReadResultType.VALID_RESPONSE && (uid == null || uid.length != RdmUid.LENGTH)) {
            throw new IllegalArgumentException("uid must contain exactly 6 bytes for VALID_RESPONSE");
        }
        if (type != DiscoveryReadResultType.VALID_RESPONSE && uid != null) {
            throw new IllegalArgumentException("uid is only allowed for VALID_RESPONSE");
        }
        this.type = type;
        this.uid = uid == null ? null : Arrays.copyOf(uid, uid.length);
    }

    public static DiscoveryReadResult noResponse() {
        return new DiscoveryReadResult(DiscoveryReadResultType.NO_RESPONSE, null);
    }

    public static DiscoveryReadResult validResponse(byte[] uid) {
        return new DiscoveryReadResult(DiscoveryReadResultType.VALID_RESPONSE, uid);
    }

    public static DiscoveryReadResult collisionOrCorrupted() {
        return new DiscoveryReadResult(DiscoveryReadResultType.COLLISION_OR_CORRUPTED, null);
    }

    public DiscoveryReadResultType getType() {
        return type;
    }

    public byte[] getUid() {
        return uid == null ? null : Arrays.copyOf(uid, uid.length);
    }
}

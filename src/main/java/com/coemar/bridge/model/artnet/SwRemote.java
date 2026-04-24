package com.coemar.bridge.model.artnet;

public final class SwRemote {

    private final boolean[] remotes = new boolean[8];

    public SwRemote(byte value) {
        int v = value & 0xFF;

        // bit 0 → Remote 1 ... bit 7 → Remote 8
        for (int i = 0; i < 8; i++) {
            remotes[i] = ((v >> i) & 1) == 1;
        }
    }

    public boolean isRemoteActive(int index) {
        // index: 0 → Remote1, 7 → Remote8
        return remotes[index];
    }

    public boolean isRemote1Active() { return remotes[0]; }
    public boolean isRemote2Active() { return remotes[1]; }
    public boolean isRemote3Active() { return remotes[2]; }
    public boolean isRemote4Active() { return remotes[3]; }
    public boolean isRemote5Active() { return remotes[4]; }
    public boolean isRemote6Active() { return remotes[5]; }
    public boolean isRemote7Active() { return remotes[6]; }
    public boolean isRemote8Active() { return remotes[7]; }

    public boolean[] getRemotes() {
        return remotes;
    }
}
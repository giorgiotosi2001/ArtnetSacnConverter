package com.coemar.bridge.rdm;

public class CommandPacket implements Comparable<CommandPacket>{

    private byte[] pd;
    private int pid;
    private byte cc;
    private final int priority;
    public byte[] getPd() {
        return pd;
    }

    public int getPriority() {
        return priority;
    }

    public int getPid() {
        return pid;
    }

    public byte getCc() {
        return cc;
    }

    public CommandPacket(byte[] pd, int pid, byte cc, int priority) {
        this.pd = pd;
        this.pid = pid;
        this.cc = cc;
        this.priority = priority;
    }




    // Metodo per confrontare i pacchetti in base alla priorità
    @Override
    public int compareTo(CommandPacket other) {
        return Integer.compare(this.priority, other.priority);
    }
}


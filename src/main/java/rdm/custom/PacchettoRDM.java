package rdm.custom;

public class PacchettoRDM {

    private static final byte START_CODE = (byte) 0xCC;
    private static final byte SUB_START_CODE = (byte) 0x01;

    private final byte[] uidDest;
    private final byte[] uidSource;
    private final byte tn;
    private final byte cc;
    private final int pid;
    private final byte[] pd;

    public PacchettoRDM(byte[] uidDest, byte[] uidSource, byte tn, byte cc, int pid, byte[] pd) {
        if (uidDest == null) {
            throw new IllegalArgumentException("uidDest non può essere null");
        }
        if (uidSource == null) {
            throw new IllegalArgumentException("uidSource non può essere null");
        }
        if (uidDest.length != 6 || uidSource.length != 6) {
            throw new IllegalArgumentException("UID devono essere lunghi 6 byte");
        }

        this.uidDest = uidDest.clone();
        this.uidSource = uidSource.clone();
        this.tn = tn;
        this.cc = cc;
        this.pid = pid;
        this.pd = pd != null ? pd.clone() : new byte[0];
    }

    public byte[] buildPacket() {
        int pdl = pd.length;
        int totalLength = 24 + pdl + 2;

        byte[] packet = new byte[totalLength];

        packet[0] = START_CODE;
        packet[1] = SUB_START_CODE;
        packet[2] = (byte) (totalLength - 2);

        System.arraycopy(uidDest, 0, packet, 3, 6);
        System.arraycopy(uidSource, 0, packet, 9, 6);

        packet[15] = tn;
        packet[16] = 0x00;
        packet[17] = 0x00;
        packet[18] = 0x00;
        packet[19] = 0x00;
        packet[20] = cc;
        packet[21] = (byte) ((pid >> 8) & 0xFF);
        packet[22] = (byte) (pid & 0xFF);
        packet[23] = (byte) pdl;

        System.arraycopy(pd, 0, packet, 24, pdl);

        int checksum = 0;
        for (int i = 0; i < totalLength - 2; i++) {
            checksum += (packet[i] & 0xFF);
            checksum &= 0xFFFF;
        }

        int checksumIndex = totalLength - 2;
        packet[checksumIndex] = (byte) ((checksum >> 8) & 0xFF);
        packet[checksumIndex + 1] = (byte) (checksum & 0xFF);

        return packet;
    }
}
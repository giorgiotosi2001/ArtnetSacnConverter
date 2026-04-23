package rdm.custom;


import java.util.Arrays;

public class RdmSerialDmxOutput implements DmxOutput {

    private final SerialPortAdapter serialPort;
    private final RdmDeviceConfig config;

    private byte transactionNumber = 0;
    private byte[] destinationUid;

    public RdmSerialDmxOutput(SerialPortAdapter serialPort, RdmDeviceConfig config) {
        if (serialPort == null) {
            throw new IllegalArgumentException("serialPort must not be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }
        this.serialPort = serialPort;
        this.config = config;
    }

    public boolean connect() {
        return serialPort.open();
    }

    public void disconnect() {
        serialPort.close();
    }

    public boolean isConnected() {
        return serialPort.isOpen();
    }

    public boolean discover() {
        ensureOpen();

        sendDiscoveryUnMute();
        sleep(50);

        sendDiscoveryUniqueBranch();

        byte[] response = readResponse(config.getDiscoveryTimeoutMs());
        if (response == null || !isValidDiscoveryResponse(response)) {
            return false;
        }

        destinationUid = parseUidFromDiscoveryResponse(response);
        return destinationUid != null;
    }

    public byte[] getDestinationUid() {
        return destinationUid != null ? destinationUid.clone() : null;
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    @Override
    public void sendFrame(DmxFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame must not be null");
        }
        ensureOpen();

        if (destinationUid == null) {
            throw new IllegalStateException("Destination UID not discovered");
        }

        byte[] payload = buildOutputChPayload(frame);

        System.out.println("Payload 0x801B: " + toHex(payload));

        PacchettoRDM packet = new PacchettoRDM(
                destinationUid,
                config.getSourceUid(),
                nextTransactionNumber(),
                RdmConstants.E120_SET_COMMAND,
                RdmConstants.COEMAR_OUTPUTCH,
                payload
        );

        byte[] bytes = packet.buildPacket();
        serialPort.write(bytes);

        byte[] ack = readResponse(config.getAckTimeoutMs());
        if (ack == null) {
            System.err.println("WARN: No ACK received after SET_COMMAND");
        }
    }

    private void sendDiscoveryUnMute() {
        PacchettoRDM packet = new PacchettoRDM(
                RdmConstants.BROADCAST_UID,
                config.getSourceUid(),
                nextTransactionNumber(),
                RdmConstants.E120_DISCOVERY_COMMAND,
                RdmConstants.E120_DISC_UN_MUTE,
                new byte[0]
        );
        serialPort.write(packet.buildPacket());
    }

    private void sendDiscoveryUniqueBranch() {
        byte[] payload = new byte[12];
        System.arraycopy(RdmConstants.DISCOVERY_LOWER_UID, 0, payload, 0, 6);
        System.arraycopy(RdmConstants.DISCOVERY_UPPER_UID, 0, payload, 6, 6);

        PacchettoRDM packet = new PacchettoRDM(
                RdmConstants.BROADCAST_UID,
                config.getSourceUid(),
                nextTransactionNumber(),
                RdmConstants.E120_DISCOVERY_COMMAND,
                RdmConstants.E120_DISC_UNIQUE_BRANCH,
                payload
        );
        serialPort.write(packet.buildPacket());
    }

    public byte[] readResponse(int timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        byte[] buffer = new byte[256];

        while (System.currentTimeMillis() < deadline) {
            if (serialPort.bytesAvailable() > 0) {
                int read = serialPort.read(buffer, timeoutMs);
                if (read > 0) {
                    return Arrays.copyOf(buffer, read);
                }
            }
            sleep(10);
        }

        return null;
    }

    private boolean isValidDiscoveryResponse(byte[] packet) {
        if (packet == null || packet.length != 24) {
            return false;
        }

        int expectedChecksum = 0;
        for (int i = 8; i <= 19; i++) {
            expectedChecksum += (packet[i] & 0xFF);
        }
        expectedChecksum &= 0xFFFF;

        byte[] receivedChecksum = parseChecksumFromDiscoveryResponse(packet);
        if (receivedChecksum == null) {
            return false;
        }

        int actualChecksum =
                ((receivedChecksum[0] & 0xFF) << 8) |
                        (receivedChecksum[1] & 0xFF);

        return expectedChecksum == actualChecksum;
    }

    private byte[] parseUidFromDiscoveryResponse(byte[] data) {
        if (data == null || data.length != 24) {
            return null;
        }

        byte[] encodedBytes = Arrays.copyOfRange(data, 8, 20);
        byte[] uid = new byte[6];

        for (int i = 0; i < 6; i++) {
            int byte1 = encodedBytes[i * 2] & 0x55;
            int byte2 = encodedBytes[i * 2 + 1] & 0xAA;
            uid[i] = (byte) (byte1 | byte2);
        }

        return uid;
    }

    private byte[] parseChecksumFromDiscoveryResponse(byte[] data) {
        if (data == null || data.length != 24) {
            return null;
        }

        int c1 = (data[20] & 0x55) | (data[21] & 0xAA);
        int c2 = (data[22] & 0x55) | (data[23] & 0xAA);

        return new byte[] { (byte) c1, (byte) c2 };
    }

    private byte[] buildOutputChPayload(DmxFrame frame) {
        int[] values = frame.getValues();

        int numColori = 6; // per payload da 15 byte: 2 meta + (6*2) + 1 finale
        byte[] pd = new byte[2 + (numColori * 2) + 1];

        // byte meta, come nel vecchio creaArrayPd(...)
        pd[0] = (byte) 0xFF;
        pd[1] = (byte) 0xFF;

        // inizializza tutto il resto a zero
        for (int i = 2; i < pd.length; i++) {
            pd[i] = 0x00;
        }

        // mappatura colori logici -> coppie MSB/LSB nel payload
        // colore 0 -> byte 2,3
        // colore 1 -> byte 4,5
        // colore 2 -> byte 6,7
        // ecc.
        int logicalColors = Math.min(values.length, numColori);

        for (int k = 0; k < logicalColors; k++) {
            int value = values[k] & 0xFF;

            int idxPayload = 2 * k + 3;
            pd[idxPayload - 1] = (byte) value;   // MSB
            pd[idxPayload]     = (byte) value;  // LSB
        }

        // byte finale extra
        pd[pd.length - 1] = (byte) 0x00;

        return pd;
    }

    private void ensureOpen() {
        if (!serialPort.isOpen()) {
            throw new IllegalStateException("Serial port is not open");
        }
    }

    private byte nextTransactionNumber() {
        return transactionNumber++;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted", e);
        }
    }
}
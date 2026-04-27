package com.coemar.bridge.rdm;


import com.coemar.bridge.dmx.DmxFrame;
import com.coemar.bridge.dmx.DmxOutput;
import com.coemar.bridge.serial.SerialPortAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

public class RdmSerialDmxOutput implements DmxOutput {

    private final SerialPortAdapter serialPort;
    private final RdmDeviceConfig config;
    private final RdmTimingConfig timing;

    private byte transactionNumber = 0;
    private RdmUid destinationUid;

    public RdmSerialDmxOutput(SerialPortAdapter serialPort, RdmDeviceConfig config) {
        if (serialPort == null) {
            throw new IllegalArgumentException("serialPort must not be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }
        this.serialPort = serialPort;
        this.config = config;
        this.timing = config.getTimingConfig();
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
        List<RdmUid> devices = discoverDevices();
        if (devices.isEmpty()) {
            return false;
        }
        destinationUid = devices.get(0);
        return true;
    }

    public List<RdmUid> discoverDevices() {
        ensureOpen();

        Set<RdmUid> discovered = new LinkedHashSet<>();
        sendDiscoveryUnMute();
        sleep(timing.getInterDiscoveryCommandDelayMs());
        discoverRange(RdmUid.MIN, RdmUid.MAX, discovered);
        return new ArrayList<>(discovered);
    }

    public byte[] getDestinationUid() {
        return destinationUid != null ? destinationUid.toByteArray() : null;
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

        RdmPacket packet = new RdmPacket(
                destinationUid.toByteArray(),
                config.getSourceUid(),
                nextTransactionNumber(),
                RdmConstants.E120_SET_COMMAND,
                RdmConstants.COEMAR_OUTPUTCH,
                payload
        );

        byte[] bytes = packet.buildPacket();
        byte[] ack = executeCommand(bytes, timing.getCommandResponseWindowMs());
        if (ack == null) {
            System.err.println("WARN: No ACK received after SET_COMMAND");
        }
    }

    private void sendDiscoveryUnMute() {
        RdmPacket packet = new RdmPacket(
                RdmConstants.BROADCAST_UID,
                config.getSourceUid(),
                nextTransactionNumber(),
                RdmConstants.E120_DISCOVERY_COMMAND,
                RdmConstants.E120_DISC_UN_MUTE,
                new byte[0]
        );
        serialPort.write(packet.buildPacket());
    }

    private void sendDiscoveryMute(RdmUid uid) {
        RdmPacket packet = new RdmPacket(
                uid.toByteArray(),
                config.getSourceUid(),
                nextTransactionNumber(),
                RdmConstants.E120_DISCOVERY_COMMAND,
                RdmConstants.E120_DISC_MUTE,
                new byte[0]
        );
        serialPort.write(packet.buildPacket());
    }

    private void sendDiscoveryUniqueBranch(RdmUid lowerUid, RdmUid upperUid) {
        byte[] payload = new byte[12];
        System.arraycopy(lowerUid.toByteArray(), 0, payload, 0, 6);
        System.arraycopy(upperUid.toByteArray(), 0, payload, 6, 6);

        RdmPacket packet = new RdmPacket(
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
        byte[] response = readResponseWindow(timeoutMs);
        return response.length == 0 ? null : response;
    }

    private void discoverRange(RdmUid lowerUid, RdmUid upperUid, Set<RdmUid> discovered) {
        if (lowerUid.compareTo(upperUid) > 0) {
            return;
        }

        while (true) {
            sendDiscoveryUniqueBranch(lowerUid, upperUid);
            byte[] response = readResponseWindow(timing.getDiscoveryResponseWindowMs());
            if (response.length == 0) {
                return;
            }

            Optional<RdmDiscoveryResponse> parsed = RdmDiscoveryResponse.parse(response);
            if (parsed.isPresent()) {
                RdmUid uid = parsed.get().getUid();
                if (uid.compareTo(lowerUid) >= 0 && uid.compareTo(upperUid) <= 0) {
                    discovered.add(uid);
                    sendDiscoveryMute(uid);
                    sleep(timing.getInterDiscoveryCommandDelayMs());
                    continue;
                }
            }

            if (lowerUid.equals(upperUid)) {
                return;
            }

            RdmUid midpoint = RdmUid.midpoint(lowerUid, upperUid);
            discoverRange(lowerUid, midpoint, discovered);
            if (!midpoint.equals(upperUid)) {
                discoverRange(midpoint.next(), upperUid, discovered);
            }
            return;
        }
    }

    private byte[] executeCommand(byte[] command, int responseWindowMs) {
        serialPort.write(command);
        byte[] response = readResponseWindow(responseWindowMs);
        return response.length == 0 ? null : response;
    }

    private byte[] readResponseWindow(int windowMs) {
        long deadline = System.nanoTime() + (windowMs * 1_000_000L);
        byte[] buffer = new byte[512];
        byte[] collected = new byte[0];

        while (System.nanoTime() < deadline) {
            int available = serialPort.bytesAvailable();
            if (available > 0) {
                int read = serialPort.read(buffer, timing.getPerReadTimeoutMs());
                if (read > 0) {
                    int oldLength = collected.length;
                    collected = Arrays.copyOf(collected, oldLength + read);
                    System.arraycopy(buffer, 0, collected, oldLength, read);
                }
                continue;
            }

            LockSupport.parkNanos(timing.getReadPollIntervalMicros() * 1_000L);
        }

        return collected;
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
        if (ms <= 0) {
            return;
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted", e);
        }
    }
}

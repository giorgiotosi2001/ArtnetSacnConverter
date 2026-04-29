package com.coemar.bridge;

import com.coemar.bridge.artnet.ArtNetParser;
import com.coemar.bridge.dmx.DmxConstants;
import com.coemar.bridge.dmx.DmxFrame;
import com.coemar.bridge.dmx.DmxFrameMapper;
import com.coemar.bridge.dmx.DmxSerialOutput;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDmxPacket;
import com.coemar.bridge.serial.JSerialCommAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OlaArtNetToSerialBridge {

    private static final int ARTNET_PORT = 6454;
    private static final int BUFFER_SIZE = 1500;

    private final String serialPortName;
    private final int targetUniverse;
    private final long refreshMillis;
    private final AtomicReference<DmxFrame> lastFrame = new AtomicReference<>(new DmxFrame(512));
    private final AtomicBoolean running = new AtomicBoolean(false);

    public OlaArtNetToSerialBridge() {
        this("/dev/ttyS0", 1, 30);
    }

    public OlaArtNetToSerialBridge(String serialPortName, int targetUniverse, long refreshMillis) {
        if (serialPortName == null || serialPortName.isBlank()) {
            throw new IllegalArgumentException("serialPortName must not be blank");
        }
        if (targetUniverse < 0) {
            throw new IllegalArgumentException("targetUniverse must not be negative");
        }
        if (refreshMillis <= 0) {
            throw new IllegalArgumentException("refreshMillis must be positive");
        }

        this.serialPortName = serialPortName;
        this.targetUniverse = targetUniverse;
        this.refreshMillis = refreshMillis;
    }

    public static void main(String[] args) {
        new OlaArtNetToSerialBridge().run();
    }

    public void run() {
        DmxSerialOutput output = new DmxSerialOutput(new JSerialCommAdapter(serialPortName));
        if (!output.connect()) {
            throw new IllegalStateException("Cannot open serial port " + serialPortName);
        }

        running.set(true);
        Thread receiverThread = new Thread(this::receiveArtNetLoop, "ola-artnet-receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();

        System.out.println("OLA Art-Net -> serial bridge started");
        System.out.println("Listening UDP " + ARTNET_PORT + ", universe " + targetUniverse
                + ", serial " + serialPortName + ", refresh " + refreshMillis + " ms");

        try {
            while (running.get()) {
                output.sendFrame(lastFrame.get());
                Thread.sleep(refreshMillis);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            running.set(false);
            output.disconnect();
        }
    }

    public void stop() {
        running.set(false);
    }

    private void receiveArtNetLoop() {
        try (DatagramSocket socket = new DatagramSocket(ARTNET_PORT)) {
            while (running.get()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(udpPacket);

                byte[] receivedData = Arrays.copyOf(udpPacket.getData(), udpPacket.getLength());
                try {
                    ArtDmxPacket artDmxPacket = ArtNetParser.parseArtDmx(receivedData, receivedData.length);
                    DmxFrame frame = DmxFrameMapper.fromArtNet(artDmxPacket);
                    acceptFrame(frame);
                } catch (Exception e) {
                    System.out.println("Art-Net packet ignored: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            if (running.get()) {
                System.err.println("Art-Net receiver stopped: " + e.getMessage());
            }
        }
    }

    private void acceptFrame(DmxFrame frame) {
        if (frame.getUniverse() != targetUniverse) {
            return;
        }
        if (frame.getStartCode() != DmxConstants.START_CODE_DMX) {
            return;
        }

        lastFrame.set(frame);
        logFrame(frame);
    }

    private void logFrame(DmxFrame frame) {
        StringBuilder firstChannels = new StringBuilder();
        int count = Math.min(5, frame.getChannelCount());
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                firstChannels.append(' ');
            }
            firstChannels.append("ch").append(i + 1).append('=').append(frame.getChannelValue(i));
        }

        System.out.println("RX Art-Net universe=" + frame.getUniverse()
                + " startCode=0x" + String.format("%02X", frame.getStartCode())
                + " length=" + frame.getChannelCount()
                + " " + firstChannels);
    }
}

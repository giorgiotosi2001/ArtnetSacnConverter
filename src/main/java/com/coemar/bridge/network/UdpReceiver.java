package com.coemar.bridge.network;

import com.coemar.bridge.artnet.ArtNetParser;
import com.coemar.bridge.model.ArtDmxPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UdpReceiver {

    private static final int ARTNET_PORT = 6454;
    private static final int BUFFER_SIZE = 1024;

    public void startArtNetReceiver() {
        try (DatagramSocket socket = new DatagramSocket(ARTNET_PORT)) {

            System.out.println("Receiver Art-Net in ascolto sulla porta " + ARTNET_PORT + "...");

            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(udpPacket);

                byte[] receivedData = Arrays.copyOf(udpPacket.getData(), udpPacket.getLength());

                System.out.println("\nPacchetto ricevuto da: "
                        + udpPacket.getAddress().getHostAddress()
                        + ":" + udpPacket.getPort());

                try {
                    ArtDmxPacket artDmxPacket = ArtNetParser.parseArtDmx(receivedData, receivedData.length);
                    System.out.println(artDmxPacket);
                } catch (Exception e) {
                    System.out.println("Pacchetto scartato: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
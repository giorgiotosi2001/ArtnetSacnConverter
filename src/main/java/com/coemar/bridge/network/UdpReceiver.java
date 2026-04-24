package com.coemar.bridge.network;

import com.coemar.bridge.artnet.ArtNetParser;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDmxPacket;
import com.coemar.bridge.model.sacn.packets.SacnDataPacket;
import com.coemar.bridge.sacn.SacnParser;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UdpReceiver {

    private static final int ARTNET_PORT = 6454;
    private static final int SACN_PORT = 5568;
    private static final int BUFFER_SIZE = 1500;

    public void startSacnReceiver() {
        try (DatagramSocket socket = new DatagramSocket(SACN_PORT)) {

            System.out.println("Receiver sACN in ascolto sulla porta " + SACN_PORT + "...");

            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(udpPacket);

                byte[] receivedData = Arrays.copyOf(udpPacket.getData(), udpPacket.getLength());

                System.out.println("\nPacchetto sACN ricevuto da: "
                        + udpPacket.getAddress().getHostAddress()
                        + ":" + udpPacket.getPort());

                try {
                    SacnDataPacket sacnPacket = SacnParser.parseDataPacket(receivedData, receivedData.length);
                    System.out.println(sacnPacket);
                } catch (Exception e) {
                    System.out.println("Pacchetto sACN scartato: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
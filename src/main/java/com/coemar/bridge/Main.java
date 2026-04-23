package com.coemar.bridge;

import com.coemar.bridge.network.UdpReceiver;

public class Main {
    public static void main(String[] args) {
        // Lo posso fare ? Funziona ? Non è un casino ? Vediamo
        UdpReceiver receiver = new UdpReceiver();

        Thread t1 = new Thread(receiver::startSacnReceiver);
        t1.start();

        Thread t2 = new Thread(receiver::startArtNetReceiver);
        t2.start();
    }
}
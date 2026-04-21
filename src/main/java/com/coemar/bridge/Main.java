package com.coemar.bridge;

import com.coemar.bridge.network.UdpReceiver;

public class Main {
    public static void main(String[] args) {
        UdpReceiver receiver = new UdpReceiver();
        //receiver.startArtNetReceiver();
        receiver.startSacnReceiver();
    }
}
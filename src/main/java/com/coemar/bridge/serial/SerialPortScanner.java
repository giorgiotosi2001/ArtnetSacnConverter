package com.coemar.bridge.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.List;

public final class SerialPortScanner {

    private SerialPortScanner() {
    }

    public static List<String> listPortNames() {
        List<String> portNames = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            portNames.add(port.getSystemPortName());
        }
        portNames.sort(String::compareTo);
        return portNames;
    }
}

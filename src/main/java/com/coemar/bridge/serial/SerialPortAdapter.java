package com.coemar.bridge.serial;

public interface SerialPortAdapter {

    String getPortName();

    boolean open();

    void close();

    boolean isOpen();

    void write(byte[] data);

    int read(byte[] buffer, int timeoutMs);

    int bytesAvailable();
}

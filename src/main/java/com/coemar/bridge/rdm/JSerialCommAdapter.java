package com.coemar.bridge.rdm;

import com.fazecast.jSerialComm.SerialPort;

public class JSerialCommAdapter implements SerialPortAdapter {

    private final SerialPort port;

    public JSerialCommAdapter(String portName) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("Port name must not be blank");
        }
        this.port = SerialPort.getCommPort(portName);
    }

    @Override
    public boolean open() {
        port.setComPortParameters(
                RdmConstants.RDM_BAUDRATE,
                8,
                SerialPort.TWO_STOP_BITS,
                SerialPort.NO_PARITY
        );
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        return port.openPort();
    }

    @Override
    public void close() {
        if (port.isOpen()) {
            port.closePort();
        }
    }

    @Override
    public boolean isOpen() {
        return port.isOpen();
    }

    @Override
    public void write(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data must not be empty");
        }
        if (!port.isOpen()) {
            throw new IllegalStateException("Serial port is not open");
        }

        try {
            port.setBreak();
            Thread.sleep(1);
            port.clearBreak();

            int written = port.writeBytes(data, data.length);
            if (written != data.length) {
                throw new IllegalStateException("Serial write failed. Written=" + written);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while writing serial data", e);
        }
    }

    @Override
    public int read(byte[] buffer, int timeoutMs) {
        if (buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("Buffer must not be empty");
        }
        if (!port.isOpen()) {
            throw new IllegalStateException("Serial port is not open");
        }

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, timeoutMs, timeoutMs);
        return port.readBytes(buffer, buffer.length);
    }

    @Override
    public int bytesAvailable() {
        return port.bytesAvailable();
    }
}
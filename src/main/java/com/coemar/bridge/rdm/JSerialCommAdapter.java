package com.coemar.bridge.rdm;

import com.fazecast.jSerialComm.SerialPort;

import java.util.concurrent.locks.LockSupport;

public class JSerialCommAdapter implements SerialPortAdapter {

    private final SerialPort port;
    private final int breakDurationMicros;
    private final int markAfterBreakMicros;

    public JSerialCommAdapter(String portName) {
        this(portName, 176, 12);
    }

    public JSerialCommAdapter(String portName, RdmTimingConfig timingConfig) {
        this(portName, timingConfig.getBreakDurationMicros(), timingConfig.getMarkAfterBreakMicros());
    }

    public JSerialCommAdapter(String portName, int breakDurationMicros, int markAfterBreakMicros) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("Port name must not be blank");
        }
        if (breakDurationMicros <= 0) {
            throw new IllegalArgumentException("breakDurationMicros must be positive");
        }
        if (markAfterBreakMicros <= 0) {
            throw new IllegalArgumentException("markAfterBreakMicros must be positive");
        }
        this.port = SerialPort.getCommPort(portName);
        this.breakDurationMicros = breakDurationMicros;
        this.markAfterBreakMicros = markAfterBreakMicros;
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

        port.setBreak();
        LockSupport.parkNanos(breakDurationMicros * 1_000L);
        port.clearBreak();
        LockSupport.parkNanos(markAfterBreakMicros * 1_000L);

        int written = port.writeBytes(data, data.length);
        if (written != data.length) {
            throw new IllegalStateException("Serial write failed. Written=" + written);
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

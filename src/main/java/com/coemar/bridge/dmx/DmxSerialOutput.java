package com.coemar.bridge.dmx;

import com.coemar.bridge.serial.SerialPortAdapter;

import java.util.concurrent.locks.LockSupport;

public class DmxSerialOutput implements DmxOutput {

    private final SerialPortAdapter serialPort;
    private final DmxTimingConfig timingConfig;
    private long nextFrameDeadlineNanos;

    public DmxSerialOutput(SerialPortAdapter serialPort) {
        this(serialPort, DmxTimingConfig.defaultDmx512());
    }

    public DmxSerialOutput(SerialPortAdapter serialPort, DmxTimingConfig timingConfig) {
        if (serialPort == null) {
            throw new IllegalArgumentException("serialPort must not be null");
        }
        if (timingConfig == null) {
            throw new IllegalArgumentException("timingConfig must not be null");
        }
        this.serialPort = serialPort;
        this.timingConfig = timingConfig;
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

    @Override
    public void sendFrame(DmxFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame must not be null");
        }
        if (!frame.isStandardDmx()) {
            throw new IllegalArgumentException("DmxSerialOutput accetta solo start code DMX standard 0x00");
        }
        if (!serialPort.isOpen()) {
            throw new IllegalStateException("Serial port is not open");
        }

        waitForRefreshPeriod();
        if (timingConfig.getMarkBeforeBreakMicros() > 0) {
            LockSupport.parkNanos(timingConfig.getMarkBeforeBreakMicros() * 1_000L);
        }
        serialPort.write(frame.toWireBytes());
        nextFrameDeadlineNanos = System.nanoTime() + (timingConfig.getRefreshPeriodMicros() * 1_000L);
    }

    private void waitForRefreshPeriod() {
        long now = System.nanoTime();
        if (nextFrameDeadlineNanos > now) {
            LockSupport.parkNanos(nextFrameDeadlineNanos - now);
        }
    }
}

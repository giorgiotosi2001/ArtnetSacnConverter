package com.coemar.bridge.rdm;

public class RdmTimingConfig {

    private final int discoveryResponseWindowMs;
    private final int commandResponseWindowMs;
    private final int interDiscoveryCommandDelayMs;
    private final int readPollIntervalMicros;
    private final int perReadTimeoutMs;
    private final int breakDurationMicros;
    private final int markAfterBreakMicros;

    public RdmTimingConfig(int discoveryResponseWindowMs,
                           int commandResponseWindowMs,
                           int interDiscoveryCommandDelayMs,
                           int readPollIntervalMicros,
                           int perReadTimeoutMs) {
        this(discoveryResponseWindowMs, commandResponseWindowMs, interDiscoveryCommandDelayMs,
                readPollIntervalMicros, perReadTimeoutMs, 176, 12);
    }

    public RdmTimingConfig(int discoveryResponseWindowMs,
                           int commandResponseWindowMs,
                           int interDiscoveryCommandDelayMs,
                           int readPollIntervalMicros,
                           int perReadTimeoutMs,
                           int breakDurationMicros,
                           int markAfterBreakMicros) {
        if (discoveryResponseWindowMs <= 0) {
            throw new IllegalArgumentException("discoveryResponseWindowMs must be positive");
        }
        if (commandResponseWindowMs <= 0) {
            throw new IllegalArgumentException("commandResponseWindowMs must be positive");
        }
        if (interDiscoveryCommandDelayMs < 0) {
            throw new IllegalArgumentException("interDiscoveryCommandDelayMs must not be negative");
        }
        if (readPollIntervalMicros <= 0) {
            throw new IllegalArgumentException("readPollIntervalMicros must be positive");
        }
        if (perReadTimeoutMs < 0) {
            throw new IllegalArgumentException("perReadTimeoutMs must not be negative");
        }
        if (breakDurationMicros <= 0) {
            throw new IllegalArgumentException("breakDurationMicros must be positive");
        }
        if (markAfterBreakMicros <= 0) {
            throw new IllegalArgumentException("markAfterBreakMicros must be positive");
        }

        this.discoveryResponseWindowMs = discoveryResponseWindowMs;
        this.commandResponseWindowMs = commandResponseWindowMs;
        this.interDiscoveryCommandDelayMs = interDiscoveryCommandDelayMs;
        this.readPollIntervalMicros = readPollIntervalMicros;
        this.perReadTimeoutMs = perReadTimeoutMs;
        this.breakDurationMicros = breakDurationMicros;
        this.markAfterBreakMicros = markAfterBreakMicros;
    }

    public static RdmTimingConfig defaults(int discoveryTimeoutMs, int ackTimeoutMs) {
        return new RdmTimingConfig(discoveryTimeoutMs, ackTimeoutMs, 2, 500, 1);
    }

    public int getDiscoveryResponseWindowMs() {
        return discoveryResponseWindowMs;
    }

    public int getCommandResponseWindowMs() {
        return commandResponseWindowMs;
    }

    public int getInterDiscoveryCommandDelayMs() {
        return interDiscoveryCommandDelayMs;
    }

    public int getReadPollIntervalMicros() {
        return readPollIntervalMicros;
    }

    public int getPerReadTimeoutMs() {
        return perReadTimeoutMs;
    }

    public int getBreakDurationMicros() {
        return breakDurationMicros;
    }

    public int getMarkAfterBreakMicros() {
        return markAfterBreakMicros;
    }
}

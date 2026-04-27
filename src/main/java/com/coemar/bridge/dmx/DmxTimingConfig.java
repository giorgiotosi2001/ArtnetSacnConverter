package com.coemar.bridge.dmx;

public class DmxTimingConfig {

    private final int breakDurationMicros;
    private final int markAfterBreakMicros;
    private final int markBeforeBreakMicros;
    private final int refreshPeriodMicros;

    public DmxTimingConfig(int breakDurationMicros,
                           int markAfterBreakMicros,
                           int markBeforeBreakMicros,
                           int refreshPeriodMicros) {
        if (breakDurationMicros < 92) {
            throw new IllegalArgumentException("breakDurationMicros deve essere almeno 92 us");
        }
        if (markAfterBreakMicros < 12) {
            throw new IllegalArgumentException("markAfterBreakMicros deve essere almeno 12 us");
        }
        if (markBeforeBreakMicros < 0) {
            throw new IllegalArgumentException("markBeforeBreakMicros must not be negative");
        }
        if (refreshPeriodMicros <= 0) {
            throw new IllegalArgumentException("refreshPeriodMicros must be positive");
        }

        this.breakDurationMicros = breakDurationMicros;
        this.markAfterBreakMicros = markAfterBreakMicros;
        this.markBeforeBreakMicros = markBeforeBreakMicros;
        this.refreshPeriodMicros = refreshPeriodMicros;
    }

    public static DmxTimingConfig defaultDmx512() {
        return new DmxTimingConfig(176, 12, 0, 22_700);
    }

    public int getBreakDurationMicros() {
        return breakDurationMicros;
    }

    public int getMarkAfterBreakMicros() {
        return markAfterBreakMicros;
    }

    public int getMarkBeforeBreakMicros() {
        return markBeforeBreakMicros;
    }

    public int getRefreshPeriodMicros() {
        return refreshPeriodMicros;
    }
}

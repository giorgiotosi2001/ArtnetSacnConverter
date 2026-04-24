package com.coemar.bridge.model.artnet;

public final class SwMacro {

    private final boolean[] macros = new boolean[8];

    public SwMacro(byte value) {
        int v = value & 0xFF;

        // bit 0 → Macro 1 ... bit 7 → Macro 8
        for (int i = 0; i < 8; i++) {
            macros[i] = ((v >> i) & 1) == 1;
        }
    }

    public boolean isMacroActive(int index) {
        // index: 0 → Macro1, 7 → Macro8
        return macros[index];
    }

    public boolean isMacro1Active() { return macros[0]; }
    public boolean isMacro2Active() { return macros[1]; }
    public boolean isMacro3Active() { return macros[2]; }
    public boolean isMacro4Active() { return macros[3]; }
    public boolean isMacro5Active() { return macros[4]; }
    public boolean isMacro6Active() { return macros[5]; }
    public boolean isMacro7Active() { return macros[6]; }
    public boolean isMacro8Active() { return macros[7]; }

    public boolean[] getMacros() {
        return macros;
    }
}
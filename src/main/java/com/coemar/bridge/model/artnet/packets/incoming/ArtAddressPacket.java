package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * Comando di configurazione remota di un nodo Art-Net.
 *
 * - Direzione tipica: controller -> nodo, normalmente in unicast.
 * - Risposta attesa: nessuna risposta dedicata; l'esito si verifica con un successivo {@code ArtPollPacket}/{@code ArtPollReplyPacket}.
 * - Serve per: nomi porta, Net/Sub-Net/Universe, direzione, merge mode e altre opzioni porta.
 * - Non fa: non trasporta livelli DMX e non e una query di stato.
 */
public class ArtAddressPacket implements Packet {

    private static final int PORT_NAME_LENGTH = 18;
    private static final int LONG_NAME_LENGTH = 64;
    private static final int PORT_ARRAY_LENGTH = 4;

    private final int opCode;
    private final int protocolVersion;
    private final int netSwitch;
    private final int bindIndex;
    private final String portName;
    private final String longName;
    private final byte[] swIn;
    private final byte[] swOut;
    private final int subSwitch;
    private final int acnPriority;
    private final int command;

    public ArtAddressPacket(int opCode, int protocolVersion, int netSwitch, int bindIndex, String portName,
                            String longName, byte[] swIn, byte[] swOut, int subSwitch, int acnPriority, int command) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.netSwitch = netSwitch;
        this.bindIndex = bindIndex;
        this.portName = normalizeFixedString(portName, PORT_NAME_LENGTH - 1);
        this.longName = normalizeFixedString(longName, LONG_NAME_LENGTH - 1);
        this.swIn = copyFixedLengthBytes(swIn, PORT_ARRAY_LENGTH, "swIn");
        this.swOut = copyFixedLengthBytes(swOut, PORT_ARRAY_LENGTH, "swOut");
        this.subSwitch = subSwitch;
        this.acnPriority = acnPriority;
        this.command = command;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getNetSwitch() {
        return netSwitch;
    }

    public int getBindIndex() {
        return bindIndex;
    }

    public String getPortName() {
        return portName;
    }

    public String getLongName() {
        return longName;
    }

    public byte[] getSwIn() {
        return Arrays.copyOf(swIn, swIn.length);
    }

    public byte[] getSwOut() {
        return Arrays.copyOf(swOut, swOut.length);
    }

    public int getSubSwitch() {
        return subSwitch;
    }

    public int getAcnPriority() {
        return acnPriority;
    }

    public int getCommand() {
        return command;
    }

    public Command getCommandType() {
        return Command.fromValue(command);
    }

    public boolean isNetSwitchProgrammingEnabled() {
        return isBitSet(netSwitch, 7);
    }

    public boolean isNetSwitchResetToPhysical() {
        return (netSwitch & 0xFF) == 0x00;
    }

    public int getNetSwitchValue() {
        return netSwitch & 0x7F;
    }

    public boolean isSubSwitchProgrammingEnabled() {
        return isBitSet(subSwitch, 7);
    }

    public boolean isSubSwitchResetToPhysical() {
        return (subSwitch & 0xFF) == 0x00;
    }

    public int getSubSwitchValue() {
        return subSwitch & 0x0F;
    }

    public boolean isInputPortProgrammingEnabled(int index) {
        return isBitSet(getPortByte(swIn, index), 7);
    }

    public boolean isInputPortResetToPhysical(int index) {
        return (getPortByte(swIn, index) & 0xFF) == 0x00;
    }

    public int getInputPortAddressValue(int index) {
        return getPortByte(swIn, index) & 0x0F;
    }

    public boolean isOutputPortProgrammingEnabled(int index) {
        return isBitSet(getPortByte(swOut, index), 7);
    }

    public boolean isOutputPortResetToPhysical(int index) {
        return (getPortByte(swOut, index) & 0xFF) == 0x00;
    }

    public int getOutputPortAddressValue(int index) {
        return getPortByte(swOut, index) & 0x0F;
    }

    public boolean isAcnPriorityNoChange() {
        return acnPriority == 0xFF;
    }

    private static boolean isBitSet(int value, int bitIndex) {
        return ((value >> bitIndex) & 1) == 1;
    }

    private static byte getPortByte(byte[] values, int index) {
        if (index < 0 || index >= values.length) {
            throw new IndexOutOfBoundsException("Port index fuori range: " + index);
        }
        return values[index];
    }

    private static String normalizeFixedString(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private static byte[] copyFixedLengthBytes(byte[] value, int expectedLength, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " non puo essere null");
        }
        if (value.length != expectedLength) {
            throw new IllegalArgumentException(fieldName + " deve essere lungo " + expectedLength + " byte");
        }
        return Arrays.copyOf(value, value.length);
    }

    private static String formatUnsignedByteArray(byte[] value) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < value.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(value[i] & 0xFF);
        }
        return builder.append(']').toString();
    }

    @Override
    public String toString() {
        return "ArtAddressPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", netSwitch=" + (netSwitch & 0xFF) +
                ", bindIndex=" + bindIndex +
                ", portName='" + portName + '\'' +
                ", longName='" + longName + '\'' +
                ", swIn=" + formatUnsignedByteArray(swIn) +
                ", swOut=" + formatUnsignedByteArray(swOut) +
                ", subSwitch=" + (subSwitch & 0xFF) +
                ", acnPriority=" + acnPriority +
                ", command=" + command +
                ", commandType=" + getCommandType() +
                '}';
    }

    public enum Command {
        AcNone(0x00),
        AcCancelMerge(0x01),
        AcLedNormal(0x02),
        AcLedMute(0x03),
        AcLedLocate(0x04),
        AcResetRxFlags(0x05),
        AcAnalysisOn(0x06),
        AcAnalysisOff(0x07),
        AcFailHold(0x08),
        AcFailZero(0x09),
        AcFailFull(0x0A),
        AcFailScene(0x0B),
        AcFailRecord(0x0C),
        AcMergeLtp0(0x10),
        AcMergeLtp1(0x11),
        AcMergeLtp2(0x12),
        AcMergeLtp3(0x13),
        AcDirectionTx0(0x20),
        AcDirectionTx1(0x21),
        AcDirectionTx2(0x22),
        AcDirectionTx3(0x23),
        AcDirectionRx0(0x30),
        AcDirectionRx1(0x31),
        AcDirectionRx2(0x32),
        AcDirectionRx3(0x33),
        AcMergeHtp0(0x50),
        AcMergeHtp1(0x51),
        AcMergeHtp2(0x52),
        AcMergeHtp3(0x53),
        AcArtNetSel0(0x60),
        AcArtNetSel1(0x61),
        AcArtNetSel2(0x62),
        AcArtNetSel3(0x63),
        AcAcnSel0(0x70),
        AcAcnSel1(0x71),
        AcAcnSel2(0x72),
        AcAcnSel3(0x73),
        AcClearOp0(0x90),
        AcClearOp1(0x91),
        AcClearOp2(0x92),
        AcClearOp3(0x93),
        AcStyleDelta0(0xA0),
        AcStyleDelta1(0xA1),
        AcStyleDelta2(0xA2),
        AcStyleDelta3(0xA3),
        AcStyleConst0(0xB0),
        AcStyleConst1(0xB1),
        AcStyleConst2(0xB2),
        AcStyleConst3(0xB3),
        AcRdmEnable0(0xC0),
        AcRdmEnable1(0xC1),
        AcRdmEnable2(0xC2),
        AcRdmEnable3(0xC3),
        AcRdmDisable0(0xD0),
        AcRdmDisable1(0xD1),
        AcRdmDisable2(0xD2),
        AcRdmDisable3(0xD3),
        AcBqp0(0xE0),
        AcBqp1(0xE1),
        AcBqp2(0xE2),
        AcBqp3(0xE3),
        AcBqp4(0xE4),
        AcBqp5(0xE5),
        AcBqp6(0xE6),
        AcBqp7(0xE7),
        AcBqp8(0xE8),
        AcBqp9(0xE9),
        AcBqp10(0xEA),
        AcBqp11(0xEB),
        AcBqp12(0xEC),
        AcBqp13(0xED),
        AcBqp14(0xEE),
        AcBqp15(0xEF),
        Unknown(-1);

        private final int value;

        Command(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Command fromValue(int value) {
            for (Command command : values()) {
                if (command.value == value) {
                    return command;
                }
            }
            return Unknown;
        }
    }
}

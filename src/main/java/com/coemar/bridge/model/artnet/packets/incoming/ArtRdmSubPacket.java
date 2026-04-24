package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.util.Arrays;

/**
 * <p>Trasporto compatto di Get/Set e relative risposte RDM per piu sub-device dello stesso device.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> bidirezionale tra controller, gateway e dispositivi che proxy/emulano RDM, in unicast.</li>
 *   <li><b>Risposta attesa:</b> dipende da {@code CommandClass}; il pacchetto puo essere sia richiesta sia risposta.</li>
 *   <li><b>Serve per:</b> ridurre banda rispetto a piu {@code ArtRdmPacket} quando si lavora con sub-device multipli.</li>
 *   <li><b>Note:</b> non sostituisce {@code ArtRdmPacket}; la dimensione del campo dati dipende da {@code CommandClass} e {@code SubCount}.</li>
 * </ul>
 */
public class ArtRdmSubPacket implements Packet {

    private static final int UID_LENGTH = 6;
    private static final int SPARE_TAIL_LENGTH = 4;

    private final int opCode;
    private final int protocolVersion;
    private final int rdmVersion;
    private final int filler2;
    private final byte[] uid;
    private final int spare1;
    private final int commandClass;
    private final int parameterId;
    private final int subDevice;
    private final int subCount;
    private final byte[] spareTail;
    private final int[] dataWords;

    public ArtRdmSubPacket(int opCode, int protocolVersion, int rdmVersion, int filler2, byte[] uid, int spare1,
                           int commandClass, int parameterId, int subDevice, int subCount, byte[] spareTail,
                           int[] dataWords) {
        if (subCount <= 0) {
            throw new IllegalArgumentException("subCount deve essere maggiore di zero");
        }

        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.rdmVersion = rdmVersion;
        this.filler2 = filler2;
        this.uid = copyFixedLengthBytes(uid, UID_LENGTH, "uid");
        this.spare1 = spare1;
        this.commandClass = commandClass;
        this.parameterId = parameterId;
        this.subDevice = subDevice;
        this.subCount = subCount;
        this.spareTail = copyFixedLengthBytes(spareTail, SPARE_TAIL_LENGTH, "spareTail");
        this.dataWords = copyWords(dataWords);
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getRdmVersion() {
        return rdmVersion;
    }

    public boolean isRdmDraftV10() {
        return rdmVersion == 0x00;
    }

    public boolean isRdmStandardV10() {
        return rdmVersion == 0x01;
    }

    public int getFiller2() {
        return filler2;
    }

    public byte[] getUid() {
        return Arrays.copyOf(uid, uid.length);
    }

    public int getSpare1() {
        return spare1;
    }

    public int getCommandClass() {
        return commandClass;
    }

    public CommandClass getCommandClassType() {
        return CommandClass.fromValue(commandClass);
    }

    public boolean isGetCommand() {
        return getCommandClassType() == CommandClass.Get;
    }

    public boolean isSetCommand() {
        return getCommandClassType() == CommandClass.Set;
    }

    public boolean isGetResponse() {
        return getCommandClassType() == CommandClass.GetResponse;
    }

    public boolean isSetResponse() {
        return getCommandClassType() == CommandClass.SetResponse;
    }

    public int getParameterId() {
        return parameterId;
    }

    public int getSubDevice() {
        return subDevice;
    }

    public boolean isRootDevice() {
        return subDevice == 0;
    }

    public int getSubCount() {
        return subCount;
    }

    public byte[] getSpareTail() {
        return Arrays.copyOf(spareTail, spareTail.length);
    }

    public int[] getDataWords() {
        return Arrays.copyOf(dataWords, dataWords.length);
    }

    public int getDataWordCount() {
        return dataWords.length;
    }

    public boolean hasDataWords() {
        return dataWords.length > 0;
    }

    public int getExpectedDataWordCount() {
        return switch (getCommandClassType()) {
            case Get, SetResponse -> 0;
            case Set, GetResponse -> subCount;
            case Unknown -> dataWords.length;
        };
    }

    public boolean hasExpectedDataWordCount() {
        return dataWords.length == getExpectedDataWordCount();
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

    private static int[] copyWords(int[] value) {
        if (value == null) {
            throw new IllegalArgumentException("dataWords non puo essere null");
        }
        return Arrays.copyOf(value, value.length);
    }

    private static String formatUid(byte[] value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            if (i > 0) {
                builder.append(':');
            }
            builder.append(String.format("%02X", value[i] & 0xFF));
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "ArtRdmSubPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", rdmVersion=" + rdmVersion +
                ", filler2=" + filler2 +
                ", uid=" + formatUid(uid) +
                ", spare1=" + spare1 +
                ", commandClass=" + commandClass +
                ", commandClassType=" + getCommandClassType() +
                ", parameterId=0x" + Integer.toHexString(parameterId) +
                ", subDevice=" + subDevice +
                ", subCount=" + subCount +
                ", dataWordCount=" + dataWords.length +
                '}';
    }

    public enum CommandClass {
        Get(0x20),
        GetResponse(0x21),
        Set(0x30),
        SetResponse(0x31),
        Unknown(-1);

        private final int value;

        CommandClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static CommandClass fromValue(int value) {
            for (CommandClass commandClass : values()) {
                if (commandClass.value == value) {
                    return commandClass;
                }
            }
            return Unknown;
        }
    }
}

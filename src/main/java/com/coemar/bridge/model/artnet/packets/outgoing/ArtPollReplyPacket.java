package com.coemar.bridge.model.artnet.packets.outgoing;

import com.coemar.bridge.artnet.ArtNetOpCode;
import com.coemar.bridge.model.artnet.fields.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import static com.coemar.bridge.util.BinarySerializeUtils.*;

/**
 * Risposta di discovery che descrive identita, porte, stato e capability del nodo.
 *
 * - Direzione tipica: nodo/media server -> controller che ha effettuato il poll.
 * - Risponde a: {@code ArtPollPacket}.
 * - Serve per: annunciare IP, nomi, report, porte, protocollo, supporto RDM/sACN e stato operativo.
 * - Non fa: non trasporta livelli DMX; serve solo a discovery e stato del nodo.
 */
public class ArtPollReplyPacket {
    private static final int IPV4_LENGTH = 4;
    private static final int MAC_LENGTH = 6;
    private static final int UID_LENGTH = 6;
    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);
    private final ArtNetOpCode opCode= ArtNetOpCode.OpPollReply;
    private final byte[] ipAddress;
    private final int port;
    private final int versionInfo;
    private final int netSwitch;
    private final int subSwitch;
    private final int oem;
    private final int ubeaVersion;
    private final ArtPollStatus1 status1;
    private final int estaManCode;
    private final String portName;
    private final String LongName;
    private final NodeReport nodeReport;
    private final int numPorts;
    private final PortTypes portTypes;
    private final GoodInput goodInput;
    private final GoodOutputA goodOutputA;
    private final int swIn;
    private final int swOut;
    private final int acnPriority;
    private final SwMacro swMacro;
    private final SwRemote swRemote;
    private final StyleCodes style;
    private final byte[] macAddress;
    private final byte[] bindIp;
    private final int bindIndex;
    private final ArtPollStatus2 artPollStatus2;
    private final GoodOutputB goodOutputB;
    private final ArtPollStatus3 artPollStatus3;
    private final byte[] defaultRespUID;
    private final int user;
    private final int refreshRate;
    private final int backgroundQueuePolicy;
    private final int filler;

    public ArtPollReplyPacket(byte[] ipAddress, int port, int versionInfo, int netSwitch, int subSwitch, int oem, int ubeaVersion, ArtPollStatus1 status1, int estaManCode, String portName, String longName, NodeReport nodeReport, int numPorts, PortTypes portTypes, GoodInput goodInput, GoodOutputA goodOutputA, int swIn, int swOut, int acnPriority, SwMacro swMacro, SwRemote swRemote, StyleCodes style, byte[] macAddress, byte[] bindIp, int bindIndex, ArtPollStatus2 artPollStatus2, GoodOutputB goodOutputB, ArtPollStatus3 artPollStatus3, byte[] defaultRespUID, int user, int refreshRate, int backgroundQueuePolicy, int filler) {
        this.ipAddress = copyFixedLengthBytes(ipAddress, IPV4_LENGTH, "ipAddress");
        this.port = port;
        this.versionInfo = versionInfo;
        this.netSwitch = netSwitch;
        this.subSwitch = subSwitch;
        this.oem = oem;
        this.ubeaVersion = ubeaVersion;
        this.status1 = status1;
        this.estaManCode = estaManCode;
        this.portName = portName;
        LongName = longName;
        this.nodeReport = nodeReport;
        this.numPorts = numPorts;
        this.portTypes = portTypes;
        this.goodInput = goodInput;
        this.goodOutputA = goodOutputA;
        this.swIn = swIn;
        this.swOut = swOut;
        this.acnPriority = acnPriority;
        this.swMacro = swMacro;
        this.swRemote = swRemote;
        this.style = style;
        this.macAddress = copyFixedLengthBytes(macAddress, MAC_LENGTH, "macAddress");
        this.bindIp = copyFixedLengthBytes(bindIp, IPV4_LENGTH, "bindIp");
        this.bindIndex = bindIndex;
        this.artPollStatus2 = artPollStatus2;
        this.goodOutputB = goodOutputB;
        this.artPollStatus3 = artPollStatus3;
        this.defaultRespUID = copyFixedLengthBytes(defaultRespUID, UID_LENGTH, "defaultRespUID");
        this.user = user;
        this.refreshRate = refreshRate;
        this.backgroundQueuePolicy = backgroundQueuePolicy;
        this.filler = filler;
    }


    public byte [] serializeArtPollReplyPacket(){
        byte[] packet = new byte[239];
        int offset = 0;

        offset = writeBytes(packet, offset, ARTNET_ID);
        offset = writeU16LE(packet, offset, opCode.getValue());
        offset = writeBytes(packet, offset, ipAddress);
        offset = writeU16LE(packet, offset, port);
        offset = writeU16BE(packet, offset, versionInfo);
        offset = writeU8(packet, offset, netSwitch);
        offset = writeU8(packet, offset, subSwitch);
        offset = writeU16BE(packet, offset, oem);
        offset = writeU8(packet, offset, ubeaVersion);
        offset = writeU8(packet, offset, status1 == null ? 0 : status1.toByte());
        offset = writeU16LE(packet, offset, estaManCode);
        offset = writeFixedAscii(packet, offset, portName, 18);
        offset = writeFixedAscii(packet, offset, LongName, 64);
        offset = writeFixedAscii(packet, offset, formatNodeReport(nodeReport), 64);
        offset = writeU16BE(packet, offset, numPorts);
        offset = writeBytes(packet, offset, encodePortTypes());
        offset = writeBytes(packet, offset, encodeGoodInput());
        offset = writeBytes(packet, offset, encodeGoodOutputA());

        // The current model stores these Art-Net byte arrays as packed integers.
        offset = writePackedUnsignedInt(packet, offset, swIn, 4);
        offset = writePackedUnsignedInt(packet, offset, swOut, 4);

        offset = writeU8(packet, offset, acnPriority);
        offset = writeU8(packet, offset, encodeSwMacro());
        offset = writeU8(packet, offset, encodeSwRemote());
        offset = writeZeroBytes(packet, offset, 3);
        offset = writeU8(packet, offset, style == null ? 0 : style.getValue());
        offset = writeBytes(packet, offset, macAddress);
        offset = writeBytes(packet, offset, bindIp);
        offset = writeU8(packet, offset, bindIndex);
        offset = writeU8(packet, offset, encodeStatus2());
        offset = writeBytes(packet, offset, encodeGoodOutputB());
        offset = writeU8(packet, offset, encodeStatus3());
        offset = writeBytes(packet, offset, defaultRespUID);
        offset = writeU16BE(packet, offset, user);
        offset = writeU16BE(packet, offset, refreshRate);
        offset = writeU8(packet, offset, backgroundQueuePolicy);
        offset = writeZeroBytes(packet, offset, 10);

        if (offset != packet.length) {
            throw new IllegalStateException("Lunghezza ArtPollReply non valida: " + offset);
        }

        return packet;
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

    private byte[] encodePortTypes() {
        byte[] encoded = new byte[4];

        if (portTypes == null) {
            return encoded;
        }

        PortTypes.PortType[] ports = portTypes.getPorts();
        for (int i = 0; i < Math.min(ports.length, encoded.length); i++) {
            PortTypes.PortType portType = ports[i];
            if (portType == null) {
                continue;
            }

            int value = 0;
            if (portType.canOutput()) {
                value |= 0x80;
            }
            if (portType.canInput()) {
                value |= 0x40;
            }
            value |= mapProtocol(portType.getProtocol());
            encoded[i] = (byte) value;
        }

        return encoded;
    }

    private int encodeSwMacro() {
        if (swMacro == null) {
            return 0;
        }

        return encodeBitArray(swMacro.getMacros());
    }

    private int encodeSwRemote() {
        if (swRemote == null) {
            return 0;
        }

        return encodeBitArray(swRemote.getRemotes());
    }

    private static int encodeBitArray(boolean[] values) {
        int encoded = 0;

        for (int i = 0; i < Math.min(values.length, 8); i++) {
            if (values[i]) {
                encoded |= 1 << i;
            }
        }

        return encoded;
    }

    private byte[] encodeGoodInput() {
        byte[] encoded = new byte[4];

        if (goodInput == null) {
            return encoded;
        }

        GoodInput.InputStatus[] inputs = goodInput.getInputs();
        for (int i = 0; i < Math.min(inputs.length, encoded.length); i++) {
            GoodInput.InputStatus input = inputs[i];
            if (input == null) {
                continue;
            }

            int value = 0;
            if (input.isDataReceived()) value |= 1 << 7;
            if (input.hasDmxTestPackets()) value |= 1 << 6;
            if (input.hasDmxSipPackets()) value |= 1 << 5;
            if (input.hasDmxTextPackets()) value |= 1 << 4;
            if (input.isInputDisabled()) value |= 1 << 3;
            if (input.hasReceiveErrorsDetected()) value |= 1 << 2;
            if (input.isConvertToSACN()) value |= 1;
            encoded[i] = (byte) value;
        }

        return encoded;
    }

    private byte[] encodeGoodOutputA() {
        byte[] encoded = new byte[4];

        if (goodOutputA == null) {
            return encoded;
        }

        GoodOutputA.OutputStatus[] outputs = goodOutputA.getOutputs();
        for (int i = 0; i < Math.min(outputs.length, encoded.length); i++) {
            GoodOutputA.OutputStatus output = outputs[i];
            if (output == null) {
                continue;
            }

            int value = 0;
            if (output.isDataBeingOutput()) value |= 1 << 7;
            if (output.hasDmxTestPackets()) value |= 1 << 6;
            if (output.hasDmxSipPackets()) value |= 1 << 5;
            if (output.hasDmxTextPackets()) value |= 1 << 4;
            if (output.isMergingData()) value |= 1 << 3;
            if (output.isShortDetected()) value |= 1 << 2;
            if (output.isMergeModeLtp()) value |= 1 << 1;
            if (output.isConvertFromSACN()) value |= 1;
            encoded[i] = (byte) value;
        }

        return encoded;
    }

    private byte[] encodeGoodOutputB() {
        byte[] encoded = new byte[4];

        if (goodOutputB == null) {
            return encoded;
        }

        GoodOutputB.OutputStatus[] outputs = goodOutputB.getOutputs();
        for (int i = 0; i < Math.min(outputs.length, encoded.length); i++) {
            GoodOutputB.OutputStatus output = outputs[i];
            if (output == null) {
                continue;
            }

            int value = 0;
            if (output.isRdmDisabled()) value |= 1 << 7;
            if (output.isContinuousOutput()) value |= 1 << 6;
            if (output.isDiscoveryNotRunning()) value |= 1 << 5;
            if (output.isBackgroundDisabled()) value |= 1 << 4;
            encoded[i] = (byte) value;
        }

        return encoded;
    }

    private int encodeStatus2() {
        if (artPollStatus2 == null) {
            return 0;
        }

        int value = 0;
        if (artPollStatus2.isRdmControlSupported()) value |= 1 << 7;
        if (artPollStatus2.isOutputSwitchSupported()) value |= 1 << 6;
        if (artPollStatus2.isSquawking()) value |= 1 << 5;
        if (artPollStatus2.isArtNetSacnSwitch()) value |= 1 << 4;
        if (artPollStatus2.isPortAddress15Bit()) value |= 1 << 3;
        if (artPollStatus2.isDhcpCapable()) value |= 1 << 2;
        if (artPollStatus2.isDhcpConfigured()) value |= 1 << 1;
        if (artPollStatus2.isWebBrowserConfigSupported()) value |= 1;
        return value;
    }

    private int encodeStatus3() {
        if (artPollStatus3 == null) {
            return 0;
        }

        int value = switch (artPollStatus3.getFailsafeState()) {
            case ALL_OUTPUTS_ZERO -> 0b01 << 6;
            case ALL_OUTPUTS_FULL -> 0b10 << 6;
            case PLAYBACK_SCENE -> 0b11 << 6;
            case HOLD_LAST_STATE -> 0;
        };

        if (artPollStatus3.isSupportsFailsafe()) value |= 1 << 5;
        if (artPollStatus3.isSupportsLlrp()) value |= 1 << 4;
        if (artPollStatus3.isSupportsPortSwitching()) value |= 1 << 3;
        if (artPollStatus3.isSupportsRdmNet()) value |= 1 << 2;
        if (artPollStatus3.isSupportsBackgroundQueue()) value |= 1 << 1;
        if (artPollStatus3.isBackgroundDiscoverySwitch()) value |= 1;
        return value;
    }

    private static int mapProtocol(PortTypes.Protocol protocol) {
        if (protocol == null) {
            return 0;
        }

        return switch (protocol) {
            case DMX512 -> 0x00;
            case MIDI -> 0x01;
            case AVAB -> 0x02;
            case CMX -> 0x03;
            case ADB_62_5 -> 0x04;
            case ART_NET -> 0x05;
            case DALI -> 0x06;
            case UNKNOWN -> 0x00;
        };
    }

    private static String formatNodeReport(NodeReport report) {
        if (report == null) {
            return "";
        }

        NodeReport.NodeReportStatusCode statusCode = report.getStatusCode();
        int code = statusCode == null ? 0 : statusCode.getValue();
        int counter = Math.floorMod(report.getCounter(), 10_000);
        String message = report.getMessage() == null ? "" : report.getMessage();

        return String.format(Locale.ROOT, "#%04X [%04d] %s", code, counter, message);
    }

    public enum StyleCodes {
        StNode(0x00),
        StController(0x01),
        StMedia(0x02),
        StRoute(0x03),
        StBackup(0x04),
        StConfig(0x05),
        StVisual(0x06);

        private final int value;

        StyleCodes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static StyleCodes fromValue(int value) {
            for (StyleCodes style : values()) {
                if (style.value == value) {
                    return style;
                }
            }
            return null;
        }
    }
}

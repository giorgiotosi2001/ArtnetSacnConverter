package com.coemar.bridge.artnet;

import com.coemar.bridge.artnet.codes.ArtNetOpCode;
import com.coemar.bridge.model.Packet;
import com.coemar.bridge.model.artnet.packets.incoming.ArtAddressPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtCommandPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDataRequestPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDiagDataPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDmxPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtIpProgPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtTimeCodePacket;
import com.coemar.bridge.model.artnet.fields.ArtPollFlags;
import com.coemar.bridge.model.artnet.packets.incoming.ArtPollPacket;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.coemar.bridge.util.BinaryParseUtils.*;

public class ArtNetParser {

    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);

    public static boolean looksLikeArtNet(byte[] data, int length) {
        if (length < 10) return false;
        return Arrays.equals(Arrays.copyOfRange(data, 0, 8), ARTNET_ID);
    }

    public static Packet parse(byte[] data, int length) {

        require(length >= 10, "Pacchetto Art-Net troppo corto");

        ArtNetOpCode opCode = ArtNetOpCode.fromValue(u16le(data, 8));

        switch (Objects.requireNonNull(opCode)) {

            case OpOutput:
                return parseArtDmx(data, length);
            case OpPoll:
                return parseArtPollPacket(data, length);
            case OpAddress:
                return parseArtAddressPacket(data, length);
            case OpCommand:
                return parseArtCommandPacket(data, length);
            case OpDataRequest:
                return parseArtDataRequestPacket(data, length);
            case OpDiagData:
                return parseArtDiagDataPacket(data, length);
            case OpIpProg:
                return parseArtIpProgPacket(data, length);
            case OpTimeCode:
                return parseArtTimeCodePacket(data, length);
            default:
                break;
        }

        throw new IllegalArgumentException("OpCode Art-Net non supportato: 0x" + Integer.toHexString(opCode.getValue()));

    }

    private static ArtPollPacket parseArtPollPacket(byte[] data, int length) {
        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        ArtPollFlags flags = new ArtPollFlags(((byte) u8(data, 12)));
        int diagPriority = u8(data, 13);
        int targetPortAddressTop = u16be(data, 14);
        int targetPortAddressBottom = u16be(data, 16);
        int estaManufacturerCode = u16be(data, 18);
        int oemCode = u16be(data, 20);

        return new ArtPollPacket(opCode, protocolVersion, flags, diagPriority, targetPortAddressTop, targetPortAddressBottom, estaManufacturerCode, oemCode);
    }

    private static ArtAddressPacket parseArtAddressPacket(byte[] data, int length) {
        require(length >= 107, "Pacchetto ArtAddress troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int netSwitch = u8(data, 12);
        int bindIndex = u8(data, 13);
        String portName = readNullTerminatedUtf8(data, 14, 18);
        String longName = readNullTerminatedUtf8(data, 32, 64);
        byte[] swIn = Arrays.copyOfRange(data, 96, 100);
        byte[] swOut = Arrays.copyOfRange(data, 100, 104);
        int subSwitch = u8(data, 104);
        int acnPriority = u8(data, 105);
        int command = u8(data, 106);

        return new ArtAddressPacket(
                opCode,
                protocolVersion,
                netSwitch,
                bindIndex,
                portName,
                longName,
                swIn,
                swOut,
                subSwitch,
                acnPriority,
                command
        );
    }

    private static ArtCommandPacket parseArtCommandPacket(byte[] data, int length) {
        require(length >= 18, "Pacchetto ArtCommand troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int estaManufacturerCode = u16be(data, 12);
        int textLength = u16be(data, 14);

        require(textLength >= 0 && textLength <= 512, "Text length ArtCommand fuori range");
        require(length >= 16 + textLength, "Payload ArtCommand incompleto");

        byte[] commandData = Arrays.copyOfRange(data, 16, 16 + textLength);

        return new ArtCommandPacket(
                opCode,
                protocolVersion,
                estaManufacturerCode,
                textLength,
                commandData
        );
    }

    private static ArtDataRequestPacket parseArtDataRequestPacket(byte[] data, int length) {
        require(length >= 40, "Pacchetto ArtDataRequest troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int estaManufacturerCode = u16be(data, 12);
        int oemCode = u16be(data, 14);
        int requestCode = u16be(data, 16);
        byte[] spare = Arrays.copyOfRange(data, 18, 40);

        return new ArtDataRequestPacket(
                opCode,
                protocolVersion,
                estaManufacturerCode,
                oemCode,
                requestCode,
                spare
        );
    }

    private static ArtDiagDataPacket parseArtDiagDataPacket(byte[] data, int length) {
        require(length >= 18, "Pacchetto ArtDiagData troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int diagPriority = u8(data, 13);
        int logicalPort = u8(data, 14);
        int filler3 = u8(data, 15);
        int textLength = u16be(data, 16);

        require(textLength >= 0 && textLength <= 512, "Text length ArtDiagData fuori range");
        require(length >= 18 + textLength, "Payload ArtDiagData incompleto");

        byte[] diagData = Arrays.copyOfRange(data, 18, 18 + textLength);

        return new ArtDiagDataPacket(
                opCode,
                protocolVersion,
                filler1,
                diagPriority,
                logicalPort,
                filler3,
                textLength,
                diagData
        );
    }

    private static ArtTimeCodePacket parseArtTimeCodePacket(byte[] data, int length) {
        require(length >= 19, "Pacchetto ArtTimeCode troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int streamId = u8(data, 13);
        int frames = u8(data, 14);
        int seconds = u8(data, 15);
        int minutes = u8(data, 16);
        int hours = u8(data, 17);
        int type = u8(data, 18);

        return new ArtTimeCodePacket(
                opCode,
                protocolVersion,
                filler1,
                streamId,
                frames,
                seconds,
                minutes,
                hours,
                type
        );
    }

    public static ArtDmxPacket parseArtDmx(byte[] data, int lengthData) {

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int sequence = u8(data, 12);
        int physical = u8(data, 13);
        int subUni = u8(data, 14);
        int net = u8(data, 15);
        int portAddress = ((net & 0x7F) << 8) | subUni;
        int length = u16be(data, 16);

        require(length >= 2 && length <= 512, "Length ArtDmx fuori range");
        require(lengthData >= 18 + length, "Payload ArtDmx incompleto");

        byte[] dmxData = Arrays.copyOfRange(data, 18, 18 + length);

        return new ArtDmxPacket(opCode, protocolVersion, sequence, physical, subUni, net, portAddress, length, dmxData);
    }

    private static ArtIpProgPacket parseArtIpProgPacket(byte[] data, int length) {
        require(length >= 34, "Pacchetto ArtIpProg troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int filler2 = u8(data, 13);
        int command = u8(data, 14);
        int filler4 = u8(data, 15);
        byte[] progIpAddress = Arrays.copyOfRange(data, 16, 20);
        byte[] progSubnetMask = Arrays.copyOfRange(data, 20, 24);
        int progPort = u16be(data, 24);
        byte[] progDefaultGateway = Arrays.copyOfRange(data, 26, 30);
        byte[] spare = Arrays.copyOfRange(data, 30, 34);

        return new ArtIpProgPacket(
                opCode,
                protocolVersion,
                filler1,
                filler2,
                command,
                filler4,
                progIpAddress,
                progSubnetMask,
                progPort,
                progDefaultGateway,
                spare
        );
    }


}

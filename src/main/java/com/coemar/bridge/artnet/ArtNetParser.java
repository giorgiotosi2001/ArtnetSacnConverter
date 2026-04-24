package com.coemar.bridge.artnet;

import com.coemar.bridge.model.Packet;
import com.coemar.bridge.model.artnet.packets.incoming.ArtAddressPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtCommandPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDataRequestPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDiagDataPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtDmxPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtFirmwareMasterPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtInputPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtIpProgPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtNzsPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtRdmPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtRdmSubPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtSyncPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtTimeCodePacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtTodControlPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtTodRequestPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtTriggerPacket;
import com.coemar.bridge.model.artnet.packets.incoming.ArtVlcPacket;
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
            case OpNzs:
                return parseArtNzs(data, length);
            case OpPoll:
                return parseArtPollPacket(data, length);
            case OpRdm:
                return parseArtRdmPacket(data, length);
            case OpRdmSub:
                return parseArtRdmSubPacket(data, length);
            case OpAddress:
                return parseArtAddressPacket(data, length);
            case OpCommand:
                return parseArtCommandPacket(data, length);
            case OpDataRequest:
                return parseArtDataRequestPacket(data, length);
            case OpDiagData:
                return parseArtDiagDataPacket(data, length);
            case OpFirmwareMaster:
                return parseArtFirmwareMasterPacket(data, length);
            case OpInput:
                return parseArtInputPacket(data, length);
            case OpIpProg:
                return parseArtIpProgPacket(data, length);
            case OpSync:
                return parseArtSyncPacket(data, length);
            case OpTimeCode:
                return parseArtTimeCodePacket(data, length);
            case OpTodControl:
                return parseArtTodControlPacket(data, length);
            case OpTodRequest:
                return parseArtTodRequestPacket(data, length);
            case OpTrigger:
                return parseArtTriggerPacket(data, length);
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

    private static ArtRdmPacket parseArtRdmPacket(byte[] data, int length) {
        require(length >= 24, "Pacchetto ArtRdm troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int rdmVersion = u8(data, 12);
        int filler2 = u8(data, 13);
        byte[] spare = Arrays.copyOfRange(data, 14, 19);
        int fifoAvail = u8(data, 19);
        int fifoMax = u8(data, 20);
        int net = u8(data, 21);
        int command = u8(data, 22);
        int address = u8(data, 23);
        byte[] rdmPacket = Arrays.copyOfRange(data, 24, length);

        return new ArtRdmPacket(
                opCode,
                protocolVersion,
                rdmVersion,
                filler2,
                spare,
                fifoAvail,
                fifoMax,
                net,
                command,
                address,
                rdmPacket
        );
    }

    private static ArtRdmSubPacket parseArtRdmSubPacket(byte[] data, int length) {
        require(length >= 32, "Pacchetto ArtRdmSub troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int rdmVersion = u8(data, 12);
        int filler2 = u8(data, 13);
        byte[] uid = Arrays.copyOfRange(data, 14, 20);
        int spare1 = u8(data, 20);
        int commandClass = u8(data, 21);
        int parameterId = u16be(data, 22);
        int subDevice = u16be(data, 24);
        int subCount = u16be(data, 26);
        byte[] spareTail = Arrays.copyOfRange(data, 28, 32);
        byte[] dataBytes = Arrays.copyOfRange(data, 32, length);

        require(subCount > 0, "SubCount ArtRdmSub deve essere maggiore di zero");
        require((dataBytes.length & 1) == 0, "Payload ArtRdmSub deve avere lunghezza pari");

        ArtRdmSubPacket.CommandClass commandClassType = ArtRdmSubPacket.CommandClass.fromValue(commandClass);
        int expectedWordCount = switch (commandClassType) {
            case Get, SetResponse -> 0;
            case Set, GetResponse -> subCount;
            case Unknown -> dataBytes.length / 2;
        };

        if (commandClassType != ArtRdmSubPacket.CommandClass.Unknown) {
            require(dataBytes.length == expectedWordCount * 2, "Payload ArtRdmSub non coerente con CommandClass e SubCount");
        }

        int[] dataWords = new int[dataBytes.length / 2];
        for (int i = 0; i < dataWords.length; i++) {
            dataWords[i] = u16be(dataBytes, i * 2);
        }

        return new ArtRdmSubPacket(
                opCode,
                protocolVersion,
                rdmVersion,
                filler2,
                uid,
                spare1,
                commandClass,
                parameterId,
                subDevice,
                subCount,
                spareTail,
                dataWords
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

    private static ArtInputPacket parseArtInputPacket(byte[] data, int length) {
        require(length >= 20, "Pacchetto ArtInput troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int bindIndex = u8(data, 13);
        int numPorts = u16be(data, 14);
        byte[] input = Arrays.copyOfRange(data, 16, 20);

        require(numPorts >= 0 && numPorts <= 4, "NumPorts ArtInput fuori range");

        return new ArtInputPacket(
                opCode,
                protocolVersion,
                filler1,
                bindIndex,
                numPorts,
                input
        );
    }

    private static ArtFirmwareMasterPacket parseArtFirmwareMasterPacket(byte[] data, int length) {
        require(length >= 552, "Pacchetto ArtFirmwareMaster troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int filler2 = u8(data, 13);
        int type = u8(data, 14);
        int blockId = u8(data, 15);
        long firmwareLength = u32be(data, 16) & 0xFFFF_FFFFL;
        byte[] spare = Arrays.copyOfRange(data, 20, 40);
        byte[] firmwareData = Arrays.copyOfRange(data, 40, 552);

        return new ArtFirmwareMasterPacket(
                opCode,
                protocolVersion,
                filler1,
                filler2,
                type,
                blockId,
                firmwareLength,
                spare,
                firmwareData
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

    private static ArtTodRequestPacket parseArtTodRequestPacket(byte[] data, int length) {
        require(length >= 56, "Pacchetto ArtTodRequest troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int filler2 = u8(data, 13);
        byte[] spare = Arrays.copyOfRange(data, 14, 21);
        int net = u8(data, 21);
        int command = u8(data, 22);
        int addCount = u8(data, 23);
        byte[] address = Arrays.copyOfRange(data, 24, 56);

        require(addCount >= 0 && addCount <= 32, "AddCount ArtTodRequest fuori range");

        return new ArtTodRequestPacket(
                opCode,
                protocolVersion,
                filler1,
                filler2,
                spare,
                net,
                command,
                addCount,
                address
        );
    }

    private static ArtTodControlPacket parseArtTodControlPacket(byte[] data, int length) {
        require(length >= 24, "Pacchetto ArtTodControl troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int filler2 = u8(data, 13);
        byte[] spare = Arrays.copyOfRange(data, 14, 21);
        int net = u8(data, 21);
        int command = u8(data, 22);
        int address = u8(data, 23);

        return new ArtTodControlPacket(
                opCode,
                protocolVersion,
                filler1,
                filler2,
                spare,
                net,
                command,
                address
        );
    }

    private static ArtSyncPacket parseArtSyncPacket(byte[] data, int length) {
        require(length >= 14, "Pacchetto ArtSync troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int aux1 = u8(data, 12);
        int aux2 = u8(data, 13);

        return new ArtSyncPacket(
                opCode,
                protocolVersion,
                aux1,
                aux2
        );
    }

    private static ArtTriggerPacket parseArtTriggerPacket(byte[] data, int length) {
        require(length >= 523, "Pacchetto ArtTrigger troppo corto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int filler1 = u8(data, 12);
        int filler2 = u8(data, 13);
        int oemCode = u16be(data, 14);
        int key = u8(data, 16);
        int subKey = u8(data, 17);
        byte[] triggerData = Arrays.copyOfRange(data, 18, 530);

        return new ArtTriggerPacket(
                opCode,
                protocolVersion,
                filler1,
                filler2,
                oemCode,
                key,
                subKey,
                triggerData
        );
    }

    public static ArtDmxPacket parseArtDmx(byte[] data, int lengthData) {
        require(lengthData >= 18, "Header ArtDmx incompleto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int sequence = u8(data, 12);
        int physical = u8(data, 13);
        int subUni = u8(data, 14);
        int net = u8(data, 15);
        int portAddress = ((net & 0x7F) << 8) | subUni;
        int length = u16be(data, 16);

        require(length >= 2 && length <= 512, "Length ArtDmx fuori range");
        require((length & 1) == 0, "Length ArtDmx deve essere pari");
        require(lengthData >= 18 + length, "Payload ArtDmx incompleto");

        byte[] dmxData = Arrays.copyOfRange(data, 18, 18 + length);

        return new ArtDmxPacket(opCode, protocolVersion, sequence, physical, subUni, net, portAddress, length, dmxData);
    }

    private static ArtNzsPacket parseArtNzs(byte[] data, int lengthData) {
        require(lengthData >= 18, "Header ArtNzs incompleto");

        int opCode = u16le(data, 8);
        int protocolVersion = u16be(data, 10);
        int sequence = u8(data, 12);
        int startCode = u8(data, 13);
        int subUni = u8(data, 14);
        int net = u8(data, 15);
        int portAddress = ((net & 0x7F) << 8) | subUni;
        int length = u16be(data, 16);

        require(length >= 1 && length <= 512, "Length ArtNzs fuori range");
        require(startCode != 0x00, "StartCode ArtNzs non puo essere zero");
        require(startCode != 0xCC, "StartCode ArtNzs non puo essere RDM");
        require(lengthData >= 18 + length, "Payload ArtNzs incompleto");

        byte[] nzsData = Arrays.copyOfRange(data, 18, 18 + length);

        if (looksLikeArtVlc(startCode, nzsData)) {
            return parseArtVlcPacket(opCode, protocolVersion, sequence, startCode, subUni, net, portAddress, length, nzsData);
        }

        return new ArtNzsPacket(opCode, protocolVersion, sequence, startCode, subUni, net, portAddress, length, nzsData);
    }

    private static boolean looksLikeArtVlc(int startCode, byte[] nzsData) {
        return startCode == 0x09
                && nzsData.length >= 22
                && u8(nzsData, 0) == 0x41
                && u8(nzsData, 1) == 0x4C
                && u8(nzsData, 2) == 0x45;
    }

    private static ArtVlcPacket parseArtVlcPacket(int opCode, int protocolVersion, int sequence, int startCode,
                                                  int subUni, int net, int portAddress, int length, byte[] nzsData) {
        int flags = u8(nzsData, 3);
        int transactionNumber = u16be(nzsData, 4);
        int slotAddress = u16be(nzsData, 6);
        int payloadCount = u16be(nzsData, 8);
        int payloadChecksum = u16be(nzsData, 10);
        int spare1 = u8(nzsData, 12);
        int vlcDepth = u8(nzsData, 13);
        int vlcFrequency = u16be(nzsData, 14);
        int vlcModulationType = u16be(nzsData, 16);
        int payloadLanguage = u16be(nzsData, 18);
        int beaconRepeatFrequency = u16be(nzsData, 20);

        require(payloadCount >= 0 && payloadCount <= 480, "PayloadCount ArtVlc fuori range");
        require(length == 22 + payloadCount, "Length ArtVlc non coerente con PayloadCount");

        byte[] payload = Arrays.copyOfRange(nzsData, 22, 22 + payloadCount);

        return new ArtVlcPacket(
                opCode,
                protocolVersion,
                sequence,
                startCode,
                subUni,
                net,
                portAddress,
                length,
                nzsData,
                flags,
                transactionNumber,
                slotAddress,
                payloadCount,
                payloadChecksum,
                spare1,
                vlcDepth,
                vlcFrequency,
                vlcModulationType,
                payloadLanguage,
                beaconRepeatFrequency,
                payload
        );
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

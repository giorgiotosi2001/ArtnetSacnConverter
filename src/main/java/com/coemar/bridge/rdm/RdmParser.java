
package com.coemar.bridge.rdm;

import java.util.Arrays;

import static com.coemar.bridge.util.BinaryParseUtils.require;
import static com.coemar.bridge.util.BinaryParseUtils.u16be;
import static com.coemar.bridge.util.BinaryParseUtils.u8;

public class RdmParser {

    public static boolean looksLikeRdm(byte[] data, int length) {
        return data != null
                && length >= 24
                && u8(data, 0) == 0xCC
                && u8(data, 1) == 0x01;
    }

    public static RdmFrame parse(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Pacchetto RDM null");
        }
        return parse(data, data.length);
    }

    public static RdmFrame parse(byte[] data, int length) {
        require(data != null, "Pacchetto RDM null");
        require(length >= 24, "Pacchetto RDM troppo corto");
        require(length <= data.length, "Length RDM oltre il buffer");

        int offset = 0;

        int startCode = u8(data, offset++);
        int subStartCode = u8(data, offset++);
        int messageLength = u8(data, offset++);

        require(startCode == 0xCC, "Start Code RDM non valido");
        require(subStartCode == 0x01, "Sub Start Code RDM non valido");
        require(length == messageLength + 2, "Message Length non coerente");
        require(messageLength >= 22 && messageLength <= 255, "Message Length RDM fuori range");

        byte[] uidDest = Arrays.copyOfRange(data, offset, offset + 6);
        offset += 6;

        byte[] uidSource = Arrays.copyOfRange(data, offset, offset + 6);
        offset += 6;

        int tn = u8(data, offset++);
        int portId = u8(data, offset++);
        int messageCount = u8(data, offset++);

        int subDevice = u16be(data, offset);
        offset += 2;

        int commandClass = u8(data, offset++);

        int parameterId = u16be(data, offset);
        offset += 2;

        int pdl = u8(data, offset++);

        require(offset + pdl == length - 2, "PDL non coerente con Message Length");

        byte[] parameterData = Arrays.copyOfRange(data, offset, offset + pdl);
        offset += pdl;

        int checksum = u16be(data, offset);

        int calc = 0;
        for (int i = 0; i < messageLength; i++) {
            calc += (data[i] & 0xFF);
            calc &= 0xFFFF;
        }

        require(calc == checksum, "Checksum non valido");

        return new RdmFrame(
                startCode,
                subStartCode,
                messageLength,
                uidDest,
                uidSource,
                tn,
                portId,
                messageCount,
                subDevice,
                commandClass,
                parameterId,
                pdl,
                parameterData,
                checksum
        );
    }
}

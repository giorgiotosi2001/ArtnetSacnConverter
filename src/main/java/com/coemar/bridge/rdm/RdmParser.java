
package com.coemar.bridge.rdm;

public class RdmParser {

    public static RdmFrame parse(byte[] data) {

        if (data == null || data.length < 24) {
            throw new IllegalArgumentException("Pacchetto RDM troppo corto");
        }

        int offset = 0;

        int startCode = data[offset++] & 0xFF;          // 0
        int subStartCode = data[offset++] & 0xFF;       // 1
        int messageLength = data[offset++] & 0xFF;      // 2

        // VALIDAZIONE BASE
        if (data.length != messageLength + 2) {
            throw new IllegalArgumentException("Message Length non coerente");
        }

        // UID DEST (3-8)
        byte[] uidDest = new byte[6];
        System.arraycopy(data, offset, uidDest, 0, 6);
        offset += 6;

        // UID SRC (9-14)
        byte[] uidSource = new byte[6];
        System.arraycopy(data, offset, uidSource, 0, 6);
        offset += 6;

        int tn = data[offset++] & 0xFF;                 // 15
        int portId = data[offset++] & 0xFF;             // 16
        int messageCount = data[offset++] & 0xFF;       // 17

        int subDevice =
                ((data[offset++] & 0xFF) << 8) |
                        (data[offset++] & 0xFF);                // 18-19

        int commandClass = data[offset++] & 0xFF;       // 20

        int parameterId =
                ((data[offset++] & 0xFF) << 8) |
                        (data[offset++] & 0xFF);                // 21-22

        int pdl = data[offset++] & 0xFF;                // 23

        // PARAMETER DATA (24 -> ...)
        if (offset + pdl > data.length - 2) {
            throw new IllegalArgumentException("PDL non valido");
        }

        byte[] parameterData = new byte[pdl];
        System.arraycopy(data, offset, parameterData, 0, pdl);
        offset += pdl;

        // CHECKSUM
        int checksum =
                ((data[offset++] & 0xFF) << 8) |
                        (data[offset] & 0xFF);

        // VALIDAZIONE CHECKSUM
        int calc = 0;
        for (int i = 0; i < messageLength; i++) {
            calc += (data[i] & 0xFF);
            calc &= 0xFFFF;
        }

        if (calc != checksum) {
            throw new IllegalArgumentException("Checksum non valido");
        }

        // COSTRUZIONE OGGETTO
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

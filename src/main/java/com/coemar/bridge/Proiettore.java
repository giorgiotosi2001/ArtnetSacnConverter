package com.coemar.bridge;

import com.coemar.bridge.rdm.CommandPacket;
import com.coemar.bridge.rdm.RdmPacket;
import com.fazecast.jSerialComm.SerialPort;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;

import com.coemar.bridge.rdm.RdmConstants;

import static com.coemar.bridge.rdm.RdmConstants.*;

public class Proiettore implements Runnable {
    public volatile boolean setCH = false;
    public volatile boolean isDeviceInfoReceived = false;
    public volatile boolean isDeviceModelDescriptionReceived = false;
    public volatile boolean isReceivedVersion = false;
    protected String MODEL_ID = "", VERSION_ID = "";
    protected String DEVICE_MODEL_DESCRIPTION_ID = "";
    protected final Object lock = new Object();
    protected final byte[] uidSrc = SRC_UID;
    public static final byte[] SRC_UID = new byte[]{
            (byte) 0x43,
            (byte) 0x4D,
            (byte) 0x49,
            (byte) 0x4D,
            (byte) 0x4D,
            (byte) 0x49
    };
    public static int tn = 0x00;
    public volatile byte[] uidDst;
    private final PriorityBlockingQueue<CommandPacket> codaComandi = new PriorityBlockingQueue<>();
    protected final ByteArrayOutputStream bufferIn = new ByteArrayOutputStream();
    public byte[] buffer_trasmissione;
    protected SerialPort porta;
    public String port;
    protected byte[] rdmBufferIn = new byte[513];  // buffer di ricezione
    protected int rdmOffsetRead = 0;
    protected boolean fResetDataRead = false;
    boolean fNewDataRead = false;
    protected boolean fNeedRetransmit = false;

    Proiettore (String port) {
        this.port = port;
        this.porta = SerialPort.getCommPort(port);
    }

    public CommandPacket prelevaComando() throws InterruptedException {
        return codaComandi.take();
    }

    public void inserisciComando(CommandPacket commandPacket) {
        codaComandi.add(commandPacket);
    }

    public boolean prepareAndOpenPort() {
        if (porta == null) {
            return false;
        }
        porta.setComPortParameters(RDM_BAUDRATE, 8, SerialPort.TWO_STOP_BITS, SerialPort.NO_PARITY);
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        return porta.openPort();
    }

    public void closePort() {
        if (porta == null) {
            return;
        }
        porta.closePort();
    }

    public void sendBytes(byte[] payload) {
        if (porta == null || !porta.isOpen()) {
            return;
        }
        try {
            porta.setBreak();

            Thread.sleep(1);
            porta.clearBreak();
            buffer_trasmissione = payload;

//            for (byte b : buffer_trasmissione) {
//                System.out.printf("%02X", b);
//            }
            //System.out.println();
            int bytesWritten = porta.writeBytes(buffer_trasmissione, buffer_trasmissione.length);
            if (bytesWritten == -1) {
                System.out.println("Errore nella scrittura sulla porta seriale");
            } else {
                //System.out.println("Bytes scritti: " + bytesWritten);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void getBytes() {
        //System.out.println(">>> getBytes() chiamata");
        if (port == null) {
            return;
        }
        if (porta == null || !porta.isOpen()) {
            return;
        }
        int bytesAvailable = porta.bytesAvailable();
        if (bytesAvailable != 0) {
            //System.out.println("Bytes disponibili sulla porta seriale: " + bytesAvailable);
        }
        //System.out.println("Bytes disponibili: " + bytesAvailable);

        if (bytesAvailable > 0) {
            byte[] tempBuffer = new byte[bytesAvailable];
            //System.out.println("Buffer temporaneo creato di dimensione: " + tempBuffer.length);

            int bytesRead = porta.readBytes(tempBuffer, tempBuffer.length);
            //System.out.println("Bytes letti: " + bytesRead);

            if (bytesRead > 0) {
                bufferIn.write(tempBuffer, 0, bytesRead);
                //System.out.println("Dati scritti su bufferIn. Lunghezza dati scritti: " + bytesRead);

                processBuffer();
                //System.out.println("processBuffer() chiamata");
            } else {
                //System.out.println("Nessun byte letto (bytesRead <= 0)");
            }
        } else {

        }
    }

    public byte[] readPacket() throws InterruptedException {
        //if (!fNewDataRead) return null;
        //rdmBufferIn Ã¨ un array piÃ¹ grande (es. 1024 byte) usato per ricevere i dati dalla seriale.
        //rdmOffsetRead rappresenta quanti byte validi ci sono attualmente nel buffer.
        byte[] packet = Arrays.copyOf(rdmBufferIn, rdmOffsetRead);
        resetRX();
        return packet;
    }

    public void resetRX() throws InterruptedException {
        rdmOffsetRead = 0;
        fResetDataRead = false;
        fNewDataRead = false;
        Arrays.fill(rdmBufferIn, (byte) 0);

    }

    protected void processBuffer() {
        byte[] data = bufferIn.toByteArray();
//        System.out.println(">> processBuffer() chiamata");
//        System.out.println("data.length: " + data.length);

        if (data.length < 24) {
//            System.out.println("âš ï¸ Buffer troppo corto per processare pacchetti");
        }

        int index = 0;

        while (data.length - index >= 24) { // dimensione minima plausibile
//            System.out.printf("âž¡ï¸ Controllo a index %d: byte = 0x%02X%n", index, data[index]);

            boolean pacchettoRDM = false;
            boolean pacchettoDISCOVERY = false;

            // Verifica se Ã¨ un pacchetto RDM (Start Code 0xCC)
            if ((data[index] & 0xFF) == 0xCC) {
//                System.out.println("Trovato possibile pacchetto RDM");
                pacchettoRDM = true;
            }

            // Verifica se Ã¨ una possibile risposta DISCOVERY (Pattern noto)
            if ((data[index] & 0xFF) == 0xFE &&          // Slot 1 (index + 0) = 0xFE
                    (data[index + 1] & 0xFF) == 0xFE &&      // Slot 2 (index + 1) = 0xFE
                    (data[index + 2] & 0xFF) == 0xFE &&      // Slot 3 (index + 2) = 0xFE
                    (data[index + 3] & 0xFF) == 0xFE &&      // Slot 4 (index + 3) = 0xFE
                    (data[index + 4] & 0xFF) == 0xFE &&      // Slot 5 (index + 4) = 0xFE
                    (data[index + 5] & 0xFF) == 0xFE &&      // Slot 6 (index + 5) = 0xFE
                    (data[index + 6] & 0xFF) == 0xFE &&      // Slot 7 (index + 6) = 0xFE
                    (data[index + 7] & 0xFF) == 0xAA) {      // Slot 8 (index + 7) = 0xAA (Preamble separator)
//                System.out.println("Trovato possibile pacchetto DISCOVERY");
                pacchettoDISCOVERY = true;
            }

            if (!pacchettoRDM && !pacchettoDISCOVERY) {
                index++;
                continue;
            }

            // Pacchetto RDM
            if (pacchettoRDM) {
                if (data.length - index < 24) {
//                    System.out.println("Pacchetto RDM troppo corto, esco");
                    break; // pacchetto troppo corto
                }

                int length = (data[index + 2] & 0xFF);
                int totalLength = length + 2;

                if (index + totalLength > data.length) {
//                    System.out.println("Pacchetto RDM incompleto, esco");
                    break; // incompleto
                }

                byte[] packet = Arrays.copyOfRange(data, index, index + totalLength);

                if (isValidRdmResponse(packet)) {
                    //System.out.println("… Pacchetto RDM valido");
                    fNewDataRead = true;
                    rdmOffsetRead = packet.length;
                    rdmBufferIn = Arrays.copyOf(packet, rdmOffsetRead);
                    fNeedRetransmit = false;
                } else {
                    System.out.println(" Pacchetto RDM non valido, scarto");
                    stampaHex(packet);
                    fNeedRetransmit = true;

                }

                index += totalLength;
                continue;
            }

            // Pacchetto DISCOVERY
            int discLen = 24; // puÃ² essere 24 o piÃ¹, verifica con specifica
            if (index + discLen > data.length) {
                System.out.println("Pacchetto DISCOVERY incompleto, esco");
                break; // incompleto
            }

            byte[] packet = Arrays.copyOfRange(data, index, index + discLen);
            if (isValidDiscoveryResponse(packet)) {
                System.out.println("✅ Pacchetto DISCOVERY trovato");
                fNewDataRead = true;
                rdmOffsetRead = packet.length;
                rdmBufferIn = Arrays.copyOf(packet, rdmOffsetRead);
            }

            index += discLen;
        }

        // Ricostruisci il buffer con i byte residui non ancora completi
        bufferIn.reset();
        if (index < data.length) {
//            System.out.println("Byte residui rimasti nel buffer: " + (data.length - index));
            bufferIn.write(data, index, data.length - index);
        }
    }

    public boolean isValidRdmResponse(byte[] packet) {

        if (packet == null || packet.length < 24) {
            System.out.println("Pacchetto troppo corto o nullo.");
            return false;
        }


        // Start Code e Sub Start Code
        if (packet[0] != (byte) 0xCC || packet[1] != (byte) 0x01) {
            System.out.println("Start code non valido.");
            return false;
        }

        int lengthFromPacket = packet[2] & 0xFF;  // unsigned
        int totalLength = lengthFromPacket + 2;   // come in buildPacket

        if (packet.length != totalLength) {
            System.out.println("Lunghezza pacchetto non corrisponde.");
            return false;
        }

        // Calcolo checksum
        long checksum = 0;
        for (int i = 0; i < totalLength - 2; i++) {
            checksum += (packet[i] & 0xFF);
            checksum &= 0xFFFF; // limit a 16 bit
        }

        int checksumIndex = totalLength - 2;
        int packetChecksum = ((packet[checksumIndex] & 0xFF) << 8) | (packet[checksumIndex + 1] & 0xFF);

        if ((checksum & 0xFFFF) != packetChecksum) {
            System.out.println("Checksum non valido.");
            return false;
        }

        return true;
    }

    public boolean isValidDiscoveryResponse(byte[] packet) {
        if (packet.length == 24) {
            int expectedChecksum = 0;

            for (int i = 8; i <= 19; i++) {
                expectedChecksum += (packet[i] & 0xFF);
            }

            expectedChecksum = expectedChecksum & 0xFFFF;

            if (convertBytesToChecksumInt(parseChecksumFromResponse(packet)) == expectedChecksum) {
                System.out.println("Pacchetto Discovery valido");
                return true; // Pacchetto Discovery valido
            }
            System.out.println("Pacchetto Discovery NON valido");
            return false; // Pacchetto Discovery valido
        }
        System.out.println("Pacchetto Discovery NON valido");
        return false; // Pacchetto Discovery valido
    }
    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (byte value : b) {
            result = (result << 8) | (value & 0xFF);
        }
        return result;
    }

    private boolean attemptDiscovery(int timeoutMs) {

        if (porta == null || !porta.isOpen()) {
            System.err.println("[DISCOVERY] Porta non disponibile per la discovery.");
            return false;
        }

        final int maxAttempts = 6;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            // Broadcast UN_MUTE
            RdmPacket unmute = new RdmPacket(
                    new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                    SRC_UID,
                    (byte) 0x00,
                    E120_DISCOVERY_COMMAND,
                    0x0003,
                    new byte[0]);
            sendBytes(unmute.buildPacket());
            try {

                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Broadcast DISC_UNIQUE_BRANCH
            byte[] pd = new byte[12];
            Arrays.fill(pd, 0, 6, (byte) 0x00);
            Arrays.fill(pd, 6, 12, (byte) 0xFF);
            RdmPacket discovery = new RdmPacket(
                    new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                    SRC_UID,
                    (byte) 0x00,
                    E120_DISCOVERY_COMMAND,
                    E120_DISC_UNIQUE_BRANCH,
                    pd);
            sendBytes(discovery.buildPacket());

            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < timeoutMs) {
                getBytes();
                if (fNewDataRead) {
                    try {
                        byte[] responsePacket = readPacket();
                        //stampaHex(responsePacket);
                        uidDst = parseUidFromResponse(responsePacket);
                        if (uidDst != null) {
                            System.out.println("[DISCOVERY] UID trovato su " + porta.getSystemPortName() + ": " + Arrays.toString(uidDst));
                            BigInteger uidNumber = new BigInteger(1, uidDst);
                            System.out.println("[DISCOVERY] UID in formato numerico: " + uidNumber.toString());
                            return true;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrotto durante la discovery", e);
                    }
                }
            }

            System.out.println("[DISCOVERY] Nessuna risposta su " + porta.getSystemPortName() + " (tentativo " + attempt + "/" + maxAttempts + ")");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public static int convertBytesToChecksumInt(byte[] checksumBytes) {
        if (checksumBytes == null || checksumBytes.length != 2) {
            // Gestisci l'errore o lancia un'eccezione se l'array non Ã¨ valido
            throw new IllegalArgumentException("Checksum byte array must be non-null and have a length of 2.");
        }

        // Il primo byte Ã¨ MSB, il secondo Ã¨ LSB
        // (byte[0] & 0xFF) lo converte in un int positivo da 0 a 255
        // << 8 lo sposta di 8 posizioni a sinistra per diventare la parte alta a 16 bit
        // (byte[1] & 0xFF) lo converte in un int positivo da 0 a 255
        // L'operazione | unisce i due valori
        return (((checksumBytes[0] & 0xFF) << 8) | (checksumBytes[1] & 0xFF));
    }

    public byte[] parseUidFromResponse(byte[] data) {
        if (data == null || data.length != 24) return null; // UID deve stare almeno da byte 3 a 8
        byte[] encodedBytes = Arrays.copyOfRange(data, 8, 20);

        byte[] uid = new byte[6];

        for (int i = 0; i < 6; i++) {
            int byte1 = encodedBytes[i * 2] & 0x55;
            int byte2 = encodedBytes[i * 2 + 1] & 0xAA;
            uid[i] = (byte) (byte1 | byte2);
        }

        return uid;
    }

    public byte[] parseChecksumFromResponse(byte[] data) {
        if (data == null || data.length != 24) return null; // Assumiamo ancora risposta di 24 byte

        // Estrai i 4 byte codificati del checksum: data[21], data[22], data[23], data[24]
        // Il range Ã¨ da 'from' (incluso) a 'to' (escluso).
        byte[] encodedChecksumBytes = Arrays.copyOfRange(data, 20, 24); // ECS3, ECS2, ECS1, ECS0

        // Il checksum finale Ã¨ di 2 byte (16 bit)
        byte[] actualChecksum = new byte[2];

        // Decodifica Cheksum1 (MSB)
        // encodedChecksumBytes[0] = ECS3 (data[21]) -> OR with 0xAA
        // encodedChecksumBytes[1] = ECS2 (data[22]) -> OR with 0x55
        int msb_byte1 = encodedChecksumBytes[0] & 0x55;
        int msb_byte2 = encodedChecksumBytes[1] & 0xAA;
        actualChecksum[0] = (byte) (msb_byte1 | msb_byte2); // Questo Ã¨ Cheksum1 (MSB)

        // Decodifica Cheksum0 (LSB)
        // encodedChecksumBytes[2] = ECS1 (data[23]) -> OR with 0xAA
        // encodedChecksumBytes[3] = ECS0 (data[24]) -> OR with 0x55
        int lsb_byte1 = encodedChecksumBytes[2] & 0x55;
        int lsb_byte2 = encodedChecksumBytes[3] & 0xAA;
        actualChecksum[1] = (byte) (lsb_byte1 | lsb_byte2); // Questo Ã¨ Cheksum0 (LSB)

        return actualChecksum;
    }

    public byte[] Command(byte[] pd, int pid, byte cc) {
        if (tn > 255) tn = 0;
        //System.out.println("uidDst = " + Arrays.toString(uidDst));
        //System.out.println("uidSrc = " + Arrays.toString(uidSrc));

        RdmPacket pacchetto = new RdmPacket(uidDst, uidSrc, (byte) tn++, cc, pid, pd);
        byte[] rdmBytes = pacchetto.buildPacket();
        //System.out.println("Bytes da inviare:");
        //System.out.println(Arrays.toString(rdmBytes));

        return rdmBytes;
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b)); // 2 cifre, maiuscole
        }
        return sb.toString();
    }

    public static void stampaHex(byte[] array) {
        for (byte b : array) {
            System.out.printf("%02X ", (int) (b & 0xFF));
        }
        //System.out.println();
    }

    @Override
    public void run() {

        System.out.println("Thread avviato per porta: " + (porta != null ? porta.getSystemPortName() : "nessuna"));
        int timeout = 2000; // 2 secondi
        // Se abbiamo gia' UID/descrizione, salta la discovery
        if (uidDst != null && uidDst.length != 0 && isDeviceModelDescriptionReceived) {
            System.out.println("[DISCOVERY] UID gia' noto, salto discovery su " + porta.getSystemPortName());
        } else {
            if (!attemptDiscovery(timeout)) {
                System.err.println("[DISCOVERY] Nessun UID trovato su " + porta.getSystemPortName() + ", interrompo il thread.");
                return;
            }
        }

        while(true) {
            timeout = 250;
            CommandPacket command;
            try {
                command = prelevaComando();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendBytes(Command(command.getPd(), command.getPid(), command.getCc()));
            long commandStartTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - commandStartTime < timeout) {
                getBytes();
                if (fNewDataRead) {
                    // Inizio blocco atomico: ricezione, elaborazione e notifica
                    synchronized (lock) {
                        int ccFromPacket = 0;
                        int pidFromPacket = 0;
                        byte[] responsePacket = new byte[0];
                        try {
                            responsePacket = readPacket();
                            pidFromPacket = ((responsePacket[21] & 0xFF) << 8) | (responsePacket[22] & 0xFF);
                            ccFromPacket = responsePacket[20] & 0xFF;
                        } catch (InterruptedException e) {

                            // Gestione delle risposte GET
                            if (ccFromPacket == E120_GET_COMMAND_RESPONSE) {
                                switch (pidFromPacket) {
                                    case DEVICE_INFO:
                                        // Logica di estrazione dei dati DEVICE_INFO
                                        int ValueMSB = responsePacket[26] & 0xFF;
                                        int ValueLSB = responsePacket[27] & 0xFF;
                                        //MODEL_ID = String.valueOf((ValueMSB << 8) | ValueLSB);
                                        MODEL_ID = String.format("0x%04X", (ValueMSB << 8) | ValueLSB);
                                        isDeviceInfoReceived = true;
                                        break;
                                    case DEVICE_MODEL_DESCRIPTION:
                                        // Logica di estrazione della descrizione
                                        int pdl = responsePacket[23] & 0xFF;
                                        byte[] modelDescriptionBytes = new byte[pdl];
                                        System.arraycopy(responsePacket, 24, modelDescriptionBytes, 0, pdl);
                                        this.DEVICE_MODEL_DESCRIPTION_ID = new String(modelDescriptionBytes, StandardCharsets.US_ASCII).trim();
                                        System.out.println("DEVICE_MODEL_DESCRIPTION trovato: " + this.DEVICE_MODEL_DESCRIPTION_ID);
                                        isDeviceModelDescriptionReceived = true;
                                        break;
                                    case SOFTWARE_VERSION_ID:
                                        pdl = responsePacket[23] & 0xFF;
                                        byte[] versionBytes = new byte[pdl];
                                        System.arraycopy(responsePacket, 24, versionBytes, 0, pdl);
                                        VERSION_ID = new String(versionBytes, StandardCharsets.US_ASCII).trim();
                                        System.out.println("VERSION_ID: " + VERSION_ID);
                                        isReceivedVersion = true;
                                        break;
                                }
                            }
                            // Gestione delle risposte SET
                            else if (ccFromPacket == E120_SET_COMMAND_RESPONSE && responsePacket[16] == (byte) 0x00) {
                                switch (pidFromPacket) {
                                    case COEMAR_OUTPUTCH:
                                        setCH = true;
                                        break;

                                }
                            }

                            throw new RuntimeException(e);
                        }
                    }}
            }

        }
    }
}

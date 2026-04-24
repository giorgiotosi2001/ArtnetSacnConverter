package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pacchetto di comando testuale application-specific per proprieta o funzioni vendor-specific.
 *
 * - Direzione tipica: controller/nodo/media server -> destinatario Art-Net, in unicast o broadcast.
 * - Risposta attesa: nessuna risposta dedicata a livello Art-Net.
 * - Serve per: inviare una o piu stringhe comando nel formato {@code Command=Data&...}.
 * - Note: il testo e ASCII, case-insensitive e orientato a funzioni applicative, non a DMX.
 */
public class ArtCommandPacket implements Packet {

    private static final int MAX_DATA_LENGTH = 512;

    private final int opCode;
    private final int protocolVersion;
    private final int estaManufacturerCode;
    private final int textLength;
    private final byte[] data;
    private final String commandText;
    private final List<ParsedCommand> parsedCommands;

    public ArtCommandPacket(int opCode, int protocolVersion, int estaManufacturerCode, int textLength, byte[] data) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.estaManufacturerCode = estaManufacturerCode;
        this.textLength = textLength;
        this.data = copyData(data);
        this.commandText = decodeCommandText(this.data);
        this.parsedCommands = parseCommands(this.commandText);
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getEstaManufacturerCode() {
        return estaManufacturerCode;
    }

    public int getTextLength() {
        return textLength;
    }

    public byte[] getData() {
        return data.clone();
    }

    public String getCommandText() {
        return commandText;
    }

    public List<ParsedCommand> getParsedCommands() {
        return List.copyOf(parsedCommands);
    }

    public boolean isBroadcastManufacturerCommand() {
        return estaManufacturerCode == 0xFFFF;
    }

    private static byte[] copyData(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("data non puo essere null");
        }
        if (value.length > MAX_DATA_LENGTH) {
            throw new IllegalArgumentException("data troppo lunga per ArtCommand");
        }
        return value.clone();
    }

    private static String decodeCommandText(byte[] value) {
        int end = 0;
        while (end < value.length && value[end] != 0) {
            end++;
        }
        return new String(value, 0, end, StandardCharsets.US_ASCII);
    }

    private static List<ParsedCommand> parseCommands(String commandText) {
        List<ParsedCommand> commands = new ArrayList<>();

        if (commandText == null || commandText.isBlank()) {
            return commands;
        }

        String[] chunks = commandText.split("&");
        for (String chunk : chunks) {
            if (chunk == null || chunk.isBlank()) {
                continue;
            }

            int separator = chunk.indexOf('=');
            if (separator < 0) {
                commands.add(new ParsedCommand(chunk.trim(), "", chunk.trim().toUpperCase(Locale.ROOT), ""));
                continue;
            }

            String command = chunk.substring(0, separator).trim();
            String value = chunk.substring(separator + 1).trim();
            commands.add(new ParsedCommand(
                    command,
                    value,
                    command.toUpperCase(Locale.ROOT),
                    value
            ));
        }

        return commands;
    }

    @Override
    public String toString() {
        return "ArtCommandPacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", estaManufacturerCode=0x" + Integer.toHexString(estaManufacturerCode) +
                ", textLength=" + textLength +
                ", broadcastManufacturerCommand=" + isBroadcastManufacturerCommand() +
                ", commandText='" + commandText + '\'' +
                ", parsedCommands=" + parsedCommands +
                '}';
    }

    public record ParsedCommand(String command, String value, String normalizedCommand, String normalizedValue) {
        public KnownCommand getKnownCommand() {
            return KnownCommand.fromCommand(normalizedCommand);
        }
    }

    public enum KnownCommand {
        SwoutText("SWOUTTEXT"),
        SwinText("SWINTEXT"),
        Unknown("");

        private final String command;

        KnownCommand(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        public static KnownCommand fromCommand(String value) {
            for (KnownCommand knownCommand : values()) {
                if (knownCommand.command.equals(value)) {
                    return knownCommand;
                }
            }
            return Unknown;
        }
    }
}

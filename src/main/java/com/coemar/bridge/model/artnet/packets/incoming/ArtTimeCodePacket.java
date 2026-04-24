package com.coemar.bridge.model.artnet.packets.incoming;

import com.coemar.bridge.model.Packet;

/**
 * <p>Trasporto di time code su Art-Net, compatibile con formati film, EBU, DF e SMPTE.</p>
 * <ul>
 *   <li><b>Direzione tipica:</b> application-specific; spesso controller -&gt; rete in broadcast.</li>
 *   <li><b>Risposta attesa:</b> nessuna risposta dedicata.</li>
 *   <li><b>Serve per:</b> distribuire time code di sincronizzazione su rete Art-Net.</li>
 *   <li><b>Non fa:</b> non controlla output DMX e non e un pacchetto di configurazione.</li>
 * </ul>
 */
public class ArtTimeCodePacket implements Packet {

    private final int opCode;
    private final int protocolVersion;
    private final int filler1;
    private final int streamId;
    private final int frames;
    private final int seconds;
    private final int minutes;
    private final int hours;
    private final int type;

    public ArtTimeCodePacket(int opCode, int protocolVersion, int filler1, int streamId, int frames, int seconds,
                             int minutes, int hours, int type) {
        this.opCode = opCode;
        this.protocolVersion = protocolVersion;
        this.filler1 = filler1;
        this.streamId = streamId;
        this.frames = frames;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.type = type;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getFiller1() {
        return filler1;
    }

    public int getStreamId() {
        return streamId;
    }

    public boolean isMasterStream() {
        return streamId == 0x00;
    }

    public int getFrames() {
        return frames;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getType() {
        return type;
    }

    public Type getTimeCodeType() {
        return Type.fromValue(type);
    }

    public String getFormattedTimeCode() {
        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frames);
    }

    @Override
    public String toString() {
        return "ArtTimeCodePacket{" +
                "opCode=" + opCode +
                ", protocolVersion=" + protocolVersion +
                ", filler1=" + filler1 +
                ", streamId=" + streamId +
                ", masterStream=" + isMasterStream() +
                ", frames=" + frames +
                ", seconds=" + seconds +
                ", minutes=" + minutes +
                ", hours=" + hours +
                ", type=" + type +
                ", timeCodeType=" + getTimeCodeType() +
                ", formattedTimeCode='" + getFormattedTimeCode() + '\'' +
                '}';
    }

    public enum Type {
        Film(0),
        EBU(1),
        DF(2),
        SMPTE(3),
        Unknown(-1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type fromValue(int value) {
            for (Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return Unknown;
        }
    }
}

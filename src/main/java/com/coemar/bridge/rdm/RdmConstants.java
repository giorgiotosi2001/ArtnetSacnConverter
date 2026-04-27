package com.coemar.bridge.rdm;

public final class RdmConstants {

    private RdmConstants() {
    }

    public static final int RDM_BAUDRATE = 250000;

    public static final byte E120_DISCOVERY_COMMAND = 0x10;
    public static final byte E120_DISCOVERY_COMMAND_RESPONSE = 0x11;
    public static final byte E120_GET_COMMAND = 0x20;
    public static final byte E120_GET_COMMAND_RESPONSE = 0x21;
    public static final byte E120_SET_COMMAND = 0x30;
    public static final byte E120_SET_COMMAND_RESPONSE = 0x31;

    public static final int SENSOR_DEFINITION = 0x0200;
    public static final int SENSOR_VALUE = 0x0201;

    public static final int DEVICE_INFO = 0x0060;
    public static final int DEVICE_MODEL_DESCRIPTION = 0x0080;
    public static final int SOFTWARE_VERSION_ID = 0x00C0;

    public static final int FAN_SPEED_CONTROL_PID = 0x8021;

    public static final int E120_DISC_UNIQUE_BRANCH = 0x0001;
    public static final int E120_DISC_UN_MUTE = 0x0003;

    public static final int COEMAR_OUTPUTCH = 0x801B;

    public static final byte[] BROADCAST_UID = new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };

    public static final byte[] DISCOVERY_LOWER_UID = new byte[]{
            0, 0, 0, 0, 0, 0
    };

    public static final byte[] DISCOVERY_UPPER_UID = new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
    };

    public static final byte[] DEFAULT_SOURCE_UID = new byte[]{
            0x43, 0x4D, 0x49, 0x4D, 0x4D, 0x49
    };

}
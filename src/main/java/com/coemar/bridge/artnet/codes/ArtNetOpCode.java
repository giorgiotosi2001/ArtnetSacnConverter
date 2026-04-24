package com.coemar.bridge.artnet.codes;

public enum ArtNetOpCode {

    OpPoll(0x2000),
    OpPollReply(0x2100),
    OpDiagData(0x2300),
    OpCommand(0x2400),
    OpDataRequest(0x2700),
    OpDataReply(0x2800),
    OpOutput(0x5000),
    OpNzs(0x5100),
    OpSync(0x5200),
    OpAddress(0x6000),
    OpInput(0x7000),
    OpTodRequest(0x8000),
    OpTodData(0x8100),
    OpTodControl(0x8200),
    OpRdm(0x8300),
    OpRdmSub(0x8400),
    OpVideoSetup(0xA010),
    OpVideoPalette(0xA020),
    OpVideoData(0xA040),
    OpMacMaster(0xF000),
    OpMacSlave(0xF100),
    OpFirmwareMaster(0xF200),
    OpFirmwareReply(0xF300),
    OpFileTnMaster(0xF400),
    OpFileFnMaster(0xF500),
    OpFileFnReply(0xF600),
    OpIpProg(0xF800),
    OpIpProgReply(0xF900),
    OpMedia(0x9000),
    OpMediaPatch(0x9100),
    OpMediaControl(0x9200),
    OpMediaControlReply(0x9300),
    OpTimeCode(0x9700),
    OpTimeSync(0x9800),
    OpTrigger(0x9900),
    OpDirectory(0x9A00),
    OpDirectoryReply(0x9B00);

    private final int value;

    ArtNetOpCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ArtNetOpCode fromValue(int value) {
        for (ArtNetOpCode op : values()) {
            if (op.value == value) return op;
        }
        return null; // oppure lanci eccezione
    }
}
package com.coemar.bridge.model.artnet;

import com.coemar.bridge.artnet.ArtNetOpCode;
import com.coemar.bridge.artnet.StyleCodes;

import java.nio.charset.StandardCharsets;

public class ArtPollReplyPacket {
    private static final byte[] ARTNET_ID =
            "Art-Net\u0000".getBytes(StandardCharsets.US_ASCII);
    private final ArtNetOpCode opCode= ArtNetOpCode.OpPollReply;
    private final int[] ipAddress = new int[4];
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
    private final int macAddress;
    private final int[] bindIp = new int[4];
    private final int bindIndex;
    private final ArtPollStatus2 artPollStatus2;
    private final GoodOutputB goodOutputB;
    private final ArtPollStatus3 artPollStatus3;
    private final int defaultRespUID;
    private final int user;
    private final int refreshRate;
    private final int backgroundQueuePolicy;
    private final int filler;

    public ArtPollReplyPacket(int port, int versionInfo, int netSwitch, int subSwitch, int oem, int ubeaVersion, ArtPollStatus1 status1, int estaManCode, String portName, String longName, NodeReport nodeReport, int numPorts, PortTypes portTypes, GoodInput goodInput, GoodOutputA goodOutputA, int swIn, int swOut, int acnPriority, SwMacro swMacro, SwRemote swRemote, StyleCodes style, int macAddress, int bindIndex, ArtPollStatus2 artPollStatus2, GoodOutputB goodOutputB, ArtPollStatus3 artPollStatus3, int defaultRespUID, int user, int refreshRate, int backgroundQueuePolicy, int filler) {
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
        this.macAddress = macAddress;
        this.bindIndex = bindIndex;
        this.artPollStatus2 = artPollStatus2;
        this.goodOutputB = goodOutputB;
        this.artPollStatus3 = artPollStatus3;
        this.defaultRespUID = defaultRespUID;
        this.user = user;
        this.refreshRate = refreshRate;
        this.backgroundQueuePolicy = backgroundQueuePolicy;
        this.filler = filler;
    }


    public byte [] serializeArtPollReplyPacket(){
        // TODO: implementare la serializzazione del pacchetto ArtPollReply
        return new byte[0];}
}

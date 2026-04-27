package com.coemar.bridge.rdm;

import com.coemar.bridge.model.Packet;

public class RdmFrame implements Packet {

    private final int startCode;
    private final int subStartCode;
    private final int messageLength;

    private final byte[] uidDest;
    private final byte[] uidSource;

    private final int transactionNumber;
    private final int portId;
    private final int messageCount;

    private final int subDevice;

    private final int commandClass;
    private final int parameterId;

    private final int parameterDataLength;
    private final byte[] parameterData;

    private final int checksum;

    public RdmFrame(int startCode,
                    int subStartCode,
                    int messageLength,
                    byte[] uidDest,
                    byte[] uidSource,
                    int transactionNumber,
                    int portId,
                    int messageCount,
                    int subDevice,
                    int commandClass,
                    int parameterId,
                    int parameterDataLength,
                    byte[] parameterData,
                    int checksum) {
        this.startCode = startCode;
        this.subStartCode = subStartCode;
        this.messageLength = messageLength;
        this.uidDest = uidDest.clone();
        this.uidSource = uidSource.clone();
        this.transactionNumber = transactionNumber;
        this.portId = portId;
        this.messageCount = messageCount;
        this.subDevice = subDevice;
        this.commandClass = commandClass;
        this.parameterId = parameterId;
        this.parameterDataLength = parameterDataLength;
        this.parameterData = parameterData.clone();
        this.checksum = checksum;
    }

    public int getStartCode() {
        return startCode;
    }

    public int getSubStartCode() {
        return subStartCode;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public byte[] getUidDest() {
        return uidDest.clone();
    }

    public byte[] getUidSource() {
        return uidSource.clone();
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public int getPortId() {
        return portId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getSubDevice() {
        return subDevice;
    }

    public int getCommandClass() {
        return commandClass;
    }

    public int getParameterId() {
        return parameterId;
    }

    public int getParameterDataLength() {
        return parameterDataLength;
    }

    public byte[] getParameterData() {
        return parameterData.clone();
    }

    public int getChecksum() {
        return checksum;
    }


}

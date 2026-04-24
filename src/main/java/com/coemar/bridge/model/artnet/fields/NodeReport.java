package com.coemar.bridge.model.artnet.fields;

public final class NodeReport {

    private final NodeReportStatusCode statusCode;
    private final int counter;
    private final String message;

    public NodeReport(byte[] nodeReport) {
        String report = new String(nodeReport).trim();

        // Formato previsto: "#xxxx [yyyy] zzzzz..."
        String codeHex = report.substring(1, 5);
        int codeValue = Integer.parseInt(codeHex, 16);

        this.statusCode = NodeReportStatusCode.fromValue(codeValue);

        int counterStart = report.indexOf('[');
        int counterEnd = report.indexOf(']');

        this.counter = Integer.parseInt(
                report.substring(counterStart + 1, counterEnd).trim()
        );

        this.message = report.substring(counterEnd + 1).trim();
    }

    public NodeReportStatusCode getStatusCode() {
        return statusCode;
    }

    public int getCounter() {
        return counter;
    }

    public String getMessage() {
        return message;
    }

    public enum NodeReportStatusCode {
        RcDebug(0x0000),
        RcPowerOk(0x0001),
        RcPowerFail(0x0002),
        RcSocketWr1(0x0003),
        RcParseFail(0x0004),
        RcUdpFail(0x0005),
        RcShNameOk(0x0006),
        RcLoNameOk(0x0007),
        RcDmxError(0x0008),
        RcDmxUdpFull(0x0009),
        RcDmxRxFull(0x000A),
        RcSwitchErr(0x000B),
        RcConfigErr(0x000C),
        RcDmxShort(0x000D),
        RcFirmwareFail(0x000E),
        RcUserFail(0x000F),
        RcFactoryRes(0x0010);

        private final int value;

        NodeReportStatusCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static NodeReportStatusCode fromValue(int value) {
            for (NodeReportStatusCode report : values()) {
                if (report.value == value) {
                    return report;
                }
            }
            return null;
        }
    }
}

package com.coemar.bridge.model.artnet.fields;

import com.coemar.bridge.artnet.codes.NodeReportStatusCode;

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
}
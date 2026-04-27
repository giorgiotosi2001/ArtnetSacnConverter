package com.coemar.bridge.rdm;

public class SoftwareVersion extends GetResult {
    private String softwareVersion;

    public SoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }
}

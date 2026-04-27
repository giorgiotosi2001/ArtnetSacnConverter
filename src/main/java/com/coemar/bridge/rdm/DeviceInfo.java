package com.coemar.bridge.rdm;

public class DeviceInfo extends GetResult{
    private String modelIdentifier;

    public DeviceInfo(String manufacturerLabel, String modelDescription, String softwareVersion) {
        this.modelIdentifier = modelDescription;
    }
}

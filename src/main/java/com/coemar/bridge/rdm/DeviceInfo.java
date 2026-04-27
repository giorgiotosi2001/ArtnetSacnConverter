package com.coemar.bridge.rdm;

public class DeviceInfo extends GetResult {

    private final int protocolVersion;
    private final int modelId;
    private final int productCategory;
    private final long softwareVersionId;
    private final int dmxFootprint;
    private final int currentPersonality;
    private final int personalityCount;
    private final int dmxStartAddress;
    private final int subDeviceCount;
    private final int sensorCount;

    public DeviceInfo(int protocolVersion,
                      int modelId,
                      int productCategory,
                      long softwareVersionId,
                      int dmxFootprint,
                      int currentPersonality,
                      int personalityCount,
                      int dmxStartAddress,
                      int subDeviceCount,
                      int sensorCount) {
        this.protocolVersion = protocolVersion;
        this.modelId = modelId;
        this.productCategory = productCategory;
        this.softwareVersionId = softwareVersionId;
        this.dmxFootprint = dmxFootprint;
        this.currentPersonality = currentPersonality;
        this.personalityCount = personalityCount;
        this.dmxStartAddress = dmxStartAddress;
        this.subDeviceCount = subDeviceCount;
        this.sensorCount = sensorCount;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getModelId() {
        return modelId;
    }

    public int getProductCategory() {
        return productCategory;
    }

    public long getSoftwareVersionId() {
        return softwareVersionId;
    }

    public int getDmxFootprint() {
        return dmxFootprint;
    }

    public int getCurrentPersonality() {
        return currentPersonality;
    }

    public int getPersonalityCount() {
        return personalityCount;
    }

    public int getDmxStartAddress() {
        return dmxStartAddress;
    }

    public int getSubDeviceCount() {
        return subDeviceCount;
    }

    public int getSensorCount() {
        return sensorCount;
    }
}

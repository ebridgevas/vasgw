package com.ebridgevas.model;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 3/15/13
 * Time: 5:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class MTSM {

    private String destinationAddress;
    private String sourceAddress;
    private String shortMessage;

    public MTSM(String destinationAddress, String sourceAddress, String shortMessage) {
        this.destinationAddress = destinationAddress;
        this.sourceAddress = sourceAddress;
        this.shortMessage = shortMessage;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getShortMessage() {
        return shortMessage;
    }
}

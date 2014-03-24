package com.ebridgevas.model;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/22/12
 * Time: 10:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmppConfig {

    private String smppIPAdress;
    private Integer smppPort;
    private String systemId;
    private String systemPassword;

    public SmppConfig(String smppIPAdress, Integer smppPort, String systemId, String systemPassword) {
        this.smppIPAdress = smppIPAdress;
        this.smppPort = smppPort;
        this.systemId = systemId;
        this.systemPassword = systemPassword;
    }

    public String getSmppIPAdress() {
        return smppIPAdress;
    }

    public Integer getSmppPort() {
        return smppPort;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getSystemPassword() {
        return systemPassword;
    }

    public String toString() {
        return "{smpp : { " +
            " smpp-ip-address : " + smppIPAdress +
                    ", smpp-port : " + smppPort +
                    ", system-id : " + systemId +
                    ", system-password : " + systemPassword +
                    "}}";
    }
}

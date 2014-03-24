package com.ebridgecommerce.sdp.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Request implements Serializable {

    private String msIsdn;
    private String sessionId;
    private String payload;

    public Request() {
    }

    public Request(String msIsdn, String sessionId, String payload) {
        this.msIsdn = msIsdn;
        this.sessionId = sessionId;
        this.payload = payload;
    }

    public String getMsIsdn() {
        return msIsdn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPayload() {
        return payload;
    }

    public String toString(){
        return  "######    msIsdn = " + msIsdn + "\n" +
                "###### sessionId = " + sessionId + "\n" +
                "######   payload = " + payload + "\n";
    }
}

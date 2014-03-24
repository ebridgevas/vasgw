package com.ebridgecommerce.sdp.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Response implements Serializable {

    private String msIsdn;
    private String sessionId;
    private String payload;
    private Boolean terminate;

    public Response(Request request) {
        this.msIsdn = request.getMsIsdn();
        this.sessionId = request.getSessionId();
    }

    public String getMsIsdn() {
        return msIsdn;
    }

    public void setMsIsdn(String msIsdn) {
        this.msIsdn = msIsdn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    public String toString(){
        return  "######    msIsdn = " + msIsdn + "\n" +
                "###### sessionId = " + sessionId + "\n" +
                "######   payload = " + payload + "\n" +
                "###### terminate = " + terminate + "\n";
    }
}

package com.ebridge.sdp.vas.smpp.events.impl;

public abstract class AbstractPDUEventHandler {

    public static final Long USSD_TIMEOUT = new Long(30000);

    public static String getUssdMessagePrefix(Boolean terminateSession, Integer sessionId){
        return ( terminateSession ? "81" : "72") + " " +
                    sessionId + ( terminateSession ? "" : " " + USSD_TIMEOUT ) + " 0 ";
    }

    protected String createResponse(Integer sessionId, String payload, Boolean terminate) {
        return getUssdMessagePrefix(terminate, sessionId) + payload;
    }

    protected Boolean isInitialDial( String payload ) {
        return payload.split(" ").length < 7;
    }

    protected Integer getSessionId( String payload) {
        return new Integer( payload.split(" ")[1] );
    }

    protected Boolean isTerminating(String payload){
        return payload.startsWith("81");
    }

}

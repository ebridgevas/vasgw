package com.ebridgevas.esme;

import com.ebridge.sdp.smpp.Session;
import com.ebridge.sdp.smpp.pdu.EnquireLink;
import com.ebridge.sdp.smpp.pdu.EnquireLinkResp;
import com.ebridgevas.model.UserSession;
import com.ebridgevas.util.CommandExecutor;
import com.ebridgevas.util.Utils;
import com.zw.ebridge.domain.USSDSession;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class EnquiryLinkGenerator extends Thread {

    private Session session;
    private Map<String, USSDSession> userSessions;
    private String commandLine;
    private String systemId;

    private final Integer MAXIMUM_RETRIES = 10;
    private Integer RETRY_COUNT = 0;

    public EnquiryLinkGenerator( Session session,
                                 Map<String,USSDSession> userSessions,
                                 String commandLine,
                                 String systemId) {
        this.session = session;
        this.userSessions = userSessions;
        this.commandLine = commandLine;
        this.systemId = systemId;
    }

    public void run() {

        while (true) {

            EnquireLinkResp response = null;

            try {
                EnquireLink request = new EnquireLink();
                System.out.println("<<<<<<<<<< " + request.debugString());
                session.enquireLink(request);
            } catch (Exception e) {
                System.out.println("########## EnquireLink Warning: " + e.getMessage());
            }

            if ( ( response == null) && (RETRY_COUNT > MAXIMUM_RETRIES) ) {
                RETRY_COUNT = 0;
//                try {
//                    System.out.println("########## Dumping sessions to file ");
//                    Utils.dumpUserSessionsToFile(userSessions, systemId);
//                } catch (FileNotFoundException e) {
//                    // TODO fatal. preseve user sessions.
//                    e.printStackTrace();
//                }
//                try {
//                    System.out.println("########## Executing command : " + commandLine);
////                    CommandExecutor.execute("/prod/ebridge/bin/" + commandLine);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            } else {
                ++RETRY_COUNT;
            }

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
            }
        }
    }
}
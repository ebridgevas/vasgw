package com.ebridge.sdp.vas.smpp;

import com.ebridge.sdp.smpp.Session;
import com.ebridge.sdp.smpp.pdu.EnquireLink;
import com.ebridge.sdp.smpp.pdu.EnquireLinkResp;

public class EnquiryLinkGenerator extends Thread {

    private Session session;
    private String commandLine;

    private final Integer MAXIMUM_RETRIES = 3;
    private Integer RETRY_COUNT = 0;

    public EnquiryLinkGenerator(Session session, String commandLine) {
        this.session = session;
        this.commandLine = commandLine;
    }

    public void run() {
        while (true) {

            EnquireLinkResp response = null;

            try {
                EnquireLink request = new EnquireLink();
                System.out.println(request.debugString());
                response = session.enquireLink(request);
                System.out.println(response.debugString());
            } catch (Exception e) {
            }

            if ( ( response == null) && (RETRY_COUNT > MAXIMUM_RETRIES) ) {
                RETRY_COUNT = 0;
//                CommandExecutor.execute("/prod/vasgw/bin/" + commandLine);
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
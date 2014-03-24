package com.ebridgecommerce.esme;

import com.ebridgecommerce.os.commandexecutor.CommandExecutor;

public class EnquiryLinkGenerator extends Thread {

    private SMPPTransciever smppTransciever;

    private final static Integer MAXIMUM_RETRIES = 2;
    private static Integer RETRY_COUNT = 0;

    public EnquiryLinkGenerator(SMPPTransciever smppTransciever) {
        this.smppTransciever = smppTransciever;
    }

    public void run(){
        while (true) {
            SMPPTransciever.linkState = "UNKNOWN";
            smppTransciever.enquireLink();

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {}

            System.out.println("################ " + SMPPTransciever.linkState);
            if ("LINK_IS_UP".equals(SMPPTransciever.linkState)) {
                RETRY_COUNT = 0;
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {}
            } else {
                ++RETRY_COUNT;
                System.out.println("################ RETRY_COUNT = " + RETRY_COUNT);
                System.out.println("################ MAXIMUM_RETRIES = " + MAXIMUM_RETRIES);
                if (RETRY_COUNT > MAXIMUM_RETRIES) {
                    RETRY_COUNT = 0;
                    /* Re-bind SMPP */
                    System.out.println("################ SMSC NOT RESPONDING TO LINK ENQUIRY - RESTARTING .....");
                    CommandExecutor.execute("/prod/vasgw/bin/ussd");
                    try {
                        smppTransciever.unbind();
                    } catch(Exception e) {
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {}
                    smppTransciever.bind();
                }
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {}
            }
        }
    }
}
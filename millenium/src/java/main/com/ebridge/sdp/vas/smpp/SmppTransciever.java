package com.ebridge.sdp.vas.smpp;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/22/12
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ebridge.sdp.smpp.*;
import com.ebridge.sdp.smpp.pdu.BindResponse;
import com.ebridge.sdp.smpp.pdu.PDUException;
import com.ebridge.sdp.vas.dto.SmppConfig;
import com.ebridge.sdp.vas.smpp.net.SmppBinder;
import org.apache.log4j.Logger;

import java.io.IOException;

public class SmppTransciever {

    private Session ussdSession;

    static Logger log = Logger.getLogger(SmppTransciever.class.getName());

    public SmppTransciever(SmppConfig ussdConfig, String queueName) {
        log.info(" version : 13.01");
        bind(ussdConfig, queueName);
    }

    private void bind(SmppConfig ussdConfig, String queueName) {

        Long timeout = new Long(20 * 1000);

        TCPIPConnection connection =
                new TCPIPConnection(ussdConfig.getSmppIPAdress(), ussdConfig.getSmppPort());
        log.info("Connecting to ip address : " + ussdConfig.getSmppIPAdress() +
                    ", port : " + ussdConfig.getSmppPort());
        connection.setReceiveTimeout( timeout );
        log.info("Creating new session ...");
        ussdSession = new Session(connection);

        log.info("Creating new session ...");
        SmppBinder ussdBinder =
                new SmppBinder(ussdSession, ussdConfig, new ServerPDUEventListenerImpl(ussdSession, queueName));
        BindResponse ussdBindResponse = null;
        while (true) {
            try {
                log.info("Binding ...");
                ussdBindResponse = ussdBinder.bind();
                log.info(ussdBindResponse != null ? ussdBindResponse.debugString() : "NOT BOUND");
                if ( ussdBindResponse!= null && ussdBindResponse.getCommandStatus() == Data.ESME_ROK) {
                    break;
                }
                log.info("Bind response returned ...");
            } catch (PDUException e) {
                // TODO Add intelligency
                log.info("PDUException ..." + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Add intelligency
                log.info("IOException ..." + e.getMessage());
                e.printStackTrace();
            } catch (WrongSessionStateException e) {
                // TODO Add intelligency
                log.info("WrongSessionStateException ..." + e.getMessage());
                e.printStackTrace();
            } catch (TimeoutException e) {
                log.info("TimeoutException ..." + e.getMessage());
                // TODO Add intelligency
                e.printStackTrace();
            }
        }

        if ( ussdBindResponse!= null && ussdBindResponse.getCommandStatus() == Data.ESME_ROK) {
            new EnquiryLinkGenerator(ussdSession, queueName).start();
        }
    }

    /* Test bootstrap*/
    public static void main(String[] args) {
        // 196.2.77.23 2775 NightPromo pwd prepaid with-promotion
        new SmppTransciever(
                new SmppConfig(args[0], Integer.parseInt(args[1]), args[2], args[3]),
                args[4]);
    }
}

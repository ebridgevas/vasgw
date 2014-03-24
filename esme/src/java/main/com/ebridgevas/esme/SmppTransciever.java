package com.ebridgevas.esme;

/**
 * @author david@ebridgevas.com
 *
 */

import com.ebridge.sdp.smpp.*;
import com.ebridge.sdp.smpp.pdu.BindResponse;
import com.ebridge.sdp.smpp.pdu.PDUException;

import com.ebridgevas.esme.net.SmppBinder;
import com.ebridgevas.model.SmppConfig;
import com.ebridgevas.services.ServerPDUEventListenerImpl;
import com.ebridgevas.util.Utils;
import com.zw.ebridge.domain.USSDSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SmppTransciever {

    private Session ussdSession;

    public SmppTransciever(SmppConfig ussdConfig, String commandLine, Boolean withPromotion, String channelType,
                           BigDecimal postpaidLimit) {
        System.out.println(" ver : 13.08 started.");
        bind(ussdConfig, commandLine, withPromotion, channelType, postpaidLimit);
    }

    private void bind(SmppConfig ussdConfig, String commandLine, Boolean withPromotion, String channelType,
                      BigDecimal postpaidLimit) {

        Long timeout = new Long(20 * 1000);

        TCPIPConnection ussdConnection = new TCPIPConnection(ussdConfig.getSmppIPAdress(), ussdConfig.getSmppPort());
        ussdConnection.setReceiveTimeout( timeout );
        ussdSession = new Session(ussdConnection);

        /* User Sessions. */
        Map<String, USSDSession> userSessions = new HashMap<String, USSDSession>();
        //Utils.readUserSessionFromFile(ussdConfig.getSmppIPAdress());

        SmppBinder ussdBinder =
                new SmppBinder( ussdSession,
                                ussdConfig,
                                new ServerPDUEventListenerImpl(ussdSession, userSessions, postpaidLimit));
        BindResponse ussdBindResponse = null;
        while (true) {
            try {
                ussdBindResponse = ussdBinder.bind();
                System.out.println("########### ussdBindResponse.debugString() = " + ussdBindResponse.debugString());
                System.out.println(ussdBindResponse != null ? ussdBindResponse.debugString() : "NOT BOUND");
                if ( ussdBindResponse!= null && ussdBindResponse.getCommandStatus() == Data.ESME_ROK) {
                    break;
                } else {
                    try { Thread.sleep(5000); } catch(Exception e){};
                }
            } catch (PDUException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WrongSessionStateException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

//        if ( ussdBindResponse!= null && ussdBindResponse.getCommandStatus() == Data.ESME_ROK) {
//            new EnquiryLinkGenerator(ussdSession, userSessions, commandLine, ussdConfig.getSmppIPAdress()).start();
//        }
    }

    public static void main(String[] args) {
        // 196.2.77.23 2775 NightPromo pwd prepaid with-promotion
        SmppConfig ussdConfig = new SmppConfig(args[0], Integer.parseInt(args[1]), args[2], args[3]);
        BigDecimal postpaidLimit = new BigDecimal(args[7]);
        new SmppTransciever(ussdConfig, args[4], ("with-promotion".equalsIgnoreCase(args[5])), args[6], postpaidLimit );
    }
}

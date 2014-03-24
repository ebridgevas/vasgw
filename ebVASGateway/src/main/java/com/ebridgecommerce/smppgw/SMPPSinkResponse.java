package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.smpp.ServerPDUEvent;
import com.ebridgecommerce.smpp.ServerPDUEventListener;
import com.ebridgecommerce.smpp.SmppObject;
import com.ebridgecommerce.smpp.pdu.EnquireLinkResp;
import com.ebridgecommerce.smpp.pdu.PDU;
import com.ebridgecommerce.smpp.util.Queue;

/**
 * Implements simple PDU listener which handles PDUs received from SMSC.
 * It puts the received requests into a queue and discards all received
 * responses. Requests then can be fetched (should be) from the queue by
 * calling to the method <code>getRequestEvent</code>.
 * @see Queue
 * @see ServerPDUEvent
 * @see ServerPDUEventListener
 * @see SmppObject
 */
public class SMPPSinkResponse extends SmppObject implements ServerPDUEventListener {

    private SMPPGateway transmitter;

    public SMPPSinkResponse(SMPPGateway transmitter) {
        this.transmitter = transmitter;
    }

    public void handleEvent(ServerPDUEvent event) {

        PDU pdu = event.getPDU();

        if (pdu.isRequest() && (pdu.getCommandId() == 21)) {

            EnquireLinkResp resp = new EnquireLinkResp();
            resp.setCommandStatus(0);
            try {
                System.out.println("<<< " + resp.debugString());
//                transmitter.session.respond(resp);
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        } else if ( pdu.isResponse() ) {
            System.out.println("Response =****************** = " + pdu.debugString());
        	System.out.print(".");
            SMPPGateway.linkState = "UP";
        }
    }
}

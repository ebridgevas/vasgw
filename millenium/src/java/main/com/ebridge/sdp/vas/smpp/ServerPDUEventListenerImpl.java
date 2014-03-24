package com.ebridge.sdp.vas.smpp;

import com.ebridge.sdp.smpp.*;
import com.ebridge.sdp.smpp.pdu.*;


import com.ebridge.sdp.vas.smpp.events.ServerPDUEventHandler;
import com.ebridge.sdp.vas.smpp.events.impl.TestServicePDUEventHandler;
import com.ebridge.sdp.vas.smpp.net.SubmitSmFactory;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Date;
import zw.co.telecel.akm.millenium.dao.PduDao;
import zw.co.telecel.akm.millenium.dto.PduDto;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;

public class ServerPDUEventListenerImpl extends SmppObject implements ServerPDUEventListener {
    private Session session;
    private PduDao dao;
    private ServerPDUEventHandler handler;

    private Logger log = Logger.getLogger(ServerPDUEventListenerImpl.class.getName());

    public ServerPDUEventListenerImpl(Session session, String queueName) {
        this.session = session;
        dao = new PduDao();
        dao.initEntityManager();
        // TODO : close dao on unbind
        try {
            handler = new TestServicePDUEventHandler(queueName);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void handleEvent( ServerPDUEvent event ) {

        PDU pdu = event.getPDU();

        if ( pdu.isRequest() )
            try {
                process((Request) pdu);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ValueNotSetException e) {
                e.printStackTrace();
            } catch (WrongSessionStateException e) {
                e.printStackTrace();
            } catch (PDUException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        else
            process((Response) pdu);
    }

    /**
     * Process request from server.
     *
     * @param request
     * @throws java.io.IOException
     * @throws com.ebridge.sdp.smpp.pdu.PDUException
     * @throws com.ebridge.sdp.smpp.WrongSessionStateException
     * @throws com.ebridge.sdp.smpp.TimeoutException
     */
    protected void process(Request request)
            throws IOException, PDUException, WrongSessionStateException, TimeoutException {

        log.debug( request.debugString() );
        switch(request.getCommandId()) {
            case 21:
                EnquireLinkResp response = new EnquireLinkResp();
                response.setCommandStatus(0);
                log.debug(response.debugString());
                session.respond(response);
                break;
            default:
                log.debug("Parsing deliverSM pdu.");
                DeliverSM deliverSM = (DeliverSM) request;

                /* Acknowledge delivery. */
                DeliverSMResp deliverSmResp = new DeliverSMResp();
                deliverSmResp.setCommandId(Data.DELIVER_SM_RESP);
                deliverSmResp.setCommandStatus(0);
                deliverSmResp.setSequenceNumber(deliverSM.getSequenceNumber());
                deliverSmResp.setOriginalRequest(request);
                log.debug("Ack: " + deliverSmResp.debugString());
                session.respond(deliverSmResp);
                log.debug("submitted.");
                if ( isDeliveryNote(deliverSM) ) return;

                /* Log. */
                PduDto pdu = new PduDto(  "" + com.ebridge.sdp.vas.utils.KeyGenerator.generateEntityId(),
                        "USSD",
                        new Date(),
                        "deliver_sm",
                        deliverSM.getSourceAddr().getAddress(),
                        deliverSM.getDestAddr().getAddress(),
                        deliverSM.getShortMessage(),
                        deliverSM.debugString() );
                dao.persist(pdu);

                /* Process. */
                log.debug("Calling event handler ...");
                String responseSM = handler.handleEvent( pdu );

                log.debug("{ responseSM : " + responseSM + " }");

                /* Response. */
                SubmitSM submitSM = new SubmitSmFactory().create(
                        deliverSM.getDestAddr().getAddress(),
                        deliverSM.getSourceAddr().getAddress(),
                        responseSM,
                        (short)9280, (byte) 3, (byte) 1 );

                String uuid = "*" + pdu.getUuid();

                PduDto pdu2 = new PduDto(
                        uuid,
                        "USSD",
                        new Date(),
                        "submitsm",
                        deliverSM.getDestAddr().getAddress(),
                        deliverSM.getSourceAddr().getAddress(),
                        responseSM,
                        "" );
                dao.persist(pdu2);

                log.debug(submitSM.debugString());
                session.submit( submitSM );
        }
    }

    /**
     * Check if delivery note.
     * @param deliverSM
     * @return
     */
    protected Boolean isDeliveryNote( DeliverSM deliverSM ) {
        return (deliverSM.getShortMessage() != null) && deliverSM.getShortMessage().startsWith("id:");
    }

    protected void process(Response response) {
        log.debug( response.debugString() );
    }
}
package com.ebridgevas.services;

import com.ebridge.commons.dto.PduDto;
import com.ebridge.sdp.smpp.*;
import com.ebridge.sdp.smpp.pdu.*;

//import com.ebridge.sdp.vas.in.postpaid.PostpaidAccountManager;

import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.in.AccountManager;
import com.ebridgevas.in.postpaid.PostpaidAccountManager;
import com.ebridgevas.in.prepaid.PrepaidAccountManager;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.model.ServiceCommand;
import com.ebridgevas.model.UserSession;
import com.ebridgevas.services.impl.*;
import com.ebridgevas.util.ServiceCommandParser;
import com.ebridgevas.util.SubmitSmFactory;
import com.zw.ebridge.domain.USSDSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/23/12
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServerPDUEventListenerImpl extends SmppObject implements ServerPDUEventListener {

    private Session session;
    public static final AccountManager PREPAID_ACCOUNT_MANAGER;
    public static final AccountManager POSTPAID_ACCOUNT_MANAGER;

    private DataBundleServicePDUEventHandler dataBundleServicePDUEventHandler;

    private static final Map<ServiceCommand, ServiceCommandProcessor> SERVICE_COMMAND_PROCESSORS;
    public static Map<String, UserSession> USER_SESSIONS;

    private static TxnDAO txnDao;
//    private static CreditAccountRequestDao requestDao;
    static {
//        requestDao = new CreditAccountRequestDao();
//        requestDao.initEntityManager();
        txnDao = new TxnDAO();
//        txnDao.initEntityManager();
    }

    /* Static initializer. */
    static {
        PREPAID_ACCOUNT_MANAGER = new PrepaidAccountManager();
//        PREPAID_ACCOUNT_MANAGER.setRequestDao(requestDao);
        PREPAID_ACCOUNT_MANAGER.setTxnDao(txnDao);

        POSTPAID_ACCOUNT_MANAGER = new PostpaidAccountManager();
//        POSTPAID_ACCOUNT_MANAGER.setRequestDao(requestDao);
        POSTPAID_ACCOUNT_MANAGER.setTxnDao(txnDao);

        SERVICE_COMMAND_PROCESSORS = new HashMap<ServiceCommand, ServiceCommandProcessor>();

        SERVICE_COMMAND_PROCESSORS.put(
                ServiceCommand.HELP, new HelpServiceCommandProcessor());

        SERVICE_COMMAND_PROCESSORS.put(
                ServiceCommand.BALANCE_ENQUIRY, new BalanceEnquiryServiceCommandProcessor());

        SERVICE_COMMAND_PROCESSORS.put(
                ServiceCommand.DATA_BUNDLE_PRICE_LISTING, new DataBundlePriceListingServiceCommandProcessor());

        SERVICE_COMMAND_PROCESSORS.put(
                ServiceCommand.DATA_BUNDLE_PURCHASE, new DataBundlePurchaseServiceCommandProcessor());

        SERVICE_COMMAND_PROCESSORS.put(
                ServiceCommand.BALANCE_TRANSFER, new BalanceTransferServiceCommandProcessor());

        SERVICE_COMMAND_PROCESSORS.put(
                ServiceCommand.VOUCHER_RECHARGE, new VoucherRechargeServiceCommandProcessor());

        USER_SESSIONS = new HashMap<String, UserSession>();

    }
    /* Static initializer ends. */

//    private PduDao dao;

    public ServerPDUEventListenerImpl(Session session, Map<String, USSDSession> userSessions, BigDecimal postpaidLimit) {
        this.session = session;
        dataBundleServicePDUEventHandler
                = new DataBundleServicePDUEventHandler( PREPAID_ACCOUNT_MANAGER,
                                                        POSTPAID_ACCOUNT_MANAGER,
                                                        userSessions, postpaidLimit );

//        dao = new PduDao();
//        dao.initEntityManager();
        SERVICE_COMMAND_PROCESSORS.get(ServiceCommand.DATA_BUNDLE_PURCHASE).setTxnDao(txnDao);
        SERVICE_COMMAND_PROCESSORS.get(ServiceCommand.DATA_BUNDLE_PURCHASE).setPostpaidLimit(postpaidLimit);
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

        System.out.println(request.debugString());
        switch(request.getCommandId()) {
            case 21:
                EnquireLinkResp response = new EnquireLinkResp();
                response.setCommandStatus(0);
                System.out.println(response.debugString());
                session.respond(response);
                break;
            default:
                System.out.println("Parsing deliverSM pdu.");
                DeliverSM deliverSM = (DeliverSM) request;

                /* Acknowledge delivery. */
                DeliverSMResp deliverSmResp = new DeliverSMResp();
                deliverSmResp.setCommandId(Data.DELIVER_SM_RESP);
                deliverSmResp.setCommandStatus(0);
                deliverSmResp.setSequenceNumber(deliverSM.getSequenceNumber());
                deliverSmResp.setOriginalRequest(request);
                System.out.println("Ack: " + deliverSmResp.debugString());
                session.respond(deliverSmResp);
                System.out.println("submitted.");
                if ( isDeliveryNote(deliverSM) ) return;

                /* Log. */

                PduDto pdu = new PduDto(  "" + System.currentTimeMillis(),
                                "33350".equals(deliverSM.getDestAddr().getAddress())
                                        || "73480".equals(deliverSM.getDestAddr().getAddress())
                                        || "33073".equals(deliverSM.getDestAddr().getAddress()) ? "SMS"  : "USSD",
                                new Date(),
                                "deliver_sm",
                                deliverSM.getSourceAddr().getAddress(),
                                deliverSM.getDestAddr().getAddress(),
                                deliverSM.getShortMessage(),
                                deliverSM.debugString() );
//                dao.persist(pdu);
                System.out.println("pdu : " + pdu.getDebugString());

                String shortMessage = null;
                if ("SMS".equalsIgnoreCase(pdu.getChannel())) {
                    try {
                        ServiceCommand serviceCommand =
                            ServiceCommandParser.parsePayload(pdu.getSourceId(), pdu.getDestinationId(), pdu.getShortMessage(), USER_SESSIONS.get(pdu.getSourceId()));
                        for ( MTSM mtsm : SERVICE_COMMAND_PROCESSORS.get(serviceCommand).process(pdu) ) {
                            SubmitSM submitSM = new SubmitSmFactory().create(
                                    mtsm.getSourceAddress(),
                                    mtsm.getDestinationAddress(),
                                    mtsm.getShortMessage(),
                                    "33350".equals(deliverSM.getDestAddr().getAddress()) ||
                                            "33073".equals(deliverSM.getDestAddr().getAddress()) ? null : (short)9280,
                                    (byte) 3,
                                    (byte) 1 );

                            System.out.println("###### submitSM : " + submitSM.debugString());
                            session.submit( submitSM );
                        }
                    } catch(Exception e) {
//                        e.printStackTrace();
                        SubmitSM submitSM = new SubmitSmFactory().create(
                                deliverSM.getDestAddr().getAddress(),
                                deliverSM.getSourceAddr().getAddress(),
                                e.getMessage(),
                                "33350".equals(deliverSM.getDestAddr().getAddress()) ? null : (short)9280,
                                (byte) 3,
                                (byte) 1 );

                        System.out.println("###### submitSM : " + submitSM.debugString());
                        session.submit( submitSM );
                    }
                } else {
                    if ("144".equals(deliverSM.getDestAddr().getAddress()) ||
                            "971".equals(deliverSM.getDestAddr().getAddress())) {
                        shortMessage = dataBundleServicePDUEventHandler.handleEvent( pdu );
                        SubmitSM submitSM = new SubmitSmFactory().create(
                                deliverSM.getDestAddr().getAddress(),
                                deliverSM.getSourceAddr().getAddress(),
                                shortMessage,
                                "33350".equals(deliverSM.getDestAddr().getAddress()) ? null : (short)9280,
                                (byte) 3,
                                (byte) 1 );

                        System.out.println("###### submitSM : " + submitSM.debugString());
                        session.submit( submitSM );
                    }
                }
//                SubmitSM submitSM = new SubmitSmFactory().create(
//                        deliverSM.getDestAddr().getAddress(),
//                        deliverSM.getSourceAddr().getAddress(),
//                        shortMessage,
//                        "33350".equals(deliverSM.getDestAddr().getAddress()) ? null : (short)9280,
//                        (byte) 3,
//                        (byte) 1 );
//
//                System.out.println("###### submitSM : " + submitSM.debugString());
//                session.submit( submitSM );

                /*
                List<MTSM> responseMTSMs = null;

                try {

                    System.out.println("Parsing payload : " + deliverSM.getShortMessage());
                    ServiceCommand serviceCommand = ServiceCommandParser.parsePayload(
                            deliverSM.getShortMessage(), USER_SESSIONS.get(deliverSM.getSourceAddr().getAddress()));


                    System.out.println("Calling processor" + serviceCommand);
                    responseMTSMs = SERVICE_COMMAND_PROCESSORS.get(serviceCommand).process(pdu);
                    System.out.println("{ responseMTSMs : " + responseMTSMs.size() + " }");
                } catch (Exception e) {
                    responseMTSMs = new ArrayList<MTSM>();
                    responseMTSMs.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), e.getMessage()));
                }

                int idx = 0;
                for (MTSM mtsm : responseMTSMs) {

                    System.out.println("{ responseMTSM { destination : " +
                               mtsm.getDestinationAddress() +
                               ", shortMessage : " + mtsm.getShortMessage() + " }");

                    SubmitSM submitSM = new SubmitSmFactory().create(
                            mtsm.getSourceAddress(),
                            mtsm.getDestinationAddress(),
                            mtsm.getShortMessage(),
                            (short)9280,
                            //                            deliverSM.getDestinationPort(),
                            //  if SMS               null,
                            (byte) 3,
                            (byte) 1 );

                    String uuid = "*" + pdu.getUuid();

                    try {
                    PduDto pdu2 = new PduDto(
                            (++idx) + uuid,
                            "USSD",
                            new Date(),
                            "submitsm",
                            mtsm.getDestinationAddress(),
                            mtsm.getSourceAddress(),
                            mtsm.getShortMessage(),
                            "" );
//                    dao.persist(pdu2);
                    System.out.println("###### pdu2 : " + pdu2.getDebugString());
                    } catch (Exception e) {
                        System.out.print("ERROR INSERTING INTO PDUDTO : ");
                        e.printStackTrace();
                    }
                    System.out.println("###### submitSM : " + submitSM.debugString());
                    session.submit( submitSM );
                }
                */
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
        System.out.println( response.debugString() );
//        if (request.getCommandStatus() == 0) {
//            SMPPTransciever.linkState = "LINK_IS_UP";
//        }
    }
}
//package com.ebridgecommerce.smppgw;
//
//import com.ebridgecommerce.smpp.ServerPDUEvent;
//import com.ebridgecommerce.smpp.ServerPDUEventListener;
//import com.ebridgecommerce.smpp.SmppObject;
//import com.ebridgecommerce.smpp.pdu.DeliverSM;
//import com.ebridgecommerce.smpp.pdu.PDU;
//import com.ebridgecommerce.smpp.pdu.Request;
//import com.ebridgecommerce.smpp.pdu.SubmitSM;
//import com.ebridgecommerce.smsc.processors.InternalServicesProcessor;
//
//public class MosmPDUListenerOld extends SmppObject implements ServerPDUEventListener {
//
//	private SMPPGateway esme;
//	private SmppServerConnector smppServerConnector;
//	private InternalServicesProcessor internalServicesProcessor;
////	private SMPPReciever reciever;
//	
//	public MosmPDUListenerOld(SMPPGateway esme ) { //, SMPPReciever reciever) {
//		System.out.println("MosmPDUListener Version 2.0. Released on 4 November 2010.");
//		this.esme = esme;
////		this.reciever = reciever;
//	}
//
//	public void setSmppServerConnector(SmppServerConnector smppServerConnector) {
//		this.smppServerConnector = smppServerConnector;
//	}
//
//	public void setInternalServicesProcessor(InternalServicesProcessor internalServicesProcessor) {
//		this.internalServicesProcessor = internalServicesProcessor;
//	}
//
//	public void handleEvent(ServerPDUEvent event) {
//		try {
//		PDU pdu = event.getPDU();
//
//		if (pdu.isRequest() && (pdu.getCommandId() == 21)) {
//			System.out.println("SMSC Enquiry Link ============= " + pdu.debugString());
//			esme.enquireLinkResp();
//
//		} else if (pdu.isRequest()) {
//
//			System.out.println("---> " + pdu.debugString());
//			Request request = (Request) pdu;
//			DeliverSM sm = (DeliverSM) request;
//
//			esme.respond(request, sm.getSequenceNumber());
//			if ((sm.getShortMessage() != null) && sm.getShortMessage().startsWith("id:")) {
//				return;
//			}
//			
//			System.out.println("MOSM for " + sm.getDestAddr().getAddress());
//			if ("23350".equals(sm.getDestAddr().getAddress()) || "350".equals(sm.getDestAddr().getAddress())) { // || "33500".equals(sm.getDestAddr().getAddress()) ) {
//				
////				if (internalServicesProcessor != null) {
////					for (SubmitSM submitSM : internalServicesProcessor.process(sm, "SMS") ) {
//////						if (("33500".equals(sm.getDestAddr().getAddress())) && submitSM.getShortMessage().startsWith("Thank you")) {
//////							smppServerConnector.deliver(sm);
//////						} else {
////							esme.submit(submitSM);
//////						}
////					}
////				} else {
////					System.out.println("Internal service processor not setup for shortcode " + sm.getDestAddr().getAddress());
////				}
//			} else if ("175".equals(sm.getDestAddr().getAddress()) || "177".equals(sm.getDestAddr().getAddress())) {
//				if (internalServicesProcessor != null) {
//					for (SubmitSM submitSM : internalServicesProcessor.process(sm, "USSD") ) {
//						esme.submit(submitSM);
//					}
//				} else {
//					System.out.println("Internal service processor not setup for shortcode " + sm.getDestAddr().getAddress());
//				}
//			} else {
//				try {
//					smppServerConnector.deliver(sm);
//				} catch(Exception e ) {
//					esme.connectSmppServer();
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e1) {
////						e1.printStackTrace();
//					}
//					smppServerConnector.deliver(sm);
//				}
//			}
//		} else if (pdu.isResponse()) {
//			if (pdu.getCommandStatus() == 0) {
//				System.out.println("---> " + pdu.debugString());
//				SMPPReciever.rLinkState = "UP";
//			}
//		}
//		} catch(Exception e ) {
//			System.out.println("================================== ||||| " + e.getMessage());
//		}
//	}
//}

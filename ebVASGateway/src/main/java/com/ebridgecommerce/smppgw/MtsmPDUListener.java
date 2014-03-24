package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.smpp.ServerPDUEvent;
import com.ebridgecommerce.smpp.ServerPDUEventListener;
import com.ebridgecommerce.smpp.SmppObject;
import com.ebridgecommerce.smpp.pdu.PDU;
import com.ebridgecommerce.smpp.pdu.Request;
import com.ebridgecommerce.smpp.pdu.SubmitSM;

public class MtsmPDUListener extends SmppObject implements ServerPDUEventListener {

	private SMPPGateway transciever;
	private static SubmitAORequestPDUFactory submitAORequestPDUFactory;
	
	public MtsmPDUListener(SMPPGateway transciever) {
		this.transciever = transciever;
		submitAORequestPDUFactory = new SubmitAORequestPDUFactory();
	}

	public void handleEvent(ServerPDUEvent event) {
		System.out.println("MtsmPDUListener event ...");
		try {
			PDU pdu = event.getPDU();
			if (pdu.isRequest() && (pdu.getCommandId() == 21)) {
			} else if (pdu.isRequest()) {

				Request request = (Request) pdu;
				SubmitSM submitSM = (SubmitSM) request;
				SubmitSM editedSM = getSubmitSM("23350", submitSM.getDestAddr().getAddress(), submitSM.getShortMessage());
				System.out.println("<--- *** EDITED *** " + editedSM.debugString());
				transciever.submit(editedSM);
			} else if (pdu.isResponse()) {
				System.out.println("<--- RESPONSE = " + pdu.debugString());
			} else {
			}
		} catch (Exception e) {
		}
	}
	private SubmitSM getSubmitSM(String sourceId, String destinationId, String text) {
		return submitAORequestPDUFactory.getInstance(sourceId, destinationId, text);
	}
}

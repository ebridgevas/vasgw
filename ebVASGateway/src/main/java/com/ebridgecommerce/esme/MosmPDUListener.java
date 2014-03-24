package com.ebridgecommerce.esme;

import com.ebridgecommerce.smpp.ServerPDUEvent;
import com.ebridgecommerce.smpp.ServerPDUEventListener;
import com.ebridgecommerce.smpp.SmppObject;
import com.ebridgecommerce.smpp.pdu.DeliverSM;
import com.ebridgecommerce.smpp.pdu.PDU;
import com.ebridgecommerce.smpp.pdu.Request;

public class MosmPDUListener extends SmppObject implements ServerPDUEventListener {

	private ESME esme;

	public MosmPDUListener(ESME esme) {
		System.out.println("MosmPDUListener Version 2.0. Released on 4 November 2010.");
		this.esme = esme;
	}

	public void handleEvent(ServerPDUEvent event) {

		PDU pdu = event.getPDU();

		if (pdu.isRequest() && (pdu.getCommandId() == 21)) {
			esme.getSmppPDUProcessor().enquireLinkResp();
		} else if (pdu.isRequest()) {
			System.out.println("---> " + pdu.debugString());
			Request request = (Request) pdu;
			DeliverSM sm = (DeliverSM) request;
			esme.getSmppPDUProcessor().respond(request, sm.getSequenceNumber());
		} else if (pdu.isResponse()) {
			if (pdu.getCommandStatus() == 0) {
				System.out.println(pdu.debugString());
				ESME.linkState = "UP";
			}
		}
	}
}

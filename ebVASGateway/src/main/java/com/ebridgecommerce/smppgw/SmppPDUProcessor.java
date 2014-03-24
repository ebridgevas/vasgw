package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.esme.ESME;
import com.ebridgecommerce.domain.SmppBindParametersDTO;
import com.ebridgecommerce.smpp.Data;
import com.ebridgecommerce.smpp.ServerPDUEventListener;
import com.ebridgecommerce.smpp.Session;
import com.ebridgecommerce.smpp.TCPIPConnection;
import com.ebridgecommerce.smpp.pdu.*;
import com.ebridgecommerce.smpp.util.SmppParamaters;

public class SmppPDUProcessor {
	
	private SmppBindParametersDTO bindParams;
	private ServerPDUEventListener listener;
	private TCPIPConnection connection;
	private Session session;
	private SubmitAORequestPDUFactory submitAORequestPDUFactory;
	
	public SmppPDUProcessor(SmppBindParametersDTO bindParams , ServerPDUEventListener listener) {
		this.bindParams = bindParams;
		this.listener = listener;
		submitAORequestPDUFactory = new SubmitAORequestPDUFactory();
	}
	
	public boolean bind() {

		try {
			BindRequest request = new BindTransciever();
			if (connection == null) {
				connection = new TCPIPConnection(bindParams.getSmppIPAdress(), bindParams.getSmppPort());
				connection.setReceiveTimeout(20 * 1000);			
			}
			session = new Session(connection);
			request.setSystemId(bindParams.getSystemId());
			request.setPassword(bindParams.getSystemPassword());
			request.setSystemType(bindParams.getSystemType());
			if ("34".equals(bindParams.getSmppVersion())) {
				request.setInterfaceVersion((byte) 0x34);
			} else if ("33".equals(bindParams.getSmppVersion())) {
				request.setInterfaceVersion((byte) 0x33);
			}
			request.setAddressRange(SmppParamaters.getAddressRange());

			System.out.println("<--- " + request.debugString());
			BindResponse response = null;
			response = session.bind(request, listener);
			System.out.println("---> " + response.debugString());
			if (response.getCommandStatus() == Data.ESME_ROK) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Bind operation failed. " + e);
			e.printStackTrace();
			return false;
		}
	}
	
	public void submit(SubmitSM request) {
		try {
			session.submit(request);
		} catch (Exception ex) {
			System.out.println("Failed to send mtsm : " + request.debugString() + " : " + ex.getMessage());
		}
	}
	
	public Boolean unbind() {
		try {
			System.out.println("<--- Unbind request");
			UnbindResp response = session.unbind();
			System.out.println("--->" + response.debugString());
			return true;
		} catch (Exception e) {
			System.out.println("Unbind operation failed. " + e);
			return Boolean.FALSE;
		}
	}

	public void respond(Response response) {
		try {
			session.respond(response);
		} catch (Exception ex) {
			System.out.println("Failed to respond : " + response.debugString() + " : " + ex.getMessage());
		}
	}
	
	public void enquireLink() {
		try {
			EnquireLink request = new EnquireLink();
			System.out.print(request.debugString());
			session.enquireLink(request);
		} catch (Exception e) {
			ESME.linkState = "DOWN";
		}
	}
	
	public void enquireLinkResp() {
		EnquireLinkResp resp = new EnquireLinkResp();
		resp.setCommandStatus(0);
		respond(resp);
	}
	public void submit(String sourceId, String destinationId, String text) {
		submit(submitAORequestPDUFactory.getInstance(sourceId, destinationId, text));
	}
	public void respond(Request request, int sequenceNumber) {
		DeliverSMResp deliverSmResp = new DeliverSMResp();
		deliverSmResp.setCommandId(Data.DELIVER_SM_RESP);
		deliverSmResp.setCommandStatus(0);
		deliverSmResp.setSequenceNumber(sequenceNumber);
		deliverSmResp.setOriginalRequest(request);
		respond(deliverSmResp);
	}
}

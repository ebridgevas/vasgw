package com.ebridgecommerce.esme;

import com.ebridgecommerce.smpp.*;
import com.ebridgecommerce.smpp.pdu.*;
import com.ebridgecommerce.smpp.util.SmppParamaters;

public class SMPPTransciever {

	public static String smppIPAdress;
	public static String linkState;
	private int smppPort;
	private String systemId;
	private String systemPassword;
	private  String carrierType;
	private String smscId;
	
	private MosmProcessor mosmProcessor;
	
	public static final String SYSTEM_TYPE = "EBridge";
	private Session session;
	private String queueName;
	private SubmitAORequestPDUFactory submitAORequestPDUFactory;
	
	public SMPPTransciever(String smppIPAdress, int smppPort, String systemId, String systemPassword, String carrierType, String smscId, String queueName) {
		
		System.out.println("eBridge VAS Gateway 12.05");
		System.out.println("SMPP Transciever");
		System.out.println("Released 25 May 2012.");
		
		this.smppIPAdress = smppIPAdress;
		this.smppPort = smppPort;
		this.systemId = systemId;
		this.systemPassword = systemPassword;
		this.carrierType = carrierType;
		this.smscId = smscId;
		this.queueName = queueName;
		mosmProcessor = new MosmProcessor(carrierType, smscId);
		showParameters();
	
		submitAORequestPDUFactory = new SubmitAORequestPDUFactory();
	}

	public boolean bind() {
		
		try {
			BindRequest request = new BindTransciever();
			TCPIPConnection connection = new TCPIPConnection(smppIPAdress, smppPort);
			connection.setReceiveTimeout(20 * 1000);
			session = new Session(connection);
			request.setSystemId(systemId);
			request.setPassword(systemPassword);
			request.setSystemType(SYSTEM_TYPE);
			request.setInterfaceVersion((byte) 0x34);
			request.setAddressRange(SmppParamaters.getAddressRange());

			System.out.println("<--- " + request.debugString());
		  BindResponse response = session.bind(request, new MosmPDUListener());
			System.out.println("---> " + response.debugString());
			if (response.getCommandStatus() == Data.ESME_ROK) {
				new MTSMProcessor(this, queueName).start();
				new EnquiryLinkGenerator(this).start();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Bind operation failed. " + e);
			return false;
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

	public void submit(String sourceId, String destinationId, String text) {
		SubmitSM submitSMrequest = submitAORequestPDUFactory.getInstance(sourceId, destinationId, text);
		System.out.println("<-- " + submitSMrequest.debugString());
		try {
			session.submit(submitSMrequest);
		} catch (Exception ex) {
			System.out.println("Failed to send aosm : " + sourceId + " -> " + destinationId + " : " + text);
		}
	}
	
	public void respond(Response response) {
		try {
			System.out.println("---> " + response.debugString());
			session.respond(response);
		} catch (Exception ex) {
			System.out.println("Failed to respond : " + response.debugString() + " : " + ex.getMessage());
		}
	}

	public void respond(Request request, int sequenceNumber) {
		DeliverSMResp deliverSmResp = new DeliverSMResp();
		deliverSmResp.setCommandId(Data.DELIVER_SM_RESP);
		deliverSmResp.setCommandStatus(0);
		deliverSmResp.setSequenceNumber(sequenceNumber);
		deliverSmResp.setOriginalRequest(request);
		respond(deliverSmResp);
	}

	public void enquireLink() {
		try {
			EnquireLink request = new EnquireLink();
			System.out.println(request.debugString());
			session.enquireLink(request);
		} catch (Exception e) {
		}
	}

	public void enquireLinkResp() {
		EnquireLinkResp resp = new EnquireLinkResp();
		resp.setCommandStatus(0);
		respond(resp);
	}

	private void showParameters() {

		System.out.println("##################################################################");
		System.out.println("## smppIPAdress    : " + smppIPAdress);
		System.out.println("## smppPort        : " + smppPort);
		System.out.println("## systemId        : " + systemId);
		System.out.println("## systemPassword  : " + systemPassword);
		System.out.println("#################################################################");
	}

	private class MosmPDUListener extends SmppObject implements ServerPDUEventListener {

		public MosmPDUListener() {
			System.out.println("MosmPDUListener Version 2.0. Released on 4 November 2010.");
		}

		public void handleEvent(ServerPDUEvent event) {

			try {

				PDU pdu = event.getPDU();

				if (pdu.isRequest() && (pdu.getCommandId() == 21)) {
					/* Enquire link request. */
					System.out.println("SMSC Enquiry Link ============= " + pdu.debugString());
					enquireLinkResp();
				} else if (pdu.isRequest()) {

					System.out.println("---> " + pdu.debugString());
					
					Request request = (Request) pdu;
					DeliverSM sm = (DeliverSM) request;
					respond(request, sm.getSequenceNumber());
					if ((sm.getShortMessage() != null) && sm.getShortMessage().startsWith("id:")) {
						/* Delivery Note. */
						return;
					}
					/* Store MOSM.*/
					System.out.println("############ shortMessage = " + sm.getShortMessage());
					mosmProcessor.process(sm);
					respond(request, sm.getSequenceNumber());
				} else if (pdu.isResponse()) {
						System.out.println("---> " + pdu.debugString());
						System.out.println("############ pdu.getCommandId() = " + pdu.getCommandId());
						System.out.println("############ pdu.getCommandStatus() = *" + pdu.getCommandStatus() + "*");
						
						// TODO Check command id if it is a link response (pdu.getCommandId() == 21)
						if (pdu.getCommandStatus() == 0) {
							SMPPTransciever.linkState = "LINK_IS_UP";
							System.out.println("------------############ SMPPTransciever.linkState = " + SMPPTransciever.linkState);
						}
						System.out.println("############ SMPPTransciever.linkState = " + SMPPTransciever.linkState);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args){
		try {
			new SMPPTransciever(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], args[5], args[6]).bind();
		} catch (NumberFormatException e) {
			System.out.println(args[1] + " is not a port number");
		} 
	}

}
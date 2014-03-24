package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.smpp.Data;
import com.ebridgecommerce.smpp.Session;
import com.ebridgecommerce.smpp.TCPIPConnection;
import com.ebridgecommerce.smpp.pdu.*;
import com.ebridgecommerce.smpp.util.SmppParamaters;
import com.ebridgecommerce.smsc.processors.InternalServicesProcessor;

import java.util.Properties;

public class SMPPGateway {

	private String telecelSmppIPAdress;
	private int telecelSmppPort;
	private String telelceSystemId;
	private String telecelSystemPassword;
	private String telecelSmppVersion;
	private Long telecelEnquireLinkRate;
	private String ebridgeSmppIPAdress;
	private int ebridgeSmppPort;
	private String ebridgeSystemId;
	private String ebridgeSystemPassword;
	private String ebridgeSmppVersion;
	private Long ebridgeEnquireLinkRate;
	private boolean isConnectedToEBridge;
	private boolean isConnectedToCcws;
	
	public static final String SYSTEM_TYPE = "EBridge";
	Properties properties = new Properties();
	private Session session;
//	private MosmPDUListenerOld pduListener;
	private static TCPIPConnection connection;
	public static String linkState;
	public long enquireLinkRate;
	private SubmitAORequestPDUFactory submitAORequestPDUFactory;

	public SMPPGateway(String telecelSmppIPAdress, int telecelSmppPort, String telelceSystemId, String telecelSystemPassword, String telecelSmppVersion,
			Long telecelEnquireLinkRate, String ebridgeSmppIPAdress, int ebridgeSmppPort, String ebridgeSystemId, String ebridgeSystemPassword, String ebridgeSmppVersion,
			Long ebridgeEnquireLinkRate, boolean isConnectedToEBridge, boolean isConnectedToCcws ) {

		this.telecelSmppIPAdress=telecelSmppIPAdress;
		this.telecelSmppPort=telecelSmppPort;
		this.telelceSystemId=telelceSystemId;
		this.telecelSystemPassword=telecelSystemPassword;
		this.telecelSmppVersion=telecelSmppVersion;
		this.telecelEnquireLinkRate=telecelEnquireLinkRate;
		this.ebridgeSmppIPAdress=ebridgeSmppIPAdress;
		this.ebridgeSmppPort=ebridgeSmppPort;
		this.ebridgeSystemId=ebridgeSystemId;
		this.ebridgeSystemPassword=ebridgeSystemPassword;
		this.ebridgeSmppVersion=ebridgeSmppVersion;
		this.ebridgeEnquireLinkRate=ebridgeEnquireLinkRate;
		this.isConnectedToEBridge = isConnectedToEBridge;
		this.isConnectedToCcws=isConnectedToCcws;

		showParameters();

		linkState = "UNKNOWN";
		submitAORequestPDUFactory = new SubmitAORequestPDUFactory();
		
	}

	public void connect() {
		connection = new TCPIPConnection(telecelSmppIPAdress, telecelSmppPort);
		connection.setReceiveTimeout(20 * 1000);
	}

	public static void connectCCWS() {
		System.out.println("connectCCWS()");
		InternalServicesProcessor.getInstance();
	}
	
	public static void connectEBridge(String ebridgeSmppIPAdress, int ebridgeSmppPort, String ebridgeSystemId, String ebridgeSystemPassword, String smppVersion, Long enquireLinkRate) {
		System.out.println("connectCCWS(connectEBridge())");
		SmppServerConnector.getInstance(null, ebridgeSmppIPAdress, ebridgeSmppPort, ebridgeSystemId, ebridgeSystemPassword);
	}
	
//	public void connectSmppServer() {
//		pduListener.setSmppServerConnector(SmppServerConnector.getInstance(this, ebridgeSmppIPAdress, ebridgeSmppPort, ebridgeSystemId, ebridgeSystemPassword));
//	}
	
	public boolean bind() {

//		pduListener = new MosmPDUListenerOld(this);

//		if (isConnectedToCcws) {
//			pduListener.setInternalServicesProcessor(InternalServicesProcessor.getInstance());
//		}

//		if (isConnectedToEBridge) {
//			pduListener.setSmppServerConnector(SmppServerConnector.getInstance(this, ebridgeSmppIPAdress, ebridgeSmppPort, ebridgeSystemId, ebridgeSystemPassword));
//		}

		try {
			BindRequest request = new BindTransciever();
			if (connection == null) {
				connect();
			}
			session = new Session(connection);
			request.setSystemId(telelceSystemId);
			request.setPassword(telecelSystemPassword);
			request.setSystemType(SYSTEM_TYPE);
			if ("34".equals(telecelSmppVersion)) {
				request.setInterfaceVersion((byte) 0x34);
			} else if ("33".equals(telecelSmppVersion)) {
				request.setInterfaceVersion((byte) 0x33);
			}
			request.setAddressRange(SmppParamaters.getAddressRange());

			System.out.println("<--- " + request.debugString());
			BindResponse response = null;
//			response = session.bind(request, pduListener);
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

	public void submit(SubmitSM request) {
		try {
			session.submit(request);
		} catch (Exception ex) {
			System.out.println("Failed to send mtsm : " + request.debugString() + " : " + ex.getMessage());
		}
	}
	
	public void respond(Response response){
		try {
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
			linkState = "UNKNOWN";
			EnquireLink request = new EnquireLink();
//			System.out.print("c");
			System.out.println("<--- " + request.debugString());
			session.enquireLink(request);
		} catch (Exception e) {
			linkState = "DOWN";
		}
	}

	public void enquireLinkResp() {
		EnquireLinkResp resp = new EnquireLinkResp();
		resp.setCommandStatus(0);
		respond(resp);
	}
	
	public void close() {
		unbind();
	}

	public void linkMonitor() {
		while (true) {
			enquireLink();
			int count = 0;
			while ("UNKNOWN".equals(linkState) && count < 5) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
				}
				++count;
			}
			if (!"UP".equals(linkState)) {
				System.out.print("-");
//				try {
//					unbind();
//				} catch(Exception e ) {	
//				}
//				bind();
			} else {
				try {
					Thread.sleep(enquireLinkRate);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	public static void main(String[] args) {
		
		if (args.length == 1 ) {
			connectCCWS();
		} else if (args.length == 6) {
			connectEBridge(args[0],Integer.parseInt(args[1]),args[2],args[3],args[4],Long.parseLong(args[5]));
		} else if (args.length == 14) {
			  SMPPGateway smpptr = new SMPPGateway(args[0], Integer.parseInt(args[1]), args[2], args[3],args[4],Long.parseLong(args[5]),
					args[6],Integer.parseInt(args[7]),args[8],args[9],args[10],Long.parseLong(args[11]),
					("connect-ebridge".equals(args[12]) ? true : false), ("connect-ccws".equals(args[13]) ? true : false));
			smpptr.bind();
			  smpptr.linkMonitor();
		} else {
			System.out.println("Usage: smppgw connect-ccws>");
			System.out.println("\nOR\n");
			System.out.println("Usage: smppgw <ebridgeSmppIPAdress <ebridgeSmppPort> <ebridgeSystemId> <ebridgeSystemPassword> ebridgeSmppVersion> <enquireLinkRate>");			
			System.out.println("\nOR\n");
			System.out.println("Usage: smppgw <telecelSmppIPAdress> <telecelSmppPort> <telelceSystemId> <telecelSystemPassword> <smppVersion> <enquireLinkRate> \\");
			System.out.println("        <ebridgeSmppIPAdress <ebridgeSmppPort> <ebridgeSystemId> <ebridgeSystemPassword> ebridgeSmppVersion> <enquireLinkRate> \\");
			System.out.println("        <connect-ebridge|not-connected-to-ebridge> <connect-ccws|not-connected-to-ccws>");
			System.exit(1);
		}
	}

	private void showParameters() {

		System.out.println("##################################################################");
		System.out.println("### e-Bridge SMSC - SMPP Transciever version 2.0");
		System.out.println("### Copyright (c) 2005 - 2010. ");
		System.out.println("### All Rights Reserved.");
		System.out.println("### Released on November 4, 2010");
		System.out.println("###");
		System.out.println("### Telecel SMSC");
		System.out.println("### telecelSmppIPAdress    : " + telecelSmppIPAdress);
		System.out.println("### telecelSmppPort        : " + telecelSmppPort);
		System.out.println("### telelceSystemId        : " + telelceSystemId);
		System.out.println("### telecelSystemPassword  : " + telecelSystemPassword);
		System.out.println("### telecelSmppVersion     : " + telecelSmppVersion);
		System.out.println("### telecelEnquireLinkRate : " + telecelEnquireLinkRate);
		System.out.println("###");
		System.out.println("### EBridge SMSC");
		System.out.println("### ebridgeSmppIPAdress    : " + ebridgeSmppIPAdress);
		System.out.println("### ebridgeSmppPort        : " + ebridgeSmppPort + " milliseconds");
		System.out.println("### ebridgeSystemId        : " + ebridgeSystemId);
		System.out.println("### ebridgeSystemPassword  : " + ebridgeSystemPassword);
		System.out.println("### ebridgeSmppVersion     : " + ebridgeSmppVersion);
		System.out.println("### ebridgeEnquireLinkRate : " + ebridgeEnquireLinkRate);
		System.out.println("###");
		System.out.println("### Comverse IN");
		System.out.println("### isConnectedToCcws      : " + isConnectedToCcws);
		System.out.println("#################################################################");		
	}
}
package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.smpp.Data;
import com.ebridgecommerce.smpp.Session;
import com.ebridgecommerce.smpp.TCPIPConnection;
import com.ebridgecommerce.smpp.pdu.*;
import com.ebridgecommerce.smpp.util.SmppParamaters;

import java.util.Properties;

public class SmppServerConnector {

	boolean asynchronous = false;
	private Properties props;
	AddressRange addressRange = new AddressRange();
	private boolean isAsynchronous = true;

	Properties properties = new Properties();
	private Session session;
	private MtsmPDUListener pduListener;
	private TCPIPConnection connection;
	public String linkState;
	public long enquireLinkRate;
	private SubmitAORequestPDUFactory submitAORequestPDUFactory;

	private SMPPGateway transciever;
	private String smscIPAddress;
	private int smscPort;
	private String smscSystemId;
	private String smscPassword;

	private static SmppServerConnector instance;

	public static SmppServerConnector getInstance(SMPPGateway transciever, String smscIPAddress, int smscPort, String smscSystemId, String smscPassword) {
		if (instance == null) {
			instance = new SmppServerConnector(transciever, smscIPAddress, smscPort, smscSystemId, smscPassword);
			instance.connect();
			instance.bind();
		} else {
			/* TODO Check if still connected, otherwise reconnect*/
			/* TODO Check if still bound otherwise rebind */
		}
		return instance;
	}

	/**
	 * Initialises the SmppReceiver configuration file and sets smscId since the
	 * application supports multiple SMSC
	 * 
	 * @param transciever
	 */
	protected SmppServerConnector(SMPPGateway transciever, String smscIPAddress, int smscPort, String smscSystemId, String smscPassword) {

		System.out.println("##################################################################");
		System.out.println("### e-Bridge SMSC Connector 8.0");
		System.out.println("###");
		System.out.println("### SMSC IP Address      : " + smscIPAddress);
		System.out.println("### SMSC Port            : " + smscPort);
		System.out.println("### SMPP System Id       : " + smscSystemId);
		System.out.println("### SMPP System Password : " + smscPassword);
		System.out.println("#################################################################");

		this.smscIPAddress = smscIPAddress;
		this.smscPort = smscPort;
		this.smscSystemId = smscSystemId;
		this.smscPassword = smscPassword;
	}

	public void connect() {
		connection = new TCPIPConnection(smscIPAddress, smscPort);
		connection.setReceiveTimeout(20 * 1000);
	}

	public boolean bind() {

		try {

			BindRequest request = new BindTransciever();

			if (connection == null) {
				connect();
			}

			session = new Session(connection);
			request.setSystemId(smscSystemId);
			request.setPassword(smscPassword);
			request.setSystemType("EBridge");
			request.setInterfaceVersion((byte) 0x34);
			request.setAddressRange(SmppParamaters.getAddressRange());

			System.out.println("---> Bind request " + request.debugString());
			BindResponse response = null;
			pduListener = new MtsmPDUListener(transciever);
			if (isAsynchronous) {
				response = session.bind(request, pduListener);
			} else {
				response = session.bind(request, pduListener);
			}
			System.out.println("<--- Bind response " + response.debugString());
			if (response.getCommandStatus() == Data.ESME_ROK) {
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
			System.out.println("Going to unbind.");
			UnbindResp response = session.unbind();
			System.out.println("Unbind response " + response.debugString());
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
			System.out.println("Failed to forward mosm : " + sourceId + " -> " + destinationId + " : " + text);
		}
	}

	public void submit(SubmitSM request) {
		try {
			session.submit(request);
		} catch (Exception ex) {
//			ex.printStackTrace();
			System.out.println("Failed to forward mosm : " + request.debugString());
		}
	}
	
	public void deliver(DeliverSM request) {
		try {
			session.deliver(request);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed to forward mosm : " + request.debugString());
		}
	}
	
	public void enquireLink() {
		try {
			linkState = "UNKNOWN";
			EnquireLink request = new EnquireLink();
			System.out.print("#");
			session.enquireLink(request);
		} catch (Exception e) {
			linkState = "DOWN";
		}
	}
	
	public void linkMonitor() {
		while (true) {
			enquireLink();
			int count = 0;

			while ("UNKNOWN".equals(linkState) && count < 10) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
				}
				++count;
			}
			System.out.println("Link is " + linkState);
			if (!"UP".equals(linkState)) {
				System.out.print("-");
				bind();
			} else {
				try {
					Thread.sleep(15000);
				} catch (InterruptedException ex) {
				}
			}
		}
	}
}
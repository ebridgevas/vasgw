package com.ebridgecommerce.esme;

import com.ebridgecommerce.smpp.pdu.Address;
import com.ebridgecommerce.smpp.pdu.SubmitSM;
import com.ebridgecommerce.smpp.pdu.WrongLengthOfStringException;

/**
 * 
 * @author DaTekeshe
 */
public class SubmitAORequestPDUFactory {

	private Address sourceAddress = new Address();
	private Address destAddress = new Address();
	private SubmitSM request;

	public SubmitAORequestPDUFactory() {
		initSubmitSM();
	}

	private void initSubmitSM() {
		request = new SubmitSM();
		try {
			request.setServiceType("");
			request.setScheduleDeliveryTime("");
			request.setValidityPeriod("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		request.setSourceAddr(sourceAddress);
		request.setReplaceIfPresentFlag(Byte.parseByte("0"));
		request.setEsmClass(Byte.parseByte("0"));
		request.setProtocolId(Byte.parseByte("0"));
		request.setPriorityFlag(Byte.parseByte("3"));
		request.setRegisteredDelivery(Byte.parseByte("1"));
		request.setDataCoding(Byte.parseByte("0"));
		request.setSmDefaultMsgId(Byte.parseByte("0"));
	}

	public SubmitSM getInstance(String sourceId, String destinationId, String shortMessage) {

		setAddressParameter("source-address", sourceAddress, (byte)5, (byte)0, sourceId);
		request.setSourceAddr(sourceAddress);
		setAddressParameter("destination-address", destAddress, (byte) 1, (byte) 1, destinationId);
		request.setDestAddr(destAddress);
		if( "196.2.77.23".equals(SMPPTransciever.smppIPAdress) ||
				"196.2.77.25".equals(SMPPTransciever.smppIPAdress)){ 
		 request.setDestinationPort( (short)9280 );
		}
		try {			
			request.setShortMessage(shortMessage != null ? shortMessage : "");
		} catch (WrongLengthOfStringException ex) {
			if ((shortMessage != null) && (shortMessage.length() > 160)) {
				try {
					request.setShortMessage(shortMessage.substring(0, 160));
				} catch (Exception e) {
				}
			}
		} catch(Exception e){
			System.out.println("############# FATAL " + e.getMessage());
		}
		System.out.println("****** " + request.debugString());
		return request;
	}

	private void setAddressParameter(String descr, Address address, byte ton, byte npi, String addr) {
		address.setTon(ton);
		address.setNpi(npi);
		try {
			address.setAddress(addr);
		} catch (WrongLengthOfStringException e) {
			System.out.println("The length of " + descr + " parameter is wrong.");
		}
	}

}

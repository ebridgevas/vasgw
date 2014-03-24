package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.smpp.pdu.Address;
import com.ebridgecommerce.smpp.pdu.DeliverSM;
import com.ebridgecommerce.smpp.pdu.WrongLengthOfStringException;

/**
 * 
 * @author DaTekeshe
 */
public class DeliverSMPDUFactory {

	private Address sourceAddress = new Address();
	private Address destAddress = new Address();
	private DeliverSM request;

	public DeliverSMPDUFactory() {
		initDeliverSM();
	}

	private void initDeliverSM() {
		request = new DeliverSM();
		try {
			request.setServiceType("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		request.setSourceAddr(sourceAddress);
		request.setEsmClass(Byte.parseByte("0"));
		request.setProtocolId(Byte.parseByte("0"));
		request.setPriorityFlag(Byte.parseByte("3"));
		request.setRegisteredDelivery(Byte.parseByte("1"));
		request.setDataCoding(Byte.parseByte("0"));
	}

	public DeliverSM getInstance(String sourceId, String destinationId, String shortMessage) {

		setAddressParameter("source-address", sourceAddress, (byte)1, (byte)1, sourceId);
		request.setSourceAddr(sourceAddress);
		setAddressParameter("destination-address", destAddress, (byte) 1, (byte) 1, destinationId);
		request.setDestAddr(destAddress);
		// request.setDestinationPort( (short)9280 );
		try {
			request.setShortMessage(shortMessage);
		} catch (WrongLengthOfStringException ex) {
			if ((shortMessage != null) && (shortMessage.length() > 160)) {
				try {
					request.setShortMessage(shortMessage.substring(0, 160));
				} catch (Exception e) {
				}
			}
		}
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

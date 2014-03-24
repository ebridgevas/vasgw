package com.ebridgecommerce.smpp.util;

import com.ebridgecommerce.smpp.pdu.AddressRange;
import com.ebridgecommerce.smpp.pdu.WrongLengthOfStringException;

public class SmppParamaters {
	public static AddressRange getAddressRange() {
		AddressRange addressRange = new AddressRange();
		addressRange.setTon(Byte.parseByte("1"));
		addressRange.setNpi(Byte.parseByte("1"));
		try {
			addressRange.setAddressRange("[0-9]"); //a-z-A-Z_]*");
		} catch (WrongLengthOfStringException e) {
			System.out.println("The length of address-range parameter is wrong.");
		}
		return addressRange;
	}
}

package com.ebridgecommerce.domain;

import com.ebridgecommerce.domain.ServiceDTO;

public class USSDMenus {

	/**
	 * TODO Correct gramma - Buy my bundle - you can't buy your own thing.
	 * @return
	 */
	public static String getRootMenu(ServiceDTO service) {
		switch (service){
		case DATA_BUNDLE_PROMOTION:
			return "Please select either 1 or 2\n1. Balance enquiry \n2. Buy my bundle";
		case USAGE_PROMOTION:
			return "Press 1 to select the special number you want to call for free\nPress 2 to unsubscribe from this promotion\nPress 3 to change the special number";
		default:
			return null;
		}		
	}
	
}

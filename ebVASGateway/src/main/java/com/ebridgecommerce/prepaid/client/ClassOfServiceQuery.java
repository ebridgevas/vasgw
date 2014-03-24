package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.services.UsagePromotionService;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.rmi.RemoteException;

public class ClassOfServiceQuery {

	private ServiceSoap soapService;
	private String identity;

	public ClassOfServiceQuery(ServiceSoap soapService, String identity) {
		this.soapService = soapService;
		this.identity = identity;
	}

	public String getClassOfService(String subscriberId) throws TransactionFailedException {

		SubscriberRetrieve subscriberRetrieve = null;

		try {
			subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(subscriberId.substring(3), identity, 1);
		} catch (RemoteException e) {
			 e.printStackTrace();
			 throw new TransactionFailedException("Your number is not on prepaid package.");
		}
		
    /* Check if msisdn is on prepaid package. */
    String cosName = subscriberRetrieve.getSubscriberData().getCOSName();
    if ( !"TEL_COS".equals( cosName ) 
    		&& !"PROMO_COS".equals( cosName) 
    		&& !"PROMO_2_COS".equals(cosName) 
    		&& !"FF_COS".equals(cosName) 
    		&& !"promotion_cos".equals(cosName)
    		&& !UsagePromotionService.ACCUMULATOR_COS.containsValue(cosName)
    		&& !UsagePromotionService.BONUS_COS.containsValue(cosName)) {
    	throw new TransactionFailedException("Your number is not on prepaid package." );
    }
    
		if (!"Active".equalsIgnoreCase(subscriberRetrieve.getSubscriberData().getCurrentState())) {
			 throw new TransactionFailedException("Your number is not active.");
		}
		
		return subscriberRetrieve.getSubscriberData().getCOSName();
	}
}
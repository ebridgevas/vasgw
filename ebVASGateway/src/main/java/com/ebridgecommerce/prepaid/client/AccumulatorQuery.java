package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.RetrieveAccumulatorValueRequest;
import com.comverse_in.prepaid.ccws.RetrieveAccumulatorValueResponse;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

public class AccumulatorQuery {

	private ServiceSoap soapService;
	private String identity;

	public AccumulatorQuery(ServiceSoap soapService, String identity) {
		this.soapService = soapService;
		this.identity = identity;
	}

	public BigDecimal retrieveAccumalatorValue(String subscriberId) throws TransactionFailedException {

		SubscriberRetrieve subscriberRetrieve = null;

		try {
			System.out.println("---> " + new Date() + " getClassOfService for " + subscriberId.substring(3));
			subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(subscriberId.substring(3), identity, 1);
			System.out.println("<--- " + new Date());
		} catch (RemoteException e) {
			// e.printStackTrace();
			// throw new TransactionFailedException("Failed to retrieve subscriber : "
			// + subscriberId.substring(3) + " - " +
			// XMLParser.getError(e.getMessage()));
			throw new TransactionFailedException("Since you are not on prepaid package, please contact help desk. ");
		}
		if (!"Active".equalsIgnoreCase(subscriberRetrieve.getSubscriberData().getCurrentState())) {
			throw new TransactionFailedException("Subscriber account is " + subscriberRetrieve.getSubscriberData().getCurrentState());
		}

		try {
			RetrieveAccumulatorValueRequest accumulator = new RetrieveAccumulatorValueRequest();
			accumulator.setSubscriberId(subscriberId);
			accumulator.setIdentity(identity);
			accumulator.setAccumulator("CALL_TIME");

			RetrieveAccumulatorValueResponse value = soapService.retrieveAccumulatorValue(accumulator);
			return new BigDecimal(value.getAccumulatorValue());
			
    } catch (RemoteException e ) {
      throw new TransactionFailedException( " Failed to retrieve accumulator value for " + subscriberId.substring(3)  + "'s friends - " + XMLParser.getError(e.getMessage()));
    }

	}
}
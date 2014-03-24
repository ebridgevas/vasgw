package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberPB;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import org.joda.time.DateTime;
import com.ebridgecommerce.domain.Account;
import com.ebridgecommerce.domain.AccountType;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.rmi.RemoteException;
import java.util.Calendar;

public class FFQuery {

	private ServiceSoap soapService;
	private String identity;

	public FFQuery(ServiceSoap soapService, String identity) {
		this.soapService = soapService;
		this.identity = identity;
	}

	public Account getFriendFor(String subscriberId) throws TransactionFailedException {

		SubscriberRetrieve subscriberRetrieve = null;
		SubscriberPB phoneBook = null;
		
		try {
//			subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(subscriberId.substring(3), identity, 8);
			
			Calendar from = new DateTime().plusDays(-5).toGregorianCalendar();
			Calendar to = new DateTime().plusDays(1).toGregorianCalendar();
			subscriberRetrieve = soapService.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities(subscriberId.substring(3), identity, 8, from, to, true);
			
			phoneBook = subscriberRetrieve.getSubscriberPhoneBook();
			
		} catch (RemoteException e) {
			throw new TransactionFailedException("Since you are not on prepaid package, please contact help desk. ");
		}


		if (phoneBook == null) {
			System.out.println("Phonebook is empty...");
			return null;
		}
		
		
		if (phoneBook.getDestNumber1() != null) {
			
			String ffMsisdn = phoneBook.getDestNumber1();
			
      /* Check if friend is on prepaid package. */
			AccountType accountType = null;
      SubscriberRetrieve ffSubscriberRetrieve = null;

      try {
      	ffSubscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( MobileNumberFormatter.format(ffMsisdn).substring(3), identity, 1 );
        String ffCosName = ffSubscriberRetrieve.getSubscriberData().getCOSName();
        if ("TEL_COS".equals( ffCosName ) || "PROMO_COS".equals( ffCosName) || "PROMO_2_COS".equals(ffCosName) || "FF_COS".equals(ffCosName) || "promotion_cos".equals(ffCosName)) {
        	accountType = AccountType.PREPAID;
        } else {
        	accountType = AccountType.POSTPAID;
        }
      } catch (RemoteException e) {
      		accountType = AccountType.POSTPAID;
      } catch (InvalidMobileNumberException e) {
      		accountType = AccountType.POSTPAID;
			}

			return new Account(ffMsisdn, accountType );
			
		} else {
			return null;
		}
		
	}
}

package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberModify;
import com.comverse_in.prepaid.ccws.SubscriberPB;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.rmi.RemoteException;

public class FFUpdate {

    private ServiceSoap soapService ;
    private String identity;

    public FFUpdate( ServiceSoap soapService, String identity ) {
        this.soapService = soapService;
        this.identity = identity;
    }

    public Boolean addFriend( String subscriberId, String ffSubscriberId ) throws TransactionFailedException {

        SubscriberRetrieve subscriberRetrieve = null;

        try {
            subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( subscriberId.substring(3), identity, 1 );
         } catch (RemoteException e) {
        	e.printStackTrace();
            throw new TransactionFailedException("Your number is not on prepaid package.");
        }

        /* Check if msisdn is on prepaid package. */
        String cosName = subscriberRetrieve.getSubscriberData().getCOSName();
        if (!"TEL_COS".equals( cosName ) && !"PROMO_COS".equals( cosName) && !"PROMO_2_COS".equals(cosName) && !"FF_COS".equals(cosName) && !"promotion_cos".equals(cosName)) {
        	throw new TransactionFailedException("Your number is not on prepaid package." );
        }
        
        if ( ! "Active".equalsIgnoreCase( subscriberRetrieve.getSubscriberData().getCurrentState() ) ) {
          throw new TransactionFailedException("Your number is not active.");
        }
      
        boolean isSubscriberOnPrepaid = false;
        
        /* Check if friend is on prepaid package. */
        SubscriberRetrieve ffSubscriberRetrieve = null;

        try {
        	ffSubscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( MobileNumberFormatter.format(ffSubscriberId).substring(3), identity, 1 );
          String ffCosName = ffSubscriberRetrieve.getSubscriberData().getCOSName();
          if ("TEL_COS".equals( ffCosName ) || "PROMO_COS".equals( ffCosName) || "PROMO_2_COS".equals(ffCosName) || "FF_COS".equals(ffCosName) || "promotion_cos".equals(ffCosName)) {
          	isSubscriberOnPrepaid = true;
          } else {
          	isSubscriberOnPrepaid = false;
          }          
        } catch (RemoteException e) {
        		isSubscriberOnPrepaid = false;
        } catch (InvalidMobileNumberException e) {
        		isSubscriberOnPrepaid = false;
				}
        

        try {
            SubscriberModify subscriber = new SubscriberModify();            
            subscriber.setSubscriberID(subscriberId.substring(3));            
            subscriber.setIdentity(identity);
            SubscriberPB phoneBook = new SubscriberPB();
            phoneBook.setDestNumber1("del".equals(ffSubscriberId) ? "" : MobileNumberFormatter.format(ffSubscriberId).substring(3) );
            phoneBook.setDestNumber2("");
            phoneBook.setDestNumber3("");
            phoneBook.setDestNumber4("");
            phoneBook.setDestNumber5("");
            phoneBook.setDestNumber6("");
            phoneBook.setDestNumber7("");
            phoneBook.setDestNumber8("");
            phoneBook.setDestNumber9("");
            phoneBook.setDestNumber10("");
						subscriber.setSubscriberPhoneBook(phoneBook);
						soapService.modifySubscriber(subscriber);   
						
            return isSubscriberOnPrepaid;
        } catch (RemoteException e ) {
            throw new TransactionFailedException( " Failed to add " + ffSubscriberId + " to " + subscriberId.substring(3)  + "'s friends - " + XMLParser.getError(e.getMessage()));
        } catch (InvalidMobileNumberException e) {
        		throw new TransactionFailedException(ffSubscriberId + " is an invalid mobile number");
				}

    }
}


package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.ChangeCOSRequest;
import com.comverse_in.prepaid.ccws.ChangeCOSResponse;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.services.UsagePromotionService;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.rmi.RemoteException;

public class ClassOfServiceUpdate {

    private ServiceSoap soapService ;
    private String identity;

    public ClassOfServiceUpdate( ServiceSoap soapService, String identity ) {
        this.soapService = soapService;
        this.identity = identity;
    }

    public Boolean changeClassOfService( String subscriberId, String newCosName ) throws TransactionFailedException {

        SubscriberRetrieve subscriberRetrieve = null;

        try {
       	    subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( subscriberId.substring(3), identity, 1 );					
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
    		
    		/* */
    		
//  			for (BalanceEntity balanceEntity : subscriberRetrieve.getSubscriberData().getBalances()) {
//  				if (sourceBalanceName.equalsIgnoreCase(balanceEntity.getBalanceName())) {
//  					sourceBalanceEntity = balanceEntity;
//  					sourceAccountBalance = new BigDecimal(balanceEntity.getBalance());
//  					break;
//  				}
//  			}
  			
        ChangeCOSRequest changeCOSRequest = new ChangeCOSRequest();
        changeCOSRequest.setIdentity( identity );
        changeCOSRequest.setSubscriberId( subscriberId.substring(3) );
        changeCOSRequest.setNewCOS( newCosName );
        
        try {
        	System.out.println("COS Change : [" + subscriberId + "] " + newCosName);  
            ChangeCOSResponse res = soapService.changeCOS(changeCOSRequest);            
            System.out.println("COS Change Status = " + res.isStatus());            
            return true;
        } catch (RemoteException e ) {
            throw new TransactionFailedException(XMLParser.getError(e.getMessage()));
        }

    }
}

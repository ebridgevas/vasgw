package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.*;
import com.ebridgecommerce.domain.BalanceDTO;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountBalanceQuery {

    private ServiceSoap soapService ;
    private String identity;

    public AccountBalanceQuery( ServiceSoap soapService, String identity ) {
        this.soapService = soapService;
        this.identity = identity;
    }

    public Map<String, BigDecimal> getBalance( String subscriberId ) throws TransactionFailedException {

        SubscriberRetrieve subscriberRetrieve = null;

        try {
        	System.out.println("---> " + new Date() + " Retrieve subscriber - " + subscriberId.substring(3));
          subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( MobileNumberFormatter.format(subscriberId).substring(3), identity, 1 );
          System.out.println("<--- " + new Date());  
        } catch (RemoteException e) {
             throw new TransactionFailedException("Failed to retrieve subscriber : " + subscriberId.substring(3)  + " - " + XMLParser.getError(e.getMessage()));
        } catch (InvalidMobileNumberException e) {
        	throw new TransactionFailedException(e.getMessage());
				} 
        if ( ! "Active".equalsIgnoreCase( subscriberRetrieve.getSubscriberData().getCurrentState() ) ) {
            throw new TransactionFailedException("Subscriber account is " + subscriberRetrieve.getSubscriberData().getCurrentState() );
        }
        
    		/* Get balances for this subscriber */
        Map<String, BigDecimal> balances = new HashMap<String, BigDecimal>();
    		for (BalanceEntity balanceEntity : subscriberRetrieve.getSubscriberData().getBalances()) {
    			balances.put(balanceEntity.getBalanceName(), new BigDecimal(balanceEntity.getBalance()));
    		}
        return balances;

    }

		public Map<String, BalanceDTO> getAccountBalance(String msisdn) throws TransactionFailedException {
      SubscriberRetrieve subscriberRetrieve = null;

      try {
      	System.out.println("---> " + new Date() + " Retrieve subscriber - " + msisdn.substring(3));
        subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( MobileNumberFormatter.format(msisdn).substring(3), identity, 1 );
        System.out.println("<--- " + new Date());  
      } catch (RemoteException e) {
           throw new TransactionFailedException("Failed to retrieve subscriber : " + msisdn.substring(3)  + " - " + XMLParser.getError(e.getMessage()));
      } catch (InvalidMobileNumberException e) {
      	throw new TransactionFailedException(e.getMessage());
			} 
      if ( ! "Active".equalsIgnoreCase( subscriberRetrieve.getSubscriberData().getCurrentState() ) ) {
          throw new TransactionFailedException("Subscriber account is " + subscriberRetrieve.getSubscriberData().getCurrentState() );
      }
      
  		/* Get balances for this subscriber */
      Map<String, BalanceDTO> balances = new HashMap<String, BalanceDTO>();
  		for (BalanceEntity balanceEntity : subscriberRetrieve.getSubscriberData().getBalances()) {
  			balances.put(balanceEntity.getBalanceName(),
  					new BalanceDTO(
  							balanceEntity.getBalanceName(),
  							new BigDecimal(balanceEntity.getAvailableBalance()),
  							balanceEntity.getAccountExpiration() != null ? balanceEntity.getAccountExpiration().getTime() : null));
  			
  		}
      return balances;
		}
		
		public void getUsage(){
			try {
				SubscriberRetrieve subscriberRetrieve = soapService.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities(null, null, 1, null, null, false);
				CallHistory[] calls = subscriberRetrieve.getCallHistories();
				for(CallHistory call: calls){
					call.getFundUsageType();
				}
				RechargeHistory[] recharge = subscriberRetrieve.getRechargeHistories();
				for ( RechargeHistory history : recharge) {
					double value = history.getFaceValue();
				}
				subscriberRetrieve.getMonetaryTransactionsRecords();
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
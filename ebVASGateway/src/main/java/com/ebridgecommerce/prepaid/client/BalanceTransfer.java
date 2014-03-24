
package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.ChangeCOSRequest;
import com.comverse_in.prepaid.ccws.ChangeCOSResponse;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.rmi.RemoteException;

public class BalanceTransfer {

    private ServiceSoap soapService ;
    private String identity;

    public BalanceTransfer( ServiceSoap soapService, String identity ) {
        this.soapService = soapService;
        this.identity = identity;
    }

    public Boolean changeClassOfService( String subscriberId, String newCosName ) throws TransactionFailedException {

        SubscriberRetrieve subscriberRetrieve = null;

        try {
            subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory( subscriberId.substring(3), identity, 1 );
        } catch (RemoteException e) {
            throw new TransactionFailedException("Failed to retrieve subscriber : " + subscriberId.substring(3)  + " - " + XMLParser.getError(e.getMessage()));
        }

        if ( ! "Active".equalsIgnoreCase( subscriberRetrieve.getSubscriberData().getCurrentState() ) ) {
            throw new TransactionFailedException("Subscriber account is " + subscriberRetrieve.getSubscriberData().getCurrentState() );
        }

        if (  newCosName.equals( subscriberRetrieve.getSubscriberData().getCOSName() ) ) {
            throw new TransactionFailedException("Your are already on per " + ("TEL_COS".equals( newCosName ) ? "minute. To change to per second, send tariff#second to 350" : " second. To change to per minute, send tariff#minute to 350") );
        }

        ChangeCOSRequest changeCOSRequest = new ChangeCOSRequest();
        changeCOSRequest.setIdentity( identity );
        changeCOSRequest.setSubscriberId( subscriberId.substring(3) );
        changeCOSRequest.setNewCOS( newCosName );
        
        try {
            ChangeCOSResponse res = soapService.changeCOS(changeCOSRequest);
            return true;
        } catch (RemoteException e ) {
            throw new TransactionFailedException( newCosName + " change failed for : " + subscriberId.substring(3)  + " - " + XMLParser.getError(e.getMessage()));
        }

    }
}

package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.MonetaryTransactionRecord;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import org.joda.time.DateTime;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonetaryTransferRecordQuery {

	private ServiceSoap soapService;
	private String identity;

	public MonetaryTransferRecordQuery(ServiceSoap soapService, String identity) {
		this.soapService = soapService;
		this.identity = identity;
	}

	public String retrieveMonetaryTransactionRecord(String subscriberId) throws TransactionFailedException {

		SubscriberRetrieve subscriberRetrieve = null;

		try {
//			subscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(subscriberId.substring(3), identity, 128);
			
			Calendar from = new DateTime().plusDays(-5).toGregorianCalendar();
			Calendar to = new DateTime().plusDays(1).toGregorianCalendar();
			subscriberRetrieve = soapService.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities(subscriberId.substring(3), identity, 128, from, to, true);
//			System.out.println("############### MTR - " + subscriberRetrieve.getMonetaryTransactionsRecords());                                                                                      *W
		} catch (RemoteException e) {
			throw new TransactionFailedException("Subscriber retrieve failed. " + e.getMessage());
		}
//		if (!"Active".equalsIgnoreCase(subscriberRetrieve.getSubscriberData().getCurrentState())) {
//			throw new TransactionFailedException("Subscriber account is " + subscriberRetrieve.getSubscriberData().getCurrentState());
//		}

		MonetaryTransactionRecord[] mtrs = subscriberRetrieve.getMonetaryTransactionsRecords();
		if (mtrs != null ) {
			
			long uuid = 0;
			
			for ( MonetaryTransactionRecord mtr : mtrs ) {
				
				
				System.out.print("\n" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(mtr.getModDate().getTime()) );
			
				System.out.print( mtr.getBonusPlanName() != null ? " :: " + mtr.getBonusPlanName() : "" );
				System.out.print(" :: MTR Type: " + mtr.getMTRComment());
				BigDecimal amount = new BigDecimal(mtr.getBonusAwarded());
				System.out.print( " :: Value: $" + amount.setScale(2, RoundingMode.HALF_UP));
				
				if (   ( mtr.getMTRComment() != null ) 
						&& ( ( mtr.getMTRComment().indexOf("Bonus") > -1 ) 
								|| ( mtr.getMTRComment().indexOf("Discount") > -1 ) ) 
						&&   mtr.getBonusAwarded() > 0.0) {
//					System.out.println("Bonus Plane Name: " + mtr.getBonusPlanName() );
//					System.out.println("MTR Comment     : " + mtr.getMTRComment());
//					System.out.println("Bonus Awarded   : " + mtr.getBonusAwarded());
					if ( mtr.getModDate().getTimeInMillis() > uuid ) {
						uuid = mtr.getModDate().getTimeInMillis();
					}
				}
				/*
				 ######### getBonusAwarded 1.0
######### getBonusPlanName VALENTINE
######### getChangeAccumulator 0.0
######### getCurrentCOSName CONT_COS
######### getChargecode null
######### getDiscountAwarded 0.0
######### getDiscountPlanName null
######### getMTRComment SDS: Bonus or Discount
				 */
			}
			
		return uuid > 0 ? "" + uuid : null;
		} else {
			return null;
		}

//		for ( MonetaryTransactionRecord mtr : subscriberRetrieve.getMonetaryTransactionsRecords() ){
//			if ( "CALL_TIME".equalsIgnoreCase(mtr.getMTRComment())) {
//				return mtr;
//			}
//		}
//		throw new TransactionFailedException("No Monetary Transaction Record found.");
	}
}
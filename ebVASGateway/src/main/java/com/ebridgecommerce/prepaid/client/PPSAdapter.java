package com.ebridgecommerce.prepaid.client;

import org.joda.time.DateTime;
import com.ebridgecommerce.domain.Messages;
import com.ebridgecommerce.domain.BalanceDTO;
import com.ebridgecommerce.smppgw.SystemParameters;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.math.BigDecimal;
import java.util.Map;

public class PPSAdapter {
	
	private PPSClient ppsClient;
	
	private static final String voiceBalanceName = "Core";
	private static final String suspenceAccount = SystemParameters.SYSTEM_PARAMETERS.get("SUSPENSE_ACCOUNT");
	
	//private static final String dataBalanceName = "Gprs_usd";
	private static final String dataBalanceName = "Gprs_bundle";
	/* 1MB = 1 049 000 Octets*/
	private static final BigDecimal DATA_UNIT = new BigDecimal(1049000);

	public PPSAdapter(){
		ppsClient = PPSClient.getInstance();
	}
	
	/**
	 * Get Balance
	 * @param uuid
	 * @param msisdn
	 * @return
	 * @throws TransactionFailedException
	 */
	public Map<String, BigDecimal> getBalance(String uuid, String msisdn) throws TransactionFailedException{		
		return ppsClient.getBalance(msisdn);	
	}
	
	public Map<String, BalanceDTO> getAccountBalance(String uuid, String msisdn) throws TransactionFailedException {
		return ppsClient.getAccountBalance(msisdn);	
	}
	
	/**
	 * Data Bundle Purchase
	 * 
	 * @param uuid
	 * @param msisdn
	 * @param price
	 * @param credit 
	 * @param debit 
	 * @param dataRate 
	 * @return
	 * @throws TransactionFailedException
	 */
	public Map<String, BalanceDTO> dataBundlePurchase(String uuid, String msisdn, BigDecimal price, BigDecimal bundleSize, BigDecimal debit, BigDecimal credit) throws TransactionFailedException {



		Map<String, BalanceDTO> balances = ppsClient.getAccountBalance(msisdn);

		/* Calculate expiration date. */
		DateTime expiry = new DateTime();
//		if ( new BigDecimal(5.00).compareTo(bundleSize) == 0 ) {
//			System.out.println("########## Bundle Size is 5MB ");
//			DateTime currentExpiry = new DateTime(balances.get(dataBalanceName).getExpiryDate());
//			if (currentExpiry.isAfter(expiry.plusDays(1)) && BigDecimal.ZERO.compareTo(balances.get(dataBalanceName).getAmount()) == -1) {
//				expiry = currentExpiry;
//			} else {
//				expiry = expiry.plusDays(1);
//			}
//		} else {
		
		int window = 30;
		
		switch((debit.multiply(new BigDecimal(100))).intValue()){
		case 50:
		case 100:
		case 250:
			window = 30;
			break;
		case 2250:
			window = 60;
			break;
		case 4250:
			window = 90;
			break;
		case 7500:
			window = 120;
			break;			
		default:
			window = 30;			
		}
		
		
		expiry = expiry.plusDays(window);
//		}
		expiry = expiry.withHourOfDay(23);
		expiry = expiry.withMinuteOfHour(59);
		expiry = expiry.withSecondOfMinute(59);
		expiry = expiry.withMillisOfSecond(999);
		if ( ! isSufficientBalance(price, balances.get(voiceBalanceName).getAmount()) ){
			throw new TransactionFailedException( Messages.INSUFFICIENT_FUNDS );
		}
			
		/* Debit voiceAccount and Credit suspenceAccount. */
		ppsClient.transfer(
				"D" + uuid, 
				msisdn, 
				voiceBalanceName, 
				debit,
				suspenceAccount, 
				"Core", 
				credit, 
				Boolean.FALSE);
		
		/* Debit suspenceAccount and Credit dataAccount. */
		try {
			ppsClient.transfer(
					"C" + uuid, 
					suspenceAccount, 
					"Core", 
					credit,
					msisdn, 
					dataBalanceName, 
//					getAmount(bundleSize), 
					credit,
					Boolean.FALSE,
					expiry.toDate()
					);
		} catch(TransactionFailedException e){
				/* Reversal: Debit suspenceAccount and Credit voiceAccount. */
				ppsClient.transfer(
						"R" + uuid, 
						suspenceAccount, 
						"Core", 
						credit,
						msisdn, 
						voiceBalanceName, 							
						debit, 
						Boolean.FALSE);			
				throw new TransactionFailedException("Maximum data bundle balance exceeded.");
		}
		return ppsClient.getAccountBalance(msisdn);

	}
	
	/* Calculate amount purchased in octets. */
	private BigDecimal getAmount(BigDecimal bundleSize) {
		return bundleSize.multiply(DATA_UNIT);
	}
	/**
	 * Is Sufficient Balance
	 * @param amount
	 * @param balance
	 * @return
	 */
	protected Boolean isSufficientBalance(BigDecimal amount, BigDecimal balance){
		return !(amount.compareTo(balance) > 0);
	}


}

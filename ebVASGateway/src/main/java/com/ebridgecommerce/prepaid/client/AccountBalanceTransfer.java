package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.BalanceCreditAccount;
import com.comverse_in.prepaid.ccws.BalanceEntity;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.dao.VasGatewayDao;
import org.joda.time.DateTime;
import com.ebridgecommerce.smppgw.SystemParameters;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 
 * @author DaTekeshe
 */
public class AccountBalanceTransfer {

	private ServiceSoap soapService;
	private String identity;
	private Statement stmt;
	
	public AccountBalanceTransfer(ServiceSoap soapService, String identity) {
		this.soapService = soapService;
		this.identity = identity;
		
		while (true) {
			try {
				Connection conn = VasGatewayDao.getConnection();
				stmt = conn.createStatement();
				break;
			} catch (Exception e) {
				try {
					System.out.print("*");
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
				}
			}
		}

	}

	public List<Map<String, String>> transfer(String uuid, String sourceAccountId, String destinationAccountId, BigDecimal amount, boolean createCDR)
			throws TransactionFailedException {

		System.out.println("### AccountBalanceTransfer 2");
		boolean creatingCDR = false;
		
		List<Map<String, String>> responses = new ArrayList<Map<String, String>>();

		SubscriberRetrieve sourceAccountRetrieve = null;
		String sourceBalanceName = "Core";
		String destinationBalanceName = "Core";
		try {
			sourceAccountRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(MobileNumberFormatter.format(sourceAccountId).substring(3), identity, 1);			
			if(
					!"TEL_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"PROMO_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"promotion_cos".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())		
    			&& !"FF_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())						
					&& !"PROMO_2_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
			){
				if (!createCDR) {
					throw new TransactionFailedException("This service is for prepaid customers only.");
				}
				creatingCDR = true;
			}
		} catch (RemoteException e) {
			System.out.println( "Error code = " + XMLParser.getErrorCode(e.getMessage()) );
			if ("4006".equals(XMLParser.getErrorCode(e.getMessage()))) {
				if(!createCDR){
					throw new TransactionFailedException(XMLParser.getError(e.getMessage()));
				} else {
					creatingCDR = true;
				}
			}
			throw new TransactionFailedException("Failed to retrieve subscriber : " + sourceAccountId + " - " + XMLParser.getError(e.getMessage()));
		} catch (InvalidMobileNumberException e) {
			throw new TransactionFailedException("Source account is invalid");
		}

		BigDecimal sourceAccountBalance = null;
		BalanceEntity sourceBalanceEntity = null;
		
		if (!creatingCDR) {
			/* Verify subscriber status */
			if (!"Active".equalsIgnoreCase(sourceAccountRetrieve.getSubscriberData().getCurrentState())) {
				throw new TransactionFailedException("Subscriber account is " + sourceAccountRetrieve.getSubscriberData().getCurrentState());
			}
	
			/* Get current balance for this subscriber */
			for (BalanceEntity balanceEntity : sourceAccountRetrieve.getSubscriberData().getBalances()) {
				if (sourceBalanceName.equalsIgnoreCase(balanceEntity.getBalanceName())) {
					sourceBalanceEntity = balanceEntity;
					sourceAccountBalance = new BigDecimal(balanceEntity.getBalance());
					break;
				}
			}
	
			/* Check minimum balance requirement. */
			BigDecimal sourceMinimumBalance = new BigDecimal(0);
			if (sourceMinimumBalance.compareTo(sourceAccountBalance.subtract(amount)) > 0) {
				throw new TransactionFailedException(
						"You have exceeded the maximum limit of airtime available for transfer. Please recharge with more airtime to proceed.");
			}
		}
		SubscriberRetrieve beneficiarySubscriberRetrieve = null;
		try {
			System.out.println("Retrieving subscriberId = " + MobileNumberFormatter.format(destinationAccountId).substring(3));
			System.out.println("Retrieving raw subscriberId = " + destinationAccountId.substring(3));
			beneficiarySubscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(MobileNumberFormatter.format(destinationAccountId).substring(3),
					identity, 1);
		} catch (RemoteException e) {
			throw new TransactionFailedException("Failed to retrieve beneficiary account # " + destinationAccountId + " : " + XMLParser.getError(e.getMessage()));
		} catch (InvalidMobileNumberException e) {
			throw new TransactionFailedException("Invalid beneficiary account # " + destinationAccountId);
		}

		System.out.println("Response = " + beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState());
		/* Verify subscriber status */
		if (!"Active".equalsIgnoreCase(beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState())
				&& !beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState().startsWith("suspended")) {
			throw new TransactionFailedException("Beneficiary account is " + beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState());
		}

		/* Get current balance for this subscriber */
		BigDecimal beneficiaryAccountBalance = null;
		BalanceEntity beneficiaryBalanceEntity = null;
		for (BalanceEntity balanceEntity : beneficiarySubscriberRetrieve.getSubscriberData().getBalances()) {
			if (destinationBalanceName.equalsIgnoreCase(balanceEntity.getBalanceName())) {
				beneficiaryBalanceEntity = balanceEntity;
				beneficiaryAccountBalance = new BigDecimal(balanceEntity.getBalance());
				break;
			}
		}

		String debitBalanceChangeComments = "Balance transfer # " + uuid + " to subscriber # " + MobileNumberFormatter.shortFormat(destinationAccountId);
		String creditBalanceChangeComments = "Balance transfer # " + uuid + ":" + " from subscriber # " + MobileNumberFormatter.shortFormat(sourceAccountId);
		String reversalBalanceChangeComments = "Reversal for balance transfer # " + uuid + "  to subscriber # "
				+ MobileNumberFormatter.shortFormat(destinationAccountId);

		/* Debit source account */
		boolean debitOk = false;
		if (!creatingCDR) {
			Date sourceExpirationDate = sourceBalanceEntity.getAccountExpiration().getTime();
			System.out.println("########## msisdn: " + sourceAccountId + ", " + ", current expiry = " + sourceExpirationDate );
			if(new DateTime(sourceExpirationDate).minusDays(1).isBeforeNow()){
				sourceExpirationDate = new DateTime().plusDays(30).toDate();
			}
			System.out.println("########## msisdn: " + sourceAccountId + ", " + ", new expiry = " + sourceExpirationDate );
			debitOk = creditAccount(sourceAccountId, sourceBalanceName, (new BigDecimal(0)).subtract(amount), sourceExpirationDate, "",
					debitBalanceChangeComments);
		} else {
			String subscriberId = sourceAccountId;
			try {
				subscriberId = MobileNumberFormatter.format(sourceAccountId);
			} catch (InvalidMobileNumberException e) {
			}
			
			String imsi = VasGatewayDao.getIMSI(stmt, subscriberId);
			if( imsi == null) {
				imsi = "";
			}
			VasGatewayDao.createCDR(stmt, subscriberId, imsi, amount);
			debitOk = true;
		}
		if (debitOk) {
			/* Credit beneficiary account */
			try {
				System.out.println("credit " + destinationAccountId + ", amount = " + amount + ", creditBalanceChangeComments = " + creditBalanceChangeComments);
				Date expirationDate = beneficiaryBalanceEntity.getAccountExpiration().getTime();
				System.out.println("########## msisdn: " + destinationAccountId + ", " + ", current expiry = " + expirationDate );

				if(new DateTime(expirationDate).minusDays(1).isBeforeNow()){
					expirationDate = new DateTime().plusDays(30).toDate();
				}
				System.out.println("########## msisdn: " + destinationAccountId + ", " + ", new expiry = " + expirationDate );

				if (creditAccount(destinationAccountId, destinationBalanceName, amount, expirationDate, "",
						creditBalanceChangeComments)) {
					if (!creatingCDR) {
						responses.add(getResponse(sourceAccountId, "Airtime transfer to " + destinationAccountId
							+ " accepted. Your balance is now " + new DecimalFormat("###,##.00").format(sourceAccountBalance.subtract(amount).doubleValue())));
						responses.add(getResponse(destinationAccountId, "Airtime transfer from " + sourceAccountId
							+ " accepted. Your balance is now " + new DecimalFormat("###,##.00").format(beneficiaryAccountBalance.add(amount).doubleValue())));
					} else {
						responses.add(getResponse(sourceAccountId, "Airtime transfer to " + destinationAccountId
								+ " accepted."));
							responses.add(getResponse(destinationAccountId, "Airtime transfer from " + sourceAccountId
								+ " accepted. Your balance is now " ));
								
					}
					return responses;
				} else {
					
					throw new TransactionFailedException("Failed to topup the account specified.");
				}
			} catch (TransactionFailedException ex) {
				/* Reverse source account debit */
				ex.printStackTrace();
				if (creditAccount(sourceAccountId, sourceBalanceName, amount, sourceBalanceEntity.getAccountExpiration().getTime(), "", reversalBalanceChangeComments)) {
					throw new TransactionFailedException("Failed to topup the account specified.");
				}
				throw new TransactionFailedException(creditBalanceChangeComments + "failed. ");
			}
		} else {
			throw new TransactionFailedException("Failed to deduct your account. ");
		}
	}

	public List<Map<String, String>> transfer(
			String uuid, 
			String sourceAccountId, 
			String sourceBalanceName, 
			BigDecimal debitAmount,
			String destinationAccountId, 
			String destinationBalanceName, 
			BigDecimal creditAmount, 
			boolean createCDR) 
					throws TransactionFailedException {
		
		System.out.println("### AccountBalanceTransfer 3");
		
		return  transfer(
				uuid, 
				sourceAccountId, 
				sourceBalanceName, 
				debitAmount,
				destinationAccountId, 
				destinationBalanceName, 
				creditAmount, 
				createCDR,
				null);
	}
	
	public List<Map<String, String>> transfer(
			String uuid, 
			String sourceAccountId, 
			String sourceBalanceName, 
			BigDecimal debitAmount,
			String destinationAccountId, 
			String destinationBalanceName, 
			BigDecimal creditAmount, 
			boolean createCDR,
			Date expirationDate)
			
		throws TransactionFailedException {

		System.out.println("### AccountBalanceTransfer 4");
		
		boolean creatingCDR = false;

		List<Map<String, String>> responses = new ArrayList<Map<String, String>>();

		SubscriberRetrieve sourceAccountRetrieve = null;

		try {
			
			String sourceMsIsdn = null;
			try {
				sourceMsIsdn = MobileNumberFormatter.format(sourceAccountId).substring(3);
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}

			String suspenceAccount = null;
			try {
				suspenceAccount = MobileNumberFormatter.format(SystemParameters.SYSTEM_PARAMETERS.get("SUSPENSE_ACCOUNT")).substring(3);
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}
			
			sourceAccountRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(MobileNumberFormatter.format(sourceAccountId).substring(3), identity, 1);			
			if(
					!"TEL_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"PROMO_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"PROMO_2_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"promotion_cos".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())		
    			&& !"FF_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())		
					&& !suspenceAccount.equals(sourceMsIsdn	)){
				
				if (!createCDR) {
					throw new TransactionFailedException("Subscriber is not on prepaid package");
				}
				creatingCDR = true;
			}
		} catch (RemoteException e) {
			System.out.println( "Error code = " + XMLParser.getErrorCode(e.getMessage()) );
			if ("4006".equals(XMLParser.getErrorCode(e.getMessage()))) {
				if(!createCDR){
					throw new TransactionFailedException(XMLParser.getError(e.getMessage()));
				} else {
					creatingCDR = true;
				}
			}
			throw new TransactionFailedException("Failed to retrieve subscriber : " + sourceAccountId + " - " + XMLParser.getError(e.getMessage()));
		} catch (InvalidMobileNumberException e) {
			throw new TransactionFailedException("Source account is invalid");
		}

		BigDecimal sourceAccountBalance = null;
		BalanceEntity sourceBalanceEntity = null;

		if (!creatingCDR) {
			/* Verify subscriber status */
			if (!"Active".equalsIgnoreCase(sourceAccountRetrieve.getSubscriberData().getCurrentState())) {
				throw new TransactionFailedException("Subscriber account is " + sourceAccountRetrieve.getSubscriberData().getCurrentState());
			}
		
			/* Get current balance for this subscriber */
			for (BalanceEntity balanceEntity : sourceAccountRetrieve.getSubscriberData().getBalances()) {
				if (sourceBalanceName.equalsIgnoreCase(balanceEntity.getBalanceName())) {
					sourceBalanceEntity = balanceEntity;
					sourceAccountBalance = new BigDecimal(balanceEntity.getAvailableBalance());
					break;
				}
			}
		
			/* Check minimum balance requirement. */
			String sourceMsIsdn = null;
			try {
				sourceMsIsdn = MobileNumberFormatter.format(sourceAccountId).substring(3);
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}

			String suspenceAccount = null;
			try {
				suspenceAccount = MobileNumberFormatter.format(SystemParameters.SYSTEM_PARAMETERS.get("SUSPENSE_ACCOUNT")).substring(3);
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}
			
			if ( !suspenceAccount.equals(sourceMsIsdn)) {
				BigDecimal sourceMinimumBalance = new BigDecimal(0);
				if (sourceMinimumBalance.compareTo(sourceAccountBalance.subtract(debitAmount)) > 0) {
					throw new TransactionFailedException(
							"You have exceeded the maximum limit of airtime available for transfer. Please recharge with more airtime to proceed.");
				}
			}
		}
		
		SubscriberRetrieve beneficiarySubscriberRetrieve = null;
		try {
			System.out.println("Retrieving subscriberId = " + MobileNumberFormatter.format(destinationAccountId).substring(3));
			System.out.println("Retrieving raw subscriberId = " + destinationAccountId.substring(3));
			beneficiarySubscriberRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(MobileNumberFormatter.format(destinationAccountId).substring(3),
					identity, 1);
		} catch (RemoteException e) {
			throw new TransactionFailedException("Failed to retrieve beneficiary account # " + destinationAccountId + " : " + XMLParser.getError(e.getMessage()));
		} catch (InvalidMobileNumberException e) {
			throw new TransactionFailedException("Invalid beneficiary account # " + destinationAccountId);
		}

		System.out.println("Response = " + beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState());
		/* Verify subscriber status */
		if (!"Active".equalsIgnoreCase(beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState())
				&& !beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState().startsWith("suspended")) {
			throw new TransactionFailedException("Beneficiary account is " + beneficiarySubscriberRetrieve.getSubscriberData().getCurrentState());
		}
		
		/* Get current balance for this subscriber */
		BigDecimal beneficiaryAccountBalance = null;
		BalanceEntity beneficiaryBalanceEntity = null;
		for (BalanceEntity balanceEntity : beneficiarySubscriberRetrieve.getSubscriberData().getBalances()) {
			if (destinationBalanceName.equalsIgnoreCase(balanceEntity.getBalanceName())) {
				beneficiaryBalanceEntity = balanceEntity;
				beneficiaryAccountBalance = new BigDecimal(balanceEntity.getAvailableBalance());
				break;
			}
		}

		String debitBalanceChangeComments = "Balance transfer # " + uuid + " to subscriber # " + MobileNumberFormatter.shortFormat(destinationAccountId);
		String creditBalanceChangeComments = "Balance transfer # " + uuid + ":" + " from subscriber # " + MobileNumberFormatter.shortFormat(sourceAccountId);
		String reversalBalanceChangeComments = "Reversal for balance transfer # " + uuid + "  to subscriber # "
				+ MobileNumberFormatter.shortFormat(destinationAccountId);
		
		/* Debit source account */
		boolean debitOk = false;
		if (!creatingCDR) {
			String sourceMsIsdn = null;
			try {
				sourceMsIsdn = MobileNumberFormatter.format(sourceAccountId).substring(3);
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}

			String suspenceAccount = null;
			try {
				suspenceAccount = MobileNumberFormatter.format(SystemParameters.SYSTEM_PARAMETERS.get("SUSPENSE_ACCOUNT")).substring(3);
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}
			
			Date sourceExpiryDate = sourceBalanceEntity.getAccountExpiration().getTime();	
			
			if ( suspenceAccount.equals(sourceMsIsdn)) {
				sourceExpiryDate.setYear(2050);
			}

			System.out.println("###### sourceExpiryDate = " + sourceExpiryDate);
			
			debitOk = creditAccount(sourceAccountId, sourceBalanceName, (new BigDecimal(0)).subtract(debitAmount), sourceExpiryDate, "",
					debitBalanceChangeComments);
		} else {
			String subscriberId = sourceAccountId;
			try {
				subscriberId = MobileNumberFormatter.format(sourceAccountId);
			} catch (InvalidMobileNumberException e) {
			}
			
			String imsi = VasGatewayDao.getIMSI(stmt, subscriberId);
			if( imsi == null) {
				imsi = "";
			}
			VasGatewayDao.createCDR(stmt, subscriberId, imsi, debitAmount);
			debitOk = true;
		}
		if (debitOk) {
			System.out.println(new Date() + "Balance Transfer: Account : " + sourceAccountId + "[" + sourceBalanceName + "], Amount: " + (new BigDecimal(0)).subtract(debitAmount));
			/* Credit beneficiary account */
			try {
				
				if ( expirationDate == null ) {
				
					if (beneficiaryBalanceEntity != null){
						expirationDate = beneficiaryBalanceEntity.getAccountExpiration()!= null ?
								beneficiaryBalanceEntity.getAccountExpiration().getTime() : new Date();
						expirationDate.setYear(2050);
					} else {
						expirationDate =  new Date();
						expirationDate.setYear(2050);
					}
				}
				
				System.out.println("########## AccountBalanceTransfer = ");
				System.out.println("########## destinationBalanceName = " + destinationBalanceName);
				System.out.println("########## creditAmount = " + creditAmount);
				System.out.println("########## expirationDate = " + expirationDate);
				
				System.out.println(new Date() + "Balance Transfer: Account : " + destinationAccountId + "[" + destinationBalanceName + "], Amount: " + creditAmount);
				if (creditAccount(destinationAccountId, destinationBalanceName, creditAmount, expirationDate, "",
						creditBalanceChangeComments)) {
					System.out.println("credit " + destinationAccountId + ", amount = " + creditAmount + ", creditBalanceChangeComments = " + creditBalanceChangeComments);
					if (!creatingCDR) {
						responses.add(getResponse(sourceAccountId, "Airtime transfer to " + destinationAccountId
							+ " accepted. Your balance is now " + new DecimalFormat("###,##.00").format(sourceAccountBalance.subtract(creditAmount).doubleValue())));
						responses.add(getResponse(destinationAccountId, "Airtime transfer from " + sourceAccountId
							+ " accepted. Your balance is now " + new DecimalFormat("###,##.00").format(beneficiaryAccountBalance.add(creditAmount).doubleValue())));
					} else {
						responses.add(getResponse(sourceAccountId, "Airtime transfer to " + destinationAccountId
								+ " accepted."));
							responses.add(getResponse(destinationAccountId, "Airtime transfer from " + sourceAccountId
								+ " accepted. Your balance is now " ));
								
					}
					return responses;
				} else {
					
					throw new TransactionFailedException("Failed to topup the account specified.");
				}
			} catch (TransactionFailedException ex) {
				/* Reverse source account debit */
				ex.printStackTrace();
				if (creditAccount(sourceAccountId, sourceBalanceName, debitAmount, sourceBalanceEntity.getAccountExpiration().getTime(), "", reversalBalanceChangeComments)) {
					throw new TransactionFailedException("Failed to topup the account specified.");
				}
				throw new TransactionFailedException(creditBalanceChangeComments + "failed. ");
			}
		} else {
			throw new TransactionFailedException("Failed to deduct your account. ");
		}
	}
	
	private boolean creditAccount(String subscriberId, String balanceName, BigDecimal amount, Date expirationDate, String balanceChangeCode,
			String balanceChangeComments) throws TransactionFailedException {

		BalanceCreditAccount[] balanceCreditAccounts = { new BalanceCreditAccount() };
		balanceCreditAccounts[0].setBalanceName(balanceName);
		balanceCreditAccounts[0].setCreditValue(amount.doubleValue());

//		if ("Gprs_bundle".equalsIgnoreCase(balanceName)){
//			expirationDate = new DateTime().plusDays(1).toDate();
//			GregorianCalendar gc = new GregorianCalendar();
//			gc.setTime(expirationDate);
//			balanceCreditAccounts[0].setExpirationDate(gc);
//		} else {		
			if (expirationDate != null) {
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(expirationDate);
				balanceCreditAccounts[0].setExpirationDate(gc);
			} else {
				expirationDate = new Date();
				expirationDate.setYear(2050);
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(expirationDate);
				balanceCreditAccounts[0].setExpirationDate(gc);
			}
			if (expirationDate.before(new Date())){
				expirationDate.setYear(2050);
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(expirationDate);
				balanceCreditAccounts[0].setExpirationDate(gc);
			}
//		}
		
		try {
			System.out.println("creditAccount :: " + subscriberId + "," + amount + "," + balanceChangeComments);
			System.out.println("expiration :: " + balanceCreditAccounts[0].getExpirationDate());
			return soapService.creditAccount(MobileNumberFormatter.format(subscriberId).substring(3), identity, balanceCreditAccounts, balanceChangeCode,
					balanceChangeComments);
		} catch (Exception ex) {
			String errorCode = XMLParser.getErrorCode(ex.getMessage());
			String errorMessage = XMLParser.getError(ex.getMessage());
			System.out.println("################# errorCode = " + errorCode + ", errorMessage = " + errorMessage);
			throw new TransactionFailedException(errorMessage);			
		} finally {
		}
	}

	private Map<String, String> getResponse(String subscriberId, String shortMessage) {
		Map<String, String> response = new HashMap<String, String>();
		try {
			response.put("subscriberId", MobileNumberFormatter.format(subscriberId));
		} catch (InvalidMobileNumberException e) {
			response.put("subscriberId", subscriberId);
		}
		response.put("shortMessage", shortMessage);
		return response;
	}
	
	public void credit(
			String sourceAccountId, 
			String sourceBalanceName, 
			BigDecimal creditAmount, 
			Date expirationDate,
			String narration)
			
		throws TransactionFailedException {

		String uuid = "" + (System.currentTimeMillis() + 1); 
				
		SubscriberRetrieve sourceAccountRetrieve = null;
		String sourceMsIsdn = null;
		
		try {
			
			try {
				sourceMsIsdn = MobileNumberFormatter.format(sourceAccountId).substring(3);
			} catch (InvalidMobileNumberException e) {
				e.printStackTrace();
				throw new TransactionFailedException("Source account is invalid");
			}
			System.out.println("###### sourceMsIsdn = " + sourceMsIsdn);
			sourceAccountRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(sourceMsIsdn, identity, 1);			
			if (
					!"TEL_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"PROMO_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"PROMO_2_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())
					&& !"promotion_cos".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())		
    			&& !"FF_COS".equals( sourceAccountRetrieve.getSubscriberData().getCOSName())){

				throw new TransactionFailedException("Subscriber is not on prepaid package");
			}
			
		} catch (RemoteException e) {
			System.out.println( "Error code = " + XMLParser.getErrorCode(e.getMessage()) + " #### " + XMLParser.getError(e.getMessage()) );
			throw new TransactionFailedException("Failed to retrieve subscriber : " + sourceAccountId + " - " + XMLParser.getError(e.getMessage()));
		} 

		BalanceEntity sourceBalanceEntity = null;
	
		/* Get current balance for this subscriber */
		for (BalanceEntity balanceEntity : sourceAccountRetrieve.getSubscriberData().getBalances()) {
			if (sourceBalanceName.equalsIgnoreCase(balanceEntity.getBalanceName())) {
				sourceBalanceEntity = balanceEntity;
				break;
			}
		}

		String creditBalanceChangeComments = "Reversal # " + uuid + " " + narration;
		
		/* Credit beneficiary account */		
		if ( expirationDate == null ) {
		
			if (sourceBalanceEntity != null){
				expirationDate = sourceBalanceEntity.getAccountExpiration()!= null ?
						sourceBalanceEntity.getAccountExpiration().getTime() : new Date();
			} else {
				expirationDate =  new Date();
				expirationDate.setYear(2013);
			}
		}
			
		System.out.println("########## AccountBalanceTransfer");
		System.out.println("########## Subscriber Id                  = " + sourceMsIsdn);
		System.out.println("########## creditAmount                   = " + creditAmount);
		System.out.println("########## expirationDate                 = " + expirationDate);
		System.out.println("########## Credit Balance Change Comments = " + creditBalanceChangeComments);
		
		System.out.println("\n\n########## Crediting ...");
		if (creditAccount(sourceMsIsdn, sourceBalanceName, creditAmount, expirationDate, "",	creditBalanceChangeComments)) {
			System.out.println("\n########## Credit Successful");
		} else {
			System.out.println("\n########## Credit Failed");
		}
	}
}

package com.ebridgecommerce.services;

import com.ebridgecommerce.dao.VasGatewayDao;
import com.ebridgecommerce.domain.*;
import com.zw.ebridge.domain.DataBundlePrice;
import com.zw.ebridge.vas.prototype.DataBundleManager;
import org.joda.time.DateTime;
import com.ebridgecommerce.prepaid.client.PPSAdapter;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBundleManagementService {

	private DataBundleManager dataBundleManager;
	private PPSAdapter ppsAdapter;
	
	private Map<String, USSDSession> sessions;
	
	private static final String voiceBalanceName = "Core";
//	private static final String dataBalanceName = "Gprs_usd";
	private static final String dataBalanceName = "Gprs_bundle";
	/* 1MB = 1 049 000 Octets*/
  private static final BigDecimal DATA_UNIT = new BigDecimal(1049000);
	private List<String> testLines = null;
	
	private Statement stmt;
	
	public DataBundleManagementService(){
		dataBundleManager = new DataBundleManager();
		ppsAdapter = new PPSAdapter();
		sessions = new HashMap<String, USSDSession>();
	
		testLines = VasGatewayDao.getTestLines();
		
		try {
			stmt = VasGatewayDao.getConnection().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// msg: 80 32990 177#

	public USSDResponse process( String uuid, String shortCode, String msisdn, String sessionId, String[] msg){
//		String errorMessage = "There is no services on this short code. Please try *144# ";
		return process( uuid, shortCode, msisdn, sessionId, msg, Boolean.FALSE);
	}
	
	public Boolean isTestLine(String uuid, String msisdn) {
		if (testLines.contains(msisdn)) {
			return Boolean.TRUE;
		} else {
			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, "This service will be available in few days time");
			return Boolean.FALSE;
		}

	}
	
	public USSDResponse process( String uuid, String shortCode, String msisdn, String sessionId, String[] msg, boolean isTest){
		try {
			if (isTest && !isTestLine(uuid, msisdn)) {
				String errorMessage = "This service will be available in few days time";
				VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, errorMessage, "096", null, shortCode);
				return getUSSDResponse(shortCode, msisdn, sessionId, errorMessage, Boolean.TRUE);
			}
			
			String ussdAnswer = null;
			if ( msg.length > 6) {
				ussdAnswer = msg[7];
			} else {
				return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.DATA_BUNDLE_PROMOTION), Boolean.FALSE);
			}
	
			USSDSession session = sessions.get( msisdn );
			
			if (session == null) {			
				/* Root menu item selected. */
				return processRootMenu(shortCode, msisdn, uuid, sessionId, ussdAnswer, isTest);
			}
			
			return processUSSDAnswer(shortCode, msisdn, uuid, sessionId, ussdAnswer, session, isTest);
		}  catch(Exception e ) {
			e.printStackTrace();
			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR_LOG, "096", null, shortCode);
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}
	}
	
//	public Boolean isTestLine(String uuid, String msisdn) {
//		if (testLines.contains(msisdn)) {
//			return Boolean.TRUE;
//		} else {
//			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, "This service will be available in few days time" );
//			return Boolean.FALSE;
//		}
//		
//	}

	private USSDResponse processUSSDAnswer(String shortCode, String msisdn, String uuid, String sessionId, String ussdAnswer, USSDSession session, boolean isTest) {

		try {
			/* Validate session. */
			if (!session.getSessionId().equals(sessionId)) {
				sessions.remove(msisdn);
				return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.DATA_BUNDLE_PROMOTION), Boolean.FALSE);
			}
			
			switch(session.getTxnType()){
			
			case DATA_BUNDLE_PURCHASE:
				
				Integer selection = null;
				try {
					selection = Integer.parseInt(ussdAnswer.trim());
				} catch(NumberFormatException e ){
					System.out.println("############# Error reading bundle id - " + e.getMessage());
					return getUSSDResponse(shortCode, msisdn, sessionId, Messages.INVALID_BUNDLE_ID_LOG, Boolean.TRUE);
				}
				
				if (!isValidDataBundleID(selection)) {
					VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.INVALID_BUNDLE_ID_LOG);
					return getUSSDResponse(shortCode, msisdn, sessionId, Messages.INVALID_BUNDLE_ID, Boolean.TRUE);
				}
				
				DataBundlePrice bundle = dataBundleManager.getPriceFor(selection, isTest);
				
				sessions.remove(msisdn);
				
				return purchaseDataBundle(shortCode, sessionId, uuid, msisdn, bundle.getBundlePrice(), bundle.getBundleSize(), bundle.getDataRate(), bundle.getDebit(), bundle.getCredit());
	
			default:
				VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.INVALID_SERVICE_COMMAND);
				return getUSSDResponse(shortCode, msisdn, sessionId,Messages.INVALID_SERVICE_COMMAND, Boolean.TRUE);
			}
		}  catch(Exception e ) {
			e.printStackTrace();
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}
	}

	protected boolean isValidDataBundleID(Integer id) {
		return (id >= 1) && (id <= 6);
	}

	public USSDResponse purchaseDataBundle(String shortCode, String sessionId, String uuid, String msisdn, BigDecimal price, BigDecimal bundleSize, BigDecimal dataRate1, BigDecimal debit, BigDecimal credit) {
		
		try {
			
//			BigDecimal dataRate = getDataRate();
//			dataRate = dataRate.setScale(2, BigDecimal.ROUND_HALF_UP);
//			
			price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
						
			Map<String, BalanceDTO> balances = ppsAdapter.dataBundlePurchase(uuid, msisdn, price, bundleSize, debit, credit);
			
			BigDecimal voiceBalance = balances.get(voiceBalanceName).getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal dataBalanceInOctets = balances.get(dataBalanceName).getAmount();
			
			BigDecimal dataBalance = null;
			if (dataBalanceInOctets != null){
				dataBalance = dataBalanceInOctets.multiply(new BigDecimal(10));
//				dataBalance = dataBalanceInOctets.divide(DATA_UNIT, 2, RoundingMode.HALF_UP);
			} else {				
				dataBalance = new BigDecimal(0);
			}
			
			dataBalance = dataBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
			
//			BigDecimal amount = getAmount(price, dataRate);
////			BigDecimal balanceAmount = getAmount(dataBalance, dataRate);
//			bundleSize = bundleSize.setScale(2, BigDecimal.ROUND_HALF_UP);

			Date expiryDate =  balances.get(dataBalanceName).getExpiryDate();

			String payload = "" + 
					"You have bought the " +
					bundleSize + 
					"mb bundle. Your bal = " +
					voiceBalance +
					"usd. Data bundle= " +
					dataBalance +
					"mb exp on " + getExpiryDate(expiryDate);
			
			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchase", price, "V=" + voiceBalance + "|D=" + dataBalance, "000", expiryDate, shortCode );	
			
			USSDResponse result = getUSSDResponse(shortCode, msisdn, sessionId, payload, Boolean.TRUE);
			result.setRawPayload(payload);
			
			return result;
			
		} catch (TransactionFailedException e) {
			
			USSDResponse result =  getUSSDResponse(shortCode, msisdn, sessionId, e.getMessage(), Boolean.TRUE);
			String msg = e.getMessage();
			result.setRawPayload(msg);
			if (msg != null && msg.toLowerCase().startsWith("subscriber account is ")) {
				msg = "Subscriber not active";
			}
			
			if (msg != null && msg.toLowerCase().startsWith("failed to retrieve subscriber")) {
				msg = "Subscriber retrieval failure";
			}
			
			if (msg != null && msg.toLowerCase().startsWith("insufficient credit for selected bundle")) {
				msg = "Insufficient credit for selected bundle";
			}
			
			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, msg);
			return result;
		} catch(Exception e ) {
			e.printStackTrace();
			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}
		
	}

	private String getExpiryDate(Date expiryDate) {
		return expiryDate != null ? String.format("%1$td/%1$tm/%1$tY", expiryDate) : null;
	}

//	private BigDecimal getAmount(BigDecimal price, BigDecimal dataRate) {
//		return price.divide(dataRate, 2, BigDecimal.ROUND_HALF_UP);
//	}

//	protected BigDecimal getDataRate(){
//		BigDecimal dataRate = new BigDecimal(0.1);
//		return dataRate;
//	}
	/**
	 * 
	 * @param shortCode
	 * @param msisdn
	 * @param uuid
	 * @param sessionId
	 * @param ussdAnswer
	 * @return
	 */
	protected USSDResponse processRootMenu(String shortCode, String msisdn, String uuid, String sessionId, String ussdAnswer, boolean isTest) {
		
		try {
			Integer selection = null;
			try {
				selection = Integer.parseInt(ussdAnswer);
			} catch(NumberFormatException e ){
				return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.DATA_BUNDLE_PROMOTION), Boolean.FALSE);
			}
			
			switch ( selection ){
			case 1:
				/* Balance inquiry. */
				return getUSSDResponse(shortCode, msisdn, sessionId, getBalance(uuid, msisdn), Boolean.TRUE);
			case 2:
				/* Data Bundle Purchase. */
				sessions.put(msisdn, new USSDSession(msisdn, sessionId, TxnType.DATA_BUNDLE_PURCHASE));
				return getUSSDResponse(shortCode, msisdn, sessionId, dataBundleManager.getPriceList(false, isTest), Boolean.FALSE );
			default:
				String selectionError = "Invalid selection.\n";
				selectionError += USSDMenus.getRootMenu(ServiceDTO.DATA_BUNDLE_PROMOTION);
				VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, "Invalid menu option selected");
				return getUSSDResponse(shortCode, msisdn, sessionId, selectionError, Boolean.FALSE);
			}
		}  catch(Exception e ) {
			e.printStackTrace();
			VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}
		
	}
	
	protected USSDResponse getUSSDResponse(String shortCode, String msisdn, String sessionId, String payload, Boolean closeSession){
		System.out.println("########### Creating a USSD Response.");
		USSDResponse response = new USSDResponse();
		response.setMobileNumber(msisdn);
		response.setShortCode(shortCode);
		response.setSessionId(sessionId);
		response.setPayload(
				closeSession ?
						"81" + " " + sessionId + " " + "0 " + payload + "." : 
							"72" + " " + sessionId + " " + 30000 + " 0 " + payload + ".");
		System.out.println("########### USSD Response = " + response.getPayload());
		return response;
	}
		
	protected String getBalance(String uuid, String msisdn){
		try {
			  System.out.println("########### DataBundleManagementService - getBalance for - " + msisdn);
		
				Map<String, BalanceDTO> balances = null;
				try {
					balances = ppsAdapter.getAccountBalance(uuid, msisdn);
				} catch(TransactionFailedException e) {
					return "Balance enquiry failed: " + e.getMessage();	
				}
			  System.out.println("########### DataBundleManagementService - Balanced recieved from IN");
		
				BigDecimal voiceBalance = balances.get(voiceBalanceName).getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		//		BigDecimal dataBalance = balances.get(dataBalanceName).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal dataBalance = null;
				BigDecimal dataInDollars = balances.get(dataBalanceName) != null ? balances.get(dataBalanceName).getAmount() : null;		
				if (dataInDollars != null) {
					dataBalance = dataInDollars.multiply(new BigDecimal(10));
					dataBalance = dataBalance.setScale(2, RoundingMode.HALF_UP);
				}
												
				VasGatewayDao.log(stmt, uuid, msisdn, "", "balanceEnquiry", null, "V=" + voiceBalance + "|D=" + dataBalance );
					
				String expiryDate = null;
				if (dataBalance == null){
					dataBalance = BigDecimal.ZERO;
				} else {
					expiryDate = getExpiryDate(balances.get(dataBalanceName).getExpiryDate());
				}
												
				String result = 
						"Your airtime bal = " + voiceBalance + 
				    "usd. " + 
			    	"GPRS= " + dataBalance + "mb exp on " + expiryDate;
				

				System.out.println("########### getBalance Result = " + result);
			return result;
		} catch(Exception e ) {
				e.printStackTrace();
				VasGatewayDao.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
				return Messages.SYSTEM_ERROR;
			}
		
	}
	
	public void listBalances(){
		Map<String, BigDecimal> balances = null;
		try {
			balances = ppsAdapter.getBalance("123466666", "263734822318");
			for(String name : balances.keySet()){
				System.out.println("263734822318 - ####### name = " + name + ", balance = " + balances.get(name));
			}
			balances = ppsAdapter.getBalance("123466664", "263734797511");
			for(String name : balances.keySet()){
				System.out.println("263734797511 - ####### name = " + name + ", balance = " + balances.get(name));
			}
			balances = ppsAdapter.getBalance("123466663", "263734797496");
			for(String name : balances.keySet()){
				System.out.println("263734797496 - ####### name = " + name + ", balance = " + balances.get(name));
			}
			balances = ppsAdapter.getBalance("123466662", "263735438172");
			for(String name : balances.keySet()){
				System.out.println("263735438172 - ####### name = " + name + ", balance = " + balances.get(name));
			}
			balances = ppsAdapter.getBalance("123466661", "263733435501");
			for(String name : balances.keySet()){
				System.out.println("263733435501 - ####### name = " + name + ", balance = " + balances.get(name));
			}
		} catch(TransactionFailedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		System.out.println("###########" + String.format("%1$td/%1$tm/%1$tY", new DateTime().plusDays(30).toDate()));
	}
}

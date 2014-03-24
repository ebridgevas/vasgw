package com.ebridgecommerce.services;

import com.ebridgecommerce.db.DBAdapter;
import com.ebridgecommerce.domain.TxnType;
import com.ebridgecommerce.prepaid.client.PPSClient;
import com.ebridgecommerce.smpp.pdu.DeliverSM;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceCommandProcessor {

	private static ServiceCommandProcessor instance;

	private PPSClient ppsClient;
	private final static BigDecimal MINIMUM_AIRTIME_TRANSER;
	private final static BigDecimal MAXIMUM_AIRTIME_TRANSER;
	private final static List<Integer> RECHARGE_PIN_LENGTHS;

	static {
		MINIMUM_AIRTIME_TRANSER = DBAdapter.getServiceParameterBigDecimal("MINIMUM_AIRTIME_TRANSER");
		MAXIMUM_AIRTIME_TRANSER = DBAdapter.getServiceParameterBigDecimal("MAXIMUM_AIRTIME_TRANSER");
		RECHARGE_PIN_LENGTHS = DBAdapter.getServiceParameterIntegerList("RECHARGE_PIN_LENGTH");
	}

	private final static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private final static SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH");
	
	protected ServiceCommandProcessor(PPSClient ppsClient, DBAdapter db) throws SQLException {
		this.ppsClient = ppsClient;
	}

	public static ServiceCommandProcessor getInstance(PPSClient ppsClient, DBAdapter db) throws SQLException {
		if (instance == null) {
			instance = new ServiceCommandProcessor(ppsClient, db);
		}
		return instance;
	}
	
	/* 15#0733435544 */
	/* 123456789012#0733435544*/
	public List<Map<String, String>> process(DeliverSM sm) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		List<Map<String, String>> responses = new ArrayList<Map<String, String>>();
		/* Hot service. */
		if (sm.getDestAddr().getAddress().equals("33500")) {
			List<Map<String, String>> result = null;
			if (sm.getShortMessage() != null) { // && (!"Stanbic".startsWith(sm.getShortMessage().trim()) || (!"stanbic".startsWith(sm.getShortMessage().trim()))) ) {
				String text = sm.getShortMessage().trim();
				text = text.replace(" ", "");
				if (!"Stanbic20".equalsIgnoreCase( text)) {
					Map<String,String> sms = new HashMap<String,String>();
					sms.put("subscriberId", sm.getSourceAddr().getAddress());
					sms.put("text", "Please send words, Stanbic 20 to 33500");
					result = new ArrayList<Map<String,String>>();
					result.add(sms);
					System.out.println("***********=============********************" + result);
					return result;
				}
			}
			try {
				System.out.println("Debiting " + sm.getSourceAddr().getAddress() + " ...");

				result = ppsClient.transfer("" + System.currentTimeMillis(), sm.getSourceAddr().getAddress(), "263731000050", new BigDecimal(0.25), true);
				log(sm.getSourceAddr().getAddress(), "263731000050", TxnType.BALANCE_TRANSFER, new BigDecimal(0.25), "CREATE_CDR");
				Map<String,String> sms = new HashMap<String,String>();
				sms.put("subscriberId", sm.getSourceAddr().getAddress());
				sms.put("text", "Thank you. The draw closes on Friday at 5pm.  Text as many times as u want to increase yr chances. SMS by tribe263.com mobile and online marketing");
				result = new ArrayList<Map<String,String>>();
				result.add(sms);
				System.out.println("*******************************" + result);
				return result;
			} catch (TransactionFailedException e) {
				if ("You have exceeded the maximum limit of airtime available for transfer. Please recharge with more airtime to proceed.".equals(e.getMessage())) {
					responses.add(getResponse(sm.getSourceAddr().getAddress(), e.getMessage()));
					return responses;				
				} else {

//				result = ppsClient.transfer("" + System.currentTimeMillis(), sm.getSourceAddr().getAddress(), "263731000050", new BigDecimal(0.25), true);
				log(sm.getSourceAddr().getAddress(), "263731000050", TxnType.BALANCE_TRANSFER, new BigDecimal(0.25), "CDR_ERR");
				Map<String,String> sms = new HashMap<String,String>();
				sms.put("subscriberId", sm.getSourceAddr().getAddress());
				sms.put("text", "Thank you. The draw closes on Thursday at 12pm.  Text as many times as u want to increase yr chances. SMS by tribe263.com mobile and online marketing");
				result = new ArrayList<Map<String,String>>();
				result.add(sms);
				System.out.println("*******************************" + result);
				return result;
			}
			}
		}
		String[] tokens = sm.getShortMessage().trim().split("#");
		if (tokens.length == 1) {
			if (RECHARGE_PIN_LENGTHS.contains(tokens[0].length())) {
				/* Pin re-charge */
				try {
					return ppsClient.voucherRecharge("" + System.currentTimeMillis(), sm.getSourceAddr().getAddress(), sm.getSourceAddr().getAddress(), tokens[0]);
				} catch (TransactionFailedException e) {
					responses.add(getResponse(sm.getSourceAddr().getAddress(), "Failed to re-charge " + sm.getSourceAddr().getAddress() + " : " + e.getMessage()));
					return responses;
				}
			} else {
				responses.add(getResponse(sm.getSourceAddr().getAddress(), tokens[1] + "Invalid re-charge voucher specified."));
				return responses;
			}
		} else if (tokens.length == 2) {
			
			/* Check if second parameter is a valid subscriber Id */
			try {
				MobileNumberFormatter.format(tokens[1]);
			} catch (InvalidMobileNumberException ex) {
				responses.add(getResponse(sm.getSourceAddr().getAddress(), tokens[1] + " is not a valid mobile number."));
				return responses;
			}
			
			/* Check if first parameter is a valid re-charge voucher. */
			if (RECHARGE_PIN_LENGTHS.contains(tokens[0].length())) {
				
				/* Re-charge another number using a pin.*/
				try {
					List<Map<String, String>> result = ppsClient.voucherRecharge("" + System.currentTimeMillis(), sm.getSourceAddr().getAddress(), tokens[1], tokens[0]);
					log(sm.getSourceAddr().getAddress(), tokens[1], TxnType.VOUCHER_RECHARGE, null, tokens[0]);
					return result;
				} catch (TransactionFailedException e) {
					responses.add(getResponse(sm.getSourceAddr().getAddress(), "Failed to re-charge " + tokens[1] + " : " + e.getMessage()));
					return responses;
				}
				
			} else {
				/* Check if first parameter is a valid amount for balance transfer.*/
				System.out.println(tokens[0]);
				if (MINIMUM_AIRTIME_TRANSER.compareTo(new BigDecimal(tokens[0])) > 0) {
					responses.add(getResponse(sm.getSourceAddr().getAddress(), "Airtime transfer amount can not be less than " + MINIMUM_AIRTIME_TRANSER + "."));
					return responses;					
				}

				if (MAXIMUM_AIRTIME_TRANSER.compareTo(new BigDecimal(tokens[0])) < 0) {
					responses.add(getResponse(sm.getSourceAddr().getAddress(), "Airtime transfer amount can not be more than " + MAXIMUM_AIRTIME_TRANSER + "."));
					return responses;					
				}
				
				try {
					List<Map<String, String>> result = ppsClient.transfer("" + System.currentTimeMillis(), sm.getSourceAddr().getAddress(), tokens[1], new BigDecimal(tokens[0]), false);
					log(sm.getSourceAddr().getAddress(), tokens[1], TxnType.BALANCE_TRANSFER, new BigDecimal(tokens[0]), null);
					return result;
				} catch (TransactionFailedException e) {
					responses.add(getResponse(sm.getSourceAddr().getAddress(), "Failed to transfer " + new BigDecimal(tokens[0]) + " to " + tokens[1] + " : " + e.getMessage()));
					return responses;
				}
			}
		} else {
			responses.add(getResponse(sm.getSourceAddr().getAddress(), "Invalid service command. Please type Help and send to 23350."));
			return responses;		
		}

	}

	private Map<String, String> getResponse(String subscriberId, String text) {
		Map<String, String> response = new HashMap<String, String>();
		try {
			response.put("subscriberId", MobileNumberFormatter.format(subscriberId));
		} catch (InvalidMobileNumberException e) {
			response.put("subscriberId", subscriberId);
		}
		response.put("text", text);
		return response;
	}

	private void log(String subscriberId, String beneficiaryId, TxnType txnType, BigDecimal amount, String narrative) {
		Date date = new Date();
		String sql = "INSERT INTO txns ( uuid, txn_date_time, txn_date, txn_hour, subscriber_id, beneficiary_id, txn_type, amount, narrative )";
		sql += " VALUES ('" + System.currentTimeMillis() + "','" + DATETIME_FORMAT.format(date) + "','" + DATE_FORMAT.format(date) + "','" + HOUR_FORMAT.format(date) + "','" + subscriberId + "','" + beneficiaryId + "','" + txnType + "'," + amount + ",'" + narrative + "')";
		System.out.println(sql);
		try {
			DBAdapter.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void dummy(String text) {
		if (text != null) { // && (!"Stanbic".startsWith(sm.getShortMessage().trim()) || (!"stanbic".startsWith(sm.getShortMessage().trim()))) ) {
//			String text = sm.getShortMessage().trim();
			text = text.replace(" ", "");
			if ("Stanbic20".equalsIgnoreCase( text)) {
//				Map<String,String> sms = new HashMap<String,String>();
//				sms.put("subscriberId", sm.getSourceAddr().getAddress());
//				sms.put("text", "Please send words, Stanbic 20 to 33500");

				System.out.println("Error***********=============********************");

			} else {
				System.out.println("ok");
			}
		}
	}
	public static void main(String[] args) {
		dummy("Stanbic20");
	}
}

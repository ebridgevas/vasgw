package com.ebridgecommerce.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ebridgecommerce.domain.Account;
import com.ebridgecommerce.domain.AccountType;
import com.ebridgecommerce.domain.Messages;
import com.ebridgecommerce.domain.ServiceDTO;
import com.ebridgecommerce.domain.TxnType;
import com.ebridgecommerce.domain.USSDMenus;
import com.ebridgecommerce.domain.USSDResponse;
import com.ebridgecommerce.domain.USSDSession;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.prepaid.client.PPSClient;
import com.zw.ebridge.vas.prototype.db.DBAdapter;
import zw.co.ebridge.jms.JMSWriter;

import javax.jms.JMSException;

public class UsagePromotionService {

	private JMSWriter jmsWriter;

	private PPSClient ppsClient;

	private Map<String, USSDSession> sessions;

	private static final String voiceBalanceName = "Core";
	// private static final String dataBalanceName = "Gprs_usd";
	private static final String dataBalanceName = "Gprs_bundle";
	/* 1MB = 1 049 000 Octets */
	private static final BigDecimal DATA_UNIT = new BigDecimal(1049000);
	private List<String> testLines = null;

	private Statement stmt;
  public static final Map<Short, String> ACCUMULATOR_COS;
  static {
  	ACCUMULATOR_COS = new HashMap<Short, String>();
  	ACCUMULATOR_COS.put(new Short("1"), "Band1");
  	ACCUMULATOR_COS.put(new Short("2"), "Band2");
  	ACCUMULATOR_COS.put(new Short("3"), "Band3");
  	ACCUMULATOR_COS.put(new Short("4"), "Band4");
  	ACCUMULATOR_COS.put(new Short("5"), "Band5");
  	ACCUMULATOR_COS.put(new Short("6"), "Band6");
  	ACCUMULATOR_COS.put(new Short("7"), "Band7");	 
  }
  
  public static final Map<Short, String> BONUS_COS; 
  static {
   	BONUS_COS = new HashMap<Short, String>();
   	BONUS_COS.put(new Short("1"), "discount1");
   	BONUS_COS.put(new Short("2"), "discount2");
   	BONUS_COS.put(new Short("3"), "discount3");
   	BONUS_COS.put(new Short("4"), "discount4");
   	BONUS_COS.put(new Short("5"), "discount5");
   	BONUS_COS.put(new Short("6"), "discount6");
   	BONUS_COS.put(new Short("7"), "discount7");
  }
  
	public UsagePromotionService() {

		ppsClient = PPSClient.getInstance();
		sessions = new HashMap<String, USSDSession>();

		testLines = DBAdapter.getTestLines();

		try {
			stmt = DBAdapter.getConnection().createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
            jmsWriter = new JMSWriter("smpp11-queue");
        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

	// msg: 80 32990 177#

	public USSDResponse process(String uuid, String shortCode, String msisdn, String sessionId, String[] msg, boolean bounceBack) {
		String errorMessage = "There is no services on this short code. Please try *144# ";
		return getUSSDResponse(shortCode, msisdn, sessionId, errorMessage, Boolean.TRUE);
	}

	public USSDResponse process(String uuid, String shortCode, String msisdn, String sessionId, String[] msg) {
		try {
			if (!isTestLine(uuid, msisdn)) {
				String errorMessage = "This service will be available in few days time";
                UsagePromotionDAO.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, errorMessage, "096", null, shortCode);
				return getUSSDResponse(shortCode, msisdn, sessionId, errorMessage, Boolean.TRUE);
			}

//			String ussdAnswer = null;
//			if (msg.length > 6) {
//				ussdAnswer = msg[7];
//			} else {
				
				/* Initial call. */
				
				/* --- If subscriber if not registered. */
				String currentCOS = retriveClassOfService(uuid, msisdn);
				
//				if ("promotion_cos".equalsIgnoreCase(currentCOS) || "FF_COS".equalsIgnoreCase(currentCOS)){
				if (ACCUMULATOR_COS.containsValue(currentCOS) || BONUS_COS.containsValue(currentCOS)){
					/* Opt Out. */					
					USSDResponse result = getUSSDResponse(shortCode, msisdn, sessionId, cancelSubscription(uuid, msisdn), Boolean.TRUE);
					DBAdapter.register(stmt, uuid, msisdn, false);
					return result;
					
				} else {	
					/* Opt In. */
					// return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.USAGE_PROMOTION), Boolean.FALSE);
					SubscriberInfo subscriberInfo = DBAdapter.getAccumulatorCosForSubscriber(stmt, uuid, msisdn);
					
					if (subscriberInfo == null) {
						return getUSSDResponse(shortCode, msisdn, sessionId, "You are not qualified for the promotion.", Boolean.TRUE);	
					}
					
					USSDResponse result = getUSSDResponse(shortCode, msisdn, sessionId, registerSubscriber(uuid, msisdn, subscriberInfo), Boolean.TRUE);
					DBAdapter.register(stmt, uuid, msisdn, true);
					return result;
				}					
//			}

//			USSDSession session = sessions.get(msisdn);

//			if (session == null) {
				/* Root menu item selected. */
//				return processRootMenu(shortCode, msisdn, uuid, sessionId, ussdAnswer);
//			}
//			return processUSSDAnswer(shortCode, msisdn, uuid, sessionId, ussdAnswer, session);
		} catch (Exception e) {
			e.printStackTrace();
			DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR_LOG, "096", null, shortCode);
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}
	}

	public Boolean isTestLine(String uuid, String msisdn) {
		if (testLines.contains(msisdn)) {
			return Boolean.TRUE;
		} else {
			DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, "This service will be available in few days time");
			return Boolean.FALSE;
		}

	}

	private USSDResponse processUSSDAnswer(String shortCode, String msisdn, String uuid, String sessionId, String ussdAnswer, USSDSession session) {

		try {
			/* Validate session. */
			if (!session.getSessionId().equals(sessionId)) {
				sessions.remove(msisdn);
				return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.USAGE_PROMOTION), Boolean.FALSE);
			}

			/* Redeem bonus. */

			String ffMsisdn = ussdAnswer.trim();
			
			switch (session.getTxnType()) {

			case REDEEM_USAGE_BONUS:
				String mtrId = retrieveMonetaryTransferRecord(msisdn);
				if (mtrId == null) {
					return getUSSDResponse(shortCode, msisdn, sessionId, "Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.", Boolean.TRUE);
				}
				
				if (DBAdapter.newMtrExists(stmt, mtrId, msisdn)) {
					return getUSSDResponse(shortCode, msisdn, sessionId, "Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.", Boolean.TRUE);
				}
				
				boolean isFFOnPrepaidPackage = false;
				try {
					isFFOnPrepaidPackage = addFriend(msisdn, ffMsisdn);
				} catch(TransactionFailedException tfe) {
					return getUSSDResponse(shortCode, msisdn, sessionId, tfe.getMessage(), Boolean.TRUE);
				}

				awardBonus(msisdn);
				DBAdapter.logMTR(stmt, mtrId, msisdn);
				
				
				AccountType accountType = isFFOnPrepaidPackage ? AccountType.PREPAID : AccountType.POSTPAID;
				/* Notify reciepient. */

				session.setTxnType(TxnType.CONFIRM_BONUS_COLLECTION);
				
				return getUSSDResponse(shortCode, msisdn, sessionId, "You have successfully chosen to call subscriber " +  MobileNumberFormatter.shortFormat(MobileNumberFormatter.format(ffMsisdn)) + " for free between 10pm and 4am. " + " Press 1 to confirm or press 2 to change number.", Boolean.FALSE);
				
			case CONFIRM_BONUS_COLLECTION:
				
				ffMsisdn = ussdAnswer.trim();
				
				if ("1".equals(ffMsisdn)) {
					Account friend = ppsClient.getFriendFor(msisdn);
					notifyReceipient(msisdn, friend.getAccountNumber(), friend.getAccountType());
					sessions.remove(msisdn);
					return getUSSDResponse(shortCode, msisdn, sessionId, "Transaction successful.", Boolean.TRUE);
				} else {

					String result = DBAdapter.isFriendChangePermitted(stmt, uuid, msisdn);
					
					if ("0".equals(result)) {
						sessions.put(msisdn, new USSDSession(msisdn, sessionId, TxnType.MODIFY_FRIEND));
						return getUSSDResponse(shortCode, msisdn, sessionId, Messages.MODIFY_FRIEND, Boolean.FALSE);
					} else {					
						return getUSSDResponse(shortCode, msisdn, sessionId, result, Boolean.TRUE);
					}
				}
				
			case CONFIRM_FRIEND:
				ffMsisdn = ussdAnswer.trim();

				if ("1".equals(ffMsisdn)) {
					sessions.remove(msisdn);
					Account friend = ppsClient.getFriendFor(msisdn);
					notifyReceipient(msisdn, friend.getAccountNumber(), friend.getAccountType());
					return getUSSDResponse(shortCode, msisdn, sessionId, "Transaction successful.", Boolean.TRUE);
				} else {
					String result = DBAdapter.isFriendChangePermitted(stmt, uuid, msisdn);
					
					if ("0".equals(result)) {
						sessions.put(msisdn, new USSDSession(msisdn, sessionId, TxnType.MODIFY_FRIEND));
						return getUSSDResponse(shortCode, msisdn, sessionId, Messages.MODIFY_FRIEND, Boolean.FALSE);
					} else {					
						return getUSSDResponse(shortCode, msisdn, sessionId, result, Boolean.TRUE);
					}					
				}
				
			case MODIFY_FRIEND:
				
				ffMsisdn = ussdAnswer.trim();

				try {
					addFriend(msisdn, ffMsisdn);
				} catch(TransactionFailedException tfe) {
					return getUSSDResponse(shortCode, msisdn, sessionId, tfe.getMessage(), Boolean.TRUE);
				}

				String msg = "You have successfully chosen to call subscriber ";
				msg += MobileNumberFormatter.shortFormat(MobileNumberFormatter.format(ffMsisdn));
				msg += " for free between 10pm and 4am. Press 1 to confirm or press 2 to change number.";
				
				session.setTxnType(TxnType.CONFIRM_FRIEND);
				
				return getUSSDResponse(shortCode, msisdn, sessionId, msg, Boolean.FALSE);

			default:
				DBAdapter.log(stmt, uuid, msisdn, "", "UsagePromotionError", null, Messages.INVALID_SERVICE_COMMAND);
				return getUSSDResponse(shortCode, msisdn, sessionId, Messages.INVALID_SERVICE_COMMAND, Boolean.TRUE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}
	}

	private void notifyReceipient(String msisdn, String ffMsisdn, AccountType accountType) {
		try {
			Map<String, String> msg = new HashMap<String, String>();
			msg.put("uuid", "" + System.currentTimeMillis());
			msg.put("destinationId", MobileNumberFormatter.format(ffMsisdn));
			msg.put("sourceId", "Telecel");
			msg.put("subscriberId", "Telecel");
			msg.put("shortMessage", "Mobile " + MobileNumberFormatter.format(msisdn) + " has chosen to call you for free between 10pm and 4am. " + (accountType == AccountType.PREPAID ? "Dial *175# to enter this promo & use $2.50 to call for free." : ""));
			jmsWriter.get("smpp11-queue").write(msg);
		} catch (InvalidMobileNumberException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String retrieveMonetaryTransferRecord(String msisdn) {
		try {
			return ppsClient.retrieveMonetaryTransferRecord(msisdn);
		} catch (TransactionFailedException e) {
			return null;
		}
	}

	private Boolean awardBonus(String msisdn) {
		try {

			/* Award. */
			try {
				ppsClient.setClassOfService(msisdn, "FF_COS");
				String currentCOS = ppsClient.getClassOfService(msisdn);
				if ("FF_COS".equalsIgnoreCase(currentCOS)) {
					return true;
				} else {
					return false;
				}
			} catch (TransactionFailedException e) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			DBAdapter.log(stmt, "" + System.currentTimeMillis(), msisdn, "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
			return false;
		}
	}

//	private Boolean saveFriend(String msisdn, String ffMsIsdn) throws TransactionFailedException {
//		ppsClient.setFriend(msisdn, ffMsIsdn);
//		DBAdapter.logFriendChange(stmt, "" + System.currentTimeMillis(), msisdn, ffMsIsdn);
//		return true;
//	}

	private Boolean addFriend(String msisdn, String ffMsIsdn) throws TransactionFailedException {
		ppsClient.setFriend(msisdn, ffMsIsdn);
		DBAdapter.logFriendChange(stmt, "" + System.currentTimeMillis(), msisdn, ffMsIsdn);
		return true;
	}
	
	protected boolean isValidDataBundleID(Integer id) {
		return (id >= 1) && (id <= 5);
	}

	public USSDResponse purchaseDataBundle(String shortCode, String sessionId, String uuid, String msisdn, BigDecimal price, BigDecimal bundleSize, BigDecimal dataRate1, BigDecimal debit, BigDecimal credit) {

		// try {
		//
		// // BigDecimal dataRate = getDataRate();
		// // dataRate = dataRate.setScale(2, BigDecimal.ROUND_HALF_UP);
		// //
		// price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
		//
		// // Map<String, BalanceDTO> balances = ppsAdapter.dataBundlePurchase(uuid,
		// msisdn, price, bundleSize, debit, credit);
		//
		// BigDecimal voiceBalance =
		// balances.get(voiceBalanceName).getAmount().setScale(2,
		// BigDecimal.ROUND_HALF_UP);
		// BigDecimal dataBalanceInOctets =
		// balances.get(dataBalanceName).getAmount();
		//
		// BigDecimal dataBalance = null;
		// if (dataBalanceInOctets != null){
		// dataBalance = dataBalanceInOctets.multiply(new BigDecimal(10));
		// // dataBalance = dataBalanceInOctets.divide(DATA_UNIT, 2,
		// RoundingMode.HALF_UP);
		// } else {
		// dataBalance = new BigDecimal(0);
		// }
		//
		// dataBalance = dataBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
		//
		// // BigDecimal amount = getAmount(price, dataRate);
		// //// BigDecimal balanceAmount = getAmount(dataBalance, dataRate);
		// // bundleSize = bundleSize.setScale(2, BigDecimal.ROUND_HALF_UP);
		//
		// Date expiryDate = balances.get(dataBalanceName).getExpiryDate();
		//
		// String payload = "" +
		// "You have bought the " +
		// bundleSize +
		// "mb bundle. Your bal = " +
		// voiceBalance +
		// "usd. Data bundle= " +
		// dataBalance +
		// "mb exp on " + getExpiryDate(expiryDate);
		//
		// DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchase", price, "V=" +
		// voiceBalance + "|D=" + dataBalance, "000", expiryDate, shortCode );
		//
		// USSDResponse result = getUSSDResponse(shortCode, msisdn, sessionId,
		// payload, Boolean.TRUE);
		// result.setRawPayload(payload);
		//
		// return result;
		//
		// } catch (TransactionFailedException e) {
		//
		// USSDResponse result = getUSSDResponse(shortCode, msisdn, sessionId,
		// e.getMessage(), Boolean.TRUE);
		// String msg = e.getMessage();
		// result.setRawPayload(msg);
		// if (msg != null &&
		// msg.toLowerCase().startsWith("subscriber account is ")) {
		// msg = "Subscriber not active";
		// }
		//
		// if (msg != null &&
		// msg.toLowerCase().startsWith("failed to retrieve subscriber")) {
		// msg = "Subscriber retrieval failure";
		// }
		//
		// if (msg != null &&
		// msg.toLowerCase().startsWith("insufficient credit for selected bundle"))
		// {
		// msg = "Insufficient credit for selected bundle";
		// }
		//
		// DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null,
		// msg);
		// return result;
		// } catch(Exception e ) {
		// e.printStackTrace();
		// DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null,
		// Messages.SYSTEM_ERROR);
		// return getUSSDResponse(shortCode, msisdn, sessionId,
		// Messages.SYSTEM_ERROR, Boolean.TRUE);
		// }
		return null;
	}

	private String getExpiryDate(Date expiryDate) {
		return expiryDate != null ? String.format("%1$td/%1$tm/%1$tY", expiryDate) : null;
	}

	// private BigDecimal getAmount(BigDecimal price, BigDecimal dataRate) {
	// return price.divide(dataRate, 2, BigDecimal.ROUND_HALF_UP);
	// }

	// protected BigDecimal getDataRate(){
	// BigDecimal dataRate = new BigDecimal(0.1);
	// return dataRate;
	// }
	/**
	 * 
	 * @param shortCode
	 * @param msisdn
	 * @param uuid
	 * @param sessionId
	 * @param ussdAnswer
	 * @return
	 */
	protected USSDResponse processRootMenu(String shortCode, String msisdn, String uuid, String sessionId, String ussdAnswer) {

		try {
			Integer selection = null;
			try {
				selection = Integer.parseInt(ussdAnswer);
			} catch (NumberFormatException e) {
				return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.USAGE_PROMOTION), Boolean.FALSE);
			}

			switch (selection) {
			case 1:
//				/* Opt In. */
//				return getUSSDResponse(shortCode, msisdn, sessionId, registerSubscriber(uuid, msisdn), Boolean.TRUE);

				
				/* Collect price. */
				
				/* Otherwise, award calls to existing friend. */
				String mtrId = retrieveMonetaryTransferRecord(msisdn);
				if (mtrId == null) {
					return getUSSDResponse(shortCode, msisdn, sessionId, "Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.", Boolean.TRUE);
				}

				if (DBAdapter.newMtrExists(stmt, mtrId, msisdn)) {
					return getUSSDResponse(shortCode, msisdn, sessionId, "Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.", Boolean.TRUE);
				}

				/* --- then prompt for friend's mobile. */
				Account friend = ppsClient.getFriendFor(msisdn);

				if (friend == null) {
					sessions.put(msisdn, new USSDSession(msisdn, sessionId, TxnType.REDEEM_USAGE_BONUS));
					return getUSSDResponse(shortCode, msisdn, sessionId, Messages.REDEEM_USAGE_BONUS, Boolean.FALSE);
				}
								
				awardBonus(msisdn);
				DBAdapter.logMTR(stmt, mtrId, msisdn);
				notifyReceipient(msisdn, friend.getAccountNumber(), friend.getAccountType());
				return getUSSDResponse(shortCode, msisdn, sessionId, "You have successfully chosen to call subscriber " + MobileNumberFormatter.shortFormat(MobileNumberFormatter.format(friend.getAccountNumber())) +  " for free between 10pm and 4am until " + getExpiryDate(new DateTime().plusDays(7).toDate()) + ". To change number dial *175#", Boolean.TRUE);
					
			case 2:
				/* Opt Out. */
				return getUSSDResponse(shortCode, msisdn, sessionId, cancelSubscription(uuid, msisdn), Boolean.TRUE);
				
//			case 3:
//				/* Can subscriber change friend? */
//				friend = ppsClient.getFriendFor(msisdn);
//
//				String result = DBAdapter.isFriendChangePermitted(stmt, uuid, msisdn);
//				
//				boolean hasActiveMtr = DBAdapter.mtrExists(stmt, "", msisdn);
//				
//				if (!hasActiveMtr) {
//					return getUSSDResponse(shortCode, msisdn, sessionId, "Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.", Boolean.TRUE);
//				}
//				
//				if (friend == null || "0".equals(result)) {
//					sessions.put(msisdn, new USSDSession(msisdn, sessionId, TxnType.MODIFY_FRIEND));
//					return getUSSDResponse(shortCode, msisdn, sessionId, Messages.MODIFY_FRIEND, Boolean.FALSE);
//				} else {					
//					return getUSSDResponse(shortCode, msisdn, sessionId, result, Boolean.TRUE);
//				}						
			default:
				DBAdapter.log(stmt, uuid, msisdn, "", "UsagePromotionError", null, Messages.INVALID_SERVICE_COMMAND);
				return getUSSDResponse(shortCode, msisdn, sessionId, Messages.INVALID_SERVICE_COMMAND, Boolean.FALSE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
			return getUSSDResponse(shortCode, msisdn, sessionId, Messages.SYSTEM_ERROR, Boolean.TRUE);
		}

	}
	
	protected String retriveClassOfService(String uuid, String msisdn) {		
		try {
			return ppsClient.getClassOfService(msisdn);
		} catch (TransactionFailedException e) {
			return "General error occured: " + e.getMessage();
		}		
	}

	protected String registerSubscriber(String uuid, String msisdn, SubscriberInfo subscriberInfo) {
		try {
			try {
				String currentCOS = ppsClient.getClassOfService(msisdn);
				if (subscriberInfo.getAccumulatorCos().equalsIgnoreCase(currentCOS)) {
					return Messages.ALREADY_REGISTERED;
				}
			} catch (TransactionFailedException e) {
				return e.getMessage();
			}

			/* Registration. */
			try {
				ppsClient.setClassOfService(msisdn, subscriberInfo.getAccumulatorCos());
				String currentCOS = ppsClient.getClassOfService(msisdn);
				if (subscriberInfo.getAccumulatorCos().equalsIgnoreCase(currentCOS)) {
					return "Thank you for opting into this promo.\nUse airtime of $" + subscriberInfo.getThreshHold().setScale(2, RoundingMode.HALF_DOWN) + " to qualify to make calls to any Telecel number for $" + subscriberInfo.getCharge().setScale(3, RoundingMode.HALF_DOWN)+ "/min between 10pm and 4am starting today.";
				} else {
					return "Promotion Service registration failed";
				}
			} catch (TransactionFailedException e) {
				return e.getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			DBAdapter.log(stmt, uuid, msisdn, "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
			return Messages.SYSTEM_ERROR;
		}
	}

	protected String cancelSubscription(String uuid, String msisdn) {
		try {
			try {
				String currentCOS = ppsClient.getClassOfService(msisdn);
//				if ("FF_COS".equalsIgnoreCase(currentCOS)) {
//					return Messages.BONUS_ACTIVE;
//				}
				if ("TEL_COS".equalsIgnoreCase(currentCOS)) {
					return Messages.NOT_REGISTERED;
				}
			} catch (TransactionFailedException e) {
				return "Valentine Promotion Service registration failed: " + e.getMessage();
			}

			/* Cancel Registration. */
			try {
				ppsClient.setClassOfService(msisdn, "TEL_COS");
				String currentCOS = ppsClient.getClassOfService(msisdn);
				if ("TEL_COS".equalsIgnoreCase(currentCOS)) {
					return Messages.USAGE_PROMOTION_REGISTRATION_CANCELLED;
				} else {
					return "Night Promotion Service cancellation failed";
				}
			} catch (TransactionFailedException e) {
				return "Night Promotion Service cancellation failed: " + e.getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
			DBAdapter.log(stmt, uuid, msisdn, "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
			return Messages.SYSTEM_ERROR;
		}
	}

	protected USSDResponse getUSSDResponse(String shortCode, String msisdn, String sessionId, String payload, Boolean closeSession) {
		System.out.println("########### Creating a USSD Response.");
		USSDResponse response = new USSDResponse();
		response.setMobileNumber(msisdn);
		response.setShortCode(shortCode);
		response.setSessionId(sessionId);
		response.setPayload(closeSession ? "81" + " " + sessionId + " " + "0 " + payload + "." : "72" + " " + sessionId + " " + 30000 + " 0 " + payload + ".");
		System.out.println("########### USSD Response = " + response.getPayload());
		return response;
	}

	protected String getBalance(String uuid, String msisdn) {
		// try {
		// System.out.println("########### DataBundleManagementService - getBalance for - "
		// + msisdn);
		//
		// Map<String, BalanceDTO> balances = null;
		// try {
		// balances = ppsAdapter.getAccountBalance(uuid, msisdn);
		// } catch(TransactionFailedException e) {
		// return "Balance enquiry failed: " + e.getMessage();
		// }
		// System.out.println("########### DataBundleManagementService - Balanced recieved from IN");
		//
		// BigDecimal voiceBalance =
		// balances.get(voiceBalanceName).getAmount().setScale(2,
		// BigDecimal.ROUND_HALF_UP);
		// // BigDecimal dataBalance = balances.get(dataBalanceName).setScale(2,
		// BigDecimal.ROUND_HALF_UP);
		// BigDecimal dataBalance = null;
		// BigDecimal dataInDollars = balances.get(dataBalanceName) != null ?
		// balances.get(dataBalanceName).getAmount() : null;
		// if (dataInDollars != null) {
		// dataBalance = dataInDollars.multiply(new BigDecimal(10));
		// dataBalance = dataBalance.setScale(2, RoundingMode.HALF_UP);
		// }
		//
		// DBAdapter.log(stmt, uuid, msisdn, "", "balanceEnquiry", null, "V=" +
		// voiceBalance + "|D=" + dataBalance );
		//
		// // BigDecimal dataRate = getDataRate();
		// // dataRate = dataRate.setScale(2, BigDecimal.ROUND_HALF_UP);
		// String expiryDate = null;
		// if (dataBalance == null){
		// dataBalance = BigDecimal.ZERO;
		// } else {
		// expiryDate =
		// getExpiryDate(balances.get(dataBalanceName).getExpiryDate());
		// }
		//
		// String result =
		// "Your airtime bal = " + voiceBalance +
		// "usd. " +
		// "GPRS= " + dataBalance + "mb exp on " + expiryDate;
		//
		// // if (expiryDate != null) {
		// // result += "exp on " + expiryDate;
		// // }
		//
		// System.out.println("########### getBalance Result = " + result);
		// return result;
		// } catch(Exception e ) {
		// e.printStackTrace();
		// DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null,
		// Messages.SYSTEM_ERROR);
		// return Messages.SYSTEM_ERROR;
		// }
		return null;
	}

	public void listBalances() {
		// Map<String, BigDecimal> balances = null;
		// try {
		// balances = ppsAdapter.getBalance("123466666", "263734822318");
		// for(String name : balances.keySet()){
		// System.out.println("263734822318 - ####### name = " + name +
		// ", balance = " + balances.get(name));
		// }
		// balances = ppsAdapter.getBalance("123466664", "263734797511");
		// for(String name : balances.keySet()){
		// System.out.println("263734797511 - ####### name = " + name +
		// ", balance = " + balances.get(name));
		// }
		// balances = ppsAdapter.getBalance("123466663", "263734797496");
		// for(String name : balances.keySet()){
		// System.out.println("263734797496 - ####### name = " + name +
		// ", balance = " + balances.get(name));
		// }
		// balances = ppsAdapter.getBalance("123466662", "263735438172");
		// for(String name : balances.keySet()){
		// System.out.println("263735438172 - ####### name = " + name +
		// ", balance = " + balances.get(name));
		// }
		// balances = ppsAdapter.getBalance("123466661", "263733435501");
		// for(String name : balances.keySet()){
		// System.out.println("263733435501 - ####### name = " + name +
		// ", balance = " + balances.get(name));
		// }
		// } catch(TransactionFailedException e) {
		// e.printStackTrace();
		// }

	}

	public static void main(String[] args) {
		// DataBundleManagementService service = new DataBundleManagementService();
		// service.listBalances();
		System.out.println("###########" + String.format("%1$td/%1$tm/%1$tY", new DateTime().plusDays(30).toDate()));
	}
}

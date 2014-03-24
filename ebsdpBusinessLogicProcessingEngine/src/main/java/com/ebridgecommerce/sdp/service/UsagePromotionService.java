package com.ebridgecommerce.sdp.service;

import com.comverse_in.prepaid.ccws.MonetaryTransactionRecord;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.sdp.dao.SimRegistrationDAO;
import com.ebridgecommerce.sdp.dao.UsagePromotionDAO;
import com.ebridgecommerce.sdp.domain.TxnType;
import com.ebridgecommerce.sdp.domain.USSDResponse;
import com.ebridgecommerce.sdp.domain.USSDSession;
import com.ebridgecommerce.sdp.dto.Request;
import com.ebridgecommerce.sdp.dto.Response;
import com.ebridgecommerce.sdp.pps.client.PPSClient;
import com.zw.ebridge.domain.SubscriberInfo;
import org.joda.time.DateTime;
import zw.co.ebridge.domain.Messages;
import zw.co.ebridge.domain.USSDMenus;
import zw.co.ebridge.jms.JMSWriter;

import zw.co.ebridge.shared.dto.Account;
import zw.co.ebridge.shared.dto.AccountType;
import zw.co.ebridge.shared.dto.ServiceDTO;
import zw.co.ebridge.util.InvalidMobileNumberException;
import zw.co.ebridge.util.MobileNumberFormatter;
import zw.co.ebridge.util.TransactionFailedException;

import javax.jms.JMSException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsagePromotionService implements ServiceCommandProcessor {

	private JMSWriter jmsWriter;
	private PPSClient ppsClient;
	private Map<String, USSDSession> sessions;

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

		ppsClient = PPSClient.getInstance("zte");
		sessions = new HashMap<String, USSDSession>();

		testLines = UsagePromotionDAO.getTestLines();

		try {
			stmt = UsagePromotionDAO.getConnection().createStatement();

            try {
                jmsWriter = new JMSWriter("smpp11-queue");
            } catch (JMSException e) {
                e.printStackTrace();
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// msg: 80 32990 177#

//	public USSDResponse process(String uuid, String shortCode, String msisdn, String sessionId, String[] msg, boolean bounceBack) {
//		String errorMessage = "There is no services on this short code. Please try *144# ";
//		return getUSSDResponse(shortCode, msisdn, sessionId, errorMessage, Boolean.TRUE);
//	}

    public Response process(Request request, Response response, Boolean testMode) throws com.ebridgecommerce.sdp.util.TransactionFailedException { // String uuid, String shortCode, String msisdn, String sessionId, String[] msg) {

        /* Sensible defaults. */
        response.setTerminate(Boolean.TRUE);
        String uuid = "" + System.currentTimeMillis();

        System.out.println("### Night Promo - payload = " + request.getPayload());
        if (request.getPayload() == null) {
            response.setPayload(com.ebridgecommerce.sdp.domain.Messages.NIGHT_PROMO_MAIN_MENU );
        } else {
            Integer answer = null;
            try {
                answer = Integer.parseInt(request.getPayload());
            } catch(Exception e) {
                response.setPayload("Invalid selection:\n" + com.ebridgecommerce.sdp.domain.Messages.NIGHT_PROMO_MAIN_MENU );
                return response;
            }

            System.out.println("### Night Promo - answer = " + answer);

            switch( answer ) {

                case 1:
                    /* Opt In. */
                    if ( ! UsagePromotionDAO.isRegistered(stmt, request.getMsIsdn()) ) {
                        SubscriberInfo subscriberInfo = UsagePromotionDAO.getAccumulatorCosForSubscriber(stmt, uuid, request.getMsIsdn());

                        if (subscriberInfo == null) {
                            response.setPayload("You are not qualified for the promotion.");
                            response.setTerminate(Boolean.TRUE);
                        } else {
                            response.setPayload(registerSubscriber(uuid, request.getMsIsdn(), subscriberInfo));
                            response.setTerminate(Boolean.TRUE);
                            UsagePromotionDAO.register(stmt, uuid, request.getMsIsdn(), true);
                        }
                    } else {
                        response.setPayload("You are already registered for the promotion.");
                    }

                    break;

                case 2:
                    /* Opt Out. */
                    response.setPayload( cancelSubscription(uuid, request.getMsIsdn()));
                    response.setTerminate(Boolean.TRUE);
                    UsagePromotionDAO.register(stmt, uuid, request.getMsIsdn(), false);

                    break;

                case 3:
                    SubscriberInfo subscriberInfo = UsagePromotionDAO.getAccumulatorCosForSubscriber(stmt, uuid, request.getMsIsdn());
                    response.setPayload( ppsClient.collectBonus(request.getMsIsdn(), Long.parseLong(uuid), subscriberInfo.getThreshHold() ) );

                    break;

                case 4:
                    response.setPayload("Thank you for using the Night Promotion Service");
                    break;
            }
        }

        return response;
    }

    @Override
	public Response process(Request request, Response response) throws com.ebridgecommerce.sdp.util.TransactionFailedException { // String uuid, String shortCode, String msisdn, String sessionId, String[] msg) {

        /* Sensible defaults. */
        response.setTerminate(Boolean.FALSE);

        String uuid = "" + System.currentTimeMillis();
		try {
			if (isTestLine(uuid, request.getMsIsdn())) {
                return process(request, response, Boolean.TRUE);
//              response.setPayload("This service will be available in few days time");
//              UsagePromotionDAO.log(stmt, uuid,  request.getMsIsdn(), "", "DataBundlePurchaseError", null, "This service will be available in few days time", "096", null, "175");
//              response.setTerminate(Boolean.TRUE);
//				return response;
			}

//			String ussdAnswer = null;
//			if (msg.length > 6) {
//				ussdAnswer = msg[7];
//			} else {

				/* Initial call. */

				/* --- If subscriber if not registered. */
				String currentCOS = retriveClassOfService(uuid, request.getMsIsdn());

//				if ("promotion_cos".equalsIgnoreCase(currentCOS) || "FF_COS".equalsIgnoreCase(currentCOS)){
				if (ACCUMULATOR_COS.containsValue(currentCOS) || BONUS_COS.containsValue(currentCOS)){
					/* Opt Out. */
                    response.setPayload( cancelSubscription(uuid, request.getMsIsdn()));
                    response.setTerminate(Boolean.TRUE);
                    UsagePromotionDAO.register(stmt, uuid, request.getMsIsdn(), false);
					return response;

				} else {
					/* Opt In. */
					// return getUSSDResponse(shortCode, msisdn, sessionId, USSDMenus.getRootMenu(ServiceDTO.USAGE_PROMOTION), Boolean.FALSE);
					SubscriberInfo subscriberInfo = UsagePromotionDAO.getAccumulatorCosForSubscriber(stmt, uuid, request.getMsIsdn());

					if (subscriberInfo == null) {
                        response.setPayload("You are not qualified for the promotion.");
                        response.setTerminate(Boolean.TRUE);
                        return response;
					}

                    response.setPayload(registerSubscriber(uuid, request.getMsIsdn(), subscriberInfo));
                    response.setTerminate(Boolean.TRUE);
                    UsagePromotionDAO.register(stmt, uuid, request.getMsIsdn(), true);
					return response;
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
            UsagePromotionDAO.log(stmt, uuid, request.getMsIsdn(), "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR_LOG, "096", null, "175");
            response.setPayload( Messages.SYSTEM_ERROR);
            response.setTerminate(Boolean.TRUE);
            return response;
		}
	}

	public Boolean isTestLine(String uuid, String msisdn) {
		if (testLines.contains(msisdn)) {
			return Boolean.TRUE;
		} else {
            UsagePromotionDAO.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, "This service will be available in few days time");
			return Boolean.FALSE;
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
			jmsWriter.write(msg);
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
			UsagePromotionDAO.log(stmt, "" + System.currentTimeMillis(), msisdn, "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
			return false;
		}
	}

	private Boolean addFriend(String msisdn, String ffMsIsdn) throws TransactionFailedException {
		ppsClient.setFriend(msisdn, ffMsIsdn);
        UsagePromotionDAO.logFriendChange(stmt, "" + System.currentTimeMillis(), msisdn, ffMsIsdn);
		return true;
	}

	protected boolean isValidDataBundleID(Integer id) {
		return (id >= 1) && (id <= 5);
	}


	private String getExpiryDate(Date expiryDate) {
		return expiryDate != null ? String.format("%1$td/%1$tm/%1$tY", expiryDate) : null;
	}

	protected Response processRootMenu(Request request, Response response) { //String shortCode, String msisdn, String uuid, String sessionId, String ussdAnswer) {

        /* Sensible defaults. */
        response.setTerminate(Boolean.FALSE);

		try {
			Integer selection = null;
			try {
				selection = Integer.parseInt(request.getPayload());
			} catch (NumberFormatException e) {
                response.setPayload(USSDMenus.getRootMenu(ServiceDTO.USAGE_PROMOTION));
				return response;
			}

			switch (selection) {
			case 1:
//				/* Opt In. */
//				return getUSSDResponse(shortCode, msisdn, sessionId, registerSubscriber(uuid, msisdn), Boolean.TRUE);


				/* Collect price. */

				/* Otherwise, award calls to existing friend. */
				String mtrId = retrieveMonetaryTransferRecord(request.getMsIsdn());
				if (mtrId == null) {
                    response.setPayload("Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.");
                    response.setTerminate(Boolean.TRUE);
					return response;
				}

				if (UsagePromotionDAO.newMtrExists(stmt, mtrId, request.getMsIsdn())) {
                    response.setPayload("Get a usage of at least $2.50 to qualify to make free calls to a chosen Telecel no. for 7 days between 10pm and 4am.");
                    response.setTerminate(Boolean.TRUE);
                    return response;
				}

				/* --- then prompt for friend's mobile. */
				Account friend = ppsClient.getFriendFor(request.getMsIsdn());

				if (friend == null) {
                    response.setPayload(Messages.REDEEM_USAGE_BONUS);
                    sessions.put(request.getMsIsdn(), new USSDSession(request.getMsIsdn(), request.getSessionId(), TxnType.REDEEM_USAGE_BONUS));
                    return response;
				}

				awardBonus(request.getMsIsdn());
                UsagePromotionDAO.logMTR(stmt, mtrId, request.getMsIsdn());
				notifyReceipient(request.getMsIsdn(), friend.getAccountNumber(), friend.getAccountType());
                response.setPayload("You have successfully chosen to call subscriber " + MobileNumberFormatter.shortFormat(MobileNumberFormatter.format(friend.getAccountNumber())) +  " for free between 10pm and 4am until " + getExpiryDate(new DateTime().plusDays(7).toDate()) + ". To change number dial *175#");
				response.setTerminate(Boolean.TRUE);
                return response;
			case 2:
				/* Opt Out. */
                response.setPayload(cancelSubscription("" + System.currentTimeMillis(), request.getMsIsdn()));
                response.setTerminate(Boolean.TRUE);
				return response;

//			case 3:
//				/* Can subscriber change friend? */
//				friend = ppsClient.getFriendFor(msisdn);
//
//				String result = UsagePromotionDAO.isFriendChangePermitted(stmt, uuid, msisdn);
//
//				boolean hasActiveMtr = UsagePromotionDAO.mtrExists(stmt, "", msisdn);
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
                response.setPayload( Messages.INVALID_SERVICE_COMMAND);
                UsagePromotionDAO.log(stmt, "" + System.currentTimeMillis(), request.getMsIsdn(), "", "UsagePromotionError", null, Messages.INVALID_SERVICE_COMMAND);
                return response;
			}
		} catch (Exception e) {
            e.printStackTrace();
            response.setPayload(Messages.SYSTEM_ERROR);
            UsagePromotionDAO.log(stmt, "" + System.currentTimeMillis(), request.getMsIsdn(), "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
            return response;
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
//			try {
//				String currentCOS = ppsClient.getClassOfService(msisdn);
//				if (subscriberInfo.getAccumulatorCos().equalsIgnoreCase(currentCOS)) {
//					return Messages.ALREADY_REGISTERED;
//				}
//			} catch (TransactionFailedException e) {
//				return e.getMessage();
//			}

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
			UsagePromotionDAO.log(stmt, uuid, msisdn, "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
			return Messages.SYSTEM_ERROR;
		}
	}

	protected String cancelSubscription(String uuid, String msisdn) {
		try {
//			try {
//				String currentCOS = ppsClient.getClassOfService(msisdn);
////				if ("FF_COS".equalsIgnoreCase(currentCOS)) {
////					return Messages.BONUS_ACTIVE;
////				}
//				if ("TEL_COS".equalsIgnoreCase(currentCOS)) {
//					return Messages.NOT_REGISTERED;
//				}
//			} catch (TransactionFailedException e) {
//				return "Valentine Promotion Service registration failed: " + e.getMessage();
//			}

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
			UsagePromotionDAO.log(stmt, uuid, msisdn, "", "UsagePromotionError", null, Messages.SYSTEM_ERROR);
			return Messages.SYSTEM_ERROR;
		}
	}

    protected String collectBonus(String uuid, String msisdn) {
//        try {
//
//            SubscriberRetrieve subscriberRetrieve
//                    = soapService.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities(
//                    subscriber, identity, 128, fromCal, toCal, true);
//            MonetaryTransactionRecord[] result = subscriberRetrieve.getMonetaryTransactionsRecords();
//
//            if ( isBonusAwarded(result) ) {
//                changeCOSRequest.setSubscriberId( subscriber );
//                soapService.changeCOS(changeCOSRequest);
//                System.out.println("#Subscriber#" + subscriber + "#discount1#" + new Date() + "#DONE");
//            }
//        } catch (Exception e) {
//            System.out.println("#Subscriber#" + subscriber + "#discount1#" + new Date() + "#FAILED#" + e.getMessage());
//        }
        return null;
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
		// UsagePromotionDAO.log(stmt, uuid, msisdn, "", "balanceEnquiry", null, "V=" +
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
		// UsagePromotionDAO.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null,
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
//		System.out.println("###########" + String.format("%1$td/%1$tm/%1$tY", new DateTime().plusDays(30).toDate()));
	}
}

package com.ebridgecommerce.esme;

import com.ebridgecommerce.dao.VasGatewayDao;
import com.ebridgecommerce.domain.USSDResponse;
import com.ebridgecommerce.domain.VASServiceProvider;

import com.ebridgecommerce.services.DataBundleManagementService;
import com.ebridgecommerce.services.UsagePromotionService;
import com.ebridgecommerce.domain.ClassOfServiceDTO;
import com.ebridgecommerce.smpp.pdu.Address;
import com.ebridgecommerce.smpp.pdu.DeliverSM;
import com.ebridgecommerce.smpp.pdu.WrongLengthOfStringException;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.Messages;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.zw.ebridge.domain.DataBundlePrice;
import com.zw.ebridge.vas.prototype.DataBundleManager;
import zw.co.ebridge.jms.JMSWriter;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class MosmProcessor {

	private DataBundleManagementService bundleManagementService;
	private DataBundleManager dataBundleManager;
	private String carrierType;

	private static final Set<String> internalShortCodes = new TreeSet<String>();

	static {
		internalShortCodes.add("350");
		internalShortCodes.add("175");
	}

//	private static Map<String, VASServiceProvider> serviceProviders;
//
//	static {
//		try {
//			serviceProviders = VasGatewayDao.getServiceProviders();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//		}
//	}
	private final static BigDecimal MINIMUM_AIRTIME_TRANSER;
	private final static BigDecimal MAXIMUM_AIRTIME_TRANSER;
	private final static List<Integer> RECHARGE_PIN_LENGTHS;

	static {
		MINIMUM_AIRTIME_TRANSER = VasGatewayDao.getServiceParameterBigDecimal("MINIMUM_AIRTIME_TRANSER");
		MAXIMUM_AIRTIME_TRANSER = VasGatewayDao.getServiceParameterBigDecimal("MAXIMUM_AIRTIME_TRANSER");
		RECHARGE_PIN_LENGTHS = VasGatewayDao.getServiceParameterIntegerList("RECHARGE_PIN_LENGTH");
	}

	private Map<String, JMSWriter> jmsWriters;
	private String smscId;

	private Statement stmt;
	
	private DataBundleManagementService dataBundleManagementService;
	private UsagePromotionService usagePromotionService;
	
	public MosmProcessor(String carrierType, String smscId) {

		dataBundleManagementService = new DataBundleManagementService();
        dataBundleManager = new DataBundleManager();

        this.carrierType = carrierType;
		this.smscId = smscId;

        try {
            jmsWriters = new HashMap<String, JMSWriter>();
            // jmsWriters.put("ccws-queue", new JMSWriter("ccws-queue"));
            jmsWriters.put("smpp11-queue", new JMSWriter("smpp11-queue"));
            jmsWriters.put("ussd-23-queue", new JMSWriter("ussd-23-queue"));
            jmsWriters.put("ussd-25-queue", new JMSWriter("ussd-25-queue"));
            // jmsWriters.put("vasgw-ebridge-smsc-queue", new JMSWriter("vasgw-ebridge-smsc-queue"));
        } catch (Exception e) {
            System.out.print("######## FATAL ERROR - FAILED TO CONNECT TO ACTIVE MQ :: " + e.getMessage());
        }

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

		bundleManagementService = new DataBundleManagementService();
		usagePromotionService = new UsagePromotionService();
	}

	public int process(DeliverSM sm) {

		Map<String, String> msg = new HashMap<String, String>();
		msg.put("uuid", "" + System.currentTimeMillis());
		msg.put("destinationId", sm.getDestAddr().getAddress());
		msg.put("shortCode", sm.getDestAddr().getAddress());
		msg.put("subscriberId", sm.getSourceAddr().getAddress());
		msg.put("messageType", carrierType);
		msg.put("shortMessage", sm.getShortMessage());
		msg.put("smscId", smscId);

		if ((sm.getShortMessage() == null) || (sm.getShortMessage().trim().length() == 0) || (sm.getShortMessage().trim().startsWith("bal")) ){
			msg.put("sourceId", msg.get("shortCode"));
			msg.put("destinationId", msg.get("subscriberId"));		
			msg.put("shortMessage", "Invalid service command. Please type Help and send to 23350.");
			jmsWriters.get("smpp11-queue").write(msg);
			return 0;
		}
		
		String destinationId = sm.getDestAddr().getAddress();
		switch (Integer.parseInt(destinationId)) {
		case 140:
			processUSSDRequest(msg, destinationId);
			break;
		case 144:
			processUSSDRequest(msg, destinationId);
			break;
		case 177:
		case 175:
			processUSSDRequest(msg, destinationId);
			break;
		case 23350:
		case 33350:
		case 33073:
			return processInternal(msg);
		default:
//			processExternal(msg, destinationId);
            msg.put("shortMessage", "There is no service on short code " + destinationId );
            msg.put("sourceId", msg.get("destinationId"));
            msg.put("destinationId", msg.get("subscriberId"));
			jmsWriters.get("smpp11-queue").write(msg);
		}
		return 0;
	}

	private void processUSSDRequest(Map<String, String> msg, String destinationId) {
		
		System.out.println("Processing USSD Request for shortCode " + destinationId);
		String text = msg.get("shortMessage");
		String[] tokens = text.split(" ");
		String msisdn = msg.get("subscriberId");
		Integer sessionId = Integer.parseInt(tokens [ 1 ]);
		String shortCode = msg.get("shortCode");
		
		String uuid = "" + (System.currentTimeMillis() + 1);
		
		USSDResponse response = null;
		if ("175".equals(destinationId)) {
			response = dataBundleManagementService.process(uuid, shortCode, msisdn, sessionId.toString(), tokens, true);
//			response = usagePromotionService.process(uuid, shortCode, msisdn, sessionId.toString(), tokens);
//		} if ("177".equals(destinationId)) {
//			response = dataBundleManagementService.process(uuid, shortCode, msisdn, sessionId.toString(), tokens, true);
		} else {
			response = dataBundleManagementService.process(uuid, shortCode, msisdn, sessionId.toString(), tokens);
		}
		System.out.println("############ Processing USSD Response ..."); 
		msg.put("sourceId", msg.get("destinationId"));
		msg.put("destinationId", msg.get("subscriberId"));
		msg.put("shortMessage", response.getPayload() );
		System.out.println("############ Sending USSD Response"); 
		System.out.println("############ sourceId      = " + msg.get("sourceId"));
		System.out.println("############ destinationId = " + msg.get("destinationId"));
		System.out.println("############ shortMessage  = " + msg.get("shortMessage"));
		
		if( "196.2.77.23".equals(SMPPTransciever.smppIPAdress) ){ 
			jmsWriters.get("ussd-23-queue").write(msg);
		} else if( "196.2.77.25".equals(SMPPTransciever.smppIPAdress) ){ 
			jmsWriters.get("ussd-25-queue").write(msg);
		}
		System.out.println("############ Sent"); 
	}

	private void bounceBackUSSDRequest(Map<String, String> msg, String destinationId) {
		
		System.out.println("Processing USSD Request for shortCode " + destinationId);
		String text = msg.get("shortMessage");
		String[] tokens = text.split(" ");
		String msisdn = msg.get("subscriberId");
		Integer sessionId = Integer.parseInt(tokens [ 1 ]);
		String shortCode = msg.get("shortCode");
		
		String uuid = "" + (System.currentTimeMillis() + 1);
		
		USSDResponse response = dataBundleManagementService.process(uuid, shortCode, msisdn, sessionId.toString(), tokens, true);
	
		System.out.println("############ Processing USSD Response ..."); 
		msg.put("sourceId", msg.get("destinationId"));
		msg.put("destinationId", msg.get("subscriberId"));
		msg.put("shortMessage", response.getPayload() );
		System.out.println("############ Sending USSD Response"); 
		System.out.println("############ sourceId      = " + msg.get("sourceId"));
		System.out.println("############ destinationId = " + msg.get("destinationId"));
		System.out.println("############ shortMessage  = " + msg.get("shortMessage"));
		
		if( "196.2.77.23".equals(SMPPTransciever.smppIPAdress) ){ 
			jmsWriters.get("ussd-23-queue").write(msg);
		} else if( "196.2.77.25".equals(SMPPTransciever.smppIPAdress) ){ 
			jmsWriters.get("ussd-25-queue").write(msg);
		}
		System.out.println("############ Sent"); 
	}
	
//	private void processExternal(Map<String, String> msg, String destinationId) {
//		System.out.println("Processing external message for " + destinationId);
//		VASServiceProvider serviceProvider = serviceProviders.get(destinationId);
//		if (serviceProvider == null) {
//			System.out.println("No service provider found.");
//			msg.put("sourceId", msg.get("destinationId"));
//			msg.put("destinationId", msg.get("subscriberId"));
//			msg.put("shortMessage", "There are no active services on shortcode " + destinationId );
//			jmsWriters.get("smpp11-queue").write(msg);
//			VasGatewayDao.log(stmt, msg.get("uuid"), msg.get("sourceId"), msg.get("destinationId"), "help", BigDecimal.ZERO, "OK" );
//		} else {
//
//			BigDecimal amount = serviceProvider.getCharge();
//			System.out.println("Service provider is " + serviceProvider.getVasServiceProviderName() +
//					", service rate is " + amount);
//			if (amount.compareTo(BigDecimal.ZERO) > 0) {
//				msg.put("shortCode", destinationId);
//				msg.put("route", "toVASProvider");
//				msg.put("serviceCommand", "airtimeTransfer");
//				msg.put("nextQueue","vasgw-ebridge-smsc-queue");
//				msg.put("amount", amount.toString());
//				msg.put("beneficiaryId", serviceProvider.getVasServiceProviderWallet());
//				System.out.println("********** DEBITING CUSTOMER FOR SERVICE *************** wallet = " +msg.get("beneficiaryId") );
//					jmsWriters.get("ccws-queue").write(msg);
//
//
//			} else {
//				jmsWriters.get("vasgw-ebridge-smsc-queue").write(msg);
//			}
//		}
//	}

	private int processInternal(Map<String, String> msg) {
		System.out.println("############## processInternal ");
		
		if (msg.get("shortMessage").trim().toLowerCase().startsWith("bund")) {
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));			
				msg.put("shortMessage", dataBundleManager.getPriceList(false, false));
				jmsWriters.get("smpp11-queue").write(msg);
				VasGatewayDao.log(stmt, msg.get("uuid"), msg.get("sourceId"), msg.get("destinationId"), "help", BigDecimal.ZERO, "OK" );
		} else if (msg.get("shortMessage").trim().toLowerCase().startsWith("tar")) {
			msg.put("serviceCommand", "ClassOfServiceQuery");
			msg.put("nextQueue","smpp11-queue");
			msg.put("beneficiaryId", msg.get("subscriberId"));		
			jmsWriters.get("ccws-queue").write(msg);
		} else if (msg.get("shortMessage").trim().toLowerCase().startsWith("reg")) {
			
			ClassOfServiceDTO cos = VasGatewayDao.getActivePromotion(stmt);
			if (cos == null) {
				msg.put("shortMessage", "Cheap rates are not available at the moment. Check press for schedule.");
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));			
//				jmsWriters.get("smpp11-queue").write(msg);				
			} else {
				msg.put("serviceCommand", "ClassOfServiceUpdate");
				msg.put("cosName","PROMO_2_COS");
				msg.put("nextQueue","smpp11-queue");
				msg.put("beneficiaryId", msg.get("subscriberId"));	
//				jmsWriters.get("ccws-queue").write(msg);
			}
			
		} else if (msg.get("shortMessage").trim().toLowerCase().startsWith("h")) {
			msg.put("sourceId", msg.get("destinationId"));
			msg.put("destinationId", msg.get("subscriberId"));			
			msg.put("shortMessage", Messages.HELP);
			jmsWriters.get("smpp11-queue").write(msg);
			msg.put("shortMessage", Messages.HELP2);
			jmsWriters.get("smpp11-queue").write(msg);
			VasGatewayDao.log(stmt, msg.get("uuid"), msg.get("sourceId"), msg.get("destinationId"), "help", BigDecimal.ZERO, "OK" );

		} else if (msg.get("shortMessage").trim().toLowerCase().startsWith("bal")) {
			  System.out.println("############## sending bal to ccws ");
				msg.put("serviceCommand", "balanceEnquiry");
				msg.put("nextQueue","smpp11-queue");
				msg.put("beneficiaryId", msg.get("subscriberId"));		
				jmsWriters.get("ccws-queue").write(msg);		
		} else if ( isValidMenuItem(msg.get("shortMessage").trim())) {
//				msg.get("shortMessage").trim().toLowerCase().startsWith("b") 
//				&& (msg.get("shortMessage").trim().toLowerCase().length() == 2)
//				&& isNumber(msg.get("shortMessage").trim().toLowerCase().substring(1))){	
			
			 Integer amount = Integer.parseInt(msg.get("shortMessage").trim());
			 if ( amount < 1 || amount > 6 ) {
					msg.put("sourceId", msg.get("destinationId"));
					msg.put("destinationId", msg.get("subscriberId"));		
				   msg.put("shortMessage", "INVALID SELECTION: Bundle does not exist. Please re-subscribe to any bundle 1-6 for cheaper rates");
					jmsWriters.get("smpp11-queue").write(msg);
					return 0;					 
			 }		
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));	
				String payload = null;
				String msisdn = msg.get("subscriberId");
				String shortCode = msg.get("sourceId");
				DataBundlePrice price =  dataBundleManager.getPriceFor(msg.get("shortMessage").trim().toLowerCase(), Boolean.FALSE);
				String uuid = "" + (System.currentTimeMillis() + 1);
				if (price != null){
//					if ( dataBundleManagementService.isTestLine(uuid, msisdn)) {
						USSDResponse result = dataBundleManagementService.purchaseDataBundle(shortCode, uuid, uuid, msisdn, price.getBundlePrice(), price.getBundleSize(), price.getDataRate(), price.getDebit(), price.getCredit());
						payload = result.getRawPayload();
//					} else {
//						payload = "This service will be available in few days time";
//					}
				} else {
					payload = "INVALID SELECTION: Bundle does not exist. Please re-subscribe to any bundle 1-6 for cheaper rates";
				}
				System.out.println("############## payload = " + payload);
				msg.put("shortMessage", payload);
				jmsWriters.get("smpp11-queue").write(msg);
				VasGatewayDao.log(stmt, msg.get("uuid"), msg.get("sourceId"), msg.get("destinationId"), "help", BigDecimal.ZERO, "OK" );			 
//			 msg.put("serviceCommand", "voiceToDataTransfer");
//			 msg.put("amount", "" + amount);
//			 msg.put("nextQueue","smpp11-queue");
//			 msg.put("beneficiaryId", msg.get("subscriberId"));					
//			 jmsWriters.get("ccws-queue").write(msg);	
//			 return;		
		} else {
			String[] tokens = msg.get("shortMessage").trim().split("#");
			if (tokens.length == 1) {
				if (RECHARGE_PIN_LENGTHS.contains(tokens[0].length())) {
					msg.put("serviceCommand", "pinRecharge");
					msg.put("rechargeVoucher", tokens[0]);
					msg.put("nextQueue","smpp11-queue");
					msg.put("beneficiaryId", msg.get("subscriberId"));		
					jmsWriters.get("ccws-queue").write(msg);
				} /*else if (tokens[0].length() == 1) {
					 Integer amount = Integer.parseInt(msg.get("shortMessage").trim().toLowerCase().substring(1));
					 if ( (amount != 1) && (amount != 2)) {
							msg.put("sourceId", msg.get("destinationId"));
							msg.put("destinationId", msg.get("subscriberId"));		
							msg.put("shortMessage", "Invalid entry for selected bundle, please enter bundle number 1 � 7 to proceed with purchase. For bundle details send word 'bundle' to 23350 ");
							jmsWriters.get("smpp11-queue").write(msg);
							return;					 
					 }		
					 msg.put("serviceCommand", "voiceToDataTransfer");
					 msg.put("amount", "" + amount);
					 msg.put("nextQueue","smpp11-queue");
					 msg.put("beneficiaryId", msg.get("subscriberId"));					
					 jmsWriters.get("ccws-queue").write(msg);	
					 return;					
				} */ else {
					msg.put("sourceId", msg.get("shortCode"));
					msg.put("destinationId", msg.get("subscriberId"));
					msg.put("shortMessage", "Invalid service command. Please type Help and send to 23350.");
//					jmsWriters.get("smpp11-queue").write(msg);
					return 0;					
				}
			} else if (tokens.length == 2) {
				
				/* Check if inter-account balance transfer.*/
				if (tokens[1].toLowerCase().trim().startsWith("dxxxxxxxxxxx") ||
						tokens[1].toLowerCase().trim().startsWith("vxxxxxxxxxx")){
//					msg.put("serviceCommand", tokens[1].toLowerCase().trim().startsWith("d") ? "voiceToDataTransfer" : "dataToVoiceTransfer");
//					msg.put("nextQueue","smpp11-queue");
//					msg.put("beneficiaryId", msg.get("subscriberId"));					
//					BigDecimal amount = null;
//					try {
//						amount = new BigDecimal(tokens[0].trim());
//						msg.put("amount", amount.toString());
//					} catch(Exception e) {
//						msg.put("sourceId", msg.get("destinationId"));
//						msg.put("destinationId", msg.get("subscriberId"));		
//						msg.put("shortMessage", tokens[0] + " is not a valid amount.\nPlease type Help and send to 23350.");
//						jmsWriters.get("smpp11-queue").write(msg);
//						return;						
//					}
//					jmsWriters.get("ccws-queue").write(msg);	
//					return;
					
				} else {
						
						/* Check if second parameter is a valid subscriber Id */
						try {
							MobileNumberFormatter.format(tokens[1]);
						} catch (InvalidMobileNumberException ex) {
							msg.put("destinationId", msg.get("subscriberId"));
							msg.put("shortMessage",tokens[1] + " is not a valid mobile number.");
							jmsWriters.get("smpp11-queue").write(msg);
							return 0;
						}
		
						/* Check if first parameter is a valid re-charge voucher. */
						if (RECHARGE_PIN_LENGTHS.contains(tokens[0].length())) {
							msg.put("serviceCommand", "pinRecharge");
							msg.put("rechargeVoucher", tokens[0]);
							msg.put("beneficiaryId", tokens[1]);
							msg.put("nextQueue","smpp11-queue");
							jmsWriters.get("ccws-queue").write(msg);
						} else {
							/* Check if first parameter is a valid amount for balance transfer. */
							System.out.println(tokens[0]);
							String s = tokens[0].trim();
							if(s.startsWith("$")) {
								s = s.substring(1);
							}
							if (MINIMUM_AIRTIME_TRANSER.compareTo(new BigDecimal(s)) > 0) {
								msg.put("destinationId", msg.get("subscriberId"));
								msg.put("shortMessage", "Airtime transfer amount can not be less than " + MINIMUM_AIRTIME_TRANSER + ".");
								jmsWriters.get("smpp11-queue").write(msg);
								return 0;
							}
		
							if (MAXIMUM_AIRTIME_TRANSER.compareTo(new BigDecimal(s)) < 0) {
								msg.put("destinationId", msg.get("subscriberId"));
								msg.put("shortMessage", "Airtime transfer amount can not be more than " + MAXIMUM_AIRTIME_TRANSER + ".");
								jmsWriters.get("smpp11-queue").write(msg);
								return 0;
							}
							msg.put("serviceCommand", "airtimeTransfer");
							msg.put("nextQueue","smpp11-queue");
							msg.put("beneficiaryId", tokens[1]);
							msg.put("amount", s);
							jmsWriters.get("ccws-queue").write(msg);
						}
				}
			} else {
				msg.put("sourceId", msg.get("shortCode"));
				msg.put("destinationId", msg.get("subscriberId"));		
				msg.put("shortMessage", "Invalid service command. Please type Help and send to 23350.");
				jmsWriters.get("smpp11-queue").write(msg);
				return 0;
			}
		}
		return 0;
	}
	
	private boolean isValidMenuItem(String text) {
		if (text != null && text.length() < 6){
			try {
				Integer.parseInt(text);
				return Boolean.TRUE;
			} catch(Exception e) {
//				e.printStackTrace();
				return Boolean.FALSE;
			}
		}
		return false;
	}

	protected Boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return Boolean.TRUE;
		} catch(Exception e){
			return Boolean.FALSE;
		}
	}

}

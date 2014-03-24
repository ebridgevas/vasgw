package com.ebridgecommerce.prepaid.client;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.ebridgecommerce.db.DBAdapter;

import com.ebridgecommerce.domain.ClassOfServiceDTO;
import com.ebridgecommerce.smppgw.SystemParameters;
import com.ebridgecommerce.smsc.processors.PDUProcessorKey;
import com.ebridgecommerce.smsc.processors.SmppPDUProcessor;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import javax.jms.*;
import java.math.BigDecimal;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCWSAdapter implements Runnable {

	private Map<PDUProcessorKey, SmppPDUProcessor> smppPDUProcessors;

	private Session session;
	private Destination destination;
	private Connection connection;
	private PPSClient ppsClient;
	private Map<String, JMSWriter> jmsWriters;
	private Statement stmt;
	
	/* 1MB = 1 049 000 Octets*/
	private static final BigDecimal DATA_UNIT = new BigDecimal(1049000);
	
	public CCWSAdapter() {
		ppsClient = PPSClient.getInstance();
		try {
			
			while (true) {
				try {
					java.sql.Connection conn = DBAdapter.getConnection();
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
			
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
					ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = connectionFactory.createConnection();
			connection.start();
			boolean transacted = false;
			session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
			jmsWriters = new HashMap<String,JMSWriter>();
			destination = session.createQueue("ccws-queue");
			jmsWriters.put("smpp11-queue", new JMSWriter("smpp11-queue"));
			jmsWriters.put("vasgw-ebridge-smsc-queue", new JMSWriter("vasgw-ebridge-smsc-queue"));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void start() {
		Thread serverThread = new Thread(this);
		serverThread.start();
	}

	@Override
	public void run() {

		try {
			MessageConsumer messageConsumer = session.createConsumer(destination);
			messageConsumer.setMessageListener(new MessageListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onMessage(Message message) {
					/* String message */
					/* sourceId|destinationId|text */
					if (message instanceof ObjectMessage) {
						try {
							ObjectMessage o = (ObjectMessage) message;
							HashMap<String, String> msg = (HashMap<String, String>) o.getObject();
							process(msg);
							// transmitter.submit((String)msg.get("sourceId"),
							// (String)msg.get("destinationId"),
							// (String)msg.get("shortMessage"));

						} catch (Exception e) {
							System.out.println("Invalid message specified.");
							e.printStackTrace();
						}
					}
				}

			});
			connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException arg0) {
					// TODO Auto-generated method stub

				}

			});
		} catch (JMSException e) {
			System.out.println("JMSException - " + e.getMessage());
		}
	}

	protected void process(HashMap<String, String> msg) {
		if ("perSecondBilling".equals(msg.get("serviceCommand"))) {
			// try {
			// msg.put("shortMessage",
			// ppsClient.getClassOfService(msg.get("subscriberId")));
			// msg.put("sourceId", msg.get("destinationId"));
			// msg.put("subscriberId", msg.get("subscriberId"));
			// jmsWriters.get(msg.get("nextQueue")).write(msg);
			msg.put("shortMessage", "Please try again in a few days or call Help Desk.");
			msg.put("sourceId", msg.get("destinationId"));
			msg.put("destinationId", msg.get("subscriberId"));
			jmsWriters.get("smpp11-queue").write(msg);
			// } catch (TransactionFailedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// 
		} else if ("airtimeTransfer".equals(msg.get("serviceCommand"))) {
			try {
				for (Map<String, String> mtsm : ppsClient.transfer(msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"),
						new BigDecimal(msg.get("amount")), "toVASProvider".equalsIgnoreCase(msg.get("route")))) {
					
					if ( (msg.get("route") != null ) && "toVASProvider".equalsIgnoreCase(msg.get("route")) ){
						System.out.println(" destination id = " + msg.get("shortCode"));
						msg.put("sourceId", msg.get("subscriberId"));						
						msg.put("destinationId", msg.get("shortCode"));
						msg.put("shortMessage", msg.get("shortMessage"));
						jmsWriters.get(msg.get("nextQueue")).write(msg);	
						break;
					} else {
						System.out.println(" **** destination id = " +mtsm.get("subscriberId") + ", from " + msg.get("shortCode"));
						msg.put("sourceId", msg.get("shortCode"));
						msg.put("destinationId", mtsm.get("subscriberId"));
						msg.put("shortMessage", mtsm.get("shortMessage"));
						jmsWriters.get(msg.get("nextQueue")).write(msg);
					}
				}
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"), "airtimeTransfer", new BigDecimal(msg.get("amount")), "OK" );
			} catch (TransactionFailedException e) {
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"), "airtimeTransfer", new BigDecimal(msg.get("amount")), e.getMessage() );
				msg.put("shortMessage", e.getMessage());
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));
				jmsWriters.get("smpp11-queue").write(msg);
			}
		} else if ("voiceToDataTransfer".equals(msg.get("serviceCommand"))) {
				
				/*
				 * Voice to Data Transfer.
				 * - getBalanceFor(subscriberMsisdn, balanceName = "Core")
				 * - getBalanceFor(subscriberMsisdn, balanceName = "Gprs_usd")
				 * - creditBalanceWith(subscriberMsisdn, balanceName = "Core", -amount)
				 * - creditBalanceWith(subscriberMsisdn, balanceName = "Gprs_usd", +amount) 
				 * - getBalanceFor(subscriberMsisdn)
				 */
			
			String serviceCommand = msg.get("serviceCommand");
			
			try {
				Map<String, BigDecimal> balances = ppsClient.getBalance(msg.get("subscriberId"));
//				BigDecimal voiceBalance = balances.get("Core");
				String voiceBalanceName = "Core";
//				BigDecimal dataBalance = balances.get("Gprs_usd");
				String dataBalanceName = "Gprs_usd";
				String uuid = msg.get("uuid");
				String subscriberMsIsdn = msg.get("subscriberId");
				String suspenceAccount = SystemParameters.SYSTEM_PARAMETERS.get("SUSPENSE_ACCOUNT");
				
				String bundleId = msg.get("amount");
				BigDecimal amount = rateBundleId(bundleId);
  			/* Debit voiceAccount and Credit suspenceAccount. */
				ppsClient.transfer("D" + uuid, subscriberMsIsdn, voiceBalanceName, amount, suspenceAccount, "Core", amount, Boolean.FALSE);
					
				/* Debit suspenceAccount and Credit dataAccount. */
				ppsClient.transfer("C" + uuid, suspenceAccount, "Core", amount, subscriberMsIsdn, dataBalanceName, getAmount(amount, new BigDecimal(0.1)), Boolean.FALSE);
				
				/* Balances after transaction. */
//				balances = ppsClient.getBalance(subscriberMsIsdn);
//				amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
//				BigDecimal voiceBalance = balances.get("Core").setScale(2, BigDecimal.ROUND_HALF_UP);
//				BigDecimal dataBalance = balances.get("Gprs_usd").setScale(2, BigDecimal.ROUND_HALF_UP);
				
				/* Change COS. */
				ppsClient.setClassOfService(subscriberMsIsdn, getCosNameForBundleId(bundleId));
								
				String text = "You are now subscribed to Bundle " + bundleId + " with a CAP of " + getBundleSizeFor(bundleId) + " Mb and valid for 1 month";
				
//				String text = "$" + amount + " transfer accepted.\n";
//				text += "Your balances are now: \n";
//				text += "Main Account : $" + voiceBalance +
//						((balances.get("Gprs_usd") != null) ? "\n Data account : $" + dataBalance : "" );
				DBAdapter.log(stmt, uuid, subscriberMsIsdn, suspenceAccount, serviceCommand, null, text);
				msg.put("shortMessage", text);
				msg.put("sourceId", "23350");
				msg.put("destinationId", msg.get("subscriberId"));
				jmsWriters.get("smpp11-queue").write(msg);
			
			} catch (TransactionFailedException e) {
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"), serviceCommand, null, e.getMessage() );
				msg.put("shortMessage", e.getMessage());
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));
				jmsWriters.get("smpp11-queue").write(msg);
			}
		} else if ("pinRecharge".equals(msg.get("serviceCommand"))) {
			try {
				for ( Map<String, String> mtsm : ppsClient.voucherRecharge(msg.get("uuid"), msg.get("subscriberId"),  msg.get("beneficiaryId"), msg.get("rechargeVoucher"))) {
					msg.put("sourceId", "23350");
					msg.put("destinationId", mtsm.get("subscriberId"));
					msg.put("shortMessage", mtsm.get("shortMessage"));
					System.out.println("--->>> " + msg);
					jmsWriters.get(msg.get("nextQueue")).write(msg);				
				}
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"), "pinRecharge", BigDecimal.ZERO, "OK" );
			} catch (TransactionFailedException e) {
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("destinationId"), "pinRecharge", BigDecimal.ZERO, e.getMessage() );
				msg.put("shortMessage", e.getMessage());
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));
				jmsWriters.get("smpp11-queue").write(msg);
			}				
		} else if ("balanceEnquiry".equals(msg.get("serviceCommand"))) {
			System.out.println("################# CCWS : " + msg.get("subscriberId"));
			try {
				
				Map<String, BigDecimal> balances = ppsClient.getBalance(msg.get("subscriberId"));
				
				BigDecimal voiceBalance = balances.get("Core").setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal dataBalance = balances.get("Gprs_usd").setScale(2, BigDecimal.ROUND_HALF_UP);
				
				String text = "Your balances are:\n Main Account : $" + voiceBalance +
						((balances.get("Gprs_usd") != null) ? "\n Data account : $" + dataBalance : "" );
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"), "balanceEnquiry", null, "V=" + voiceBalance + "|D=" + dataBalance );
				msg.put("shortMessage", text);
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));
				System.out.println("################# MTSM : " + msg.get("shortMessage"));
				jmsWriters.get("smpp11-queue").write(msg);
			
			} catch (TransactionFailedException e) {
				DBAdapter.log(stmt, msg.get("uuid"), msg.get("subscriberId"), msg.get("beneficiaryId"), "balanceEnquiry", null, e.getMessage() );
				msg.put("shortMessage", e.getMessage());
				msg.put("sourceId", msg.get("destinationId"));
				msg.put("destinationId", msg.get("subscriberId"));
				jmsWriters.get("smpp11-queue").write(msg);
			}			
		} else if ("ClassOfServiceQuery".equals(msg.get("serviceCommand"))) {
			 try {				 
				 String subscriberMsIsdn = msg.get("subscriberId");
				 String uuid =  msg.get("subscriberId");
				 String cosName = ppsClient.getClassOfService(subscriberMsIsdn);
				 
				 msg.put("shortMessage", "Your are on " + getCosDescriptionFosCosName(cosName)); 
				
				 DBAdapter.logClassOfServiceQuery(stmt, uuid, "ClassOfServiceQuery", subscriberMsIsdn, cosName, null);
				 				 
				 msg.put("sourceId","23350");
				 msg.put("subscriberId", subscriberMsIsdn);
				 msg.put("destinationId", subscriberMsIsdn);
				 
				 jmsWriters.get("smpp11-queue").write(msg);
				 
			 } catch (TransactionFailedException e) {
				 msg.put("sourceId","23350");
				 msg.put("subscriberId", msg.get("subscriberId"));
				 msg.put("destinationId", msg.get("subscriberId"));
				 
				 jmsWriters.get("smpp11-queue").write(msg);
			 }			 
		} else if ("ClassOfServiceUpdate".equals(msg.get("serviceCommand"))) {
			 try {				 
				 String promotionId = msg.get("promotionId");
				 String uuid = msg.get("uuid");
				 String subscriberMsIsdn = msg.get("subscriberId");
				 String cosName = msg.get("cosName");
				 String currentCosName = ppsClient.getClassOfService(subscriberMsIsdn);
				 if (cosName.equalsIgnoreCase(currentCosName)) {
					 msg.put("shortMessage", "You are already registered for cheap rates.");
				 } else {
					 ppsClient.setClassOfService(subscriberMsIsdn, cosName);
					 msg.put("shortMessage", "Your are now registered for " + 
							 ("TEL_COS".equalsIgnoreCase(ppsClient.getClassOfService(subscriberMsIsdn)) ? "standard rate." : "cheap rate."));
					 DBAdapter.safClassOfService(stmt, promotionId, uuid, subscriberMsIsdn, currentCosName, cosName);
				 }				 
				 msg.put("sourceId","23350");
				 msg.put("subscriberId", subscriberMsIsdn);
				 msg.put("destinationId", subscriberMsIsdn);
				 
				 jmsWriters.get(msg.get("nextQueue")).write(msg);
				 
			 } catch (TransactionFailedException e) {
				 msg.put("sourceId","23350");
				 msg.put("subscriberId", msg.get("subscriberId"));
				 msg.put("destinationId", msg.get("subscriberId"));
				 
				 jmsWriters.get(msg.get("nextQueue")).write(msg);
			 }			 
		} else if ("restoreClassOfService".equals(msg.get("serviceCommand"))) {
			 try {
				 
				 String promotionId = msg.get("promotionId");
				 while( true ){
					 List<ClassOfServiceDTO> coss = DBAdapter.getSafedClassOfService(stmt, promotionId, 10);
					 for(ClassOfServiceDTO cos : coss){
						 ppsClient.setClassOfService(cos.getSubscriberMsisdn(), cos.getOriginalCosName());
					 }
					 if (coss.size() == 0){
						 break;
					 }
					 DBAdapter.updateSafedClassOfService(stmt, coss);
				 }				 
			 } catch (TransactionFailedException e) {
				 msg.put("sourceId","23350");
				 msg.put("subscriberId", msg.get("subscriberId"));
				 msg.put("destinationId", msg.get("subscriberId"));
				 
				 jmsWriters.get(msg.get("nextQueue")).write(msg);
			 }			 
		}
	}

	private String getCosDescriptionFosCosName(String cosName) {
		if ("TEL_COS".equals(cosName)){
			return " per second billing.";
		} else if ("PROMO_COS".equals(cosName)){
			return " 20 cent per MB of Data.";
		} else if ("PROMO_2_COS".equals(cosName)){
			return " 50 cent per MB of Data.";
		} else {
			return " standard rate.";
		}
	}

	private String getCosNameForBundleId(String bundleId) {
		if ("1".equals(bundleId)){
			return "PROMO_COS";
		} else if ("2".equals(bundleId)){
			return "PROMO_2_COS";
		} else {
			return null;
		}
	}

	protected Integer getBundleSizeFor(String bundleId) {
		if ("1".equals(bundleId)){
			return new Integer(5);
		} else if ("2".equals(bundleId)){
			return new Integer(50);
		} else {
			return null;
		}
	}

	protected BigDecimal rateBundleId(String bundleId) {
		/*
		 Note : SO far the 2 bundles we have created on the IN should be : 
		 B1 -> cost 1$  to be charged 0.2$/MB  with a step charging 30Kb .                                                                                                                                    
     B2 -> cost 4$  to be charged 0.5$/MB  with a step charging 30Kb .
		 */
		if ("1".equals(bundleId)){
			return new BigDecimal("1");
		} else if ("2".equals(bundleId)){
			return new BigDecimal("4");
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		new CCWSAdapter().start();
	}
	
	/* Calculate amount purchased in octets. */
	private BigDecimal getAmount(BigDecimal price, BigDecimal dataRate) {
		return (price.divide(dataRate)).multiply(DATA_UNIT);
	}
}
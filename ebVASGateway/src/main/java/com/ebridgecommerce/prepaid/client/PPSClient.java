package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.ebridgecommerce.prepaid.ws.WebServiceConnector;
import org.joda.time.DateTime;

import com.ebridgecommerce.domain.Account;
import com.ebridgecommerce.domain.BalanceDTO;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PPSClient {

	private static PPSClient instance;

	private ServiceSoap soapService;

	private AccountBalanceQuery accountBalanceQuery;
	private ClassOfServiceQuery classOfServiceQuery;
	private ClassOfServiceUpdate classOfServiceUpdate;
	 private AccountBalanceTransfer accountBalanceTransfer;
	private VoucherRecharge voucherRecharge;
	private FFQuery ffQuery;
	private FFUpdate ffUpdate;
	private MonetaryTransferRecordQuery mtrQuery;

	static {
		System.out.println("##########################################################################");
		System.out.println("### PPSClient version 2.0");
		System.out.println("### Released on January 1, 2012");
		System.out.println("##########################################################################");
	}

	protected PPSClient() {
		connect();
		classOfServiceQuery = new ClassOfServiceQuery(soapService, null);
		classOfServiceUpdate = new ClassOfServiceUpdate(soapService, null);
		accountBalanceTransfer = new AccountBalanceTransfer(soapService, null);
		voucherRecharge = new VoucherRecharge(soapService, null);
		accountBalanceQuery = new AccountBalanceQuery(soapService, null);
		ffQuery = new FFQuery(soapService, null);
		ffUpdate = new FFUpdate(soapService, null);
		mtrQuery = new MonetaryTransferRecordQuery(soapService, null);
	}

	public static PPSClient getInstance() {
		if (instance == null) {
			instance = new PPSClient();
		}
		return instance;
	}

	private void connect() {
		System.out.println("Connecting to CCWS ...... ");
		while (true) {
			try {
				soapService = new WebServiceConnector().getConnection();
				System.out.print("Connected.");
				break;
			} catch (ServiceException e) {
				System.out.print("*");
				try {
					Thread.sleep(5000);
				} catch (Exception ex) {
				}
			}
		}
	}

	public Map<String, BigDecimal> getBalance(String subscriberId) throws TransactionFailedException {
		return accountBalanceQuery.getBalance(subscriberId);
	}

	public Map<String, BalanceDTO> getAccountBalance(String msisdn) throws TransactionFailedException {
		return accountBalanceQuery.getAccountBalance(msisdn);
	}

	public String getClassOfService(String subscriberId) throws TransactionFailedException {
		return classOfServiceQuery.getClassOfService(subscriberId);
	}

	public void setClassOfService(String subscriberId, String cosName) throws TransactionFailedException {
		classOfServiceUpdate.changeClassOfService(subscriberId, cosName);
	}

	public Account getFriendFor(String subscriberId) throws TransactionFailedException {
		return ffQuery.getFriendFor(subscriberId);
	}

	public Boolean setFriend(String subscriberId, String ffSubscriberId) throws TransactionFailedException {
		return ffUpdate.addFriend(subscriberId, ffSubscriberId);
	}

	public String retrieveMonetaryTransferRecord(String subscriberId) throws TransactionFailedException {
		return mtrQuery.retrieveMonetaryTransactionRecord(subscriberId);
	}

	public List<Map<String, String>> transfer(String uuid, String sourceId, String destinationId, BigDecimal amount, boolean createCDR) throws TransactionFailedException {
		System.out.println("### PPSClient 2 : transfer(uuid, sourceId, destinationId, amount, createCDR)");
		return accountBalanceTransfer.transfer(uuid, sourceId, destinationId, amount, createCDR);
	}

	public List<Map<String, String>> transfer(String uuid, String sourceId, String sourceBalanceName, BigDecimal debitPrice, String destinationId, String destinationBalanceName, BigDecimal amount, boolean createCDR) throws TransactionFailedException {
		System.out.println("### PPSClient 3 : transfer(uuid, sourceId, destinationId, amount, createCDR)");
		return accountBalanceTransfer.transfer(uuid, sourceId, sourceBalanceName, debitPrice, destinationId, destinationBalanceName, amount, createCDR);
	}

	public List<Map<String, String>> transfer(String uuid, String sourceId, String sourceBalanceName, BigDecimal debitPrice, String destinationId, String destinationBalanceName, BigDecimal amount, boolean createCDR, Date expirationDate) throws TransactionFailedException {
		System.out.println("### PPSClient 4 : transfer(uuid, sourceId, destinationId, amount, createCDR)");
		return accountBalanceTransfer.transfer(uuid, sourceId, sourceBalanceName, debitPrice, destinationId, destinationBalanceName, amount, createCDR, expirationDate);
	}

	public List<Map<String, String>> voucherRecharge(String uuid, String sourceId, String destinationId, String rechargeVoucher) throws TransactionFailedException {
		return voucherRecharge.voucherRecharge(uuid, sourceId, destinationId, rechargeVoucher);
	}

	public void credit( String sourceAccountId, 
											String sourceBalanceName, 
											BigDecimal creditAmount, 
											Date expirationDate,
											String narration ) throws TransactionFailedException {
		accountBalanceTransfer.credit(sourceAccountId, sourceBalanceName, creditAmount, expirationDate, narration);
	}
	
	public static void main(String[] args) {

		PPSClient ppsClient = PPSClient.getInstance();

		if ("cos".equalsIgnoreCase(args[0])) {

			if (args.length == 3) {
				String msisdn = args[1];
				// cos query
				try {
					String result = ppsClient.getClassOfService(msisdn);
					System.out.println("######### COS [" + msisdn + "] = " + result);
				} catch (TransactionFailedException e) {
					e.printStackTrace();
				}
			} else if (args.length == 4) {
				String msisdn = args[1];
				// cos update
				try {
					ppsClient.setClassOfService(msisdn, args[2]);
					String result = ppsClient.getClassOfService(msisdn);
					System.out.println("######### COS [" + msisdn + "] = " + result);
				} catch (TransactionFailedException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("######### To query  COS: ppd cos <msisdn>");
				System.out.println("######### To update COS: ppd cos <msisdn> <cos>");
			}

		} else if ("ff".equalsIgnoreCase(args[0])) {
			if (args.length == 3) {
				// list friend
				String msisdn = args[1];
				try {
					String result = ppsClient.getFriendFor(msisdn).getAccountNumber();
					if (result == null ) {
						System.out.println("######### Subscriber [ " + msisdn + " ] has no friends.");
					} else {
						System.out.print("######### Subscriber [ " + msisdn + " ] friend is: " + result);
					}
				} catch (TransactionFailedException e) {
					e.printStackTrace();
				}
			} else if (args.length == 4) {
				// add friend
				String msisdn = args[1];
				try {
					Boolean result = ppsClient.setFriend(msisdn, args[2]);
					if (result) {
						String result2 = ppsClient.getFriendFor(msisdn).getAccountNumber();
						if (result2 == null ) {
							System.out.println("######### Subscriber [ " + msisdn + " ] has no friends.");
						} else {
							System.out.print("######### Subscriber [ " + msisdn + " ] friend is: " + result);
						}
					}
				} catch (TransactionFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if ("mtr".equalsIgnoreCase(args[0])) {
			if (args.length == 3) {
				// Get MTR
				String msisdn = args[1];
				try {
					String time = ppsClient.retrieveMonetaryTransferRecord(msisdn);
					System.out.println("######### PROCESSING RESULTS ... [" + time + "]");
					if (time != null) {
						System.out.println("######### Validating bonus which was awarded on: " + new SimpleDateFormat("dd/MM/yyyy").format(new DateTime(Long.parseLong(time)).toDate()) );					
						if (new DateTime(Long.parseLong(time)).getDayOfYear()  == new DateTime().getDayOfYear()) {	
							System.out.println("######### There was a bonus awarded today");		
						}
					}
					
//					List<MonetaryTransactionRecord> mtrs = ppsClient.retrieveMonetaryTransferRecord(msisdn);
//					if (mtrs != null) {
//						System.out.println("######### MTRs for subscriber [ " + msisdn + " ]: ");
//						for ( MonetaryTransactionRecord mtr : mtrs ) {
//							System.out.println("######### getAccumulator " + mtr.getAccumulator());
//							System.out.println("######### getBonusAwarded " + mtr.getBonusAwarded());
//							System.out.println("######### getBonusPlanName " + mtr.getBonusPlanName());
//							System.out.println("######### getChangeAccumulator " + mtr.getChangeAccumulator());
//							System.out.println("######### getCurrentCOSName " + mtr.getCurrentCOSName());
//							System.out.println("######### getChargecode " + mtr.getChargecode());
//							System.out.println("######### getDiscountAwarded " + mtr.getDiscountAwarded());
//							System.out.println("######### getDiscountPlanName " + mtr.getDiscountPlanName());
//							System.out.println("######### getMTRComment " + mtr.getMTRComment());
//							System.out.println("######### getModDate " + mtr.getModDate());
//						}
//					} else {
//						System.out.print("######### No MTR for subscriber [ " + msisdn + " ] found.");
//					}
					
				} catch (TransactionFailedException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("######### To query  MTR: ppd mtr");
				System.out.println("######### To update MTR: ppd mtr <msisdn>");
			}
		} else if ("bal".equalsIgnoreCase(args[0])) {

			System.out.println("######### Balance Enquiry");
			if (args.length > 1) {
				String msisdn = args[1];
				try {
					 Map<String, BigDecimal> result = ppsClient.getBalance(msisdn);
					 for(String balanceName : result.keySet()){
						 System.out.println("######### Balances for subscriber [" + msisdn + "]");
						 System.out.println("######### " + balanceName + " = " + result.get(balanceName));
					 }
					
				} catch (TransactionFailedException e) {
					e.printStackTrace();
				}
			}
		} else if ("credit".equalsIgnoreCase(args[0])) {

			if (args.length > 5) {
				// ./ppd credit 0735985612 Gprs_bundle 0.50 09-03-2012 DataBundleNotCredited
				String msisdn = args[1].trim();
				String sourceBalanceName = args[2].trim();
				String creditAmount      = args[3].trim();
				String expirationDateStr    = args[4].trim();
				String narration         = args[5].trim();
				
				System.out.println("######### msisdn = " + msisdn);
				System.out.println("######### sourceBalanceName = " + sourceBalanceName);
				System.out.println("######### creditAmount = " + creditAmount);
				System.out.println("######### expirationDateStr = " + expirationDateStr);
				System.out.println("######### narration = " + narration);
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date expirationDate = null;
					if (!"nodate".equalsIgnoreCase(expirationDateStr)) {
						expirationDate = dateFormat.parse(expirationDateStr);
					}
					ppsClient.credit(msisdn, sourceBalanceName, new BigDecimal(creditAmount), expirationDate, narration);				
				} catch (TransactionFailedException e) {
					System.out.println("Error - " + e.getMessage());
					System.out.println("Usage: pps credit mobileNumber balanceName creditAmount dd-MM-yyyy DataBundleNotCredited");
				} catch (ParseException e) {
					System.out.println("Invalid date or command");
					System.out.println("Usage: pps credit mobileNumber balanceName creditAmount dd-MM-yyyy DataBundleNotCredited");
				}
			}
		}
	}
}

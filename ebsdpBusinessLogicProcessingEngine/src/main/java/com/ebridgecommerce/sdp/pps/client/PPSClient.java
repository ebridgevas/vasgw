package com.ebridgecommerce.sdp.pps.client;

import com.comverse_in.prepaid.ccws.ChangeCOSRequest;
import com.comverse_in.prepaid.ccws.MonetaryTransactionRecord;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.ebridgecommerce.sdp.dao.UsagePromotionDAO;
import com.ebridgecommerce.sdp.pps.client.net.WebServiceConnector;
import org.joda.time.DateTime;
import zw.co.ebridge.jms.JMSWriter;
import zw.co.ebridge.pps.client.*;
import zw.co.ebridge.shared.dto.Account;
import zw.co.ebridge.shared.dto.BalanceDTO;
import zw.co.ebridge.util.TransactionFailedException;

import javax.jms.JMSException;
import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private NumberFormat formatter = new DecimalFormat("##0.00");
    static {
        System.out.println("##########################################################################");
        System.out.println("### PPSClient version 2.0");
        System.out.println("### Released on January 1, 2012");
        System.out.println("##########################################################################");
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
    private static final SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");

    private String identity = null;
    private Statement stmt;
    private final static ChangeCOSRequest changeCOSRequest;
    static {
        changeCOSRequest = createChangeCOSRequest();
    }

    private static ChangeCOSRequest createChangeCOSRequest(){
        ChangeCOSRequest changeCOSRequest = new ChangeCOSRequest();
        changeCOSRequest.setIdentity( null );
        changeCOSRequest.setNewCOS( "discount1" );
        return changeCOSRequest;
    }

    protected PPSClient(String pps) {
        connect(pps);
        classOfServiceQuery = new ClassOfServiceQuery(soapService, null);
        classOfServiceUpdate = new ClassOfServiceUpdate(soapService, null);
        accountBalanceTransfer = new AccountBalanceTransfer(soapService, null);
        voucherRecharge = new VoucherRecharge(soapService, null);
        accountBalanceQuery = new AccountBalanceQuery(soapService, null);
        ffQuery = new FFQuery(soapService, null);
        ffUpdate = new FFUpdate(soapService, null);
        mtrQuery = new MonetaryTransferRecordQuery(soapService, null);

        try {
            stmt = UsagePromotionDAO.getConnection().createStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static PPSClient getInstance(String pps) {
        if (instance == null) {
            instance = new PPSClient(pps);
        }
        return instance;
    }

    private void connect(String pps) {
        System.out.println("Connecting to CCWS ...... ");
        while (true) {
            try {
                soapService = new WebServiceConnector(pps).getConnection();
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

    public void credit(String sourceAccountId,
                       String sourceBalanceName,
                       BigDecimal creditAmount,
                       Date expirationDate,
                       String narration) throws TransactionFailedException {
        accountBalanceTransfer.credit(sourceAccountId, sourceBalanceName, creditAmount, expirationDate, narration);
    }

    public String collectBonus(String msisdn, Long uuid, BigDecimal threshHold) {

        String response = null;

        try {
            DateTime from = new DateTime();
            from = from.withHourOfDay(0);
            from = from.withMinuteOfHour(0);
            from = from.withSecondOfMinute(0);

            DateTime to = new DateTime();
            to = to.withHourOfDay(23);
            to = to.withMinuteOfHour(59);
            to = to.withSecondOfMinute(59);

            Calendar fromCal = Calendar.getInstance();
            fromCal.setTime(from.toDate());

            Calendar toCal = Calendar.getInstance();
            toCal.setTime(to.toDate());

            SubscriberRetrieve subscriberRetrieve
                    = soapService.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities(
                    msisdn, null, 128, fromCal, toCal, true);
            MonetaryTransactionRecord[] result = subscriberRetrieve.getMonetaryTransactionsRecords();

            Date  dateAwarded = isBonusAwarded(result);
            if ( dateAwarded != null ) {
                changeCOSRequest.setSubscriberId( msisdn.substring(3) );
                soapService.changeCOS(changeCOSRequest);
                response = "Congrats! You now qualify for a 50% discount on any Telecel calls made between 10pm today and 4am tomorrow";
                UsagePromotionDAO.log(stmt,uuid, msisdn, dateAwarded, "nightpromo","000", response  );
            } else {
                response =  "You have not attained a usage of USD" + formatter.format(threshHold) +
                        " between " + dateFormat.format(from.toDate()) + " " +
                        hourFormat.format(from.toDate()) +  " and " + hourFormat.format(to.toDate());

                UsagePromotionDAO.log(stmt,uuid, msisdn, null, "nightpromo","051", response );
            }
        } catch (Exception e) {
            UsagePromotionDAO.log(stmt,uuid, msisdn, null, "nightpromo","096",e.getMessage());
            response = "An error occured. Please try again after a few minutes.";
        }
        return response;
    }

    public Date isBonusAwarded(MonetaryTransactionRecord[] mtrs) {
        if (mtrs == null) {
            return null;
        }
        for ( MonetaryTransactionRecord mtr : mtrs ) {
            if ( ( mtr.getMTRComment() != null )
                    && ( ( mtr.getMTRComment().indexOf("Bonus") > -1 )
                    || ( mtr.getMTRComment().indexOf("Discount") > -1 ) )
                    && mtr.getBonusAwarded() > 0.0) {
                return mtr.getModDate().getTime();
            }
        }
        return null;
    }
    public static void main(String[] args) {

        String pps = null;
        String serviceCommand = args[0];
        if ( serviceCommand.startsWith("com_") ) {
            pps = "comverse";
            serviceCommand = serviceCommand.substring(4);

        } else {
            pps = "zte";
        }

        PPSClient ppsClient = PPSClient.getInstance(pps);

        if ("cos".equalsIgnoreCase(serviceCommand)) {

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
                System.out.println("######### To query COS: ppd cos <msisdn>");
                System.out.println("######### To update COS: ppd cos <msisdn> <cos>");
            }

        } else if ("ff".equalsIgnoreCase(serviceCommand)) {
            if (args.length == 3) {
// list friend
                String msisdn = args[1];
                try {
                    String result = ppsClient.getFriendFor(msisdn).getAccountNumber();
                    if (result == null) {
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
                        if (result2 == null) {
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
        } else if ("mtr".equalsIgnoreCase(serviceCommand)) {
            if (args.length == 3) {
// Get MTR
                String msisdn = args[1];
                try {
                    String time = ppsClient.retrieveMonetaryTransferRecord(msisdn);
                    System.out.println("######### PROCESSING RESULTS ... [" + time + "]");
                    if (time != null) {
                        System.out.println("######### Validating bonus which was awarded on: " + new SimpleDateFormat("dd/MM/yyyy").format(new DateTime(Long.parseLong(time)).toDate()));
                        if (new DateTime(Long.parseLong(time)).getDayOfYear() == new DateTime().getDayOfYear()) {
                            System.out.println("######### There was a bonus awarded today");
                        }
                    }

// List<MonetaryTransactionRecord> mtrs = ppsClient.retrieveMonetaryTransferRecord(msisdn);
// if (mtrs != null) {
// System.out.println("######### MTRs for subscriber [ " + msisdn + " ]: ");
// for ( MonetaryTransactionRecord mtr : mtrs ) {
// System.out.println("######### getAccumulator " + mtr.getAccumulator());
// System.out.println("######### getBonusAwarded " + mtr.getBonusAwarded());
// System.out.println("######### getBonusPlanName " + mtr.getBonusPlanName());
// System.out.println("######### getChangeAccumulator " + mtr.getChangeAccumulator());
// System.out.println("######### getCurrentCOSName " + mtr.getCurrentCOSName());
// System.out.println("######### getChargecode " + mtr.getChargecode());
// System.out.println("######### getDiscountAwarded " + mtr.getDiscountAwarded());
// System.out.println("######### getDiscountPlanName " + mtr.getDiscountPlanName());
// System.out.println("######### getMTRComment " + mtr.getMTRComment());
// System.out.println("######### getModDate " + mtr.getModDate());
// }
// } else {
// System.out.print("######### No MTR for subscriber [ " + msisdn + " ] found.");
// }

                } catch (TransactionFailedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("######### To query MTR: ppd mtr");
                System.out.println("######### To update MTR: ppd mtr <msisdn>");
            }
        } else if ("bal".equalsIgnoreCase(serviceCommand)) {

            System.out.println("######### Balance Enquiry");
            if (args.length > 1) {
                String msisdn = args[1];
                try {
                    Map<String, BalanceDTO> result = ppsClient.getAccountBalance(msisdn);
                    for (String balanceName : result.keySet()) {
// System.out.println("######### Balances for subscriber [" + msisdn + "]");
                        System.out.println("######### " + balanceName + " = " + result.get(balanceName).getAmount() + " Expiry Date = " + result.get(balanceName).getExpiryDate());
                    }

                } catch (TransactionFailedException e) {
                    e.printStackTrace();
                }
            }
        } else if ("x".equalsIgnoreCase(serviceCommand)) {

            if (args.length > 5) {
// ./ppd credit 0735985612 Gprs_bundle 0.50 09-03-2012 DataBundleNotCredited
                String msisdn = args[1].trim();
                String sourceBalanceName = args[2].trim();
                String creditAmount = args[3].trim();
                String expirationDateStr = args[4].trim();
                String destinationMsisdn = args[5].trim();
                String destinationBalanceName = args[6].trim();
                System.out.println("######### msisdn = " + msisdn);
                System.out.println("######### sourceBalanceName = " + sourceBalanceName);
                System.out.println("######### creditAmount = " + creditAmount);
                System.out.println("######### expirationDateStr = " + expirationDateStr);
//                System.out.println("######### narration = " + narration);
                System.out.println("######### destinationMsisdn = " + destinationMsisdn);
                System.out.println("######### destinationBalanceName = " + destinationBalanceName);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date expirationDate = null;
                    if (!"nodate".equalsIgnoreCase(expirationDateStr)) {
                        expirationDate = dateFormat.parse(expirationDateStr);
                    }
                    ppsClient.credit(msisdn, sourceBalanceName, BigDecimal.ZERO.subtract(new BigDecimal(creditAmount)), expirationDate, "Transfer to " + destinationMsisdn );
                    ppsClient.credit(destinationMsisdn, destinationBalanceName, new BigDecimal(creditAmount), expirationDate, "Transfer from " + msisdn );
                } catch (TransactionFailedException e) {
                    System.out.println("Error - " + e.getMessage());
                    System.out.println("Usage: pps credit mobileNumber balanceName creditAmount dd-MM-yyyy DataBundleNotCredited");
                } catch (ParseException e) {
                    System.out.println("Invalid date or command");
                    System.out.println("Usage: pps credit mobileNumber balanceName creditAmount dd-MM-yyyy DataBundleNotCredited");
                }
            }
        } else if ("credit".equalsIgnoreCase(serviceCommand)) {

            if (args.length > 5) {
// ./ppd credit 0735985612 Gprs_bundle 0.50 09-03-2012 DataBundleNotCredited
                String msisdn = args[1].trim();
                String sourceBalanceName = args[2].trim();
                String creditAmount = args[3].trim();
                String expirationDateStr = args[4].trim();
                String narration = args[5].trim();

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

package com.ebridgevas.services;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.dto.TxnDto;
import com.ebridgevas.in.AbstractAccountManager;
import com.ebridgevas.in.AccountManager;
import com.ebridgevas.model.AccountType;
import com.ebridgevas.model.DataBundle;
import com.zw.ebridge.domain.USSDSession;
import zw.co.ebridge.domain.TxnType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

public class DataBundleServicePDUEventHandler extends AbstractPDUEventHandler implements ServerPDUEventHandler {

    private AccountManager prepaidAccountManager;
    private AccountManager postpaidAccoungManager;

    private Map<String, USSDSession> sessions;

//    private DataBundleManager dataBundleManager;
//    private PPSAdapter ppsAdapter;
//

//
//    private static final String voiceBalanceName = "Core";
//    private static final String dataBalanceName = "Gprs_bundle";
//    private static final BigDecimal DATA_UNIT = new BigDecimal(1049000);

    private Statement stmt;

//    private WebServices postpaid;

    private TxnDAO txnDao;
    private BigDecimal postpaidLimit;
//    private CreditAccountRequestDao requestDao;
//    private CreditAccountResponseDao responseDao;

//    private static final Map<ServiceCommand, ServiceCommandProcessor> PROCESSORS;

//    static {
//        PROCESSORS = new HashMap<ServiceCommand, ServiceCommandProcessor>();
//        PROCESSORS.put(ServiceCommand.DATA_BUNDLE_PURCHASE, new BundlePurchaseServiceCommandProcessor());
//        PROCESSORS.put(ServiceCommand.BALANCE_ENQUIRY, new BalanceEnquiryServiceCommandProcessor());
//        PROCESSORS.put(ServiceCommand.BALANCE_TRANSFER, new AirtimeTransferServiceCommandProcessor());
//        PROCESSORS.put(ServiceCommand.VOUCHER_RECHARGE, new VoucherRechargeServiceCommandProcessor());
//        PROCESSORS.put(ServiceCommand.HELP, new HelpServiceCommandProcessor());
//    }

    public DataBundleServicePDUEventHandler( AccountManager prepaidAccountManager,
                                             AccountManager postpaidAccountManager,
                                             Map<String, USSDSession> sessions,
                                             BigDecimal postpaidLimit) { //}, String channelType) {

//        super("");

//        this.channelType = channelType;

//        log.debug("in constructor.");
        this.prepaidAccountManager = prepaidAccountManager;
        this.postpaidAccoungManager = postpaidAccountManager;
        this.sessions = sessions; // new HashMap<String, USSDSession>();
//        txnDao = new TxnDao();
//        txnDao.initEntityManager();
        txnDao = prepaidAccountManager.getTxnDao();
        this.postpaidLimit = postpaidLimit;
//        requestDao = new CreditAccountRequestDao();
//        requestDao.initEntityManager();
//
//        responseDao = new CreditAccountResponseDao();
//        responseDao.initEntityManager();

//        prepaidAccountManager.setTxnDao(txnDao);
//        prepaidAccountManager.setRequestDao(requestDao);
//        prepaidAccountManager.setResponseDao(responseDao);

//        postpaidAccountManager.setTxnDao(txnDao);
//        postpaidAccountManager.setRequestDao(requestDao);
//        postpaidAccountManager.setResponseDao(responseDao);
    }

    @Override
    public String handleEvent( PduDto pdu ) {

        TxnDto txn =
            new TxnDto( new BigInteger(pdu.getUuid()), pdu.getDestinationId(), pdu.getSourceId(),
                         pdu.getChannel(),new Date() );

            System.out.println("{ msisdn : " + pdu.getSourceId() + ", payload : " + pdu.getShortMessage()  + "}");

//            if ( pdu.getShortMessage().split(" ").length > 7 ) {
//                String[] tokens = pdu.getShortMessage().split(" ");
//                if ("0".equals(tokens[7])) {
//                    /* Balance inquiry. */
//                    System.out.println("balance enquiry.");
//                    String result =
//                            getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) +
//                                    prepaidAccountManager.getAccountBalance(txn.getSourceId());
//
////                                    ( isPostpaid ? postpaidAccoungManager.getAccountBalance(txn.getSourceId()) :
////                                            prepaidAccountManager.getAccountBalance(txn.getSourceId()));
//                    /* Log. */
//                    txn.setTxnDate(new Date());
//                    txn.setStatus("000");
//                    txn.setTxnType("BalanceEnquiry");
//                    txn.setNarrative("Balance Enquiry Successful.");
//                    txn.setShortMessage(result);
//                    txnDao.persist(txn);
//                    return result;
//                } else {
//                    DataBundle bundle = AbstractAccountManager.DATA_BUNDLES.get(tokens[7]);
//                    if (bundle != null) {
//                        txn.setTxnType("DataBundlePurchase");
//                        txn.setProductCode(bundle.getBundleType());
//                        txn.setAmount(bundle.getDebit());
//
//                        String result =
//                                getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) +
//                                        prepaidAccountManager.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE);
////                                        ( isPostpaid ?
////                                                postpaidAccoungManager.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE) :
////                                                prepaidAccountManager.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE));
//
//                    /* Log. */
//                        txn.setTxnDate(new Date());
//                        txn.setStatus("000");
//                        txn.setNarrative("Data Bundle Purchase Successful.");
//                        txn.setShortMessage(result);
//                        txnDao.persist(txn);
//                    }
//                }
//            }

//        if ("ussd".equalsIgnoreCase(channelType)) {

            txn.setSessionId(getSessionId(pdu.getShortMessage()));

//            Boolean isPostpaid = isPostpaid( txn.getSourceId() );

            if ( isInitialDial( pdu.getShortMessage() ) ) {
//                if (isPostpaid) {
//                    return processRootMenu(txn, "2");
//                } else {
                    return getUssdMessagePrefix(Boolean.FALSE, getSessionId( pdu.getShortMessage()) ) + "Welcome to Telecel DATA bundles.\nPlease select either 1 or 2\n1. Balance enquiry \n2. Buy my DATA  bundle";
//                }
            }

            String ussdAnswer = //("ussd".equalsIgnoreCase(channelType)) ?
                    getContent(pdu.getShortMessage());// : pdu.getShortMessage();

            System.out.println("selection : " + ussdAnswer);

            USSDSession session = sessions.get( txn.getSourceId() );
            if (session == null) {
                return processRootMenu(txn, ussdAnswer);
            }

            return processUSSDAnswer(txn, ussdAnswer, session);

//        } else {
//            txn.setShortMessage(pdu.getShortMessage());
//            return processSMSServiceCommand(txn);
//        }
    }

    protected String processSMSServiceCommand(TxnDto txn) {

        if (txn.getShortMessage() == null) {
            return "Invalid command. Please type help";
        }

        Boolean isPostpaid = isPostpaid( txn.getSourceId() );

//        if (isPostpaid) {
//            return "This service is currently available for prepaid subscribers only.";
//        }

//        ServiceCommand serviceCommand =
//                ServiceCommandParser.parsePayload(
//                        txn.getShortMessage().trim().toLowerCase(), sessions.get( txn.getSourceId()));

        return null; // PROCESSORS.get(serviceCommand).process(serviceCommand, txn);
    }


    protected String processRootMenu(TxnDto txn, String ussdAnswer) {
        System.out.println("processing root menu.");

        Integer selection = null;

        try {
            selection = Integer.parseInt(ussdAnswer);
        } catch(NumberFormatException e ){
            System.out.println("selection not a number. displaying root menu again.");
            txn.setStatusCode("055");
            txn.setNarrative("selection not a number. displaying root menu again.");
            txn.setShortMessage("Invalid Selection. Please select either 1 or 2\n" + "1. Balance enquiry \n" + "2. Buy my bundle");
            txnDao.persist(txn);
            return getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) + txn.getShortMessage();
        }

        if ((selection != 1) && (selection != 2) ) {
            txn.setStatusCode("055");
            txn.setNarrative("selection not a number. displaying root menu again.");
            txn.setShortMessage("Invalid Selection. Please select either 1 or 2\n" + "1. Balance enquiry \n" + "2. Buy my bundle");
            txnDao.persist(txn);
            return getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) + txn.getShortMessage();
        }

        try {

            Boolean isPostpaid = isPostpaid( txn.getSourceId() );

//            if (isPostpaid && "144".equals(txn.getDestinationId())) {
//                return getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) + "This service is currently available for prepaid subscribers only.";
//            }
//
//            if (!isPostpaid && "971".equals(txn.getDestinationId())) {
//                return getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) + "This service is currently available for postpaid subscribers only.";
//            }

            switch ( selection ){
                case 1:
                    /* Balance inquiry. */
                    String result =
                            getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) +
                                    ( isPostpaid ? postpaidAccoungManager.getAccountBalance(txn.getSourceId()) :
                                            prepaidAccountManager.getAccountBalance(txn.getSourceId()));
                    /* Log. */
                    txn.setStatusCode("000");
                    txn.setTransactionType("BalanceEnquiry");
                    txn.setNarrative("Balance Enquiry Successful.");
                    txn.setShortMessage(result);
                    txnDao.persist(txn);
                    return result;
                case 2:
                    /* Data Bundle Purchase. */
                    sessions.put(txn.getSourceId(), new USSDSession(txn.getSourceId(), txn.getSessionId().toString(), TxnType.DATA_BUNDLE_PURCHASE));
                    result = getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) +
                                getDataBundlePrices(isPostpaid ? AccountType.POSTPAID : AccountType.PREPAID);

                    /* Log. */
                    txn.setStatusCode("000");
                    txn.setTransactionType("DataBundleListing");
                    txn.setNarrative("DataBundleListing submitted.");
                    txn.setShortMessage(result);
                    txnDao.persist(txn);
                    return result;
                default:
                    /* Unreachable code. */
//                    log.debug("Invalid selection.");
//                    String selectionError = "Invalid selection.\n";
//                    selectionError += getDataBundlePrices();
////                    DBAdapter.log(stmt, uuid, sourceId, "", "DataBundlePurchaseError", null, "Invalid menu option selected");
//                    return getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) + selectionError;
//
                    txn.setStatusCode("055");
                    txn.setNarrative("selection not a number. displaying root menu again.");
                    txn.setShortMessage("Invalid Selection. Please select either 1 or 2\n" + "1. Balance enquiry \n" + "2. Buy my bundle");
                    txnDao.persist(txn);
                    return getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) + txn.getShortMessage();

            }
        }  catch(Exception e ) {
            /* Unreachable code. */
            e.printStackTrace();
//            DBAdapter.log(stmt, uuid, sourceId, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
            return getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) +  "An error occurred. Please try again.";
        }
    }

    public Boolean isPostpaid(String msisdn) {
        String cos = prepaidAccountManager.getClassOfService(msisdn);
        return cos == null || "STAFF_COS".equalsIgnoreCase(cos);
    }
//    protected String getBalance(String uuid, String msisdn){
//        try {
//            System.out.println("########### DataBundleManagementService - getBalance for - " + msisdn);
//
//            Map<String, BalanceDTO> balances = null;
//            try {
//                balances = ppsAdapter.getAccountBalance(uuid, msisdn);
//            } catch(TransactionFailedException e) {
//                return "Balance enquiry failed: " + e.getMessage();
//            }
//            System.out.println("########### DataBundleManagementService - Balanced recieved from IN");
//
//            BigDecimal voiceBalance = balances.get(voiceBalanceName).getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal dataBalance = null;
//            BigDecimal dataInDollars = balances.get(dataBalanceName) != null ? balances.get(dataBalanceName).getAmount() : null;
//            if (dataInDollars != null) {
//                dataBalance = dataInDollars.divide(new BigDecimal(0.11), 2, RoundingMode.HALF_UP);
//                dataBalance = dataBalance.setScale(2, RoundingMode.HALF_UP);
//            }
//
//            DBAdapter.log(stmt, uuid, msisdn, "", "balanceEnquiry", null, "V=" + voiceBalance + "|D=" + dataBalance );
//
//            String expiryDate = null;
//            if (dataBalance == null){
//                dataBalance = BigDecimal.ZERO;
//            } else {
//                expiryDate = getExpiryDate(balances.get(dataBalanceName).getExpiryDate());
//            }
//
//            String result =
//                    "Your airtime bal = " + voiceBalance +
//                            "usd. " +
//                            "GPRS= " + dataBalance + "mb exp on " + expiryDate;
//
//            System.out.println("########### getBalance Result = " + result);
//            return result;
//        } catch(Exception e ) {
//            e.printStackTrace();
//            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
//            return Messages.SYSTEM_ERROR;
//        }
//
//    }

    private String processUSSDAnswer(TxnDto txn, String ussdAnswer, USSDSession session) {

        try {

            Boolean isPostpaid = isPostpaid(txn.getSourceId());

            /* Validate session. */
            if (!session.getSessionId().equals(txn.getSessionId().toString())) {
                sessions.remove( txn.getSourceId() );
                String result = getUssdMessagePrefix(
                                    Boolean.FALSE, txn.getSessionId()) +
                                    getDataBundlePrices( isPostpaid ? AccountType.POSTPAID : AccountType.PREPAID);

                /* Log. */
                txn.setStatusCode("000");
                txn.setTransactionType("BundlePriceEnquiry");
                txn.setNarrative("Bundle Price Enquiry Successful.");
                txn.setShortMessage(result);
                txnDao.persist(txn);

                return result;
            }

            switch( session.getTxnType() ){

                case DATA_BUNDLE_PURCHASE:

                    Integer selection = null;
                    try {
                        selection = Integer.parseInt( ussdAnswer.trim() );
                    } catch(NumberFormatException e ){
                        System.out.println("############# Error reading bundle id - " + e.getMessage());
                        String result = getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) + " invalid bundle type selected.";

                        /* Log. */
                        txn.setStatusCode("055");
                        txn.setNarrative("selection not a number. displaying root menu again.");
                        txn.setShortMessage("INVALID SELECTION: Bundle does not exist. Please re-subscribe to any bundle 1-8 for cheaper rates.");
                        txnDao.persist(txn);
                        return result;
                    }

                    if (!isValidDataBundleID(selection)) {
//                        DBAdapter.log(stmt, uuid, sourceId, "", "DataBundlePurchaseError", null, Messages.INVALID_BUNDLE_ID_LOG);
                        String result = getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId()) + " INVALID SELECTION: Bundle does not exist. Please re-subscribe to any bundle 1-8 for cheaper rates.";

                        /* Log. */
                        txn.setStatusCode("055");
                        txn.setNarrative("selection not a number. displaying root menu again.");
                        txn.setShortMessage("INVALID SELECTION: Bundle does not exist. Please re-subscribe to any bundle 1-8 for cheaper rates.");
                        txnDao.persist(txn);
                        return result;
                    }

                    sessions.remove(txn.getSourceId());

                    txn.setTransactionType("DataBundlePurchase");
                    txn.setProductCode(selection.toString());
                    txn.setAmount(AbstractAccountManager.DATA_BUNDLES.get(selection.toString()).getDebit());

                    if (isPostpaid) {
                        if ( txnDao.isLimitReached(txn, postpaidLimit ) ) {
                            String errorMessage = "You have reached your data bundle credit limit of " +
                                    postpaidLimit.setScale(2, RoundingMode.HALF_UP) +
                                    " for this month. Contact our call centre team on 150 for any further enquiries.' " ;
                            String result = getUssdMessagePrefix(Boolean.FALSE, txn.getSessionId())   + errorMessage;
                            txn.setStatusCode("055");
                            txn.setNarrative(errorMessage);
                            txn.setShortMessage(errorMessage);
                            txnDao.persist(txn);
                            return result;
                        }
                    }

                    String result =
                         getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) +
                            ( isPostpaid ?
                                postpaidAccoungManager.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE) :
                                    prepaidAccountManager.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE));

                    /* Log. */
                    txn.setStatusCode("000");
                    txn.setNarrative("Data Bundle Purchase Successful.");
                    txn.setShortMessage(result);
                    txnDao.persist(txn);

                    return result;
                default:
//                    DBAdapter.log(stmt, uuid, sourceId, "", "DataBundlePurchaseError", null, Messages.INVALID_SERVICE_COMMAND);
                    return getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) + " invalid command. Please try again.";
            }
        }  catch(Exception e ) {
            e.printStackTrace();
            return getUssdMessagePrefix(Boolean.TRUE, txn.getSessionId()) + "An error occurred. Please try again.";
        }
    }

    protected boolean isValidDataBundleID(Integer id) {
        return (id >= 1) && (id <= 8);
    }

//    public String purchaseDataBundle(Integer sessionId, String uuid, String msisdn, BigDecimal price, BigDecimal bundleSize, BigDecimal dataRate, BigDecimal debit, BigDecimal credit) {
//
//        try {
//            price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
//
//            Map<String, BalanceDTO> balances = ppsAdapter.dataBundlePurchase(uuid, msisdn, price, bundleSize, debit, credit);
//
//            BigDecimal voiceBalance = balances.get(voiceBalanceName).getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal dataBalanceInOctets = balances.get(dataBalanceName).getAmount();
//
//            BigDecimal dataBalance = null;
//            if (dataBalanceInOctets != null){
//                dataBalance = dataBalanceInOctets.divide(dataRate, 2, RoundingMode.HALF_UP);
//            } else {
//                dataBalance = new BigDecimal(0);
//            }
//
//            dataBalance = dataBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
//            Date expiryDate =  balances.get(dataBalanceName).getExpiryDate();
//            String payload = "" +
//                    "You have bought the " +
//                    bundleSize +
//                    "mb bundle. Your bal = " +
//                    voiceBalance +
//                    "usd. Data bundle= " +
//                    dataBalance +
//                    "mb exp on " + getExpiryDate(expiryDate);
//
//            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchase", price, "V=" + voiceBalance + "|D=" + dataBalance, "000", expiryDate, "144" );
//
//            return getUssdMessagePrefix(Boolean.TRUE, sessionId) + payload;
//
//        } catch (TransactionFailedException e) {
//
//            String msg = e.getMessage();
//            if (msg != null && msg.toLowerCase().startsWith("subscriber account is ")) {
//                msg = "Subscriber not active";
//            }
//
//            if (msg != null && msg.toLowerCase().startsWith("failed to retrieve subscriber")) {
//                msg = "Subscriber retrieval failure";
//            }
//
//            if (msg != null && msg.toLowerCase().startsWith("insufficient credit for selected bundle")) {
//                msg = "Insufficient credit for selected bundle";
//            }
//
//            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, msg);
//            return getUssdMessagePrefix(Boolean.TRUE, sessionId) + e.getMessage();
//        } catch(Exception e ) {
//            e.printStackTrace();
//            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
//            return getUssdMessagePrefix(Boolean.TRUE, sessionId) + Messages.SYSTEM_ERROR;
//        }
//    }

//    public String purchasePostpaidDataBundle(Integer sessionId, String uuid, String msisdn, String bundleType) {
//
//        try {
//
//            Date expiry = AccountTransferUtils.getExpiryDateByType(bundleType);
//
//            AddUserAcctByIndiPricePlanSubsReqDto request = new AddUserAcctByIndiPricePlanSubsReqDto();
//            request.setBundleType(bundleType);
//            request.setEffDate(new Date());
//            request.setExpDate(expiry);
//            request.setMSISDN(msisdn); //263733474747       :String
//            request.setPricePlanID("166");
//
//            postpaid.addUserAcctByIndiPricePlanSubs(request);
//
//            String payload = "";
//
//            QueryAcmBalReqDto bal = new QueryAcmBalReqDto();
//            bal.setMSISDN(msisdn);
//            String plan = "166";
//            bal.setPricePlanID(plan);  // 166 for all balance queries    :String
//            QueryAcmBalRetDto balResponse = null;
//            try {
//                balResponse = postpaid.queryAcmBal(bal);
//                Long balanceMb = ( Long.parseLong(balResponse.getBalance()) / 1048576 );
//                String expiryDate = balResponse.getExpDate();
//
//                String result = "Your GPRS bal= " + balanceMb + "mb exp on " + expiryDate;
//
//                payload = "" +
//                        "You have bought the " +
//                        getBundleSizeFor(bundleType) +
//                        "mb bundle. Your data bale= " +
//                        balanceMb +
//                        "mb exp on " + expiryDate;
//
//
//                log.debug("{ balance : msisdn : " + msisdn + ", payload : " + payload + "}");
//
//            } catch (RemoteException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//
////            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchase", price, "V=" + voiceBalance + "|D=" + dataBalance, "000", expiryDate, "144" );
//
//            return getUssdMessagePrefix(Boolean.TRUE, sessionId) + payload;
//
//       // } catch (Exception e) {
//
////            String msg = e.getMessage();
////            if (msg != null && msg.toLowerCase().startsWith("subscriber account is ")) {
////                msg = "Subscriber not active";
////            }
////
////            if (msg != null && msg.toLowerCase().startsWith("failed to retrieve subscriber")) {
////                msg = "Subscriber retrieval failure";
////            }
////
////            if (msg != null && msg.toLowerCase().startsWith("insufficient credit for selected bundle")) {
////                msg = "Insufficient credit for selected bundle";
////            }
////
////            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, msg);
////            return getUssdMessagePrefix(Boolean.TRUE, sessionId) + e.getMessage();
//        } catch(Exception e ) {
//            e.printStackTrace();
////            DBAdapter.log(stmt, uuid, msisdn, "", "DataBundlePurchaseError", null, Messages.SYSTEM_ERROR);
//            return getUssdMessagePrefix(Boolean.TRUE, sessionId) + "An error occurred. Please try again.";
//        }
//    }

//    private String getExpiryDate(Date expiryDate) {
//        return expiryDate != null ? String.format("%1$td/%1$tm/%1$tY", expiryDate) : null;
//    }
}

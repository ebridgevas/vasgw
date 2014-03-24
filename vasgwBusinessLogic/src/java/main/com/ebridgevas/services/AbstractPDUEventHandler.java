package com.ebridgevas.services;

import com.ebridgevas.model.AccountType;
import com.ebridgevas.model.ServiceCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPDUEventHandler {

    public static final Long USSD_TIMEOUT = new Long(30000);

    private static final Map<String, String> BUNDLE_TYPES;

//    protected static String channelType;

    static {
        BUNDLE_TYPES = new HashMap<String, String>();

        BUNDLE_TYPES.put("1","5 MB");
        BUNDLE_TYPES.put("2","10 MB");
        BUNDLE_TYPES.put("3","40 MB");
        BUNDLE_TYPES.put("4","160 MB");
        BUNDLE_TYPES.put("5","400 MB");
        BUNDLE_TYPES.put("6","1000 MB");
        BUNDLE_TYPES.put("7","2000 MB");
    }

//    protected AbstractPDUEventHandler(String channelType) {
//        this.channelType = channelType;
//    }

    public static String getUssdMessagePrefix(Boolean terminateSession, Integer sessionId){
        return // "ussd".equalsIgnoreCase(channelType) ?
                ( terminateSession ? "81" : "72") + " " + sessionId + ( terminateSession ? "" : " " + USSD_TIMEOUT ) + " 0 "; // : "";
    }
//
    protected Boolean isInitialDial( String payload ) {
        return payload.split(" ").length < 7;
    }
//
//    protected Boolean isInitialMenu(String sourceId) {
//        return USSD_SESSIONS.containsKey(sourceId);
//    }
//
//    protected ServiceCommand getServiceCommand( String payload ) {
//
//        if ( payload.split(" ").length > 7 ) {
//            String[] tokens = payload.split(" ");
//            if ("0".equals(tokens[7])) {
//                return ServiceCommand.BALANCE_ENQUIRY;
//            }
//        }
//    }
    //

    protected Integer getSessionId( String payload) {
        return // "ussd".equalsIgnoreCase(this.channelType) ?
                new Integer( payload.split(" ")[1] ); // : new Integer(0);
    }

    protected static String getContent( String payload ) {
        String[] tokens = payload.split(" ");
        return tokens.length > 6 ? str(Arrays.copyOfRange(tokens, 7, tokens.length)) : "";
    }

    /**
     * String array to string convertor
     * @param a
     * @return
     */
    protected static String str(String[] a) {
        StringBuilder sb = new StringBuilder();
        for (String s : a) {
            sb.append(s + " ");
        }
        return sb.toString().trim();
    }

    protected Boolean isTerminating(String payload){
        return payload.startsWith("81");
    }

    protected String getDataBundlePrices(AccountType accountType) {
        switch (accountType) {
            case POSTPAID:
                return  "Select bundle 1 to 8\n" +
                        "1=50c for 5MB\n" +
                        "2=$1  for 10MB\n" +
                        "3=$3  for 80MB\n" +
                        "4=$5  for 150MB\n" +
                        "5=$10 for 320MB\n" +
                        "6=$20 for 800MB\n" +
                        "7=$45 for 2000MB\n" +
                        "8=$75 for 4000MB\n";
            case PREPAID:

                return  "Select bundle 1 to 8\n" +
                        "1=50c for 5MB\n" +
                        "2=$1  for 10MB\n" +
                        "3=$3  for 80MB\n" +
                        "4=$5  for 150MB\n" +
                        "5=$10 for 320MB\n" +
                        "6=$20 for 800MB\n" +
                        "7=$45 for 2000MB\n" +
                        "8=$75 for 4000MB\n";
            default:
                return null;
        }
    }

    protected String getBundleSizeFor(String bundleType) {
        return BUNDLE_TYPES.get(bundleType);
    }
}

package com.ebridgevas.in;

import com.ebridgevas.model.DataBundle;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAccountManager {

    public static final Map<String, String> DATA_BUNDLES_AMOUNT_TO_TYPE;

    public static final Map<String, DataBundle> DATA_BUNDLES;

    public static final Long USSD_TIMEOUT = new Long(30000);

    private static final Map<String, String> BUNDLE_TYPES;
    private static final Map<String, String> BUNDLE_TYPES_POSTPAID;

    static {
        BUNDLE_TYPES = new HashMap<String, String>();

        BUNDLE_TYPES.put("1","5 MB");
        BUNDLE_TYPES.put("2","10 MB");
        BUNDLE_TYPES.put("3","80 MB");
        BUNDLE_TYPES.put("4","150 MB");
        BUNDLE_TYPES.put("5","160 MB");
        BUNDLE_TYPES.put("6","400 MB");
        BUNDLE_TYPES.put("7","1000 MB");
        BUNDLE_TYPES.put("8","2000 MB");
    }

    static {
        BUNDLE_TYPES_POSTPAID = new HashMap<String, String>();

        BUNDLE_TYPES_POSTPAID.put("1","5 MB");
        BUNDLE_TYPES_POSTPAID.put("2","10 MB");
        BUNDLE_TYPES_POSTPAID.put("3","80 MB");
        BUNDLE_TYPES_POSTPAID.put("4","150 MB");
        BUNDLE_TYPES_POSTPAID.put("5","320 MB");
        BUNDLE_TYPES_POSTPAID.put("6","800 MB");
        BUNDLE_TYPES_POSTPAID.put("7","2000 MB");
        BUNDLE_TYPES_POSTPAID.put("8","4000 MB");
    }

    static {
        DATA_BUNDLES_AMOUNT_TO_TYPE = new HashMap<String, String>();

        DATA_BUNDLES_AMOUNT_TO_TYPE.put("0.50","1");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("1", "2");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("3", "3");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("5", "4");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("10", "5");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("20", "6");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("45", "7");
        DATA_BUNDLES_AMOUNT_TO_TYPE.put("75", "8");
    }

    static {
        DATA_BUNDLES = new HashMap<String, DataBundle>();
        DATA_BUNDLES.put("1", new DataBundle(   "1", "1= 50c    for 5MB", new BigDecimal("5.00"), new BigDecimal("0.108"), new BigDecimal("0.50"), new BigDecimal("0.55")));
        DATA_BUNDLES.put("2", new DataBundle(   "2", "2= $1     for 10MB", new BigDecimal("10.00"), new BigDecimal("0.10"), new BigDecimal("1.00"), new BigDecimal("1.10")));
        DATA_BUNDLES.put("3", new DataBundle(   "3", "3= $3.00  for 80MB", new BigDecimal("80.00"), new BigDecimal("0.08"), new BigDecimal("3.00"), new BigDecimal("8.80")));
        DATA_BUNDLES.put("4", new DataBundle(   "4", "5= $5.00  for 150MB", new BigDecimal("150.00"), new BigDecimal("0.08"), new BigDecimal("5.00"), new BigDecimal("16.50")));
        DATA_BUNDLES.put("5", new DataBundle(   "5", "5= $10.00 for 160MB", new BigDecimal("160.00"), new BigDecimal("0.06"), new BigDecimal("10.00"), new BigDecimal("17.60")));
        DATA_BUNDLES.put("6", new DataBundle(   "6", "6= $20.00 for 400MB", new BigDecimal("400.00"), new BigDecimal("0.05"), new BigDecimal("20.00"), new BigDecimal("44.00")));
        DATA_BUNDLES.put("7", new DataBundle(   "7", "8= $45.00 for 1000MB", new BigDecimal("1000.00"), new BigDecimal("0.05"), new BigDecimal("45.00"), new BigDecimal("110.00")));
        DATA_BUNDLES.put("8", new DataBundle(   "8", "8= $75.00 for 2000MB", new BigDecimal("2000.00"), new BigDecimal("0.04"), new BigDecimal("75.00"), new BigDecimal("220.00")));
    }

    public static String getUssdMessagePrefix(Boolean terminateSession, Integer sessionId){
        return ( terminateSession ? "81" : "72") + " " + sessionId + ( terminateSession ? "" : " " + USSD_TIMEOUT ) + " 0 ";
    }

    protected Boolean isInitialDial( String payload ) {
        return payload.split(" ").length < 7;
    }

    protected Integer getSessionId( String payload) {
        return new Integer( payload.split(" ")[1] );
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

    protected String getDataBundlePrices() {
        return  "Select bundle 1 to 8\n" +
                "1= 50c for 5MB\n" +
                "2= $1  for 10MB\n" +
                "3= $3  for 80MB\n" +
                "4= $5  for 150MB\n" +
                "5= $10 for 320MB\n" +
                "6= $20 for 800MB\n" +
                "6= $45 for 2GB\n" +
                "7= $75 for 4GB\n";
    }

    protected String getBundleSizeFor(String bundleType) {
        return getBundleSizeFor(bundleType, Boolean.FALSE);
    }

    protected String getBundleSizeFor(String bundleType, Boolean isPostpaid) {
        return isPostpaid ? BUNDLE_TYPES_POSTPAID.get(bundleType) : BUNDLE_TYPES.get(bundleType);
    }

    protected DataBundle getDataBundleByType(String bundleType) {
        return DATA_BUNDLES.get( bundleType );
    }

    public static String formatMobileNumber( String mobileNumber ) {

        try {
            mobileNumber = mobileNumber.replaceAll(" ", "");
        } catch (Exception e) {
        }
        byte len = (byte) mobileNumber.length();
        switch (len) {
            case 8:
                if (mobileNumber.startsWith("23")) {
                    break;
                }
            case 9:
                if (mobileNumber.startsWith("023") || mobileNumber.startsWith("73")) {
                    break;
                }
            case 10:
                if (mobileNumber.startsWith("073")) {
                    break;
                }
            case 11:
                if (mobileNumber.startsWith("26323")) {
                    break;
                }
            case 12:
                if (mobileNumber.startsWith("26373")) {
                    break;
                }
            default:
                return null;
        }
        if (mobileNumber.startsWith("26373")) {
            return mobileNumber;
        } else {
            return "26373" + mobileNumber.substring(len - 7);
        }
    }
    public static String shortFormat( String mobileNumber ) {
        return "0" + mobileNumber.substring(3);
    }
}

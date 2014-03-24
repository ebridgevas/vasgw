package com.ebridgevas.util;

import com.ebridgevas.model.ServiceCommand;
import com.ebridgevas.model.UserSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceCommandParser {

    private Map<String, UserSession> userSessions;

    public final static BigDecimal MINIMUM_AIRTIME_TRANSER;
    public final static BigDecimal MAXIMUM_AIRTIME_TRANSER;

    static {
        MINIMUM_AIRTIME_TRANSER = new BigDecimal(1.00);
        MAXIMUM_AIRTIME_TRANSER = new BigDecimal(100.00);
    }

    public static ServiceCommand parsePayload(String sourceId, String destinationId, String payload, UserSession session) throws Exception {

        if (payload == null) {
            throw new Exception("Invalid command. Send Help to 33350");
        }

        payload = payload.trim().toLowerCase();

        if (payload.startsWith("bun")) {

             /* Check if short code is meant for balance transfer. */
            if (! "33073".equals(destinationId) ) {
                throw new Exception("Please send Data Bundle Purchase Requests to 33073 only.");
            }
            return ServiceCommand.DATA_BUNDLE_PRICE_LISTING;
        } else if (payload.startsWith("bal")) {
            return ServiceCommand.BALANCE_ENQUIRY;
        } else if (payload.startsWith("h")) {
            return ServiceCommand.HELP;
        } else {

            String[] tokens = payload.trim().split("#");

            if (tokens.length == 1) {
                /* Is it a voucher. */
                if ( Pattern.compile("\\d{12}").matcher(payload).matches()
                        || Pattern.compile("\\d{16}").matcher(payload).matches() ) {

                    return ServiceCommand.VOUCHER_RECHARGE;
                } else if ((session != null) && (session.getServiceCommand() == ServiceCommand.DATA_BUNDLE_PURCHASE)) {

                     /* Check if short code is meant for balance transfer. */
                    if (! "33073".equals(destinationId) ) {
                        throw new Exception("Please send Data Bundle Purchase Requests to 33073 only.");
                    }

                    /* Bundle purchase product selection. */
                    if (Pattern.compile("[1234567]").matcher(payload).matches()) {
                        return ServiceCommand.DATA_BUNDLE_PURCHASE;
                    } else {
                        throw new Exception(
                                "INVALID SELECTION: Bundle does not exist. Please re-subscribe to any bundle 1-7 for cheaper rates");
                    }
                } else {
                    throw new Exception("Invalid command. Send Help to 33350");
                }
            } else if (tokens.length == 2) {

                String token1 = tokens[0];

                if ((token1 == null) || token1.isEmpty()) {
                    throw new Exception("Invalid command. Send Help to 33350");
                }
                token1 = token1.trim();
                token1 = token1.replaceAll(" ", "");

                /* Check if inter-account balance transfer.*/

                    /* Check if second parameter is a valid subscriber Id */
                String msisdn = MobileNumberFormatter.format(tokens[1].trim());
                if (msisdn == null) {
                    throw new Exception(
                            tokens[1] + " is not a valid mobile number");
                }

                /* Is it a voucher. */
                if ( Pattern.compile("\\d{12}").matcher(token1).matches()
                        || Pattern.compile("\\d{16}").matcher(token1).matches() ) {

                    return ServiceCommand.VOUCHER_RECHARGE;
                } else  {

//                    Boolean isTestLine = PrepaidAccountManager.TEST_LINES.contains(sourceId);

                    /* Check if first parameter is a valid amount for balance transfer. */
                    String s = token1.trim();
                    s = s.replaceAll("$", "");

                    BigDecimal amount = BigDecimal.ZERO;
                    try {
                        amount = new BigDecimal(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception(
                                "Invalid command. Please SMS help to 33350 ");
                    }

                    /* Check if short code is meant for balance transfer. */
                    if (! "33350".equals(destinationId) ) {
                        throw new Exception("Please send Airtime Transfer Requests to 33350 only.");
                    }

//                    BigDecimal minimumAirtimeTransfer = isTestLine ? new BigDecimal(0.25) :  MINIMUM_AIRTIME_TRANSER;
                    BigDecimal minimumAirtimeTransfer =  new BigDecimal(0.25);

                    if ( minimumAirtimeTransfer.compareTo(amount) > 0) {
                        throw new Exception(
//                        "Sorry minimum airtime transfer is $" + minimumAirtimeTransfer.setScale(2, RoundingMode.HALF_UP) + (isTestLine ? "and it costs $0.02 to transfer airtime." : ""));
                        "Sorry minimum airtime transfer is $" + minimumAirtimeTransfer.setScale(2, RoundingMode.HALF_UP) + "and it costs $0.02 to transfer airtime." );

                    }

                    if (MAXIMUM_AIRTIME_TRANSER.compareTo(amount) < 0) {
                        throw new Exception(
//                                "Sorry maximum airtime transfer is $100.00 " + ( isTestLine ? "and it costs $0.02 to transfer airtime." : "") );
                                "Sorry maximum airtime transfer is $100.00 and it costs $0.02 to transfer airtime." );
                    }

                    return ServiceCommand.BALANCE_TRANSFER;
                }
            } else {
                throw new Exception(
                        "Invalid command. Please SMS help to 33350 ");
            }
        }
    }
}

package com.ebridgevas.services.impl;

import com.ebridge.commons.processors.MobileDeviceCommandProcessor;

import java.util.Map;

/**
 * david@ebridgevas.com
 *
 */
public class MillenniumServiceCommandProcessor implements MobileDeviceCommandProcessor<String> {

    @Override
    public String process(Map<String, String[]> command) {
        return null;
    }

    public String process(String msisdn, String payload) {

        /*
        System.out.println("########## Received " + msisdn);
        MilleniumlModel txn = activeTxns.get(msisdn);



        if ((txn == null) || (payload == null)) {

            activeTxns.put(msisdn, new MilleniumlModel(msisdn, 0));
            System.out.println("########## Returned menu " + msisdn);
            return "Welcome to Telecel-Telecel Daily VOICE Bundles\nPlease select\n" +
                    "1. Subscribe\n" +
                    "2. Balance Enquiry\n" +
                    "3. UnSubscribe";
        } else {
            int level = txn.getLevel();
            if ((level == 0) && ("3".equals(payload.trim()))) {
                // opt out

                activeTxns.remove(msisdn);
                txn.unSubscribe(msisdn);
                return "- " + "Your request to unsubscribe from Telecel Daily Package will be confirmed via SMS shortly";
            } else if ((level == 0) && ("2".equals(payload.trim()))) {
                //balance enquiry
                activeTxns.remove(msisdn);

                return "- " + txn.balanceEnquiry(msisdn);
            } else if ((level == 0) && ("1".equals(payload.trim()))) {
                // subscription request
                activeTxns.remove(msisdn);
                activeTxns.put(msisdn, new MilleniumlModel(msisdn, 1));
                return "To Subscribe Please Select \n 1= $0.50 for 90mins\n 2= $1 for 200mins";

            } else if ((level == 1) && ("1".equals(payload.trim()))) {
                // subscribe
                txn.setOperation(Operation.ONE);
                activeTxns.remove(msisdn);
                return "- " + txn.purchaseBundle(msisdn);
            } else if ((level == 1) && ("2".equals(payload.trim()))) {
                txn.setOperation(Operation.TWO);
                activeTxns.remove(msisdn);
                return "- " + txn.purchaseBundle(msisdn);
            } else {
                //invalid selection
                activeTxns.remove(msisdn);
                activeTxns.put(msisdn, new MilleniumlModel(msisdn, 0));
                return "Welcome to Telecel-Telecel Daily VOICE Bundles\nPlease select\n" +
                        "1. Subscribe\n" +
                        "2. Balance Enquiry\n" +
                        "3. UnSubscribe";
            }
        }

        */

        return null;
    }
}

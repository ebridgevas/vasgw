package com.ebridgecommerce.sdp.business;

import com.ebridgecommerce.sdp.domain.TxnType;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/22/12
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberRegistrar {

    public void process(TxnType txnType, Map<String, String> data) {
        switch (txnType) {
            case INITIATE_SIM_REGISTRATION:
                initiateSimRegistration(txnType, data);
                break;
            case SIM_REGISTRATION:
                simRegistration(txnType, data);
                break;
        }
    }

    private void initiateSimRegistration(TxnType txnType, Map<String, String> data) {
        // subscriber lookup
        //   not found
        //     db.initiateSimRegistration
        //     ussd response
    }

    private void simRegistration(TxnType txnType, Map<String, String> data) {
        // create subscriber

        //  ussd response
    }
}

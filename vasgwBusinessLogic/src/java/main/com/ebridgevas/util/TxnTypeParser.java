package com.ebridgevas.util;

import zw.co.ebridge.domain.TxnType;

/**
 * @author david@ebridgevas.com
 *
 */
public class TxnTypeParser {

    public static TxnType parse(String txnType) {
        if (txnType.equals("DATA_BUNDLE_PURCHASE")) {
            return TxnType.DATA_BUNDLE_PURCHASE;
        } else {
            return null;
        }
    }
}

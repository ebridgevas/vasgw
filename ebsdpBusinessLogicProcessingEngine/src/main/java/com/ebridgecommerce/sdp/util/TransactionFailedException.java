package com.ebridgecommerce.sdp.util;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionFailedException extends Exception {
    public TransactionFailedException(String message) {
        super(message);
    }
}

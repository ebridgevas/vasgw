package com.ebridgecommerce.sdp.util;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/25/12
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberPhysicalAddressValidator {

    public static boolean isValid(String name) throws BlankNameException, InvalidNameException {
        if (name == null || name.trim().length() == 0) {
            throw new BlankNameException("Empty name");
        }
        return true;
    }
}

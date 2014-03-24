package com.ebridgecommerce.sdp.util;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/27/12
 * Time: 7:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Validator {
    boolean isValid(String name) throws BlankNameException, InvalidNameException;
}

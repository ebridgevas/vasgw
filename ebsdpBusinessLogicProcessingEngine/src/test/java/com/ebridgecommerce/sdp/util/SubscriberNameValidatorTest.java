package com.ebridgecommerce.sdp.util;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/27/12
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberNameValidatorTest {
    public static void main(String[] args) {
        SubscriberNameValidator test = new SubscriberNameValidator();
        try {
            test.isValid("xxxx");
        } catch (BlankNameException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidNameException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

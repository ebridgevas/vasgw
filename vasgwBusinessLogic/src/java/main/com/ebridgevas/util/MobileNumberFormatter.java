package com.ebridgevas.util;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 8/8/13
 * Time: 3:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class MobileNumberFormatter {

    private static String errorMessage;

    // 07369729644
    // 263737379760
    // 0737379760
    public static String format( String mobileNumber ) {
        try { mobileNumber = mobileNumber.replaceAll(" ", "");} catch(Exception e){}
        byte len = (byte)mobileNumber.length();
        switch ( len ) {
            case 8: if ( mobileNumber.startsWith("23") ) { break; }
            case 9: if ( mobileNumber.startsWith("023") || mobileNumber.startsWith("73") ) { break; }
            case 10: if ( mobileNumber.startsWith("073") ) { break; }
            case 11: if ( mobileNumber.startsWith("26323") ) { break; }
            case 12: if ( mobileNumber.startsWith("26373") ) { break; }
            default:
                errorMessage = "Mobile number " + mobileNumber + " length is invalid.";
                return null;
        }
        if (mobileNumber.startsWith("26373") ) {
            return mobileNumber;
        } else {
            return "26373" + mobileNumber.substring( len - 7 );
        }
    }
    public static String shortFormat( String mobileNumber ) {
        return "0" + mobileNumber.substring(3);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static void main(String[] args) {
        MobileNumberFormatter formatter = new MobileNumberFormatter();
        formatter.format("07369729644");
        System.out.println(formatter.getErrorMessage());
    }
}


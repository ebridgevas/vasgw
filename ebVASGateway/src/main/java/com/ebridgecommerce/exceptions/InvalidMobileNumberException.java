package com.ebridgecommerce.exceptions;

/**
*
* @author DaTekeshe
*/
public class InvalidMobileNumberException extends Exception {
	
	private static final long serialVersionUID = 9031541682243853757L;

	public InvalidMobileNumberException( String message ) {
       super( message );
   }
}
package com.ebridgecommerce.exceptions;

/**
*
* @author DaTekeshe
*/
public class TransactionFailedException extends Exception {

	private static final long serialVersionUID = 8569370664324254931L;

	public TransactionFailedException( String message ) {
       super( message );
   }
}

package com.ebridgecommerce.exceptions;

public class SubscriberAccountNotActiveException extends Exception {

	private static final long serialVersionUID = 3941411069983240383L;

	public SubscriberAccountNotActiveException(String message) {
		super(message);
	}
}

package com.ebridgecommerce.exceptions;

public class Util {
	public void sleep(Long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException ex) {
		}
	}
}

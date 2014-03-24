/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebridge.sdp.vas.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author arthurmatsaudza@gmail.com
 */
public class KeyGenerator {
    
    public static String generateEntityId() {
		Long l = System.currentTimeMillis();
		Integer randomNumber = (int)(Math.random() * 100000);
		String entityId = l.toString() + randomNumber;

		return entityId;
	}

	public static String generateDealerCode() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Integer year = now.get(Calendar.YEAR);
		String firstPart = "DC"
                        + year.toString().substring(2);

		Integer randomNumber = (int)(Math.random() * 1000);
		while (randomNumber < 100) {
			randomNumber = (int)(Math.random() * 1000);
		}

		String array = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int index = (int)(Math.random() * 10);
		while (index > 25) {
			index = (int)(Math.random() * 10);
		}
		char lastChar = array.charAt(index);

		return firstPart + randomNumber + lastChar;

}
        
        public static String generateStockTransactionID() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Integer year = now.get(Calendar.YEAR);
		String firstPart = "ST"
                        + year.toString().substring(2);

		Integer randomNumber = (int)(Math.random() * 1000);
		while (randomNumber < 100) {
			randomNumber = (int)(Math.random() * 1000);
		}

		String array = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int index = (int)(Math.random() * 10);
		while (index > 25) {
			index = (int)(Math.random() * 10);
		}
		char lastChar = array.charAt(index);

		return firstPart + randomNumber + lastChar;

}
        
       


    
}


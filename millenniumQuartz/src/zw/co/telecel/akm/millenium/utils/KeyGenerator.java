/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author matsaudzaa
 */
public class KeyGenerator {
    
    public static String generateJobId(String msisdn){
        return msisdn+"J"+KeyGenerator.generateEntityId();
    }
    public static String generateTriggerId(String msisdn){
        return msisdn+"T"+KeyGenerator.generateEntityId();
    }
    public static String generateReminder6JobId(String msisdn){
        return msisdn+"RJ6"+KeyGenerator.generateEntityId();
    }
    public static String generateReminder6TriggerId(String msisdn){
        return msisdn+"RT6"+KeyGenerator.generateEntityId();
    }
    public static String generateReminder7JobId(String msisdn){
        return msisdn+"RJ7"+KeyGenerator.generateEntityId();
    }
    public static String generateReminder7TriggerId(String msisdn){
        return msisdn+"RT7"+KeyGenerator.generateEntityId();
    }
     public static String generateEntityId() {
		Long l = System.currentTimeMillis();
		Integer randomNumber = (int)(Math.random() * 1000);
		String entityId = l.toString() + randomNumber;

		return entityId;
	}
    
    public static String generateTransactionID() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Integer year = now.get(Calendar.YEAR);
		String firstPart = "RE"
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

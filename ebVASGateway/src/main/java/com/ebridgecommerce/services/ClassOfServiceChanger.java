package com.ebridgecommerce.services;

import com.zw.ebridge.vas.prototype.db.DBAdapter;
import org.joda.time.DateTime;
import com.ebridgecommerce.db.DBAdapter;
import com.ebridgecommerce.prepaid.client.PPSClient;
import com.ebridgecommerce.exceptions.TransactionFailedException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class ClassOfServiceChanger {

	public static void main(String[] args){
		changeClassOfServiceTo(args[0]);		
	}
	
	public static void changeClassOfServiceTo(String newCOS){
		
		PPSClient ppsClient = PPSClient.getInstance();
		
		System.out.println(new Date() + ":: COS = " + newCOS);
		
		Statement stmt = null;
		try {
			stmt = DBAdapter.getConnection().createStatement();
			for( String msisdn : DBAdapter.getRegister(stmt)) {
				SubscriberInfo subscriberInfo = DBAdapter.getAccumulatorCosForSubscriber(stmt, null, msisdn);
				if (subscriberInfo == null) {
					continue;
				}
				if ("FF_COS".equalsIgnoreCase(newCOS.trim())){
					String time = ppsClient.retrieveMonetaryTransferRecord(msisdn);
					if (time != null) {
						if (new DateTime(Long.parseLong(time)).getDayOfYear() == new DateTime().getDayOfYear()) {	
							String bonusCos = subscriberInfo.getBonusCos();
							System.out.println(new Date() + ":: Changing COS for " + msisdn + " to " + bonusCos != null ? bonusCos : "FF_COS");
							try {
								ppsClient.setClassOfService(msisdn, bonusCos != null ? bonusCos : "FF_COS");
							} catch(TransactionFailedException e){
									System.out.println(new Date() + ":: Error : " + e.getMessage());	
							}
						}
					}
				} else {
					try {
						System.out.println(new Date() + ":: Changing COS for " + msisdn + " to " + subscriberInfo.getAccumulatorCos());
						ppsClient.setClassOfService(msisdn, subscriberInfo != null ? subscriberInfo.getAccumulatorCos() : newCOS);
					} catch (NullPointerException e) {
						System.out.println(new Date() + ":: Error : " + e.getMessage());
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try{stmt.close();} catch(Exception e1){}
		}
		
	}
}

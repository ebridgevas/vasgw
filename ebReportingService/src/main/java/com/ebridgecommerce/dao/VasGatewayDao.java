package com.ebridgecommerce.dao;

import com.ebridgecommerce.domain.TransactionDTO;
import com.ebridgecommerce.dto.DailyTransactionStatsDTO;
import com.ebridgecommerce.dto.Txn;
import com.zw.ebridge.domain.DataBundlePrice;
import com.zw.ebridge.domain.SubscriberInfo;
import org.joda.time.DateTime;
import zw.co.ebridge.shared.dto.ClassOfServiceDTO;
import zw.co.ebridge.shared.dto.HourlyStatsDTO;
import zw.co.ebridge.shared.dto.StatsDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class VasGatewayDao {

	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	private static final SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

	public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver").newInstance();
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/vas", "postgres", "postgres");
	}

	public static Map<Integer, DataBundlePrice> getDataBundlePriceList(Boolean isTest) {
		Map<Integer, DataBundlePrice> prices = new HashMap<Integer, DataBundlePrice>();
			String sql = "";
			sql += " SELECT bundle_id, bundle_narration, bundle_narration_sms, bundle_price, bundle_size, data_rate, debit, credit ";	
			sql += " FROM " + ( isTest ?  " bundle_prices_test " :  " bundle_prices ");
			sql += " ORDER BY bundle_id ";
			System.out.println("########## " + sql);
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = getConnection().createStatement();
				rs = stmt.executeQuery(sql);
				int count = 0;
				while (rs.next()){
					System.out.println("########## " + count);
					++count;
					prices.put(
							rs.getInt("bundle_id"), 
							new DataBundlePrice(
									rs.getInt("bundle_id"), 
									rs.getString("bundle_narration"),
									rs.getString("bundle_narration_sms"), 
									rs.getBigDecimal("bundle_price"), 
									rs.getBigDecimal("bundle_size"), 
									rs.getBigDecimal("data_rate"), 
									rs.getBigDecimal("debit"), 
									rs.getBigDecimal("credit")
								)
						);
				}
				return prices;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error reading price list - " + e.getMessage());
				return null;
			} finally {
				try {stmt.close();}catch(Exception e){}
				try {rs.close();}catch(Exception e){}
			}
			
	}
	
	public static ArrayList<String> getTestLines(){
		ArrayList<String> testLines = new ArrayList<String>();
		String sql = "";
		sql += " SELECT msisdn ";	
		sql += " FROM test_lines ";
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()){
				testLines.add(rs.getString("msisdn"));
			}
			return testLines;
		} catch (Exception e) {
			System.out.println("Error reading price list - " + e.getMessage());
			return null;
		} finally {
			try {stmt.close();}catch(Exception e){}
			try {rs.close();}catch(Exception e){}
		}		
		
	}

	public static void log(Statement stmt, String uuid, String sourceId, String destinationId, String serviceCommand, BigDecimal amount, String status ){
		String sql = "INSERT INTO txns(uuid, subscriber_id, beneficiary_id, txn_type, amount, status, txn_date_time, txn_date, txn_hour ) ";
		sql += " VALUES('" + uuid + "','" + sourceId + "','" + destinationId + "','" + serviceCommand + "'," + amount + ",'" + status + "',";
		sql += "'" + dateTimeFormat.format(new Date()) + "','" + dateFormat.format(new Date()) + "','" + dateHourFormat.format(new Date()) + "')";
		try {
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
	}
	
	public static void register(Statement stmt, String uuid, String msisdn, boolean register ){
		
		ResultSet rs = null;
		try {
			
			/* Friend already exist? */				
			String sql = "SELECT COUNT(*) AS count FROM register WHERE msisdn = '" + msisdn + "'";						
			rs = stmt.executeQuery(sql);
			rs.next();
			if (rs.getInt("count") > 0) {
				  sql = "UPDATE register SET status = " + (register ? "'Active'" : "'InActive'") + ", date_registered=now() " 
		 						+ " WHERE msisdn = '" + msisdn + "'";
				  stmt.executeUpdate(sql);
 		  } else {
 		  	 if (register) {
 					 	sql  = "INSERT INTO register(msisdn, status, date_registered ) ";
 		  	 		sql += " VALUES('" + msisdn + "','Active',now()) ";
 		  	 	  System.out.println("####-####" + sql);
 		  	  	int r = stmt.executeUpdate(sql);
 		  	  	System.out.println("####-####" + sql + " ### result = " + r);
 		  	 }
			 } 
		} catch (SQLException e) {
			System.out.println("Error registering " + msisdn + " ::: " + e.getMessage());
		} finally {
			try{rs.close();} catch(Exception e1){}
		}
	}

	public static SubscriberInfo getAccumulatorCosForSubscriber(Statement stmt, String uuid, String msisdn ){
		
		ResultSet rs = null;
		try {
			
			/* Friend already exist? */				
			String sql = "SELECT thresh_hold, accumulator_cos, bonus_cos, thresh_hold, charge FROM band_price b, arpu_bands a WHERE b.band_id = a.band_id AND a.msisdn = '" + msisdn + "'";						
			rs = stmt.executeQuery(sql);
			if ( rs.next() ) {
				return new SubscriberInfo( 	msisdn,
																		rs.getString("accumulator_cos"), 
																		rs.getString("bonus_cos"),
																		rs.getBigDecimal("thresh_hold"),
																		rs.getBigDecimal("charge"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			System.out.println("Error reading Accumultor COS for " + msisdn + " ::: " + e.getMessage());
			return null;
		} finally {
			try{rs.close();} catch(Exception e1){}
		}
	}
	
	public static String getBonusCosForSubscriber(Statement stmt, String uuid, String msisdn ){
		
		ResultSet rs = null;
		try {
			
			/* Friend already exist? */				
			String sql = "SELECT bonus_cos FROM band_price b, arpu_bands a WHERE b.band_id = a.band_id AND a.msisdn = '" + msisdn + "'";						
			rs = stmt.executeQuery(sql);
			if ( rs.next() ) {
				return rs.getString("bonus_cos");
			} else {
				return null;
			}
		} catch (SQLException e) {
			System.out.println("Error reading Accumultor COS for " + msisdn + " ::: " + e.getMessage());
			return null;
		} finally {
			try{rs.close();} catch(Exception e1){}
		}
	}
	
	public static void logMTR(Statement stmt, String uuid, String sourceId ){
		String sql = "INSERT INTO mtr(uuid, subscriber_id, txn_date_time, txn_date, txn_hour ) ";
		sql += " VALUES('" + uuid + "','" + sourceId + "',";
		sql += "'" + dateTimeFormat.format(new Date()) + "','" + dateFormat.format(new Date()) + "','" + dateHourFormat.format(new Date()) + "')";
		try {
//			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
	}

	public static List<String> getRegister(Statement stmt){
		
		List<String> registry = new ArrayList<String>();	
		
		ResultSet rs = null;
		try {
						
			String sql = "SELECT msisdn FROM register WHERE status = 'Active'";						
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				 registry.add(rs.getString("msisdn"));
			}
			return registry;
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage());
			return registry;
		} finally {
			try{rs.close();} catch(Exception e1){}
		}
	}
	
	public static String logFriendChange(Statement stmt, String uuid, String subscriberId, String ffSubscriberId){
		
		ResultSet rs = null;
		try {
			
			/* Friend already exist? */				
			String sql = "SELECT count, txn_month, today_count, txn_date FROM phone_book WHERE subscriber_id = '" + subscriberId + "'";						
			rs = stmt.executeQuery(sql);
			 if (rs.next()) {
				  boolean thisMonth = yearMonthFormat.format(new Date()).equals(rs.getString("txn_month"));
				  boolean today = new DateTime(rs.getDate("txn_date").getTime()).isAfter(new DateTime().minusDays(1));
				  System.out.println("today : " + today);
				  
				  if ( (rs.getInt("count") < 2) || !thisMonth ){
		 				sql = "UPDATE phone_book SET ff_subscriber_id = '" + ffSubscriberId 
		 						+ "', count = " + (thisMonth ? 2 : 1) 
		 						+ ", today_count = 1 "
		 						+ " WHERE subscriber_id = '" + subscriberId + "'";
		 				stmt.executeUpdate(sql);
			 			return "0";
				  } else if (today) {
				  	if (rs.getInt("today_count") < 2) {
				 				sql = "UPDATE phone_book SET ff_subscriber_id = '" + ffSubscriberId + "', "
				 						+ " today_count = today_count + 1 "
				 						+ " WHERE subscriber_id = '" + subscriberId + "'";
				 				stmt.executeUpdate(sql);
					 			return "0";				  			
				  	} else {
				  			return "You have already changed your friend today.";
				  	}
				  } else {
			 			return "You have already changed your friend twice this month.";
			 		}
			 } else {			 
				 /* Add friend */
				 sql  = "INSERT INTO phone_book(uuid, subscriber_id, ff_subscriber_id, count, today_count, txn_date_time, txn_date, txn_hour, txn_month ) ";
				 sql += " VALUES('" + uuid + "','" + subscriberId + "','" + ffSubscriberId + "',1,1,"  ;
				 sql += "'" + dateTimeFormat.format(new Date()) + "','" + dateFormat.format(new Date()) + "','" + dateHourFormat.format(new Date()) + "','" + yearMonthFormat.format(new Date()) + "')";
				 System.out.println(sql);
				 stmt.executeUpdate(sql);
				 return "0";
			 }
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage());
			return "-1";
		} finally {
			try{rs.close();} catch(Exception e1){}
		}
	}
	
	public static String isFriendChangePermitted(Statement stmt, String uuid, String subscriberId ){
		
		ResultSet rs = null;
		try {
			
			/* Friend already exist? */				
			String sql = "SELECT count, txn_month, today_count, txn_date FROM phone_book WHERE subscriber_id = '" + subscriberId + "'";						
			rs = stmt.executeQuery(sql);
			 if (rs.next()) {
				  boolean thisMonth = yearMonthFormat.format(new Date()).equals(rs.getString("txn_month"));
				  boolean today = new DateTime(rs.getDate("txn_date").getTime()).isAfter(new DateTime().minusDays(1));
				 
				  if((rs.getInt("count") < 2) || !thisMonth) {
				  	return "0";
				  } else {
				  	 if (today) {
						  	return (rs.getInt("today_count") < 2) ? "0" : "You have already changed your friend today.";
						 } else {
							 	return "You have already changed your friend twice this month.";
						 }
				  }
			 } else {			 
				 return "0";
			 }
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage());
			return "-1";
		} finally {
			try{rs.close();} catch(Exception e1){}
		}
	}
	
	public static boolean newMtrExists(Statement stmt, String uuid, String sourceId ){
		String sql = "SELECT COUNT(*) FROM mtr WHERE uuid = '" + uuid + "' AND subscriber_id = '" + sourceId + "'";
		ResultSet rs = null;
		try {
//			System.out.println(sql);
			 rs = stmt.executeQuery(sql);
			 return rs.next() ? rs.getInt(1) > 0 : false;			 
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return false;
		} finally {
			try {rs.close();} catch(Exception e){}
		}
	}
	
	public static boolean mtrExists(Statement stmt, String uuid, String sourceId ){
		String sql = "SELECT MAX(txn_date) AS txn_date FROM mtr WHERE subscriber_id = '" + sourceId + "'";
		ResultSet rs = null;
		try {
//			System.out.println(sql);
			 rs = stmt.executeQuery(sql);
			 if ( rs.next() ) {
				  return new DateTime(rs.getDate("txn_date").getTime()).isAfter(new DateTime().minusDays(8));			 
			 } else {
				 return false;
			 }
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return false;
		} finally {
			try {rs.close();} catch(Exception e){}
		}
	}
	
	public static void log(Statement stmt,
			String uuid, 
			String sourceId,
			String destinationId, 
			String serviceCommand, 
			BigDecimal amount, 
			String status, 
			String statusCode, 
			Date expiryDate, 
			String shortCode){
		
		if (expiryDate != null) {
			dateTimeFormat.format(expiryDate);
		}
		
		String sql = "INSERT INTO txns(uuid, subscriber_id, beneficiary_id, txn_type, amount, status, status_code, txn_date_time, txn_date, txn_hour, short_code ) ";
		sql += " VALUES('" + uuid + "','" + sourceId + "','" + destinationId + "','" + serviceCommand + "'," + amount + ",'" + status + "','" + statusCode + "',";		
		sql += "'" + dateTimeFormat.format(new Date()) + "','" + dateFormat.format(new Date()) + "','" + dateHourFormat.format(new Date()) + "','" + shortCode + "')";
		try {
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
	}

	public static void safClassOfService(Statement stmt, String promotionId, String uuid, String subscriberMsisdn, String originalCosName, String newCosName){
		String sql = "INSERT INTO cos_saf(promotion_id, uuid, subscriber_msisdn, original_cos_name, new_cos_name, date_change, status ) ";
		sql += " VALUES('" + promotionId + "','" + uuid + "','"+ subscriberMsisdn + "','" + originalCosName + "','" + newCosName + "',now(),'Active')";
		try {
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
	}
	
	public static void logClassOfServiceQuery(Statement stmt, String uuid, String serviceCommand, String subscriberMsisdn, String originalCosName, String newCosName){
		String sql = "INSERT INTO cos_log(uuid, service_command, subscriber_msisdn, original_cos_name, new_cos_name, date_change, status ) ";
		sql += " VALUES('"+ serviceCommand + "','" + subscriberMsisdn + "','" + originalCosName + "','" + newCosName + "',now(),'OK')";
		try {
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
	}
	
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
	
	public static ClassOfServiceDTO getActivePromotion(Statement stmt){
		String sql = "SELECT * FROM promotions WHERE now() BETWEEN promotion_start_date AND promotion_end_date";
		sql += " AND '" + timeFormat.format(new Date()) + "' BETWEEN window_start_time AND window_end_time ";
		sql += " AND status = 'Active'";
		
		try {
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				ClassOfServiceDTO cos = new ClassOfServiceDTO();
				cos.setPromotionId(rs.getString("uuid"));
				return cos;
			} else {
				return null;
			}			
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return null;
		}

	}
	
	public static List<ClassOfServiceDTO> getSafedClassOfService(Statement stmt, String promotionId, Integer maximum){
		List<ClassOfServiceDTO> result = new ArrayList<ClassOfServiceDTO>();
		String sql = "SELECT * FROM cos_saf WHERE promotion_id = '" + promotionId + " AND status = 'Active' LIMIT " + maximum;		
		try {
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				result.add(new ClassOfServiceDTO(rs.getString("promotion_id"), rs.getString("uuid"), rs.getString("subscriber_msisdn"), rs.getString("original_cos_name"), rs.getString("new_cos_name")));
			}			
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}		
		return result;
	}
	
	public static void updateSafedClassOfService(Statement stmt, List<ClassOfServiceDTO> coss){
		for (ClassOfServiceDTO cos : coss) {
			try {
				String sql = "UDATE cos_saf SET status = 'Closed' WHERE promotion_id = '" + cos.getPromotionId() + "' AND uuid = '" + cos.getUuid() + "' AND subscriber_msisdn = '" + cos.getSubscriberMsisdn() + "'";		
				System.out.println();
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println("Error : " + e.getMessage());
			}	
		}		
	}
	
	public static String getIMSI(Statement stmt, String mobileNumber) {
		String sql = "SELECT imsi FROM hlrdump WHERE msisdn = '" + mobileNumber + "'";
		sql +=       " AND category = 'postpaid'";
		try {
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next()) {
				return rs.getString("imsi");
			} else {
				return null;
			}
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
		return null;
	}

	public static void createCDR(Statement stmt, String mobileNumber, String imsi, BigDecimal amount) {
		String sql = "INSERT INTO cdr(uuid, imsi, msisdn, category, amount, cdr_date, status ) ";
		sql += " VALUES('" + System.currentTimeMillis() + "','" + imsi + "','" + mobileNumber + "','postpaid'," + amount + ",now(),'Open')";
		try {
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
		}
		
	}
	
	public static List<StatsDTO> findTransactionSummary(Date startDate, Date endDate) {


		List<StatsDTO> result = new ArrayList<StatsDTO>();

//        String sql =
//                "select txn_date, " +
//                        " ifnull(( select count(*) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchase'),0) as successfulSubscriptionCount," +
//                        " ifnull(( select sum(amount) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchase'),0.00) as revenue," +
//                        " ifnull(( select count(*) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchaseError'),0) as rejectedSubscriptionCount" +
//                        " from txns t " +
//                        " where t.txn_date >= '" + new java.sql.Date(startDate.getTime()) + "'" +
//                        " and t.txn_date <= '" + new java.sql.Date(endDate.getTime()) + "'" +
//                        " and (txn_type = 'DataBundlePurchase'" +
//                        " or txn_type = 'DataBundlePurchaseError' )" +
//                        " group by txn_date" +
//                        " order by txn_date asc";

//		String sql =
//				"select txn_date, " +
//				" ifnull(( select count(*) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchase'),0) as successfulSubscriptionCount," +
//				" ifnull(( select sum(amount) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchase'),0.00) as revenue," +
//				" ifnull(( select count(*) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchaseError'),0) as rejectedSubscriptionCount" +
//				" from txns t " +
//				" where t.txn_date >= '" + new java.sql.Date(startDate.getTime()) + "'" +
//				" and t.txn_date <= '" + new java.sql.Date(endDate.getTime()) + "'" +
//				" and (txn_type = 'DataBundlePurchase'" +
//				" or txn_type = 'DataBundlePurchaseError' )" +
//				" group by txn_date" +
//				" order by txn_date asc";

//        String sql =
//                " select TXNDATE, " +
//
//                     "coalesce((" +
//                            "select count(*) " +
//                            " from TXNS st " +
//                            "where st.TXNDATE = t.TXNDATE and st.STATUS='000' and TXNTYPE = 'DataBundlePurchase'),1) as successfulSubscriptionCount, " +
//
//                    "coalesce(( " +
//                            "select SUM(AMOUNT) " +
//                            "from TXNS st " +
//                            "where st.TXNDATE = t.TXNDATE and st.STATUS='000' and TXNTYPE = 'DataBundlePurchase'),1.00) as revenue, " +
//
//                    "coalesce(( " +
//                            "select count(*) " +
//                            "from TXNS st " +
//                            "where st.TXNDATE = t.TXNDATE and st.STATUS <> '000') and TXNTYPE = 'DataBundlePurchase',1) as rejectedSubscriptionCount " +
//
//                "from TXNS t  " +
//                "where t.TXNDATE >= '" +  new java.sql.Date(startDate.getTime()) + "' " +
//                "and t.TXNDATE <= '" + new java.sql.Date(endDate.getTime()) + "' " +
//                "and TXNTYPE = 'DataBundlePurchase' " +
//                "group by TXNDATE " +
//                "order by TXNDATE asc ";

        String sql =
                " select txndate, " +

                        "(" +
                        "select count(*) " +
                        " from TXNS st " +
                        "where st.TXNDATE = t.TXNDATE and st.STATUS='000' and TXNTYPE = 'DataBundlePurchase') as successfulSubscriptionCount, " +

                        "( " +
                        "select SUM(AMOUNT) " +
                        "from TXNS st " +
                        "where st.TXNDATE = t.TXNDATE and st.STATUS='000' and TXNTYPE = 'DataBundlePurchase') as revenue, " +

                        "1  as rejectedSubscriptionCount " +

                        "from TXNS t  " +
                        "where t.TXNDATE >= '" +  new java.sql.Date(startDate.getTime()) + "' " +
                        "and t.TXNDATE <= '" + new java.sql.Date(endDate.getTime()) + "' " +
                        "and TXNTYPE = 'DataBundlePurchase' " +
                        "group by txndate " +
                        "order by txndate asc ";

        Connection conn = null;
        Statement stmt = null;
        try {
			conn = getConnection();
            stmt = conn.createStatement();
            System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next()) {
				StatsDTO item = new StatsDTO();
				item.setTransactionDate(rs.getDate("TXNDATE"));
				item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
				item.setRejectedSubscriptionCount(rs.getLong("rejectedSubscriptionCount"));
				item.setRevenue(rs.getBigDecimal("revenue"));
				result.add(item);
			}
			return result;
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return null;
		} finally {
            try {stmt.close();} catch (Exception e1){}
            try {conn.close();} catch (Exception e1){}
        }
	}

    public static List<Txn> getTxns() {

        List<Txn> txns = new ArrayList<Txn>();

        /* Determin the first day of month and yesterday. */

        DateTime yesterday = new DateTime().minusDays(1);
        Date fromDate = yesterday.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
        Date toDate = yesterday.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate();

        String sql = "" +
           " SELECT substring(transaction_date::text,1,10) AS txndate, source_id, status_code, count(*) as txnsCount, " +
                "   sum(amount) AS txnsValue " +
           "   FROM txns " +
           "  WHERE transaction_type = 'AirtimeTransfer' " +
           "    AND transaction_date between '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fromDate) + "' " +
           "    AND '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(toDate) + "'" +
           "  GROUP BY txnDate, source_id,status_code " +
           "  ORDER BY txnDate";

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next()) {
                txns.add(
                        new Txn(
                                rs.getDate("txndate"),
                                rs.getString("source_id"),
                                rs.getString("status_code"),
                                rs.getInt("txnsCount"),
                                rs.getBigDecimal("txnsValue"),
                                new BigDecimal(0.02))
                );
            }
            return txns;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
            return null;
        } finally {
            try {stmt.close();} catch (Exception e1){}
            try {conn.close();} catch (Exception e1){}
        }
    }

    public static Map<String, DailyTransactionStatsDTO> collateTxns() {
        Map<String, DailyTransactionStatsDTO> map = new HashMap<String, DailyTransactionStatsDTO>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<Txn> txns = getTxns();
        System.out.println("txns.size() : " + txns.size());

        for (Txn txn : txns ) {
            DailyTransactionStatsDTO stats = map.get(formatter.format(txn.getTxnDate()));
            if ( stats == null) {
                if ( "000".equals(txn.getStatusCode()) ) {
                    stats = new DailyTransactionStatsDTO(
                                    txn.getTxnCount(),    // Success count
                                    0,                    // Failed count
                                    txn.getTxnValue(),    // Txn Value
                                    txn.getTxnCharge().multiply( new BigDecimal(txn.getTxnCount() ) ),  // Charges
                                    1,                   // Subscribers today
                                    1                    // Subscribers to date
                            );
                } else {
                    stats = new DailyTransactionStatsDTO(
                                0,                    // Success count
                                txn.getTxnCount(),    // Failed count
                                BigDecimal.ZERO,      // Txn Value
                                BigDecimal.ZERO,      // Charges
                                0,                    // Subscribers today
                                0                     // Subscribers to date
                            );
                }
                map.put(formatter.format(txn.getTxnDate()), stats);
            } else {
                if ( "000".equals(txn.getStatusCode()) ) {
                    stats.setDaySuccessfulTxns(stats.getDaySuccessfulTxns() + txn.getTxnCount());
                    stats.setDayTxnRevenue(stats.getDayTxnRevenue().add(txn.getTxnValue()));
                    stats.setDayTxnCharges(stats.getDayTxnCharges().add(txn.getTxnCharge().multiply( new BigDecimal(txn.getTxnCount() ) )));
                    stats.setDaySubscribers(stats.getDaySubscribers() + 1);
                } else {
                    stats.setDaySubscribers(stats.getDaySubscribers() + 1 );
                    stats.setDayFailedTxns(stats.getDayFailedTxns() + txn.getTxnCount());
                }
                map.put(formatter.format(txn.getTxnDate()), stats);
            }
        }
        return map;
    }

    public static List<TransactionDTO> findTransactionDetails(Date reportDate, String txnType) {


        List<TransactionDTO> result = new ArrayList<TransactionDTO>();

        String sql =
//                "select txn_date_time, subscriber_id, " +
//                        " ifnull(amount,0.00) as amount" +
//                        " from txns t " +
//                        " where t.txn_date = '" + new java.sql.Date(reportDate.getTime()) + "'" +
//                        " and txn_type = '" + txnType + "'" +
//                        " order by txn_date_time asc";
//
         "  select substring(narrative,1,19) as TXNDATE, SOURCEID, ifnull(AMOUNT,0.00) as AMOUNT " +
           "  from TXN t " +
            "where t.TXNDATE = '" + new java.sql.Date(reportDate.getTime()) + "'" +
           "   and TXNTYPE = 'DataBundlePurchase' " +
           " order by TXNDATE asc;";

        System.out.println("##### " + sql);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next()) {
                TransactionDTO item = new TransactionDTO();
                item.setTransactionDate(new Date(rs.getTimestamp("TXNDATE").getTime()));
                item.setSubscriberMsisdn(rs.getString("SOURCEID"));
                item.setAmount(rs.getBigDecimal("AMOUNT"));
                result.add(item);
            }
            return result;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage() + " : " + sql);
            return null;
        } finally {
            try {stmt.close();} catch (Exception e1){}
            try {conn.close();} catch (Exception e1){}
        }
    }
//	public static List<HourlyStatsDTO> findTransactionSummaryHourly(Statement stmt, String startHour, String endHour) {
//
//		List<HourlyStatsDTO> result = new ArrayList<HourlyStatsDTO>();
//
//		String sql =
//				"select txn_hour, " +
//				" ifnull(( select count(*) from txns st where st.txn_hour = t.txn_hour and st.txn_type = 'DataBundlePurchase'),0) as successfulSubscriptionCount," +
//				" ifnull(( select sum(amount) from txns st where st.txn_hour = t.txn_hour and st.txn_type = 'DataBundlePurchase'),0.00) as revenue," +
//				" ifnull(( select count(*) from txns st where st.txn_hour = t.txn_hour and st.txn_type = 'DataBundlePurchaseError'),0) as rejectedSubscriptionCount" +
//				" from txns t " +
//				" where t.txn_hour >= '" + startHour + "'" +
//				" and t.txn_hour <= '" + endHour + "'" +
//				" and (txn_type = 'DataBundlePurchase'" +
//				" or txn_type = 'DataBundlePurchaseError' )" +
//				" group by txn_hour" +
//				" order by txn_hour asc";
//
////		System.out.println("##### " + sql);
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				HourlyStatsDTO item = new HourlyStatsDTO();
//				item.setHourOfDay(rs.getString("txn_hour"));
//				item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
//				item.setRejectedSubscriptionCount(rs.getLong("rejectedSubscriptionCount"));
//                BigDecimal revenue = new BigDecimal(0.01);
//                if (!rs.getBigDecimal("revenue").equals(BigDecimal.ZERO) ) {
//                    revenue = rs.getBigDecimal("revenue");
//                }
//                item.setRevenue(revenue);
//				result.add(item);
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//	}
	
	public static Map<Date, List<StatsDTO>> findTransactionDetails(Date startDate, Date endDate) {

		List<Date> dates = new ArrayList<Date>();
		
		String sql = 
//				"select txn_date " +
//				" from txns t " +
//				" where t.txn_date >= '" + new java.sql.Date(startDate.getTime()) + "'" +
//				" and t.txn_date <= '" + new java.sql.Date(endDate.getTime()) + "'" +
//				" and txn_type = 'DataBundlePurchase'" +
//				" group by txn_date" +
//				" order by txn_date asc";

                " select TXNDATE " +
                "  from TXNS t " +
                " where t.TXNDATE >= '" + new java.sql.Date(startDate.getTime()) + "' " +
                " and t.TXNDATE <= '" + new java.sql.Date(endDate.getTime()) + "' " +
                " and TXNTYPE = 'DataBundlePurchase' " +
                " AND STATUS = '000' " +
                "group by TXNDATE " +
                "order by TXNDATE asc ";
        System.out.println("##### " + sql);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);
			while ( rs.next()) {
				dates.add(rs.getDate("TXNDATE"));
			}
			
			Map<Date, List<StatsDTO>> result = new HashMap<Date, List<StatsDTO>>();
			
			for (Date date : dates) {
				
				List<StatsDTO> stats = new ArrayList<StatsDTO>();
				
//				sql = " select txn_date, count(*) as successfulSubscriptionCount, amount as bundleId, sum(amount) as revenue";
//				sql += "  from txns where txn_date = '" + new java.sql.Date(date.getTime()) + "'";
//				sql += " and txn_type = 'DataBundlePurchase'";
//				sql += " group by amount";
//				sql += " order by revenue";

                sql =   " select txndate as transactionDate, count(*) as successfulSubscriptionCount, AMOUNT as bundleId, sum(AMOUNT) as revenue " +
                        "  from TXNS " +
                        " where TXNDATE = '" + new java.sql.Date(date.getTime()) + "'" +
                        "   and TXNTYPE = 'DataBundlePurchase' " +
                        "   and STATUS = '000' " +
                        " group by txndate, amount " +
                        " order by revenue asc;";
                         System.out.println("##### " + sql);


                rs = stmt.executeQuery(sql);
				while ( rs.next()) {
					StatsDTO item = new StatsDTO();
					item.setTransactionDate(rs.getDate("transactionDate"));
					item.setBundleId(rs.getDouble("bundleId"));
					item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
					item.setRejectedSubscriptionCount(0L);
                    item.setRevenue(rs.getBigDecimal("revenue"));
					stats.add(item);
				}							
				result.put(date, stats);
				
			}
			return result;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage() + " : " + sql);
            return null;
        } finally {
            try {rs.close();} catch (Exception e1){}
            try {stmt.close();} catch (Exception e1){}
            try {conn.close();} catch (Exception e1){}
        }
	}
	
	public static Map<String, List<HourlyStatsDTO>> findTransactionDetailsHourly(Statement stmt, String startHour, String endHour) {

		List<String> hours = new ArrayList<String>();
		
		String sql = 
				"select txn_hour " +
				" from txns t " + 
				" where t.txn_hour >= '" + startHour + "'" + 
				" and t.txn_hour <= '" + endHour + "'" + 
				" and txn_type = 'DataBundlePurchase'" + 
				" group by txn_hour" +
				" order by txn_hour asc";
		
//		System.out.println("##### " + sql);
		try {
						
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next()) {
				hours.add(rs.getString("txn_hour"));
			}
			
			Map<String, List<HourlyStatsDTO>> result = new HashMap<String, List<HourlyStatsDTO>>();
			
			for (String hour : hours) {
				
				List<HourlyStatsDTO> stats = new ArrayList<HourlyStatsDTO>();
				
				sql = " select txn_hour, count(*) as successfulSubscriptionCount, amount as bundleId, sum(amount) as revenue";
				sql += "  from txns where txn_hour = '" + hour + "'";
				sql += " and txn_type = 'DataBundlePurchase'";
				sql += " group by amount";
				sql += " order by revenue";
				
				rs = stmt.executeQuery(sql);
				while ( rs.next()) {
					HourlyStatsDTO item = new HourlyStatsDTO();
					item.setHourOfDay(rs.getString("txn_hour"));
					item.setBundleId(rs.getDouble("bundleId"));
					item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
					item.setRejectedSubscriptionCount(0L);
					item.setRevenue(rs.getBigDecimal("revenue"));
					stats.add(item);
				}							
				result.put(hour, stats);
				
			}
			return result;
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return null;
		}
	}
	
	public static List<StatsDTO>  findRejections(Statement stmt, Date startDate, Date endDate) {
	
		List<StatsDTO> result = new ArrayList<StatsDTO>();
		String sql = 
//				"select txn_date as transactionDate, " +
//				"   count(*) as rejectionCount, status" +
//				" from txns " +
//				" where txn_date = '" + endDate + "'" +
////				" and txn_date <= '" + endDate + "'" +
//  			" and txn_type = 'DataBundlePurchaseError'" +
//				" group by txn_date, status" +
//				" order by txn_date asc";
                " select TXNDATE, count(*) as rejectionCount, substring(narrative,21) AS STATUS " +
                "   from TXN t " +
                "  where t.TXNDATE >= '" +  new java.sql.Date(startDate.getTime()) + "' " +
                "    and t.TXNDATE <= '" + new java.sql.Date(endDate.getTime()) + "' " +
                "    and TXNTYPE = 'DataBundlePurchase' " +
                "    and t.STATUS<>'000' " +
                "  group by TXNDATE, STATUS " +
                "  order by TXNDATE asc";
		try {
						
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next()) {
				StatsDTO item = new StatsDTO();
				item.setTransactionDate(rs.getDate("TXNDATE"));
				item.setRejectedSubscriptionCount(rs.getLong("rejectionCount"));
				item.setNarrative(rs.getString("STATUS"));
				result.add(item);
			}
			return result;
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return null;
		}
		
	}

    public static Map<Date, List<StatsDTO>> findDailyRejections(Date startDate, Date endDate) {

        List<Date> dates = new ArrayList<Date>();

        String sql =
//				"select txn_date " +
//				" from txns t " +
//				" where t.txn_date >= '" + new java.sql.Date(startDate.getTime()) + "'" +
//				" and t.txn_date <= '" + new java.sql.Date(endDate.getTime()) + "'" +
//				" and txn_type = 'DataBundlePurchaseError'" +
//				" group by txn_date" +
//				" order by txn_date asc";
                " select TXNDATE " +
                        "  from TXNS t " +
                        " where t.TXNDATE >= '" + new java.sql.Date(startDate.getTime()) + "' " +
                        " and t.TXNDATE <= '" + new java.sql.Date(endDate.getTime()) + "' " +
                        " and TXNTYPE = 'DataBundlePurchase' " +
                        " AND STATUS <> '000' " +
                        "group by TXNDATE " +
                        "order by TXNDATE asc ";

        System.out.println("##### " + sql);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(sql);
            while ( rs.next()) {
                dates.add(rs.getDate("TXNDATE"));
            }

            Map<Date, List<StatsDTO>> result = new HashMap<Date, List<StatsDTO>>();

            for (Date date : dates) {

                List<StatsDTO> stats = new ArrayList<StatsDTO>();

                sql =
//						"select txn_date as transactionDate, " +
//						"   count(*) as rejectionCount, status" +
//						" from txns " +
//						" where txn_date = '" + new java.sql.Date(date.getTime()) + "'" +
//		  			" and txn_type = 'DataBundlePurchaseError'" +
//						" group by txn_date, status" +
//						" order by txn_date asc";

//                        "select txn_hour, " +
//                                "   count(*) as rejectionCount, status" +
//                                " from txns " +
//                                " where txn_hour >= '" + startHour + "'" +
//                                " and txn_hour <= '" + endHour + "'" +
//                                " and txn_type = 'DataBundlePurchaseError'" +
//                                " group by txn_hour, status" +
//                                " order by txn_hour asc";

//                "select txn_date as transactionDate, " +
//				"   count(*) as rejectionCount, status" +
//				" from txns " +
//				" where txn_date = '" + new java.sql.Date(date.getTime()) + "'" +
//		  		" and txn_type = 'DataBundlePurchaseError'" +
//				" group by txn_date, status" +
//				" order by txn_date asc";

                        " select TXNDATE as transactionDate,  count(*) as rejectionCount, substring(NARRATIVE,21) AS NARRATIVE " +
                                "  from TXNS " +
                                " where TXNDATE = '" + new java.sql.Date(date.getTime()) + "'" +
                                "   and TXNTYPE = 'DataBundlePurchase' " +
                                "   and STATUS <> '000' " +
                                " group by TXNDATE, substring(NARRATIVE,21) " +
                                " order by TXNDATE asc;";

                rs = stmt.executeQuery(sql);
                while ( rs.next()) {
                    StatsDTO item = new StatsDTO();
                    item.setTransactionDate(rs.getDate("transactionDate"));
                    item.setRejectedSubscriptionCount(rs.getLong("rejectionCount"));
                    item.setNarrative(rs.getString("NARRATIVE"));
                    stats.add(item);
                }
                result.put(date, stats);

            }
            return result;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage() + " : " + sql);
            return null;
        } finally {
            try {rs.close();} catch (Exception e1){}
            try {stmt.close();} catch (Exception e1){}
            try {conn.close();} catch (Exception e1){}
        }
    }

	public static Map<Date, List<StatsDTO>> dummyFindDailyRejections(Date startDate, Date endDate) {


        Map<Date, List<StatsDTO>> result = new HashMap<Date, List<StatsDTO>>();

        DateTime date = new DateTime(startDate);
        DateTime toDate = new DateTime(endDate);

        while (date.isBefore(toDate.plusDays(1))) {
            System.out.println("Date : " + date);
            List<StatsDTO> stats = new ArrayList<StatsDTO>();

            StatsDTO item = new StatsDTO();
            item.setTransactionDate(new java.sql.Date(date.getMillis()));
            item.setRejectedSubscriptionCount(new Long(1));
            item.setNarrative("Insufficient funds");
            stats.add(item);

            result.put(date.toDate(), stats);
            date = date.plusDays(1);
        }

        return result;
	}
	public static List<HourlyStatsDTO> findRejectionsHourly(Statement stmt, String startHour, String endHour) {

		List<HourlyStatsDTO> result = new ArrayList<HourlyStatsDTO>();
		
		String sql = 
				"select txn_hour, " +
				"   count(*) as rejectionCount, status" +
				" from txns " + 
				" where txn_hour >= '" + startHour + "'" + 
				" and txn_hour <= '" + endHour + "'" + 
  			" and txn_type = 'DataBundlePurchaseError'" +
				" group by txn_hour, status" +
				" order by txn_hour asc";
			
		System.out.println("##### " + sql);
		try {
						
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next()) {
				HourlyStatsDTO item = new HourlyStatsDTO();
				item.setHourOfDay(rs.getString("txn_hour"));
				item.setRejectedSubscriptionCount(rs.getLong("rejectionCount"));
				item.setNarrative(rs.getString("status"));
				result.add(item);
			}
			return result;
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return null;
		}
		
	}	
	
	public static Date getExpiryDate(Statement stmt, String msisdn) {
		String sql = "SELECT MAX(expiry_date) AS expiry_date FROM txns WHERE msisdn = '" + msisdn + "' AND txn_type='DataBundlePurchase' AND state='ACTIVE' AND status_code='000'";
		try {						
			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next()) {
				return rs.getDate("expiry_date");
			}
		} catch (SQLException e) {
			System.out.println("Error : " + e.getMessage() + " : " + sql);
			return null;
		}
		return null;
	}

    public static void main(String[] args) {

        DateTime yesterday = new DateTime().minusDays(1);
        Date fromDate = yesterday.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
        Date toDate = yesterday.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate();
        System.out.println(new SimpleDateFormat("MMMMM yyyy").format(fromDate) + ", " + toDate);
    }
}
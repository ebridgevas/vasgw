package com.ebridgecommerce.sdp.dao;

import com.ebridgecommerce.sdp.domain.ClassOfServiceDTO;
import com.zw.ebridge.domain.DataBundlePrice;
import com.zw.ebridge.domain.SubscriberInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class UsagePromotionDAO {

	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	private static final SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

	public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/telecel", "root", "changeit");
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
   /*
  create table promo_logs (
  uuid long,date_awarded datetime, datetime_collected datetime, date_collected date, msisdn varchar(20),
  txn_type varchar(20), status_code varchar(3), narrative varchar(100), key (date_collected));
    */
    public static void log(Statement stmt, Long uuid, String msisdn, Date dateAwarded, String serviceCommand, String statusCode, String narrative ){
        Date datetimeCollected = new Date();
        java.sql.Date dateCollected = new java.sql.Date(datetimeCollected.getTime());
        String sql = "INSERT INTO promo_logs(uuid, date_awarded, datetime_collected, date_collected, msisdn, txn_type, status_code, narrative ) ";
        sql += " VALUES(" + uuid + ",'" + dateAwarded + "','" + datetimeCollected  + "','" + dateCollected + "','" + msisdn + "','" +
                serviceCommand + "'," + statusCode + "','" + narrative + "')";
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

    public static Boolean isRegistered(Statement stmt, String msisdn){

        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) AS count FROM register WHERE msisdn = '" + msisdn + "' AND status = 'Active'";
            rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getInt("count") > 0 ? true : false;

        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
            return false;
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

    public static BigDecimal getServiceParameterBigDecimal(String parameter_key) {
        try {
            return new BigDecimal(getString("SELECT parameter_value FROM service_parameters WHERE parameter_key = '" + parameter_key + "'"));
        } catch (SQLException e) {
            return null;
        }
    }

    public static List<Integer> getServiceParameterIntegerList(String parameter_key_prefix) {
        List<Integer> list = new ArrayList<Integer>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().createStatement();

            rs = stmt.executeQuery("SELECT parameter_value FROM service_parameters WHERE parameter_key LIKE '" + parameter_key_prefix + "%'");
            while(rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch(Exception ex) {
            return null;
        } finally {

            try {
                stmt.close();
            } catch (Exception e1) {
            }
            try {
                rs.close();
            } catch (Exception e1) {
            }
        }
        return list;
    }

    public static String getString(String sql) throws SQLException {

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new SQLException("Record not found");
            }
        } catch(Exception e ) {
            throw new SQLException(e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e1) {
            }
            try {
                rs.close();
            } catch (Exception e1) {
            }
        }
    }
}
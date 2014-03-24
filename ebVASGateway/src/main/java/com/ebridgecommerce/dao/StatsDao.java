package main.java.com.ebridgecommerce.dao;//package com.ebridgecommerce.dao;
//
//import com.ebridgecommerce.domain.*;
//import com.ebridgecommerce.smpp.pdu.SubmitSM;
//import com.ebridgecommerce.smsc.SMSC;
//import org.joda.time.DateTime;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class StatsDao {
//
//	private static DBAdapter instance;
//
//	private Connection conn = null;
//
//	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
//	private static final SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
//
//
//
//	public static void init() throws SQLException, Exception {
//
//		gPool = new GenericObjectPool();
//		Properties props = new Properties();
//		props.setProperty("user", "root");
//		props.setProperty("password", "changeit");
//		ConnectionFactory cf = new DriverConnectionFactory(new com.mysql.jdbc.Driver(), "jdbc:mysql://localhost/telecel", props);
//
//		KeyedObjectPoolFactory kopf = new GenericKeyedObjectPoolFactory(null, SMSC.DB_CONNECTION_POOL_SIZE);
//
//		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, kopf, null, false, true);
//		gPool.setMaxActive(50);
//		for (int i = 0; i < SMSC.DB_CONNECTION_POOL_SIZE; i++) {
//			gPool.addObject();
//		}
////		System.out.println(" a = " + gPool.getNumActive() + " i = " + gPool.getNumIdle() + " mi = " + gPool.getMaxIdle());
////		System.out.println(" max = " + gPool.getMaxActive());
//		// PoolingDataSource pds = new PoolingDataSource(gPool);
//		PoolingDriver pd = new PoolingDriver();
//		pd.registerPool("vasDBCP", gPool);
//
//	}
//
//
//	public static String getString(String sql) throws SQLException {
//
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			stmt = getConnection().createStatement();
//			rs = stmt.executeQuery(sql);
//			if (rs.next()) {
//				return rs.getString(1);
//			} else {
//				throw new SQLException("Record not found");
//			}
//		} catch(Exception e ) {
//			throw new SQLException(e.getMessage());
//		} finally {
//			try {
//				stmt.close();
//			} catch (Exception e1) {
//			}
//			try {
//				rs.close();
//			} catch (Exception e1) {
//			}
//		}
//	}
//
//	public static List<StatsDTO> findTransactionSummary(Statement stmt, Date startDate, Date endDate) {
//
//		List<StatsDTO> result = new ArrayList<StatsDTO>();
//
//		String sql =
//				"select txn_date, " +
//				" ifnull(( select count(*) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchase'),0) as successfulSubscriptionCount," +
//				" ifnull(( select sum(amount) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchase'),0.00) as revenue," +
//				" ifnull(( select count(*) from txns st where st.txn_date = t.txn_date and st.txn_type = 'DataBundlePurchaseError'),0) as rejectedSubscriptionCount" +
//				" from txns t " +
//				" where t.txn_date >= '" + startDate + "'" +
//				" and t.txn_date <= '" + endDate + "'" +
//				" and (txn_type = 'DataBundlePurchase'" +
//				" or txn_type = 'DataBundlePurchaseError' )" +
//				" group by txn_date" +
//				" order by txn_date asc";
//
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				StatsDTO item = new StatsDTO();
//				item.setTransactionDate(rs.getDate("txn_date"));
//				item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
//				item.setRejectedSubscriptionCount(rs.getLong("rejectedSubscriptionCount"));
//				item.setRevenue(rs.getBigDecimal("revenue"));
//				result.add(item);
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//	}
//
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
//				item.setRevenue(rs.getBigDecimal("revenue"));
//				result.add(item);
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//	}
//
//	public static Map<Date, List<StatsDTO>> findTransactionDetails(Statement stmt, Date startDate, Date endDate) {
//
//		List<Date> dates = new ArrayList<Date>();
//
//		String sql =
//				"select txn_date " +
//				" from txns t " +
//				" where t.txn_date >= '" + startDate + "'" +
//				" and t.txn_date <= '" + endDate + "'" +
//				" and txn_type = 'DataBundlePurchase'" +
//				" group by txn_date" +
//				" order by txn_date asc";
//
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				dates.add(rs.getDate("txn_date"));
//			}
//
//			Map<Date, List<StatsDTO>> result = new HashMap<Date, List<StatsDTO>>();
//
//			for (Date date : dates) {
//
//				List<StatsDTO> stats = new ArrayList<StatsDTO>();
//
//				sql = " select txn_date, count(*) as successfulSubscriptionCount, amount as bundleId, sum(amount) as revenue";
//				sql += "  from txns where txn_date = '" + date + "'";
//				sql += " and txn_type = 'DataBundlePurchase'";
//				sql += " group by amount";
//				sql += " order by revenue";
//
//				rs = stmt.executeQuery(sql);
//				while ( rs.next()) {
//					StatsDTO item = new StatsDTO();
//					item.setTransactionDate(rs.getDate("txn_date"));
//					item.setBundleId(rs.getDouble("bundleId"));
//					item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
//					item.setRejectedSubscriptionCount(0L);
//					item.setRevenue(rs.getBigDecimal("revenue"));
//					stats.add(item);
//				}
//				result.put(date, stats);
//
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//	}
//
//	public static Map<String, List<HourlyStatsDTO>> findTransactionDetailsHourly(Statement stmt, String startHour, String endHour) {
//
//		List<String> hours = new ArrayList<String>();
//
//		String sql =
//				"select txn_hour " +
//				" from txns t " +
//				" where t.txn_hour >= '" + startHour + "'" +
//				" and t.txn_hour <= '" + endHour + "'" +
//				" and txn_type = 'DataBundlePurchase'" +
//				" group by txn_hour" +
//				" order by txn_hour asc";
//
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				hours.add(rs.getString("txn_hour"));
//			}
//
//			Map<String, List<HourlyStatsDTO>> result = new HashMap<String, List<HourlyStatsDTO>>();
//
//			for (String hour : hours) {
//
//				List<HourlyStatsDTO> stats = new ArrayList<HourlyStatsDTO>();
//
//				sql = " select txn_hour, count(*) as successfulSubscriptionCount, amount as bundleId, sum(amount) as revenue";
//				sql += "  from txns where txn_hour = '" + hour + "'";
//				sql += " and txn_type = 'DataBundlePurchase'";
//				sql += " group by amount";
//				sql += " order by revenue";
//
//				rs = stmt.executeQuery(sql);
//				while ( rs.next()) {
//					HourlyStatsDTO item = new HourlyStatsDTO();
//					item.setHourOfDay(rs.getString("txn_hour"));
//					item.setBundleId(rs.getDouble("bundleId"));
//					item.setSuccessfulSubscriptionCount(rs.getLong("successfulSubscriptionCount"));
//					item.setRejectedSubscriptionCount(0L);
//					item.setRevenue(rs.getBigDecimal("revenue"));
//					stats.add(item);
//				}
//				result.put(hour, stats);
//
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//	}
//
//	public static List<StatsDTO>  findRejections(Statement stmt, Date startDate, Date endDate) {
//
//		List<StatsDTO> result = new ArrayList<StatsDTO>();
//		String sql =
//				"select txn_date as transactionDate, " +
//				"   count(*) as rejectionCount, status" +
//				" from txns " +
//				" where txn_date = '" + endDate + "'" +
////				" and txn_date <= '" + endDate + "'" +
//  			" and txn_type = 'DataBundlePurchaseError'" +
//				" group by txn_date, status" +
//				" order by txn_date asc";
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				StatsDTO item = new StatsDTO();
//				item.setTransactionDate(rs.getDate("transactionDate"));
//				item.setRejectedSubscriptionCount(rs.getLong("rejectionCount"));
//				item.setNarrative(rs.getString("status"));
//				result.add(item);
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//
//	}
//
//	public static Map<Date, List<StatsDTO>> findDailyRejections(Statement stmt, Date startDate, Date endDate) {
//
//		List<Date> dates = new ArrayList<Date>();
//
//		String sql =
//				"select txn_date " +
//				" from txns t " +
//				" where t.txn_date >= '" + startDate + "'" +
//				" and t.txn_date <= '" + endDate + "'" +
//				" and txn_type = 'DataBundlePurchaseError'" +
//				" group by txn_date" +
//				" order by txn_date asc";
//
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				dates.add(rs.getDate("txn_date"));
//			}
//
//			Map<Date, List<StatsDTO>> result = new HashMap<Date, List<StatsDTO>>();
//
//			for (Date date : dates) {
//
//				List<StatsDTO> stats = new ArrayList<StatsDTO>();
//
//				sql =
//						"select txn_date as transactionDate, " +
//						"   count(*) as rejectionCount, status" +
//						" from txns " +
//						" where txn_date = '" + dateFormat.format(date) + "'" +
//		  			" and txn_type = 'DataBundlePurchaseError'" +
//						" group by txn_date, status" +
//						" order by txn_date asc";
//				rs = stmt.executeQuery(sql);
//				while ( rs.next()) {
//					StatsDTO item = new StatsDTO();
//					item.setTransactionDate(rs.getDate("transactionDate"));
//					item.setRejectedSubscriptionCount(rs.getLong("rejectionCount"));
//					item.setNarrative(rs.getString("status"));
//					stats.add(item);
//				}
//				result.put(date, stats);
//
//			}
//		return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//	}
//	public static List<HourlyStatsDTO> findRejectionsHourly(Statement stmt, String startHour, String endHour) {
//
//		List<HourlyStatsDTO> result = new ArrayList<HourlyStatsDTO>();
//
//		String sql =
//				"select txn_hour, " +
//				"   count(*) as rejectionCount, status" +
//				" from txns " +
//				" where txn_hour >= '" + startHour + "'" +
//				" and txn_hour <= '" + endHour + "'" +
//  			" and txn_type = 'DataBundlePurchaseError'" +
//				" group by txn_hour, status" +
//				" order by txn_hour asc";
//
//		System.out.println("##### " + sql);
//		try {
//
//			ResultSet rs = stmt.executeQuery(sql);
//			while ( rs.next()) {
//				HourlyStatsDTO item = new HourlyStatsDTO();
//				item.setHourOfDay(rs.getString("txn_hour"));
//				item.setRejectedSubscriptionCount(rs.getLong("rejectionCount"));
//				item.setNarrative(rs.getString("status"));
//				result.add(item);
//			}
//			return result;
//		} catch (SQLException e) {
//			System.out.println("Error : " + e.getMessage() + " : " + sql);
//			return null;
//		}
//
//	}
//
//    @SuppressWarnings("serial")
//    public static class HourlyStatsDTO implements Serializable {
//
//        private String hourOfDay;
//        private Long rejectedSubscriptionCount;
//        private Long successfulSubscriptionCount;
//        private BigDecimal failureRatio;
//        private double bundleId;
//        private BigDecimal revenue;
//        private String narrative;
//
//        public String getHourOfDay() {
//            return hourOfDay;
//        }
//        public void setHourOfDay(String hourOfDay) {
//            this.hourOfDay = hourOfDay;
//        }
//        public Long getRejectedSubscriptionCount() {
//            return rejectedSubscriptionCount;
//        }
//        public void setRejectedSubscriptionCount(Long rejectedSubscriptionCount) {
//            this.rejectedSubscriptionCount = rejectedSubscriptionCount;
//        }
//        public Long getSuccessfulSubscriptionCount() {
//            return successfulSubscriptionCount;
//        }
//        public void setSuccessfulSubscriptionCount(Long successfulSubscriptionCount) {
//            this.successfulSubscriptionCount = successfulSubscriptionCount;
//        }
//        public BigDecimal getFailureRatio() {
//            return failureRatio;
//        }
//        public void setFailureRatio(BigDecimal failureRatio) {
//            this.failureRatio = failureRatio;
//        }
//
//        public double getBundleId() {
//            return bundleId;
//        }
//        public void setBundleId(double bundleId) {
//            this.bundleId = bundleId;
//        }
//        public BigDecimal getRevenue() {
//            return revenue;
//        }
//        public void setRevenue(BigDecimal revenue) {
//            this.revenue = revenue;
//        }
//        public String getNarrative() {
//            return narrative;
//        }
//        public void setNarrative(String narrative) {
//            this.narrative = narrative;
//        }
//
//        public int compareTo(HourlyStatsDTO o){
//            return this.getHourOfDay().compareTo(o.getHourOfDay());
//        }
//    }
//}
package com.ebridgecommerce.dao;

import com.ebridgecommerce.domain.TransactionDTO;
import com.ebridgecommerce.dto.SimRegistrationStatsDTO;
//import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
//import com.ebridgecommerce.sdp.service.SimRegistrationServiceCommandProcessor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 7/1/12
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportingEngineDAO {

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/telecel", "root", "changeit");
    }

    public static List<SimRegistrationStatsDTO> findSimRegistrationStats(Date reportDate) {

        List<SimRegistrationStatsDTO> result = new ArrayList<SimRegistrationStatsDTO>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql =
            " SELECT date_created, " +
            "       ( SELECT COUNT(*) " +
            "          FROM  simregister s2 " +
            "         WHERE  state='registered' " +
            "           AND  s2.date_created = s.date_created) AS registered, " +
            "       ( SELECT COUNT(*) " +
            "           FROM simregister s2 " +
            "          WHERE state<>'registered' " +
            "            AND s2.date_created = s.date_created) AS pending, " +
            "       ( SELECT COUNT(*) " +
            "           FROM txns t " +
            "          WHERE txn_type='simreg' " +
            "            AND status_code <> '000' " +
            "            AND t.txn_date = s.date_created) AS rejected " +
            " FROM     simregister s " +
            " WHERE date_created <= '" + new SimpleDateFormat("yyyy-MM-dd").format(reportDate) + "'" +
            " GROUP BY date_created ";

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result.add(new SimRegistrationStatsDTO(
                        rs.getDate("date_created"),
                        rs.getInt("registered"),
                        rs.getInt("pending"),
                        rs.getInt("rejected")
                ));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {rs.close();} catch (Exception e){}
            try {stmt.close();} catch (Exception e){}
            try {conn.close();} catch (Exception e){}
        }
    }

    public static List<SimRegistrationStatsDTO> findSimRegStats(Date reportDate) {
        List<SimRegistrationStatsDTO> result = new ArrayList<SimRegistrationStatsDTO>();

        Map<Date, SimRegistrationStatsDTO> simregs = new HashMap<Date, SimRegistrationStatsDTO>();
        Map<Date, Integer> errors = new HashMap<Date, Integer>();
        List<Date> dates = new ArrayList<Date>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            String sql = null;
            sql =
                     "SELECT txn_date, COUNT(*) AS count" +
                            "  FROM txns " +
                            " WHERE txn_date BETWEEN '2012-06-29' AND '"  + new SimpleDateFormat("yyyy-MM-dd").format(reportDate) + "'" +
                            "   AND status_code <> '000' " +
                            " GROUP BY txn_date ";

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                errors.put(rs.getDate("txn_date"), rs.getInt("count"));
            }
            System.out.println("Error size = " + errors.size());
            sql =
                    " SELECT date_created, state, COUNT(*) AS count " +
                            "   FROM     simregister s " +
                            "  WHERE date_created <= '" + new SimpleDateFormat("yyyy-MM-dd").format(reportDate) + "'" +
                            "  GROUP BY date_created, state " +
                            " ORDER BY date_created";

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SimRegistrationStatsDTO stats = simregs.get(rs.getDate("date_created"));

                if (stats == null) {
                    Integer errorCount = errors.get(rs.getDate("date_created"));
                    stats = new SimRegistrationStatsDTO(rs.getDate("date_created"),0,0,errorCount != null ? errorCount : 0);
                    dates.add(rs.getDate("date_created"));
                }

                if ("registered".equalsIgnoreCase(rs.getString("state") )) {
                    stats.setRegistered(rs.getInt("count"));
                } else {
                    stats.setPending(stats.getPending() + rs.getInt("count"));
                }
                simregs.put(rs.getDate("date_created"), stats);
            }
            System.out.println("simregs size = " + simregs.size());
            System.out.println("formating");
            for (Date date : dates) {
                result.add(simregs.get(date));
            }
            System.out.println("don");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {rs.close();} catch (Exception e){}
            try {stmt.close();} catch (Exception e){}
            try {conn.close();} catch (Exception e){}
        }
    }
//    public static List<SimRegistrationDTO> findSimRegistrationDetails(String state, Date reportDate) {
//        List<SimRegistrationDTO> result = new ArrayList<SimRegistrationDTO>();
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try {
//            String sql = "" +
//                    " SELECT ms_isdn, state, id_number, firstname, lastname, physical_address, datetime_created " +
//                    " FROM simregister " +
//                    " WHERE state " + ( state.startsWith("!") ? "<>" : "=" ) + "'" + state + "'" +
//                    "   AND date_created = '" + new SimpleDateFormat("yyyy-MM-dd").format(reportDate) + "'" +
//                    " ORDER BY date_created ";
//            conn = getConnection();
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(sql);
//            while ( rs.next() ) {
//                SimRegistrationDTO sim = new SimRegistrationDTO(rs.getString("ms_isdn"), rs.getString("state"));
//                sim.setIdNumber(rs.getString("id_number"));
//                sim.setFirstname(rs.getString("firstname"));
//                sim.setLastname(rs.getString("lastname"));
//                sim.setPhysicalAddress(rs.getString("physical_address"));
//                sim.setDateCreated(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs.getString("datetime_created")) );
//                result.add(sim);
//            }
//        } catch (Exception e) {
//            System.out.println("Error getting subscriber info - " + e.getMessage());
//        } finally {
//            try {rs.close();} catch (Exception e){}
//            try {stmt.close();} catch (Exception e){}
//            try {conn.close();} catch (Exception e){}
//        }
//        return result;
//    }

    public static List<SimRegistrationStatsDTO> findSimRegistrationErrorDetails(Date reportDate) {
        List<SimRegistrationStatsDTO> result = new ArrayList<SimRegistrationStatsDTO>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = " " +
                    " SELECT txn_date_time, subscriber_id, status " +
                    "   FROM txns " +
                    "  WHERE txn_type='simreg' " +
                    "    AND status_code <> '000' " +
                    "    AND txn_date = '" + new SimpleDateFormat("yyyy-MM-dd").format(reportDate) + "'" +
                    "  ORDER BY status ";
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                result.add(
                        new SimRegistrationStatsDTO(
                                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs.getString("txn_date_time")),
                                rs.getString("subscriber_id"),
                                rs.getString("status")));
            }
        } catch (Exception e) {
            System.out.println("Error getting subscriber info - " + e.getMessage());
        } finally {
            try {rs.close();} catch (Exception e){}
            try {stmt.close();} catch (Exception e){}
            try {conn.close();} catch (Exception e){}
        }
        return result;
    }
}

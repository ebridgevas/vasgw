package com.ebridgecommerce.sdp.dao;

import com.ebridgecommerce.sdp.domain.StatusDTO;
import com.ebridgecommerce.sdp.domain.SubscriberInfo;
import com.ebridgecommerce.sdp.domain.UssdMenuItem;
import com.ebridgecommerce.sdp.util.Util;
import com.ebridgecommerce.sdp.util.Validator;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VasGatewayDao {

	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	private static final SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

	public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/telecel", "root", "changeit");
	}

    public static Map <StatusDTO, UssdMenuItem> getUssdMenuItems(){
        Map <StatusDTO, UssdMenuItem> map = new HashMap<StatusDTO, UssdMenuItem>();

        String sql =
                " SELECT menu_id, menu_text, error_text, blank_error_text, validator " +
                " FROM ussd_menu_items ";

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()){
                map.put(Util.getEnumFromString(StatusDTO.class, rs.getString("menu_id")),
                        new UssdMenuItem(
                                Util.getEnumFromString(StatusDTO.class, rs.getString("menu_id")),
                                rs.getString("menu_text"),
                                rs.getString("error_text"),
                                rs.getString("blank_error_text"),
                                (Validator)Class.forName(rs.getString("validator")).newInstance()
                        ));
            }
            return map;
        } catch (Exception e) {
            System.out.println("Error reading price list - " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {stmt.close();}catch(Exception e){}
            try {rs.close();}catch(Exception e){}
        }
    }

	public static Map<StatusDTO, String> getSubscriberInfo(String msisdn){

        String sql =
                " SELECT msisdn, status, val " +
                " FROM simregister " +
                " WHERE msisdn = '" + msisdn + "'";
        Statement stmt = null;
        ResultSet rs = null;
        Map<StatusDTO, String> info = new HashMap<StatusDTO, String>();
        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                info.put(Util.getEnumFromString(StatusDTO.class, rs.getString("status")), rs.getString("val"));
            }
            return info.size() > 0 ? info : null;
        } catch (Exception e) {
            System.out.println("Error reading SubscriberInfo - " + e.getMessage());
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
	
	public static boolean addInfo( String msisdn, Map<StatusDTO, String> info ) {

        Statement stmt = null;
		ResultSet rs = null;
		try {

            stmt = getConnection().createStatement();

            for (StatusDTO statusDTO : info.keySet()) {
                String sql = "" +
                      " SELECT * " +
                      " FROM simregister " +
                      " WHERE msisdn = '" + msisdn + "'" +
                      "   AND status = '" + statusDTO.toString().toLowerCase() + "'";
                System.out.println(sql);
                rs = stmt.executeQuery(sql);
                if ( rs.next() ) {
                    sql  =  " UPDATE simregister " +
                            " SET   val = '" + info.get(statusDTO) + "'," +
                            "       entry_date = now()" +
                            " WHERE msisdn = '" + msisdn + "'" +
                            "   AND status = '" + statusDTO.toString().toLowerCase() + "'";

                } else {
                    sql  = " INSERT INTO simregister (msisdn, status, val, entry_date ) " +
                            " VALUES ('" + msisdn + "','" + statusDTO.toString().toLowerCase() + "','" + info.get(statusDTO) + "',now())";
                }
                System.out.println(sql);
                stmt.executeUpdate(sql);
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error adding subscriber info - " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {stmt.close();}catch(Exception e){}
            try {rs.close();}catch(Exception e){}
        }
	}
}
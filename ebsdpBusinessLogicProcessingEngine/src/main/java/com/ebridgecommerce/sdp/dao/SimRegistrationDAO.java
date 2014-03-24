package com.ebridgecommerce.sdp.dao;

import com.ebridgecommerce.sdp.domain.StatusDTO;
import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationDAO {

    private static GenericObjectPool gPool;

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("yyyy-MM-dd HH");

    public static Statement statement;

    public static void init() throws SQLException, Exception {

//        gPool = new GenericObjectPool();
//        Properties props = new Properties();
//        props.setProperty("user", "root");
//        props.setProperty("password", "changeit");
//        ConnectionFactory cf = new DriverConnectionFactory(new com.mysql.jdbc.Driver(), "jdbc:mysql://localhost/telecel", props);
//
//        KeyedObjectPoolFactory kopf = new GenericKeyedObjectPoolFactory(null, SMSC.DB_CONNECTION_POOL_SIZE);
//
//        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, kopf, null, false, true);
//        gPool.setMaxActive(50);
//        for (int i = 0; i < SMSC.DB_CONNECTION_POOL_SIZE; i++) {
//            gPool.addObject();
//        }
//        PoolingDriver pd = new PoolingDriver();
//        pd.registerPool("vasDBCP", gPool);

        statement = getConnection().createStatement();
    }

    public static Connection getConnection() throws Exception {
        if (gPool == null) {
            init();
        }
        return DriverManager.getConnection("jdbc:apache:commons:dbcp:vasDBCP");
    }

//    public static Connection getConnection() throws Exception {
//        Class.forName("com.mysql.jdbc.Driver").newInstance();
//        return DriverManager.getConnection("jdbc:mysql://localhost:3306/telecel", "root", "changeit");
//    }

    public static SimRegistrationDTO getPendingSimRegistration(String msIsdn) {
        SimRegistrationDTO sim = null;
        ResultSet rs = null;
        try {
            String sql = "" +
                    " SELECT ms_isdn, state, id_number, firstname, lastname, physical_address " +
                    " FROM simregister " +
                    " WHERE ms_isdn = '" + msIsdn + "'";
            System.out.println(sql);
            rs = statement.executeQuery(sql);
            if ( rs.next() ) {
                sim = new SimRegistrationDTO(rs.getString("ms_isdn"), rs.getString("state"));
                sim.setIdNumber(rs.getString("id_number"));
                sim.setFirstname(rs.getString("firstname"));
                sim.setLastname(rs.getString("lastname"));
                sim.setPhysicalAddress(rs.getString("physical_address"));
            }
        } catch (Exception e) {
            System.out.println("Error getting subscriber info - " + e.getMessage());
        } finally {
            try {rs.close();}catch(Exception e){}
        }
        return sim;
    }

    public static Boolean initRegistrationFor( String msIsdn ){
        ResultSet rs = null;
        try {
            String sql = " INSERT INTO simregister (ms_isdn, state, date_created, datetime_created ) " +
                    " VALUES ('" + msIsdn + "','main', '" +  dateFormat.format(new Date())+ "',now())";
            statement.executeUpdate(sql);
            System.out.println(sql);
            return true;
        } catch (Exception e) {
            System.out.println("Error adding subscriber info - " + e.getMessage());
            return false;
        } finally {
            try {rs.close();}catch(Exception e){}
        }
    }

    public static boolean updateRegistration( SimRegistrationDTO registration ) {

        ResultSet rs = null;
        try {
            String sql = " UPDATE simregister " +
                    " SET state = '" + registration.getState() + "'," +
                    " id_number = '" + registration.getIdNumber() + "'," +
                    " firstname = '" + registration.getFirstname() + "'," +
                    " lastname = '" + registration.getLastname() + "'," +
                    " physical_address = '" + registration.getPhysicalAddress() + "'," +
                    " date_last_updated = now()" +
                    " WHERE ms_isdn = '" + registration.getMsIsdn() + "'";
            System.out.println(sql);
            statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error modifying registation info - " + e.getMessage());
            return false;
        } finally {
            try {rs.close();}catch(Exception e){}
        }
    }

    public static List<SimRegistrationDTO> getSimRegistrations(StatusDTO statusDTO) {

        List<SimRegistrationDTO> result = new ArrayList<SimRegistrationDTO>();
        ResultSet rs = null;
        Statement stmt = null;
        try {
            String sql = "" +
                    " SELECT ms_isdn, state, id_number, firstname, lastname, physical_address " +
                    " FROM simregister " +
                    " WHERE state = '" + statusDTO.toString().toLowerCase() + "'";
            System.out.println(sql);
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next() ) {
                SimRegistrationDTO sim = new SimRegistrationDTO(rs.getString("ms_isdn"), rs.getString("state"));
                sim.setIdNumber(rs.getString("id_number"));
                sim.setFirstname(rs.getString("firstname"));
                sim.setLastname(rs.getString("lastname"));
                sim.setPhysicalAddress(rs.getString("physical_address"));
                result.add( sim );
            }
        } catch (Exception e) {
            System.out.println("Error getting subscriber info - " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {rs.close();}catch(Exception e){}
            try {stmt.close();}catch(Exception e){}
        }
        return result;
    }

    public static void log(
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
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage() + " : " + sql);
        }
    }
}
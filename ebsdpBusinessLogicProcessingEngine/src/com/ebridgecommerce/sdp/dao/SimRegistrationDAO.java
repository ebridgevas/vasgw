package com.ebridgecommerce.sdp.dao;

import com.ebridgecommerce.sdp.domain.StatusDTO;
import com.ebridgecommerce.sdp.domain.UssdMenuItem;
import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import com.ebridgecommerce.sdp.service.SimRegistrationServiceCommandProcessor;
import com.ebridgecommerce.sdp.util.Util;
import com.ebridgecommerce.sdp.util.Validator;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import zw.co.ebridge.smsc.SMSC;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationDAO {

    private static GenericObjectPool gPool;

    public static void init() throws SQLException, Exception {

        gPool = new GenericObjectPool();
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "changeit");
        ConnectionFactory cf = new DriverConnectionFactory(new com.mysql.jdbc.Driver(), "jdbc:mysql://localhost/telecel", props);

        KeyedObjectPoolFactory kopf = new GenericKeyedObjectPoolFactory(null, SMSC.DB_CONNECTION_POOL_SIZE);

        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, kopf, null, false, true);
        gPool.setMaxActive(50);
        for (int i = 0; i < SMSC.DB_CONNECTION_POOL_SIZE; i++) {
            gPool.addObject();
        }
// System.out.println(" a = " + gPool.getNumActive() + " i = " + gPool.getNumIdle() + " mi = " + gPool.getMaxIdle());
// System.out.println(" max = " + gPool.getMaxActive());
// PoolingDataSource pds = new PoolingDataSource(gPool);
        PoolingDriver pd = new PoolingDriver();
        pd.registerPool("vasDBCP", gPool);

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
            rs = SimRegistrationServiceCommandProcessor.statement.executeQuery(sql);
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
            String sql = " INSERT INTO simregister (ms_isdn, state, date_created ) " +
                    " VALUES ('" + msIsdn + "','main', now())";
            SimRegistrationServiceCommandProcessor.statement.executeUpdate(sql);
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
            SimRegistrationServiceCommandProcessor.statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            System.out.println("Error modifying registation info - " + e.getMessage());
            return false;
        } finally {
            try {rs.close();}catch(Exception e){}
        }
    }

}
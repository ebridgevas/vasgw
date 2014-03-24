package com.ebridgecommerce.sdp.util;

import com.ebridgecommerce.sdp.dao.UsagePromotionDAO;
import zw.co.ebridge.pps.client.PPSClient;
import zw.co.ebridge.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 9/1/12
 * Time: 4:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClassOfServiceFix {

    static List<String> subscribers = new ArrayList<String>();
    static Map<String, String> bands = new HashMap<String, String>();
    static PPSClient ppsClient = PPSClient.getInstance("zte");

    public static final Map<Short, String> ACCUMULATOR_COS;

    static {
        ACCUMULATOR_COS = new HashMap<Short, String>();
        ACCUMULATOR_COS.put(new Short("1"), "Band1");
        ACCUMULATOR_COS.put(new Short("2"), "Band2");
        ACCUMULATOR_COS.put(new Short("3"), "Band3");
        ACCUMULATOR_COS.put(new Short("4"), "Band4");
        ACCUMULATOR_COS.put(new Short("5"), "Band5");
        ACCUMULATOR_COS.put(new Short("6"), "Band6");
        ACCUMULATOR_COS.put(new Short("7"), "Band7");
    }

    public static void loadBands() {
        System.out.print("Loading subscriber bands .... ");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = UsagePromotionDAO.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select r.msisdn, band_id from register r, arpu_bands a where r.msisdn = a.msisdn and r.status='Active'");
            while (rs.next()){
               bands.put(rs.getString("msisdn"), ACCUMULATOR_COS.get(rs.getShort("band_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {rs.close();} catch(Exception e1){}
            try {stmt.close();} catch(Exception e1){}
            try {conn.close();} catch(Exception e1){}

            System.out.println( bands.size() + " loaded ");
        }
    }

    public static void loadSubscribersFromFile( String path) {
        System.out.print("Loading subscriber bands .... ");
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String msisdn = null;
            while ( ( msisdn = in.readLine()) != null) {
                // 734435370
                if (msisdn.length() > 7) {
                   subscribers.add("263" + msisdn.trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            System.out.println( subscribers.size() + " loaded ");
        }
    }

    public static void fixClassOfService() {
        System.out.println("Fixing class of service .... ");
        long count = 0;
        for (String msisdn : subscribers) {

            try {
                if ( (count % 100) == 0) {
                    System.out.print("..." + count);
                }
                System.out.println("Setting " + msisdn + " to " + bands.get(msisdn));
                if (bands.get(msisdn) != null) {
                    ppsClient.modifyCOS(msisdn, bands.get(msisdn));
                }
                ++count;
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static void main(String[] args) {
        loadSubscribersFromFile(args[0]);
        loadBands();
        fixClassOfService();
    }
}

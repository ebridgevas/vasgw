package com.ebridgevas.dao;

import com.ebridgevas.dto.TxnDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author david@ebridgevas.com
 *
 */
public class TxnDAO {

    private static Connection connection;

    static {
        try {
            connection = DataBaseConnectionPool.getConnection();
        } catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }
    }

    public static void persist(TxnDto txn) {

        if ( connection == null) {
            try {
                connection = DataBaseConnectionPool.getConnection();
            } catch (Exception e) {
                // TODO handle exception
                e.printStackTrace();
            }
        }

        String sql = " INSERT INTO txns (uuid, source_id, destination_id, delivery_channel, transaction_date," +
                     "                   transaction_type, product_code, amount, status_code, narrative," +
                     "                   short_message ) " +
                     " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setLong(  1, txn.getUuid().longValue());
            stmt.setString(2, txn.getSourceId());
            stmt.setString(3, txn.getDestinationId());
            stmt.setString(4, txn.getDeliveryChannel());
            stmt.setTimestamp( 5, new Timestamp(txn.getTransactionDate().getTime()));
            stmt.setString(6, txn.getTransactionType());
            stmt.setString(7, txn.getProductCode());
            stmt.setBigDecimal(8, txn.getAmount());
            stmt.setString(9, txn.getStatusCode());
            stmt.setString(10,txn.getNarrative());
            stmt.setString(11, txn.getShortMessage());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {stmt.close();} catch (Exception e){}
        }

    }


    public boolean isLimitReached(TxnDto txn, BigDecimal limit) {

        if ( connection == null) {
            try {
                connection = DataBaseConnectionPool.getConnection();
            } catch (Exception e) {
                // TODO handle exception
                e.printStackTrace();
            }
        }

        String sql = " INSERT INTO txns (uuid, source_id, destination_id, delivery_channel, transaction_date," +
                "                   transaction_type, product_code, amount, status_code, narrative," +
                "                   short_message ) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        // CREATE INDEX sourceid_transaction_date_type ON txns USING BTREE (source_id, transaction_date, transaction_type);

        sql = "SELECT sum(amount) as usageValue FROM txns WHERE source_id = ? AND transaction_date >= ?  AND " +
                                                          " transaction_type = 'DataBundlePurchase' AND status_code = '000' ";

        System.out.println("SQL::" + sql);
        System.out.println("LIMIT::" + limit);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, txn.getSourceId());
            stmt.setTimestamp(2, getBillingCycleStartDate() );

            rs = stmt.executeQuery();
            if (rs.next()) {

                try {
                    System.out.println("Usage + Amount::" + rs.getBigDecimal("usageValue").add(txn.getAmount()));
                    return limit.compareTo(rs.getBigDecimal("usageValue").add(txn.getAmount())) == -1;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("########## FATAL - Failed to check credit limit for " + txn.getSourceId() + " : " + e.getMessage() );
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {stmt.close();} catch (Exception e){}
            try {rs.close();} catch (Exception e){}
        }
        return false;
    }

    protected Timestamp getBillingCycleStartDate() {

        DateTime startFrom = new DateTime();

        DateTime today = new DateTime();

        if (today.getDayOfMonth() <= 25 ) {
            startFrom = startFrom.minusMonths(1);
        }

        startFrom = startFrom.withDayOfMonth(26).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);

        return new Timestamp(startFrom.toDate().getTime());
    }
//
//    public static void persist(TxnDto txnDto) {
//
//        if ( connection == null) {
//            try {
//                connection = DataBaseConnectionPool.getConnection();
//            } catch (Exception e) {
//                // TODO handle exception
//                e.printStackTrace();
//            }
//        }
//
//        /*
//        +---------------+---------------+------+-----+---------+-------+
//| UUID          | varchar(20)   | NO   |     |         |       |
//| SOURCEID      | varchar(20)   | YES  |     | NULL    |       |
//| DESTINATIONID | varchar(20)   | YES  |     | NULL    |       |
//| CHANNEL       | varchar(20)   | YES  |     | NULL    |       |
//| BENEFICIARYID | varchar(20)   | YES  |     | NULL    |       |
//| TXNTYPE       | varchar(20)   | YES  |     | NULL    |       |
//| AMOUNT        | decimal(18,2) | YES  |     | NULL    |       |
//| SESSIONID     | varchar(20)   | YES  |     | NULL    |       |
//| STATUS        | varchar(120)  | YES  |     | NULL    |       |
//| PRODUCTCODE   | varchar(20)   | YES  |     | NULL    |       |
//| NARRATIVE     | varchar(1024) | YES  |     | NULL    |       |
//| TXNDATE       | datetime      | YES  | MUL | NULL    |       |
//| SHORTMESSAGE  | varchar(1024) | YES  |     | NULL    |       |
//+---------------+---------------+------+-----+---------+-------+
//         */
//        String sql =
//                " INSERT INTO TXNS ( UUID, SOURCEID, DESTINATIONID, CHANNEL, BENEFICIARYID, TXNTYPE, AMOUNT, " +
//                        "          SESSIONID, STATUS, PRODUCTCODE, NARRATIVE, TXNDATE, SHORTMESSAGE) " +
//                        "   VALUES( " +
//                        "          '" + txnDto.getUuid() + "'," +
//                        "          '" + txnDto.getSourceId() + "'," +
//                        "          '" + txnDto.getDestinationId() + "'," +
//                        "          '" + txnDto.getChannel() + "'," +
//                        "          '" + txnDto.getBeneficiaryId() + "'," +
//                        "          '" + txnDto.getTxnType() + "'," +
//                        "           " + txnDto.getAmount() + "," +
//                        "          '" + txnDto.getSessionId() + "'," +
//                        "          '" + txnDto.getStatus() + "'," +
//                        "          '" + txnDto.getProductCode() + "'," +
//                        "          '" + txnDto.getNarrative() + "'," +
//                        "          '" + txnDto.getTxnDate() + "'," +
//                        "          '" + txnDto.getShortMessage() + "')";
//
//        Statement stmt = null;
//        ResultSet rs = null;
//        try {
//
//            stmt = connection.createStatement();
//            stmt.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
////            throw new DatabaseException( e.getMessage() );
//        } finally {
//            try {rs.close();} catch (Exception e){}
//            try {stmt.close();} catch (Exception e){}
//        }
//    }



    public static void main(String[] args) {
        TxnDto txn = new TxnDto(new BigInteger("" + System.currentTimeMillis()),"000","263733803480","manual", new Date());
        TxnDAO.persist(txn);
    }
}

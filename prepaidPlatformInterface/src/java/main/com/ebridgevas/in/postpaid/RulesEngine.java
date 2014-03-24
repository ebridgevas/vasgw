package com.ebridgevas.in.postpaid;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 2/19/14
 * Time: 9:30 AM
 *
 UUID          | varchar(20)   | NO   |     |         |       |
 | SOURCEID      | varchar(20)   | YES  |     | NULL    |       |
 | DESTINATIONID | varchar(20)   | YES  |     | NULL    |       |
 | CHANNEL       | varchar(20)   | YES  |     | NULL    |       |
 | BENEFICIARYID | varchar(20)   | YES  |     | NULL    |       |
 | AMOUNT        | decimal(18,2) | YES  |     | NULL    |       |
 | SESSIONID     | varchar(20)   | YES  |     | NULL    |       |
 | STATUS        | varchar(120)  | YES  | MUL | NULL    |       |
 | PRODUCTCODE   | varchar(20)   | YES  |     | NULL    |       |
 | NARRATIVE     | varchar(1024) | YES  |     | NULL    |       |
 | SHORTMESSAGE  | varchar(1024) | YES  |     | NULL    |       |
 | TXNTYPE       | varchar(255)  | YES  |     | NULL    |       |
 | TXNDATE       | varchar(255)  | YES  |     | NULL    |       |
 | TXNDATETIME   | datetime      | YES  |     | NULL    |       |
 | TXN_DATETIME
 *
 */
public class RulesEngine {


    /*
     *  Is limit for the month reached?
     */
    public void validPurchase() {

    }
}

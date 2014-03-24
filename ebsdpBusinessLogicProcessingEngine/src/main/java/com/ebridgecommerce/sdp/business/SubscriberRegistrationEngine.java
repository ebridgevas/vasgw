//package com.ebridgecommerce.sdp.business;
//
//import java.rmi.RemoteException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.logging.Level;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//
//import org.jsmpp.bean.*;
//import org.jsmpp.extra.ProcessRequestException;
//import org.jsmpp.session.DataSmResult;
//import org.jsmpp.session.MessageReceiverListener;
//import org.jsmpp.session.Session;
//import org.jsmpp.util.InvalidDeliveryReceiptException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import zw.co.telecel.akm.simreg.model.SubscriberRaw;
//
//import zw.co.telecel.akm.simreg.session.SubscriberRawFacadeLocal;
//import zw.co.telecel.akm.simreg.utils.Utils;
//
///**
//* SubscriberRegistrationEngine.
//*/
//public class SubscriberRegistrationEngine implements MessageReceiverListener {
//
//    private Utils utilities = new Utils();
//
//
//    private final Logger logger = LoggerFactory.getLogger(SMPPListener.class);
//    private String smscName;
//    private SMPPAdapter smppAdapter;
//
//    private ConcurrentMap<String, Object> map = new ConcurrentHashMap<String, Object>();
//
//
//    public void setSmppAdapter(SMPPAdapter smppAdapter) {
//    }
//
//    @Override
//    public void onAcceptAlertNotification(AlertNotification alertNotification) {
//    }
//
//    public String getSmscName() {
//        return smscName;
//    }
//
//    public void setSmscName(String smscName) {
//        this.smscName = smscName;
//    }
//
//    /**
//     * used to process all incoming SMPP messages convert them to xml then send
//     * them to processing incoming queue
//     */
//    @Override
//    public void process(DeliverSm deliverSm) throws ProcessRequestException {
//
//        String sourceMobile = deliverSm.getSourceAddr();
//        String destinationAddress = deliverSm.getDestAddress();
//        String message = new String(deliverSm.getShortMessage());
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
//        System.out.println("Source : " + sourceMobile);
//        System.out.println("Destination : " + destinationAddress);
//        System.out.println("Message : " + message);
//
//
//        String[] tokens = null;
//        tokens = message.split(" ");
//        int l = tokens.length;
//        int ourLength = l - 1;  //counting starts at zero
//
//        for (String msg : tokens) {
//            System.out.println("Msg : " + msg);
//        }
//        System.out.println("TransactionID : " + tokens[1]);
//        String transactionID = tokens[1];
//        String text = "";
//        System.out.println("tokens[0] : " + tokens[0]);
//        if ("74".equals(tokens[0])) {
//            text = tokens[7];
//            if (ourLength > 7) {
//                int iter = ourLength - 7;
//                for (int i = 1; i <= iter; i++) {
//                    text = text + " " + tokens[7 + i];
//                }
//            }
//            System.out.println("Text submitted : " + text);
//        }
//
//        // 76 Transaction ID  coding TEXT
//
//        Map<String, Object> tempMap = (Map<String, Object>) map.get(sourceMobile);
//        if (map.containsKey(sourceMobile)) {
//            int selection = (Integer) tempMap.get("selection");
//            int level = (Integer) tempMap.get("level");
//
//            if (("2".equals(text)) && (level == 0)) {
//                // balance enquiry
//
//                String actualMsg = "Exiting SIM Registration, Good Bye";
//                String msgHeaders;
//                String smsMessage;
//
//
//                msgHeaders = "81 " + transactionID + " Unicode ";
//                smsMessage = msgHeaders + actualMsg;
//                System.out.println("Exiting SIM Registration Message : " + smsMessage);
//                List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                for (SMPPAdapter s : aList) {
//                    s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                }
//                //  SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                map.remove(sourceMobile);
//
//            } else if ((level == 0) && !("1".equals(text)) && !("2".equals(text))) {
//                long timeout = 60000; //30 seconds
//                String msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                String actualMsg = "INVALID SELECTION\nPlease Select either 1 or 2\n 1. Register SIM \n 2. Exit";
//                String smsMessage = msgHeaders + actualMsg;
//                System.out.println("Service Re-Selection Message : " + smsMessage);
//                List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                for (SMPPAdapter s : aList) {
//                    s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                }
//                // SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//
//            } else if (("1".equals(text)) && (level == 0)) {
//                // start of sim registration
//                //enter first name
//                DBConnect db = new DBConnect();
//                if (db.checkMobileNumber(sourceMobile) == false) { //check if mobile Number is already in system
//                    String msgHeaders = "81 " + transactionID + " Unicode ";
//                    String actualMsg = "Error, You entered a Mobile Number that is already in use in the system.Please contact the Call Center";
//                    String smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                    map.remove(sourceMobile);
//                } else {
//                    long timeout = 60000; //30 seconds
//                    String msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    String actualMsg = "Please enter your first name";
//                    String smsMessage = msgHeaders + actualMsg;
//                    tempMap.put("level", 1);
//
//                    System.out.println("Please enter your first name Message : " + smsMessage);
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //   SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                }
//
//            } else if ((level == 1)) {
//                // first name entered
//
//                String actualMsg;
//                String msgHeaders;
//                String smsMessage;
//                if ((utilities.containsDigit(text) == true)) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error, You entered an invalid first name, please re-enter your first name";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//
//                } else if (text.isEmpty() || text == null) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error, first name cannot be empty, please re-enter your first name";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //  SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                } else {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Please enter your last name";
//                    smsMessage = msgHeaders + actualMsg;
//                    tempMap.put("level", 2);
//                    tempMap.put("name", text);
//
//
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                }
//
//
//            } else if ((level == 2)) {
//                // last name entered
//
//                String actualMsg;
//                String msgHeaders;
//                String smsMessage;
//
//                if ((utilities.containsDigit(text) == true)) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error, You entered an last name, please re-enter your last name";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    // SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//
//                } else if (text.isEmpty() || text == null) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error, last name cannot be empty, please re-enter your last name";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                } else {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Please enter your ID Number without spaces or any special characters";
//                    smsMessage = msgHeaders + actualMsg;
//                    tempMap.put("level", 3);
//                    tempMap.put("lastName", text);
//
//
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                }
//
//
//            } else if ((level == 3)) {
//                // id or passport number entered
//
//                String actualMsg;
//                String msgHeaders;
//                String smsMessage;
//
//                if ((utilities.validateIDNumber(text) == false)) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error, You entered an invalid ID Number, please re-enter your ID Number without spaces or any special characters";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    // SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//
//                } else if (text.isEmpty() || text == null) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error, ID Number cannot be empty, please re-enter your ID Number";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //  SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                } else {
//                    long timeout = 60000; //60 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Please enter your full address including city";
//                    smsMessage = msgHeaders + actualMsg;
//                    tempMap.put("level", 4);
//                    tempMap.put("id", text);
//
//
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                }
//            } else if ((level == 4)) {
//                // address entered
//                String actualMsg;
//                String msgHeaders;
//                String smsMessage;
//                if (text.isEmpty() || text == null) {
//                    long timeout = 60000; //30 seconds
//                    msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//                    actualMsg = "Error,address cannot be empty, please re-enter your full address including city";
//                    smsMessage = msgHeaders + actualMsg;
//                    List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                    for (SMPPAdapter s : aList) {
//                        s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                    }
//                    //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                } else {
//                    String subsriberAddress = text;
//                    String name = (String) tempMap.get("name");
//                    String lastName = (String) tempMap.get("lastName");
//                    String idNumber = (String) tempMap.get("id");
//                    String address = subsriberAddress;// (String)tempMap.get("address");
//
//                    //save details to database
//                    try {
//                        SubscriberRaw subscriber = new SubscriberRaw();
//                        subscriber.setMobileNumber(sourceMobile);
//                        subscriber.setFirstName(name);
//                        subscriber.setLastName(lastName);
//                        subscriber.setIdNumber(idNumber);
//                        System.out.println("TEST DATE : " + new Date());
//                        subscriber.setRegistrationDate(new Date());
//                        subscriber.setMedium("USSD");
//                        subscriber.setStatus("INACTIVE");
//                        subscriber.setModeration("FALSE");
//                        subscriber.setAddress(address);
//                        DBConnect db = new DBConnect();
//                        // subscriberManager.create(subscriber);
//                        boolean result = db.createSubscriber(subscriber);
//                        if (result) {
//                            System.out.println("**********************************************************************");
//                            System.out.println();
//                            System.out.println("RECEIVED SUBSCRIBER DETAILS");
//                            System.out.println();
//                            System.out.println("First Name : " + name);
//                            System.out.println("Last Name : " + lastName);
//                            System.out.println("ID/Passport Number : " + idNumber);
//                            System.out.println("Address  : " + subsriberAddress);
//                            System.out.println("Mobile Number  : " + sourceMobile);
//                            System.out.println();
//                            System.out.println("**********************************************************************");
//
//
//                            msgHeaders = "81 " + transactionID + " Unicode ";
//
//                            // actualMsg = "Hi "+name+",Thank you for registering with Telecel Zimbabwe,Go tell someone!";
//                            actualMsg = "Hi " + name + ",Thank you for registering your line. It will be active within 1hr, SO GO AHEAD TELL SOMEONE!";
//                            smsMessage = msgHeaders + actualMsg;
//                            System.out.println("Registration Successfull, Thank you. : " + smsMessage);
//                            List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                            for (SMPPAdapter s : aList) {
//                                s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                            }
//                            // SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                            map.remove(sourceMobile);
//
//                        } else {
//                            msgHeaders = "81 " + transactionID + " Unicode ";
//                            actualMsg = "Error,processing registration, please contact the call center";
//                            smsMessage = msgHeaders + actualMsg;
//                            List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                            for (SMPPAdapter s : aList) {
//                                s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                            }
//                            //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                            map.remove(sourceMobile);
//
//                        }
//
//
//                    } catch (Exception ex) {
//
//                        msgHeaders = "81 " + transactionID + " Unicode ";
//                        actualMsg = "Error,processing registration, please contact the call center";
//                        smsMessage = msgHeaders + actualMsg;
//                        List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//                        for (SMPPAdapter s : aList) {
//                            s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//                        }
//                        // SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//                        map.remove(sourceMobile);
//                        ex.printStackTrace();
//                    }
//
//
//                }
//
//
//            }
//
//
//        } else {
//            map.put(sourceMobile, new HashMap() {{
//                put("selection", 0);
//                put("level", 0);
//                put("name", "");
//                put("lastName", "");
//                put("id", "");
//                put("address", "");
//
//            }});
//            long timeout = 60000; //30 seconds
//            String msgHeaders = "72 " + transactionID + " " + timeout + " Unicode ";
//            String actualMsg = "Please Select either 1 or 2\n 1. Register SIM \n 2. Exit";
//            //String actualMsg = "Please Select either 1 or 2\n 1= $0.50 for 5mb\n 2= $45 for 550mb\n 3=$ 150 for 3000mb";
//            String smsMessage = msgHeaders + actualMsg;
//            System.out.println("Starter Message : " + smsMessage);
//            List<SMPPAdapter> aList = SMPPConnectionPool.getAdapters();
//            for (SMPPAdapter s : aList) {
//                s.submitSMS(destinationAddress, smsMessage, sourceMobile, transactionID);
//            }
//            //SMPPConnectionPool.getRandomAdapter().submitSMS(destinationAddress , smsMessage , sourceMobile,transactionID);
//        }
//
//    }
//
//}
//
//    @Override
//    public DataSmResult onAcceptDataSm(DataSm dataSm, Session session) throws ProcessRequestException {
//        return null;
//    }
//
//    public boolean test() {
//
//        return true;
//    }
//
//
//}
//

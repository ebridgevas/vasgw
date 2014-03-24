package com.ebridgevas.util;

import com.zw.ebridge.domain.USSDSession;
import zw.co.ebridge.domain.TxnType;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author david@ebridgevas.com
 *
 */
public class Utils {

    public static Boolean dumpUserSessionsToFile(
                            Map<String, USSDSession> userSessions, String systemId) throws FileNotFoundException {

        PrintWriter out = new PrintWriter(new File("/prod/ebridge/spool/" + systemId + "user.sessions.dump"));
        for (String mobileNumber : userSessions.keySet()) {
            USSDSession userSession = userSessions.get( mobileNumber );
            out.println( userSession.getMsisdn() + " " +
                         userSession.getSessionId() + " " +
                         userSession.getTxnType());
        }
        out.flush();
        return Boolean.TRUE;
    }

    public static Map<String, USSDSession> readUserSessionFromFile(String systemId) {
        Map<String, USSDSession> userSessions = new HashMap<String, USSDSession>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("/prod/ebridge/spool/" + systemId + "user.sessions.dump"));
            String line = null;
            while((line = in.readLine()) != null) {
                String[] tokens = line.split(" ");
                userSessions.put(tokens[0],
                        new USSDSession(tokens[0], tokens[1], TxnTypeParser.parse(tokens[2])));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new File("/prod/ebridge/spool/" + systemId + "user.sessions.dump").delete();
        } catch (Exception e) {
        }

        return userSessions;
    }

    /* Tests. */
    public static void main(String[] args) throws IOException {
        Map<String, USSDSession> userSessions = new HashMap<String, USSDSession>();
        userSessions.put("mobile1", new USSDSession("mobile1","sessionId1", TxnType.DATA_BUNDLE_PURCHASE));
        userSessions.put("mobile2", new USSDSession("mobile2","sessionId2", TxnType.DATA_BUNDLE_PURCHASE));
        userSessions.put("mobile3", new USSDSession("mobile3","sessionId3", TxnType.DATA_BUNDLE_PURCHASE));
        userSessions.put("mobile4", new USSDSession("mobile4","sessionId4", TxnType.DATA_BUNDLE_PURCHASE));
        userSessions.put("mobile5", new USSDSession("mobile5","sessionId5", TxnType.DATA_BUNDLE_PURCHASE));

        Utils.dumpUserSessionsToFile(userSessions, "196.2.77.23");

        Map<String, USSDSession> result = Utils.readUserSessionFromFile("196.2.77.23");
        for (String mobileNumber : result.keySet()) {
            USSDSession userSession = result.get(mobileNumber);
            System.out.println("### " + userSession.getMsisdn() + ", " + userSession.getSessionId() +
                                ", " + userSession.getTxnType());
        }
    }
}

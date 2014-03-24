package com.ebridgevas.util;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 10/31/13
 * Time: 9:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("rm -f /prod/ebridge/spool/user_sessions.dump");
    }
}

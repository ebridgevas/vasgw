package com.ebridgevas.util;

import java.io.IOException;

/**
 * @author david@ebridgevas.com
 *
 */
public class CommandExecutor {

    public static void execute(String command) throws IOException {
        Runtime.getRuntime().exec(command);
    }
}

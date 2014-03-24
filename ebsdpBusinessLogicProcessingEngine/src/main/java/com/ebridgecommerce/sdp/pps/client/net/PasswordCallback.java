package com.ebridgecommerce.sdp.pps.client.net;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 7/27/12
 * Time: 7:11 AM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class PasswordCallback implements CallbackHandler {

    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if ("zsmart2".equals(pc.getIdentifer())) {
                    pc.setPassword("zsmart2");
                } else if ("vas".equals(pc.getIdentifer())) {
                    pc.setPassword("changeit");
                }
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
}
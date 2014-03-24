package com.ebridgevas.in.util;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 9/3/13
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;
import com.comverse_in.prepaid.ccws.*;
/**
 * PWCallback for the Client
 */
public class PasswordCallback implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc =
                        (WSPasswordCallback)callbacks[i];

                if ("zsmart2".equals(pc.getIdentifer())) {
                    pc.setPassword("zsmart2");
                }
            }
            else {
                throw new UnsupportedCallbackException(callbacks[i],
                        "Unrecognized Callback");
            }
        }
    }
}

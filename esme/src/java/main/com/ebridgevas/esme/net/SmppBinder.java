package com.ebridgevas.esme.net;

import com.ebridge.sdp.smpp.ServerPDUEventListener;
import com.ebridge.sdp.smpp.Session;
import com.ebridge.sdp.smpp.TimeoutException;
import com.ebridge.sdp.smpp.WrongSessionStateException;
import com.ebridge.sdp.smpp.pdu.BindRequest;
import com.ebridge.sdp.smpp.pdu.BindResponse;
import com.ebridge.sdp.smpp.pdu.BindTransciever;
import com.ebridge.sdp.smpp.pdu.PDUException;
import com.ebridge.sdp.smpp.util.SmppParamaters;
import com.ebridgevas.model.SmppConfig;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/22/12
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmppBinder {

    private Session session;
    private SmppConfig config;
    private ServerPDUEventListener serverPDUEventListener;

    public static final String SYSTEM_TYPE;
    private static final Byte SMPP_VERSION;
    static {
        SYSTEM_TYPE = "EBridge";
        SMPP_VERSION = (byte) 0x34;
    }

    /**
     * SmppBinder
     * @param config
     * @param serverPDUEventListener
     */
    public SmppBinder(Session session, SmppConfig config, ServerPDUEventListener serverPDUEventListener) {
        this.session = session;
        this.config = config;
        this.serverPDUEventListener = serverPDUEventListener;
    }

    /**
     * Bind
     * @return
     * @throws com.ebridge.sdp.smpp.pdu.PDUException
     * @throws java.io.IOException
     * @throws com.ebridge.sdp.smpp.WrongSessionStateException
     * @throws com.ebridge.sdp.smpp.TimeoutException
     */
    public BindResponse bind() throws PDUException, IOException, WrongSessionStateException, TimeoutException {

        System.out.println("binding to " + config);
        BindRequest request = new BindTransciever();
        request.setSystemId(config.getSystemId());
        request.setPassword(config.getSystemPassword());
        request.setSystemType(SYSTEM_TYPE);
        request.setInterfaceVersion(SMPP_VERSION);
        request.setAddressRange(SmppParamaters.getAddressRange());
        System.out.println("########### bind request: " + request.debugString());
        return session.bind(request, serverPDUEventListener);
    }
}

package com.ebridge.sdp.vas.smpp.net;

import com.ebridge.sdp.smpp.*;
import com.ebridge.sdp.smpp.pdu.*;
import com.ebridge.sdp.smpp.util.SmppParamaters;
import com.ebridge.sdp.vas.dto.SmppConfig;
import org.apache.log4j.Logger;

import java.io.IOException;

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
    static Logger log = Logger.getLogger(SmppBinder.class.getName());

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
        log.info("binding to " + config);
        BindRequest request = new BindTransciever();
        request.setSystemId(config.getSystemId());
        request.setPassword(config.getSystemPassword());
        request.setSystemType(SYSTEM_TYPE);
        request.setInterfaceVersion(SMPP_VERSION);
        request.setAddressRange(SmppParamaters.getAddressRange());
        log.info("bind request: " + request.debugString());
        return session.bind(request, serverPDUEventListener);
    }
}

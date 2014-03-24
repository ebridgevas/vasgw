package com.ebridgevas.util;

import com.ebridge.sdp.smpp.TimeoutException;
import com.ebridge.sdp.smpp.WrongSessionStateException;
import com.ebridge.sdp.smpp.pdu.Address;
import com.ebridge.sdp.smpp.pdu.PDUException;
import com.ebridge.sdp.smpp.pdu.SubmitSM;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/23/12
 * Time: 1:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SubmitSmFactory {

    private static final byte TON;
    private static final byte NPI;
    static {
        TON = (byte) 1;
        NPI = (byte) 1;
    }

    /**
     *
     * @param sourceId
     * @param destinationId
     * @param destinationSmppPort e.g. (short)9280
     * @param priorityFlag e.g. (byte) 3
     * @param  registeredDelivery e.g (byte) 1
     * @throws com.ebridge.sdp.smpp.pdu.PDUException
     * @throws java.io.IOException
     * @throws com.ebridge.sdp.smpp.WrongSessionStateException
     * @throws com.ebridge.sdp.smpp.TimeoutException
     */
    public SubmitSM create (
                String sourceId,
                String destinationId,
                String shortMessage,
                Short destinationSmppPort,
                Byte priorityFlag,
                Byte registeredDelivery  )
            throws PDUException, IOException, WrongSessionStateException, TimeoutException {

        SubmitSM submitSM = new SubmitSM();
        submitSM.setSourceAddr(new Address(TON, NPI, sourceId));
        submitSM.setDestAddr( new Address(TON, NPI, destinationId));
        submitSM.setShortMessage( shortMessage );
        if (destinationSmppPort != null )
            submitSM.setDestinationPort(destinationSmppPort);
        submitSM.setPriorityFlag( priorityFlag );
        submitSM.setRegisteredDelivery( registeredDelivery );

        return submitSM;
    }
}

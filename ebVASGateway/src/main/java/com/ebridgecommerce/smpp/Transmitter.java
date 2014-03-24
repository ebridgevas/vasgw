/*
 * Copyright (c) 2003 - 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */
package com.ebridgecommerce.smpp;

import com.ebridgecommerce.smpp.pdu.PDU;
import com.ebridgecommerce.smpp.pdu.ValueNotSetException;

import java.io.IOException;


/**
 * Class <code>Transmitter</code> transmits PDUs over connection.
 * It is used by <code>Session</code>.
 *
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 * @see Connection
 * @see javax.sound.midi.Receiver
 * @see Session
 *
 */

public class Transmitter extends SmppObject
{
    /**
     * The connection object. It is used for transmitting the PDUs. It's
     * created outside of the <code>Transmitter</code> and passed to
     * transmitter as a constructor parameter.
     * @see Connection
     */
    private Connection connection = null;

    /**
     * Default constructor made protected as it's not desirable to
     * allow creation of <code>Transmitter</code> without providing
     * <code>Connection</code>.
     */
    protected Transmitter()
    {
    }

    /**
     * Creates <code>Transmitter</code> which uses provided
     * <code>Connection</code>. Typically the <code>connection</code>
     * parameter will be an instance of <code>TCPIPConnection</code> class.
     *
     * @param connection connection used for transmitting the PDUs
     */
    public Transmitter(Connection c)
    {
        connection = c;
    }

    /**
     * Assigns unique sequence number to PDU, if necessary, and sends its
     * data over connection.
     *
     * @param pdu the PDU to send
     *
     * @exception java.io.IOException exception during communication
     * @exception ValueNotSetException optional param not set but requested
     */
    public void send(PDU pdu)
    throws ValueNotSetException,
           IOException
    {
//        debug.enter(DCOM,this,"send");
        pdu.assignSequenceNumber();
        try {
//            debug.write(DCOM,"going to send pdu's data over connection");
            connection.send(pdu.getData());
//            debug.write(DCOM,"successfully sent pdu's data over connection");
        } finally {
//            debug.exit(DCOM,this);
        }
    }

}

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

import java.util.EventObject;


/**
 * The base class for events representing receiving a pdu by
 * receiver.
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 *
 */
public class ReceivedPDUEvent extends EventObject
{
	/**
     * The connection over which was the pdu received.
     */
    private transient Connection connection = null;

    /**
     * The received pdu.
     */
    private transient PDU pdu = null;
    
    /**
     * Construct event for pdu received over connection belonging
     * to the receiver.
     */
    public ReceivedPDUEvent(ReceiverBase source,
                            Connection connection, PDU pdu)
    {
        super(source);
        this.connection = connection;
        this.pdu = pdu;
    }        

    /**
     * Return the connection over which the pdu was received.
     */
    public Connection getConnection() { return connection; }
    
    /**
     * Return the received pdu.
     */
    public PDU getPDU() { return pdu; }

}

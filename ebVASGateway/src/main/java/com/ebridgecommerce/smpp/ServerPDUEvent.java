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

/**
 * Instance of this class is created and passed to
 * the <code>ServerPDUEventListener</code> by the <code>Receiver</code>.
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 *
 */
public class ServerPDUEvent extends ReceivedPDUEvent
{

	/**
     * Creates event for provided <code>Receiver</code> and
     * <code>Connection</code> with the received <code>PDU</code>.
     */
    public ServerPDUEvent(Receiver source,
                          Connection connection, PDU pdu)
    {
        super(source,connection,pdu);
    }

    /**
     * Returns the receiver thru which was received the PDU this
     * event relates to.
     */
    public Receiver getReceiver() { return (Receiver)getSource(); }

}

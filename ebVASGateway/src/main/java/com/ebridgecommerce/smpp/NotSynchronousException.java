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

/**
 * Exception <code>NotSynchronousException</code> is thrown when
 * <code>Session</code>'s (synchronous) method <code>receive</code> is called when
 * the <code>Receiver</code> is in asynchronous state, i.e when all PDUs received
 * from the SMSC are passed to an instance of implementation of
 * <code>ServerPDUListener</code> class.
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 *
 */

public class NotSynchronousException extends SmppException
{
	
	private Session session = null;

    public NotSynchronousException() { }

    public NotSynchronousException(Session session)
    {
        this.session = session;
    }

    public Session getSession() { return session; }

}

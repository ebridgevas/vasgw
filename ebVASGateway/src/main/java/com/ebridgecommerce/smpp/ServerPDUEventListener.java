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

import java.util.EventListener;

/**
 * The interface <code>ServerPDUEventListener</code> defines method for processing
 * of PDUs received by the <code>Receiver</code> from the SMSC.
 * Implementation of this interface is used when the communication with
 * the SMSC is <i>asynchronous</i>. The asynchronous communication means that
 * the <code>Session</code> after sending a request to the SMSC doesn't wait for
 * a response to the request sent, instead it returns null. All PDUs received from the SMSC,
 * i.e  both responses to the sent requests and requests sent on behalf of
 * the SMSC, are passed to the instance of <code>ServerPDUEventListener</code>
 * implementation class. Users of the library are expected to implement
 * the listener.
 * <emp>Important:</emp>The <code>handleEvent</code> method is called
 * from the receiver's thread context, so the implementation of the listener
 * should ensure that there is no deadloock, or at least not too much
 * time spent in the method.
 *
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 *
 * @see Receiver#setServerPDUEventListener(ServerPDUEventListener)
 * @see Session#setServerPDUEventListener(ServerPDUEventListener)
 *  
 */
public interface ServerPDUEventListener extends EventListener
{
    /**
     * Meant to process PDUs received from the SMSC.
     * This method is called by the <code>Receiver</code> whenever a
     * PDU is received from the SMSC.
     * @param request the request received from the SMSC
     */
    public abstract void handleEvent(ServerPDUEvent event);
    
}

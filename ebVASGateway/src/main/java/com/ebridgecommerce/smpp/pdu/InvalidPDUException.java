/*
 * Copyright (c) 2003 - 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */

package com.ebridgecommerce.smpp.pdu;

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class InvalidPDUException extends PDUException
{
    private Exception underlyingException = null;

    public InvalidPDUException(PDU pdu, Exception e)
    {
        super(pdu);
        underlyingException = e;
    }
    
    public InvalidPDUException(PDU pdu, String s)
    {
        super(pdu,s);
    }
    
    public String toString()
    {
        String s = super.toString();
        if (underlyingException != null) {
            s += "\nUnderlying exception: " + underlyingException.toString();
        }
        return s;
    }

    public Exception getException() { return underlyingException; }
}
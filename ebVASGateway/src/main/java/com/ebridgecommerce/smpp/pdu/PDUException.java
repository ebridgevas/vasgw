/*
 * Copyright (c) 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */

package com.ebridgecommerce.smpp.pdu;

import com.ebridgecommerce.smpp.SmppException;

/**
 * 
 * Incorrect format of PDU passed as a parameter or received from SMSC.
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class PDUException extends SmppException
{
    private transient PDU pdu = null;
    
    public PDUException()
    {
    }
    
    public PDUException(PDU pdu)
    {
        setPDU(pdu);
    }
    
    public PDUException(String s)
    {
        super(s);
    }
    
    public PDUException(PDU pdu, String s)
    {
        super(s);
        setPDU(pdu);
    }
    
    public String toString()
    {
        String s = super.toString();
        if (pdu != null) {
            s += "\nPDU debug string: " + pdu.debugString();
        }
        return s;
    }
    
    public void setPDU(PDU pdu) { this.pdu = pdu; }
    public PDU getPDU()         { return pdu; }
    public boolean hasPDU()     { return pdu != null; }
}

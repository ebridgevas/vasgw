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

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class UnknownCommandIdException extends PDUException
{
    private transient PDUHeader header = null;
    
    public UnknownCommandIdException()
    {
    }

    public UnknownCommandIdException(PDUHeader header)
    {
        this.header = header;
    }
    
    private void checkHeader()
    {
        if (header == null) {
            header = new PDUHeader();
        }
    }
    
    public int getCommandLength()
    {
        return header == null ? 0 : header.getCommandLength();
    }

    public int getCommandId()
    {
        return header == null ? 0 : header.getCommandId();
    }

    public int getCommandStatus()
    {
        return header == null ? 0 : header.getCommandStatus();
    }

    public int getSequenceNumber()
    {
        return header == null ? 0 : header.getSequenceNumber();
    }

}

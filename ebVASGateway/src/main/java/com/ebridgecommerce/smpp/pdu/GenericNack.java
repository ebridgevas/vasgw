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

import com.ebridgecommerce.smpp.Data;
import com.ebridgecommerce.smpp.util.ByteBuffer;
import com.ebridgecommerce.smpp.util.NotEnoughDataInByteBufferException;
import com.ebridgecommerce.smpp.util.TerminatingZeroNotFoundException;

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class GenericNack extends Response
{
    public GenericNack()
    {
        super(Data.GENERIC_NACK);
    }
    
    public GenericNack(int commandStatus, int sequenceNumber)
    {
        super(Data.GENERIC_NACK);
        setCommandStatus(commandStatus);
        setSequenceNumber(sequenceNumber);
    }
    
    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
    }
    
    public ByteBuffer getBody()
    {
        return null;
    }
    
    public String debugString()
    {
        String dbgs = "(genericnack: ";
        dbgs += super.debugString();
        dbgs += ")";
        return dbgs;
    }

}

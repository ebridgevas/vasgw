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
public class DeliverSMResp extends Response
{
    private String messageId = Data.DFLT_MSGID;

	public DeliverSMResp()
	{
	    super(Data.DELIVER_SM_RESP);
	}

    public void setBody(ByteBuffer buffer)
	throws NotEnoughDataInByteBufferException,
	       TerminatingZeroNotFoundException,
	       WrongLengthOfStringException
	{
	    setMessageId(buffer.removeCString());
    }

    public ByteBuffer getBody()
    {
	    ByteBuffer buffer = new ByteBuffer();
        buffer.appendCString(messageId);
	    return buffer;
    }

    public void setMessageId(String value)
    throws WrongLengthOfStringException {
        checkString(value, Data.SM_MSGID_LEN);
        messageId = value;
    }
    
    public String getMessageId() { return messageId; }

    public String debugString()
    {
        String dbgs = "(deliver_resp: ";
        dbgs += super.debugString();
        dbgs += getMessageId(); dbgs += " ";
        dbgs += debugStringOptional();
        dbgs += ") ";
        return dbgs;
    }
    
}
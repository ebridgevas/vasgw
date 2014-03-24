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
public class QuerySM extends Request
{
    private String messageId   = Data.DFLT_MSGID;
    private Address sourceAddr = new Address();

    public QuerySM()
    {
        super(Data.QUERY_SM);
    }

    protected Response createResponse()
    {
        return new QuerySMResp();
    }

    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
	    setMessageId(buffer.removeCString());
        sourceAddr.setData(buffer); // ?
    }

    public ByteBuffer getBody()
    {
	    ByteBuffer buffer = new ByteBuffer();
        buffer.appendCString(messageId);
        buffer.appendBuffer(getSourceAddr().getData());
	    return buffer;
    }
    public void setMessageId(String value)
    throws WrongLengthOfStringException {
        checkString(value, Data.SM_MSGID_LEN);
        messageId = value;
    }
    
    public void setSourceAddr(Address value) { sourceAddr = value; }
    public void setSourceAddr(String address)
    throws WrongLengthOfStringException {
        setSourceAddr(new Address(address));
    }
    
    public void setSourceAddr(byte ton, byte npi, String address)
    throws WrongLengthOfStringException {
        setSourceAddr(new Address(ton, npi, address));
    }
    
    public String getMessageId()   { return messageId; }
    public Address getSourceAddr() { return sourceAddr; }

    public String debugString()
    {
        String dbgs = "(query: ";
        dbgs += super.debugString();
        dbgs += getMessageId(); dbgs += " ";
        dbgs += getSourceAddr().debugString(); dbgs += " ";
        dbgs += debugStringOptional();
        dbgs += ") ";
        return dbgs;
    }
}
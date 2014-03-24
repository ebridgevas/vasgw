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

public class SubmitMultiSMResp extends Response
{
    private String messageId = Data.DFLT_MSGID;
    private UnsuccessSMEsList unsuccessSMEs = new UnsuccessSMEsList();

    public SubmitMultiSMResp()
    {
        super(Data.SUBMIT_MULTI_RESP);
    }

    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
        setMessageId(buffer.removeCString());
        unsuccessSMEs.setData(buffer);
    }

    public ByteBuffer getBody()
    throws ValueNotSetException
    {
        ByteBuffer buffer = new ByteBuffer();
        buffer.appendCString(messageId);
        buffer.appendBuffer(unsuccessSMEs.getData());
        return buffer;
    }

    public void setMessageId(String value)
    throws WrongLengthOfStringException {
        checkString(value, Data.SM_MSGID_LEN);
        messageId = value;
    }
    
    public void addUnsuccessSME(UnsuccessSME unsuccessSME)
    throws TooManyValuesException {
        unsuccessSMEs.addValue(unsuccessSME);
    }

    public String getMessageId()  { return messageId; }
    public short getNoUnsuccess() { return (short)unsuccessSMEs.getCount(); }
    public UnsuccessSME getUnsuccessSME(int i) {
        return (UnsuccessSME)unsuccessSMEs.getValue(i);
    }

    public String debugString()
    {
        String dbgs = "(submitmulti_resp: ";
        dbgs += super.debugString();
        dbgs += getMessageId(); dbgs += " ";
        dbgs += unsuccessSMEs.debugString(); dbgs += " ";
        dbgs += debugStringOptional();
        dbgs += ") ";
        return dbgs;
    }

    private class UnsuccessSMEsList extends ByteDataList
    {
        public UnsuccessSMEsList()
        {
            super(Data.SM_MAX_CNT_DEST_ADDR,1);
        }
        
        public ByteData createValue()
        {
            return new UnsuccessSME();
        }

        public String debugString()
        {
            return "(unsuccess_addr_list: " + super.debugString() + ")";
        }

    }

}

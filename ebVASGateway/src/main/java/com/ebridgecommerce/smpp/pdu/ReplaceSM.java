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

import java.io.UnsupportedEncodingException;


/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class ReplaceSM extends Request
{
    private String messageId = Data.DFLT_MSGID;
    private Address sourceAddr              = new Address();
    private String scheduleDeliveryTime     = Data.DFLT_SCHEDULE;
    private String validityPeriod           = Data.DFLT_VALIDITY;
    private byte registeredDelivery         = Data.DFLT_REG_DELIVERY;
    private byte smDefaultMsgId             = Data.DFLT_DFLTMSGID;
    private short smLength                  = Data.DFLT_MSG_LEN;
    private ShortMessage shortMessage       = new ShortMessage(Data.SM_MSG_LEN);

    public ReplaceSM()
    {
        super(Data.REPLACE_SM);
    }

    protected Response createResponse()
    {
        return new ReplaceSMResp();
    }

    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
        setMessageId(buffer.removeCString());
        sourceAddr.setData(buffer); // ?
        setScheduleDeliveryTime(buffer.removeCString());
        setValidityPeriod(buffer.removeCString());
        setRegisteredDelivery(buffer.removeByte());
        setSmDefaultMsgId(buffer.removeByte());
        setSmLength(decodeUnsigned(buffer.removeByte()));
        shortMessage.setData(buffer.removeBuffer(getSmLength()));
    }

    public ByteBuffer getBody()
    {
        ByteBuffer buffer = new ByteBuffer();
        buffer.appendCString(messageId);
        buffer.appendBuffer(getSourceAddr().getData());
        buffer.appendCString(getScheduleDeliveryTime());
        buffer.appendCString(getValidityPeriod());
        buffer.appendByte(getRegisteredDelivery());
        buffer.appendByte(getSmDefaultMsgId());
        buffer.appendByte(encodeUnsigned(getSmLength()));
        buffer.appendBuffer(shortMessage.getData());
        return buffer;
    }

    public void setMessageId(String value)
    throws WrongLengthOfStringException {
        checkString(value, Data.SM_MSGID_LEN);
        messageId = value;
    }
    
    public void setScheduleDeliveryTime(String value)
    throws WrongDateFormatException {
        checkDate(value);
        scheduleDeliveryTime = value;
    }
    
    public void setValidityPeriod(String value)
    throws WrongDateFormatException {
        checkDate(value);
        validityPeriod = value;
    }

    public void setShortMessage(String value)
    throws WrongLengthOfStringException
    {
        shortMessage.setMessage(value);
        setSmLength((short)shortMessage.getLength());
    }

    public void setShortMessage(String value, String encoding)
    throws WrongLengthOfStringException,
           UnsupportedEncodingException
    {
        shortMessage.setMessage(value,encoding);
        setSmLength((short)shortMessage.getLength());
    }

    public void setSourceAddr(Address value)          { sourceAddr = value; }
    public void setSourceAddr(String address) throws WrongLengthOfStringException
                                                      { setSourceAddr(new Address(address)); }
    public void setSourceAddr(byte ton, byte npi, String address) throws WrongLengthOfStringException
                                                      { setSourceAddr(new Address(ton, npi, address)); }
    public void setRegisteredDelivery(byte value)     { registeredDelivery = value; }
    public void setSmDefaultMsgId(byte value)         { smDefaultMsgId = value; }
    // setSmLength() is private as it's set to length of the message
    private void setSmLength(short value)             { smLength = value; }

    
    public String getMessageId()            { return messageId; }
    public String getScheduleDeliveryTime() { return scheduleDeliveryTime; }
    public String getValidityPeriod()       { return validityPeriod; }
    public String getShortMessage()         { return shortMessage.getMessage(); }
    public String getShortMessage(String encoding)
    throws UnsupportedEncodingException     { return shortMessage.getMessage(encoding); }
    public Address getSourceAddr()          { return sourceAddr; }
    public byte getRegisteredDelivery()     { return registeredDelivery; }
    public byte getSmDefaultMsgId()         { return smDefaultMsgId; }
    public short getSmLength()              { return smLength; }

    public String debugString()
    {
        String dbgs = "(replace: ";
        dbgs += super.debugString();
        dbgs += getMessageId(); dbgs += " ";
        dbgs += getSourceAddr().debugString(); dbgs += " ";
        dbgs += shortMessage.debugString(); dbgs += " ";
        dbgs += debugStringOptional();
        dbgs += ") ";
        return dbgs;
    }
}

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
import com.ebridgecommerce.smpp.pdu.tlv.*;
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
public class SubmitMultiSM extends Request
{
    // mandatory parameters
    private String serviceType              = Data.DFLT_SRVTYPE;
    private Address sourceAddr              = new Address();
    private DestAddressList destAddresses   = new DestAddressList();
    private byte esmClass                   = Data.DFLT_ESM_CLASS;
    private byte protocolId                 = Data.DFLT_PROTOCOLID;
    private byte priorityFlag               = Data.DFLT_PRIORITY_FLAG;
    private String scheduleDeliveryTime     = Data.DFLT_SCHEDULE;
    private String validityPeriod           = Data.DFLT_VALIDITY;
    private byte registeredDelivery         = Data.DFLT_REG_DELIVERY;
    private byte replaceIfPresentFlag       = Data.DFTL_REPLACE_IFP;
    private byte dataCoding                 = Data.DFLT_DATA_CODING;
    private byte smDefaultMsgId             = Data.DFLT_DFLTMSGID;
    private short smLength                  = Data.DFLT_MSG_LEN;
    private ShortMessage shortMessage       = new ShortMessage(Data.SM_MSG_LEN);
    
    // optional parameters
    private TLVShort  userMessageReference  = new TLVShort(Data.OPT_PAR_USER_MSG_REF);
    private TLVShort  sourcePort            = new TLVShort(Data.OPT_PAR_SRC_PORT);
    private TLVByte   sourceAddrSubunit     = new TLVByte(Data.OPT_PAR_SRC_ADDR_SUBUNIT);
    private TLVShort  destinationPort       = new TLVShort(Data.OPT_PAR_DST_PORT);
    private TLVByte   destAddrSubunit       = new TLVByte(Data.OPT_PAR_DST_ADDR_SUBUNIT);
    private TLVShort  sarMsgRefNum          = new TLVShort(Data.OPT_PAR_SAR_MSG_REF_NUM);
    private TLVUByte  sarTotalSegments      = new TLVUByte(Data.OPT_PAR_SAR_TOT_SEG);
    private TLVUByte  sarSegmentSeqnum      = new TLVUByte(Data.OPT_PAR_SAR_SEG_SNUM);
    private TLVByte   payloadType           = new TLVByte(Data.OPT_PAR_PAYLOAD_TYPE);
    private TLVOctets messagePayload        = new TLVOctets(Data.OPT_PAR_MSG_PAYLOAD,Data.OPT_PAR_MSG_PAYLOAD_MIN,Data.OPT_PAR_MSG_PAYLOAD_MAX);
    private TLVByte   privacyIndicator      = new TLVByte(Data.OPT_PAR_PRIV_IND);
    private TLVOctets callbackNum           = new TLVOctets(Data.OPT_PAR_CALLBACK_NUM,Data.OPT_PAR_CALLBACK_NUM_MIN,Data.OPT_PAR_CALLBACK_NUM_MAX); // 4-19
    private TLVByte   callbackNumPresInd    = new TLVByte(Data.OPT_PAR_CALLBACK_NUM_PRES_IND);
    private TLVOctets callbackNumAtag       = new TLVOctets(Data.OPT_PAR_CALLBACK_NUM_ATAG,Data.OPT_PAR_CALLBACK_NUM_ATAG_MIN,Data.OPT_PAR_CALLBACK_NUM_ATAG_MAX); // 1-65
    private TLVOctets sourceSubaddress      = new TLVOctets(Data.OPT_PAR_SRC_SUBADDR,Data.OPT_PAR_SRC_SUBADDR_MIN,Data.OPT_PAR_SRC_SUBADDR_MAX); // 2-23 
    private TLVOctets destSubaddress        = new TLVOctets(Data.OPT_PAR_DEST_SUBADDR,Data.OPT_PAR_DEST_SUBADDR_MIN,Data.OPT_PAR_DEST_SUBADDR_MAX);
    private TLVByte   displayTime           = new TLVByte(Data.OPT_PAR_DISPLAY_TIME);
    private TLVShort  smsSignal             = new TLVShort(Data.OPT_PAR_SMS_SIGNAL);
    private TLVByte   msValidity            = new TLVByte(Data.OPT_PAR_MS_VALIDITY);
    private TLVByte   msMsgWaitFacilities   = new TLVByte(Data.OPT_PAR_MSG_WAIT); // bit mask
    private TLVEmpty  alertOnMsgDelivery    = new TLVEmpty(Data.OPT_PAR_ALERT_ON_MSG_DELIVERY);
    private TLVByte   languageIndicator     = new TLVByte(Data.OPT_PAR_LANG_IND);


    public SubmitMultiSM()
    {
        super(Data.SUBMIT_MULTI);

        registerOptional(userMessageReference);
        registerOptional(sourcePort);
        registerOptional(sourceAddrSubunit);
        registerOptional(destinationPort);
        registerOptional(destAddrSubunit);
        registerOptional(sarMsgRefNum);
        registerOptional(sarTotalSegments);
        registerOptional(sarSegmentSeqnum);
        registerOptional(payloadType);
        registerOptional(messagePayload);
        registerOptional(privacyIndicator);
        registerOptional(callbackNum);
        registerOptional(callbackNumPresInd);
        registerOptional(callbackNumAtag);
        registerOptional(sourceSubaddress);
        registerOptional(destSubaddress);
        registerOptional(displayTime);
        registerOptional(smsSignal);
        registerOptional(msValidity);
        registerOptional(msMsgWaitFacilities);
        registerOptional(alertOnMsgDelivery);
        registerOptional(languageIndicator);
    }

    protected Response createResponse()
    {
        return new SubmitMultiSMResp();
    }

    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
        setServiceType(buffer.removeCString());
        sourceAddr.setData(buffer); // ?
        destAddresses.setData(buffer);
        setEsmClass(buffer.removeByte());
        setProtocolId(buffer.removeByte());
        setPriorityFlag(buffer.removeByte());
        setScheduleDeliveryTime(buffer.removeCString());
        setValidityPeriod(buffer.removeCString());
        setRegisteredDelivery(buffer.removeByte());
        setReplaceIfPresentFlag(buffer.removeByte());
        setDataCoding(buffer.removeByte());
        setSmDefaultMsgId(buffer.removeByte());
        setSmLength(decodeUnsigned(buffer.removeByte()));
        shortMessage.setData(buffer.removeBuffer(getSmLength()));
    }

    public ByteBuffer getBody()
    throws ValueNotSetException
    {
        ByteBuffer buffer = new ByteBuffer();
        buffer.appendCString(getServiceType());
        buffer.appendBuffer(getSourceAddr().getData());
        buffer.appendBuffer(destAddresses.getData());
        buffer.appendByte(getEsmClass());
        buffer.appendByte(getProtocolId());
        buffer.appendByte(getPriorityFlag());
        buffer.appendCString(getScheduleDeliveryTime());
        buffer.appendCString(getValidityPeriod());
        buffer.appendByte(getRegisteredDelivery());
        buffer.appendByte(getReplaceIfPresentFlag());
        buffer.appendByte(getDataCoding());
        buffer.appendByte(getSmDefaultMsgId());
        buffer.appendByte(encodeUnsigned(getSmLength()));
        buffer.appendBuffer(shortMessage.getData());
        return buffer;
    }

    public void setServiceType(String value)
    throws WrongLengthOfStringException {
        checkCString(value, Data.SM_SRVTYPE_LEN);
        serviceType = value;
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
    public void setSourceAddr(String address)
    throws WrongLengthOfStringException {
        setSourceAddr(new Address(address));
    }
    public void setSourceAddr(byte ton, byte npi, String address)
    throws WrongLengthOfStringException {
        setSourceAddr(new Address(ton, npi, address));
    }
    
    public void addDestAddress(DestinationAddress destAddr)
    throws TooManyValuesException {
        destAddresses.addValue(destAddr);
    }

    public void setEsmClass(byte value)               { esmClass = value; }
    public void setProtocolId(byte value)             { protocolId = value; }
    public void setPriorityFlag(byte value)           { priorityFlag = value; }
    public void setRegisteredDelivery(byte value)     { registeredDelivery = value; }
    public void setReplaceIfPresentFlag(byte value)   { replaceIfPresentFlag = value; }
    public void setDataCoding(byte value)             { dataCoding = value; }
    public void setSmDefaultMsgId(byte value)         { smDefaultMsgId = value; }
    // setSmLength() is private as it's set to length of the message
    private void setSmLength(short value)             { smLength = value; }

    
    public String getServiceType()          { return serviceType; }
    public String getScheduleDeliveryTime() { return scheduleDeliveryTime; }
    public String getValidityPeriod()       { return validityPeriod; }
    public String getShortMessage()         { return shortMessage.getMessage(); }
    public String getShortMessage(String encoding)
    throws UnsupportedEncodingException     { return shortMessage.getMessage(encoding); }
    public Address getSourceAddr()          { return sourceAddr; }
    public short getNumberOfDests()         { return (short)destAddresses.getCount(); }
    public DestinationAddress getDestAddress(int i) {
        return (DestinationAddress)destAddresses.getValue(i);
    }
    public byte getEsmClass()               { return esmClass; }
    public byte getProtocolId()             { return protocolId; }
    public byte getPriorityFlag()           { return priorityFlag; }
    public byte getRegisteredDelivery()     { return registeredDelivery; }
    public byte getReplaceIfPresentFlag()   { return replaceIfPresentFlag; }
    public byte getDataCoding()             { return dataCoding; }
    public byte getSmDefaultMsgId()         { return smDefaultMsgId; }
    public short getSmLength()              { return smLength; }

    public boolean hasUserMessageReference()   { return userMessageReference.hasValue(); }
    public boolean hasSourcePort()             { return sourcePort.hasValue(); }
    public boolean hasSourceAddrSubunit()      { return sourceAddrSubunit.hasValue(); }
    public boolean hasDestinationPort()        { return destinationPort.hasValue(); }
    public boolean hasDestAddrSubunit()        { return destAddrSubunit.hasValue(); }
    public boolean hasSarMsgRefNum()           { return sarMsgRefNum.hasValue(); }
    public boolean hasSarTotalSegments()       { return sarTotalSegments.hasValue(); }
    public boolean hasSarSegmentSeqnum()       { return sarSegmentSeqnum.hasValue(); }
    public boolean hasPayloadType()            { return payloadType.hasValue(); }
    public boolean hasMessagePayload()         { return messagePayload.hasValue(); }
    public boolean hasPrivacyIndicator()       { return privacyIndicator.hasValue(); }
    public boolean hasCallbackNum()            { return callbackNum.hasValue(); }
    public boolean hasCallbackNumPresInd()     { return callbackNumPresInd.hasValue(); }
    public boolean hasCallbackNumAtag()        { return callbackNumAtag.hasValue(); }
    public boolean hasSourceSubaddress()       { return sourceSubaddress.hasValue(); } 
    public boolean hasDestSubaddress()         { return destSubaddress.hasValue(); }
    public boolean hasDisplayTime()            { return displayTime.hasValue(); }
    public boolean hasSmsSignal()              { return smsSignal.hasValue(); }
    public boolean hasMsValidity()             { return msValidity.hasValue(); }
    public boolean hasMsMsgWaitFacilities()    { return msMsgWaitFacilities.hasValue(); }
    public boolean hasAlertOnMsgDelivery()     { return alertOnMsgDelivery.hasValue(); }
    public boolean hasLanguageIndicator()      { return languageIndicator.hasValue(); }

    public void setUserMessageReference(short value)  { userMessageReference.setValue(value); }
    public void setSourcePort(short value)            { sourcePort.setValue(value); }
    public void setSourceAddrSubunit(byte value)      { sourceAddrSubunit.setValue(value); }
    public void setDestinationPort(short value)       { destinationPort.setValue(value); }
    public void setDestAddrSubunit(byte value)        { destAddrSubunit.setValue(value); }
    public void setSarMsgRefNum(short value)          { sarMsgRefNum.setValue(value); }
    public void setSarTotalSegments(short value)
    throws IntegerOutOfRangeException                 { sarTotalSegments.setValue(value); }
    public void setSarSegmentSeqnum(short value)
    throws IntegerOutOfRangeException                 { sarSegmentSeqnum.setValue(value); }
    public void setPayloadType(byte value)            { payloadType.setValue(value); }
    public void setMessagePayload(ByteBuffer value)   { messagePayload.setValue(value); }
    public void setPrivacyIndicator(byte value)       { privacyIndicator.setValue(value); }
    public void setCallbackNum(ByteBuffer value)      { callbackNum.setValue(value); }
    public void setCallbackNumPresInd(byte value)     { callbackNumPresInd.setValue(value); }
    public void setCallbackNumAtag(ByteBuffer value)  { callbackNumAtag.setValue(value); }
    public void setSourceSubaddress(ByteBuffer value) { sourceSubaddress.setValue(value); } 
    public void setDestSubaddress(ByteBuffer value)   { destSubaddress.setValue(value); }
    public void setDisplayTime(byte value)            { displayTime.setValue(value); }
    public void setSmsSignal(short value)             { smsSignal.setValue(value); }
    public void setMsValidity(byte value)             { msValidity.setValue(value); }
    public void setMsMsgWaitFacilities(byte value)    { msMsgWaitFacilities.setValue(value); }
    public void setAlertOnMsgDelivery(boolean value)  { alertOnMsgDelivery.setValue(value); }
    public void setLanguageIndicator(byte value)      { languageIndicator.setValue(value); }

    public short getUserMessageReference()
    throws ValueNotSetException { return userMessageReference.getValue(); }

    public short getSourcePort()
    throws ValueNotSetException { return sourcePort.getValue(); }
    
    public byte getSourceAddrSubunit() 
    throws ValueNotSetException { return sourceAddrSubunit.getValue(); }
    
    public short getDestinationPort() 
    throws ValueNotSetException { return destinationPort.getValue(); }
    
    public byte getDestAddrSubunit() 
    throws ValueNotSetException { return destAddrSubunit.getValue(); }
    
    public short getSarMsgRefNum() 
    throws ValueNotSetException { return sarMsgRefNum.getValue(); }
    
    public short getSarTotalSegments()
    throws ValueNotSetException { return sarTotalSegments.getValue(); }
    
    public short getSarSegmentSeqnum()
    throws ValueNotSetException { return sarSegmentSeqnum.getValue(); }
    
    public byte getPayloadType() 
    throws ValueNotSetException { return payloadType.getValue(); }
    
    public ByteBuffer getMessagePayload() 
    throws ValueNotSetException { return messagePayload.getValue(); }
    
    public byte getPrivacyIndicator() 
    throws ValueNotSetException { return privacyIndicator.getValue(); }
    
    public ByteBuffer callbackNum() 
    throws ValueNotSetException { return callbackNum.getValue(); }
    
    public byte getCallbackNumPresInd() 
    throws ValueNotSetException { return callbackNumPresInd.getValue(); }
    
    public ByteBuffer getCallbackNumAtag() 
    throws ValueNotSetException { return callbackNumAtag.getValue(); }
    
    public ByteBuffer getSourceSubaddress() 
    throws ValueNotSetException { return sourceSubaddress.getValue(); }
        
    public ByteBuffer getDestSubaddress() 
    throws ValueNotSetException { return destSubaddress.getValue(); }
    
    public byte getDisplayTime() 
    throws ValueNotSetException { return displayTime.getValue(); }
    
    public short getSmsSignal() 
    throws ValueNotSetException { return smsSignal.getValue(); }
    
    public byte getMsValidity() 
    throws ValueNotSetException { return msValidity.getValue(); }
    
    public byte getMsMsgWaitFacilities() 
    throws ValueNotSetException { return msMsgWaitFacilities.getValue(); }
    
    public boolean getAlertOnMsgDelivery() 
    throws ValueNotSetException { return alertOnMsgDelivery.getValue(); }
    
    public byte getLanguageIndicator() 
    throws ValueNotSetException { return languageIndicator.getValue(); }
    
    public String debugString()
    {
        String dbgs = "(submitmulti: ";
        dbgs += super.debugString();
        dbgs += getSourceAddr().debugString();
        dbgs += destAddresses.debugString(); dbgs += " ";
        dbgs += shortMessage.debugString(); dbgs += " ";
        dbgs += debugStringOptional();
        dbgs += ") ";
        return dbgs;
    }
    
    private class DestAddressList extends ByteDataList
    {
        public DestAddressList()
        {
            super(Data.SM_MAX_CNT_DEST_ADDR,1);
        }
        
        public ByteData createValue()
        {
            return new DestinationAddress();
        }

        public String debugString()
        {
            return "(dest_addr_list: " + super.debugString() + ")";
        }

    }

}

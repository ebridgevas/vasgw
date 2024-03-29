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
public class AddressRange extends ByteData
{
    private byte ton            = Data.getDefaultTon();
    private byte npi            = Data.getDefaultNpi();
    private String addressRange = Data.DFLT_ADDR_RANGE;
    
    public AddressRange()
    {
        super();
        setTon(Data.getDefaultTon());
        setNpi(Data.getDefaultNpi());
    }
    
    public AddressRange(String addressRange)
    throws WrongLengthOfStringException
    {
        super();
        setTon(Data.getDefaultTon());
        setNpi(Data.getDefaultNpi());
        setAddressRange(addressRange);
    }
    
    public AddressRange(byte ton, byte npi, String addressRange)
    throws WrongLengthOfStringException
    {
        setTon(ton);
        setNpi(npi);
        setAddressRange(addressRange);
    }
    
    public void setData(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           WrongLengthOfStringException
    {
        byte ton = buffer.removeByte();
        byte npi = buffer.removeByte();
        String addressRange = buffer.removeCString();
        setAddressRange(addressRange);
        setTon(ton);
        setNpi(npi);
    }

    public ByteBuffer getData()
    {
        ByteBuffer addressBuf = new ByteBuffer();
        addressBuf.appendByte(getTon());
        addressBuf.appendByte(getNpi());
        addressBuf.appendCString(getAddressRange());
        return addressBuf;
    }
    
    public void setTon(byte t) { ton = t; }
    public void setNpi(byte n) { npi = n; }
    public void setAddressRange(String a)
    throws WrongLengthOfStringException {
        checkCString(a,Data.SM_ADDR_RANGE_LEN);
        addressRange = a;
    }
    
    public byte getTon()            { return ton; }
    public byte getNpi()            { return npi; }
    public String getAddressRange() { return addressRange; }
    
    public String debugString()
    {
        String dbgs = "(addrrang: ";
        dbgs += super.debugString();
        dbgs += Integer.toString(getTon()); dbgs += " ";
        dbgs += Integer.toString(getNpi()); dbgs += " ";
        dbgs += getAddressRange();
        dbgs += ") ";
        return dbgs;
    }
    
}
/*
 * Copyright (c) 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */

package com.ebridgecommerce.smpp.pdu.tlv;

import com.ebridgecommerce.smpp.pdu.ValueNotSetException;
import com.ebridgecommerce.smpp.util.ByteBuffer;
import com.ebridgecommerce.smpp.util.NotEnoughDataInByteBufferException;

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class TLVOctets extends TLV
{
    private ByteBuffer value = null;
    
    public TLVOctets()
    {
        super();
    }
    
    public TLVOctets(short p_tag)
    {
        super(p_tag);
    }
    
    public TLVOctets(short p_tag, int min, int max)
    {
        super(p_tag,min,max);
    }
    
    public TLVOctets(short p_tag, ByteBuffer p_value)
    throws TLVException
    {
        super(p_tag);
        setValueData(p_value);
    }
    
    public TLVOctets(short p_tag, int min, int max, ByteBuffer p_value)
    throws TLVException
    {
        super(p_tag, min, max);
        setValueData(p_value);
    }
    
    protected void setValueData(ByteBuffer buffer)
    throws TLVException
    {
        checkLength(buffer);
        if (buffer != null) {
            try {
                value = buffer.removeBuffer(buffer.length());
            } catch (NotEnoughDataInByteBufferException e) {
                throw new Error("Removing buf.length() data from ByteBuffer buf " +
                                "reported too little data in buf, which shouldn't happen.");
            }
        } else {
            value = null;
        }
        markValueSet();
    }
    
    protected ByteBuffer getValueData()
    throws ValueNotSetException
    {
        ByteBuffer valueBuf = new ByteBuffer();
        valueBuf.appendBuffer(getValue());
        return valueBuf;
    }

    public void setValue(ByteBuffer p_value)
    {
        if (p_value != null) {
            try {
                value = p_value.removeBuffer(p_value.length());
            } catch (NotEnoughDataInByteBufferException e) {
                throw new Error("Removing buf.length() data from ByteBuffer buf " +
                                "reported too little data in buf, which shouldn't happen.");
            }
        } else {
            value = null;
        }
        markValueSet();
    }

    public ByteBuffer getValue()
    throws ValueNotSetException
    {
        if (hasValue()) {
            return value;
        } else {
            throw new ValueNotSetException();
        }
    }

    public String debugString()
    {        
        String dbgs = "(oct: ";
        dbgs += super.debugString();
        dbgs += value==null ? "" : value.getHexDump();
        dbgs += ") ";
        return dbgs;
    }
    
}

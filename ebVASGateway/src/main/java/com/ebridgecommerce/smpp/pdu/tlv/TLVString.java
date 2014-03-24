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
import com.ebridgecommerce.smpp.util.TerminatingZeroNotFoundException;

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */

public class TLVString extends TLV
{
    private String value;

    public TLVString()
    {
        super();
    }
    
    public TLVString(short tag)
    {
        super(tag);
    }
    
    public TLVString(short tag, int min, int max)
    {
        super(tag,min,max);
    }
    
    public TLVString(short tag, String value)
    throws TLVException
    {
        super(tag);
        setValue(value);
    }
    
    public TLVString(short tag, int min, int max, String value)
    throws TLVException
    {
        super(tag, min, max);
        setValue(value);
    }
    
    public void setValueData(ByteBuffer buffer)
    throws TLVException
    {
        checkLength(buffer);
        if (buffer != null) {
            try {
                value = buffer.removeCString();
            } catch (NotEnoughDataInByteBufferException e) {
                throw new TLVException("Not enough data for string in the buffer.");
            } catch (TerminatingZeroNotFoundException e) {
                throw new TLVException("String terminating zero not found in the buffer.");
            }
        } else {
            value = new String("");
        }
        markValueSet();
    }
    
    public ByteBuffer getValueData()
    throws ValueNotSetException
    {
        ByteBuffer valueBuf = new ByteBuffer();
        valueBuf.appendCString(getValue());
        return valueBuf;
    }
    
    public void setValue(String value)
    throws WrongLengthException
    {
        checkLength(value.length() + 1);
        this.value = value;
        markValueSet();
    }
    
    public String getValue()
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
        String dbgs = "(str: ";
        dbgs += super.debugString();
        dbgs += value;
        dbgs += ") ";
        return dbgs;
    }

}

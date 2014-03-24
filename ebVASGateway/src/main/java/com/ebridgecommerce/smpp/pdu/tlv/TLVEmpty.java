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

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class TLVEmpty extends TLV
{
    private boolean present = false;
    
    public TLVEmpty()
    {
        super(0,0);
    }
    
    public TLVEmpty(short p_tag)
    {
        super(p_tag,0,0);
    }
    
    public TLVEmpty(short p_tag, boolean p_present)
    {
        super(p_tag,0,0);
        present = p_present;
        markValueSet();
    }
    
    public ByteBuffer getValueData()
    {
        // nothing, just present or not
        return null;
    }
    
    public void setValueData(ByteBuffer buffer)
    throws WrongLengthException
    {
        // nothing, just set presence
        checkLength(buffer);
        setValue(true);
    }

    public void setValue(boolean p_present)
    {
        present = p_present;
        markValueSet();
    }

    public boolean getValue()
    throws ValueNotSetException
    {
        if (hasValue()) {
            return present;
        } else {
            throw new ValueNotSetException();
        }
    }

    public String debugString()
    {        
        String dbgs = "(empty: ";
        dbgs += super.debugString();
        dbgs += ") ";
        return dbgs;
    }
   
}
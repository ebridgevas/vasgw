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
public class DistributionList extends ByteData
{
    private String dlName = Data.DFLT_DL_NAME;
    
    public DistributionList()
    {
    }
    
    public DistributionList(String dlName)
    throws WrongLengthOfStringException
    {
        setDlName(dlName);
    }
    
    public void setData(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           WrongLengthOfStringException
    {
        setDlName(buffer.removeCString());
    }
    
    public ByteBuffer getData()
    {
        ByteBuffer buffer = new ByteBuffer();
        buffer.appendCString(getDlName());
        return buffer;
    }
    
    public void setDlName(String dln)
    throws WrongLengthOfStringException {
        checkCString(dln,Data.SM_DL_NAME_LEN);
        dlName = dln;
    }
    
    public String getDlName() { return dlName; }
    
    public String debugString()
    {
        String dbgs = "(dl: ";
        dbgs += super.debugString();
        dbgs += getDlName();
        dbgs += ") ";
        return dbgs;
    }
    
}
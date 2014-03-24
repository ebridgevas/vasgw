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
public class UnsuccessSME extends Address
{
    public int errorStatusCode = Data.ESME_ROK;
    
    public UnsuccessSME()
    {
    }
    
    public UnsuccessSME(String address, int err)
    throws WrongLengthOfStringException
    {
        super(address);
        setErrorStatusCode(err);
    }
    
    public UnsuccessSME(byte ton, byte npi, String address, int err)
    throws WrongLengthOfStringException
    {
        super(ton,npi,address);
        setErrorStatusCode(err);
    }
    

	public void setData(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           WrongLengthOfStringException
    {
        super.setData(buffer);
        setErrorStatusCode(buffer.removeInt());
    }

	public ByteBuffer getData()
	{
	    ByteBuffer buffer = super.getData();
	    buffer.appendInt(getErrorStatusCode());
	    return buffer;
	}
	
	public void setErrorStatusCode(int sc) { errorStatusCode = sc; }
	public int getErrorStatusCode()        { return errorStatusCode; }

    public String debugString()
    {
        String dbgs = "(unsucsme: ";
        dbgs += super.debugString();
        dbgs += Integer.toString(getErrorStatusCode());
        dbgs += ") ";
        return dbgs;
    }
    


}
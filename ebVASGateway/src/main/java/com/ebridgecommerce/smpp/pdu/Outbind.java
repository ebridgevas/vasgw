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
public class Outbind extends Request
{
    private String systemId = Data.DFLT_SYSID;
    private String password = Data.DFLT_PASS;

    public Outbind()
    {
        super(Data.OUTBIND);
    }

    protected Response createResponse()
    {
        return null;
    }

	public boolean canResponse()
	{
		return false;
	}
	
    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
        setSystemId(buffer.removeCString());
        setPassword(buffer.removeCString());
    }

    public ByteBuffer getBody()
    {
	    ByteBuffer buffer = new ByteBuffer();
	    buffer.appendCString(getSystemId());
	    buffer.appendCString(getPassword());
        return buffer;
    }
    
    public void setSystemId(String sysId)
    throws WrongLengthOfStringException {
        checkString(sysId, Data.SM_SYSID_LEN);
        systemId = sysId;
    }
    
    public void setPassword(String pwd)
    throws WrongLengthOfStringException {
        checkString(pwd, Data.SM_PASS_LEN);
        password = pwd;
    }
    
    public String getSystemId() { return systemId; }
    public String getPassword() { return password; }
    
    public String debugString()
    {
        String dbgs = "(outbind: ";
        dbgs += super.debugString();
        dbgs += getSystemId();
        dbgs += " ";
        dbgs += getPassword();
        dbgs += ")";
        return dbgs;
    }

    // special equals() for outbind: as we don't care
    // about outbind's sequence number, any outbind is equal to
    // any other outbind, sort of :-)
    public boolean equals(Object object)
    {
       if (object == null) {
            return false;
       } else {
            PDU pdu = (PDU)object;
            return pdu.getCommandId() == getCommandId();
       }
    }

}

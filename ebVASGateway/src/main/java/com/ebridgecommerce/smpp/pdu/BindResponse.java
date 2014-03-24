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
import com.ebridgecommerce.smpp.pdu.tlv.TLVByte;
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

public abstract class BindResponse extends Response
{

    // mandatory parameters
    private String systemId = Data.DFLT_SYSID;

    // optional parameters
    private TLVByte scInterfaceVersion = new TLVByte(Data.OPT_PAR_SC_IF_VER);
    
    public BindResponse(int commandId)
    {
        super(commandId);
        
        registerOptional(scInterfaceVersion);
    }

    public void setBody(ByteBuffer buffer)
    throws NotEnoughDataInByteBufferException,
           TerminatingZeroNotFoundException,
           PDUException
    {
        if (getCommandStatus() == 0) { // ok => have body
            setSystemId(buffer.removeCString());
        }
    }

    public ByteBuffer getBody()
    {
        ByteBuffer buffer = new ByteBuffer();
        if (getCommandStatus() == 0) { // ok => append body
            buffer.appendCString(getSystemId());
        }
        return buffer;
    }

    public void setSystemId(String sysId)
    throws WrongLengthOfStringException {
        checkString(sysId, Data.SM_SYSID_LEN);
        systemId = sysId;
    }

    public String getSystemId() { return systemId; }

    public boolean hasScInterfaceVersion() { return scInterfaceVersion.hasValue(); }

    public void setScInterfaceVersion(byte value) { scInterfaceVersion.setValue(value); }

    public byte getScInterfaceVersion()
    throws ValueNotSetException { return scInterfaceVersion.getValue(); }
    
    public String debugString()
    {
        String dbgs = "(bindresp: ";
        dbgs += super.debugString();
        dbgs += getSystemId();
        if (hasScInterfaceVersion()) {
            dbgs += " ";
            try {
                dbgs += getScInterfaceVersion();
            } catch (Exception e) {
                // don't want to throw exception in debug code!
            }
        }
        dbgs += ") ";
        return dbgs;
    }
}

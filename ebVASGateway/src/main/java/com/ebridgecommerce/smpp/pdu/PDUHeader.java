/*
 * Copyright (c) 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock System (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */

package com.ebridgecommerce.smpp.pdu;

import com.ebridgecommerce.smpp.util.ByteBuffer;
import com.ebridgecommerce.smpp.util.NotEnoughDataInByteBufferException;

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */

public class PDUHeader extends ByteData
{
    private int commandLength = 0;
    private int commandId = 0;
    private int commandStatus = 0;
    private int sequenceNumber = 1;
	
	public ByteBuffer getData()
	{
        ByteBuffer buffer = new ByteBuffer();
        buffer.appendInt(getCommandLength());
        buffer.appendInt(getCommandId());
        buffer.appendInt(getCommandStatus());
        buffer.appendInt(getSequenceNumber());
        return buffer;
	}
	
	public void setData(ByteBuffer buffer)
	throws NotEnoughDataInByteBufferException
	{
		commandLength = buffer.removeInt();
		commandId = buffer.removeInt();
		commandStatus = buffer.removeInt();
		sequenceNumber = buffer.removeInt();
	}
	

	public int getCommandLength()
	{
		return commandLength;
	}
	
	public int getCommandId()
	{
		return commandId;
	}
	
	public int getCommandStatus()
	{
		return commandStatus;
	}
	
	public int getSequenceNumber()
	{
		return sequenceNumber;
	}
	
	public void setCommandLength(int cmdLen)
	{
		commandLength = cmdLen;
	}
	
	public void setCommandId(int cmdId)
	{
		commandId = cmdId;
	}
	
	public void setCommandStatus(int cmdStatus)
	{
		commandStatus = cmdStatus;
	}
	
	public void setSequenceNumber(int seqNr)
	{
		sequenceNumber = seqNr;
	}

}
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

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class BindTransmitter extends BindRequest
{
    public BindTransmitter()
    {
        super(Data.BIND_TRANSMITTER);
    }
    
    protected Response createResponse()
    {
        return new BindTransmitterResp();
    }

    public boolean isTransmitter()
    {
        return true;
    }

    public boolean isReceiver()
    {
        return false;
    }
}
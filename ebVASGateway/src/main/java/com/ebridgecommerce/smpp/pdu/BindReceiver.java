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
public class BindReceiver extends BindRequest
{
    public BindReceiver()
    {
        super(Data.BIND_RECEIVER);
    }
    
    protected Response createResponse()
    {
        return new BindReceiverResp();
    }

    public boolean isTransmitter()
    {
        return false;
    }

    public boolean isReceiver()
    {
        return true;
    }
}
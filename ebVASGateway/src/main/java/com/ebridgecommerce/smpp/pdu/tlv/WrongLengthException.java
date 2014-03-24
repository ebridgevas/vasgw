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

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */

public class WrongLengthException extends TLVException
{
    public WrongLengthException()
    {
        super("The TLV is shorter or longer than allowed.");
    }
    
    public WrongLengthException(int min, int max, int actual)
    {
        super("The TLV is shorter or longer than allowed: " +
            " min=" + min +
            " max=" + max +
            " actual=" + actual+
            ".");
    }
}
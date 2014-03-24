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


/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */
public class IntegerOutOfRangeException extends PDUException
{
    public IntegerOutOfRangeException()
    {
        super("The integer is lower or greater than required.");
    }
    
    public IntegerOutOfRangeException(int min, int max, int val)
    {
        super("The integer is lower or greater than required: " +
            " min=" + min +
            " max=" + max +
            " actual=" + val+
            ".");
    }
}
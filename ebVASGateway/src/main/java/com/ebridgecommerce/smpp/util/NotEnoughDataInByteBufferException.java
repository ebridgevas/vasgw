/*
 * Copyright (c) 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */
package com.ebridgecommerce.smpp.util;

import com.ebridgecommerce.smpp.SmppException;

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */

public class NotEnoughDataInByteBufferException extends SmppException
{

	private int available;
    private int expected;
    
    public NotEnoughDataInByteBufferException(int p_available, int p_expected)
    {
	    super("Not enough data in byte buffer. " + 
	        "Expected " + p_expected + 
	        ", available: "+p_available + ".");
        available = p_available;
        expected = p_expected;
    }
    
    public NotEnoughDataInByteBufferException(String s)
    {
	    super(s);
        available = 0;
        expected = 0;
    }
    
    public int getAvailable()
    {
        return available;
    }
    
    public int getExpected()
    {
        return expected;
    }
}
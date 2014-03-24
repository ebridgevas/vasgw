/*
 * Copyright (c) 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */

package com.ebridgecommerce.smpp;

/**
 * 
 * Class <code>SmppException</code> is the root of all SMPP Library
 * exceptions. Every exception defined in the library <code>SmppException</code>
 * as a superclass -- this way class <code>SmppException</code>
 * provides single class for <code>catch</code> clause.
 *  
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */

public class SmppException extends Exception
{
	/**
     * Constructs a <code>SmppException</code> with no specified detail
     * message. 
     */
    public SmppException()
    {
        super();
    }
    
    /**
     * Constructs a <code>SmppException</code> with the specified detail
     * message. 
     *
     * @param   s   the detail message.
     */
    public SmppException(String s)
    {
        super(s);
    }
}
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

/**
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 */


import com.ebridgecommerce.smpp.SmppException;

/**
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version 1.0, 11 Jun 2001
 * @viz.diagram TerminatingZeroNotFoundException.tpx
 */

public class TerminatingZeroNotFoundException extends SmppException
{
    public TerminatingZeroNotFoundException()
    {
        super("Terminating zero not found in buffer.");
    }
    
    public TerminatingZeroNotFoundException(String s)
    {
        super(s);
    }
    
    
}
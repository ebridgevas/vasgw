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

public class WrongDateFormatException extends PDUException
{
    public WrongDateFormatException()
    {
        super("Date must be either null or of format YYMMDDhhmmsstnnp");
    }
    
    public WrongDateFormatException(String dateStr)
    {
        super("Date must be either null or of format YYMMDDhhmmsstnnp and not " + dateStr + ".");
    }

    public WrongDateFormatException(String dateStr, String msg)
    {
        super("Invalid date "+dateStr+": "+msg);
    }
}

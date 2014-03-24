/*
 * Copyright (c) 2003 - 2006
 * TopClock Systems (Private) Limited
 * All rights reserved.
 *
 * This software is distributed under TopClock Systems (Private) Limited ("Licence Agreement"). 
 * You shall use it and distribute only in accordance with the terms of the License Agreement.
 *
 */

package com.ebridgecommerce.smpp;

/**
 * Thrown when only a part of PDU was received and the rest of the PDU 
 * hasn't been received for too long time.
 *
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 *
 */
 
/*
  01-10-01 ticp@logica.com javadoc wording improved
*/
public class TimeoutException extends SmppException
{

	/** The expired timeout. */
    public long timeout = 0;

    /** The expected bytes. */
    public int expected = 0;

    /** The received bytes. */
    public int received = 0;

    /** Don't allow default constructor */
    private TimeoutException() {}
    
    /**
     * Construct with provided timeout and expected and received amount
     * of data.
     */
    public TimeoutException(long timeout, int expected, int received)
    {
        super("The rest of pdu not received for " + (timeout / 1000) + " seconds. " +
              "Expected " + expected + " bytes, received " + received + " bytes.");
        this.timeout = timeout;
        this.expected = expected;
        this.received = received;
    }

}

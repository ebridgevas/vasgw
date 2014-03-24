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
//
//import zw.co.kewell.smpp.debug.Debug;
//import zw.co.kewell.smpp.debug.DefaultDebug;
//import zw.co.kewell.smpp.debug.DefaultEvent;
//import zw.co.kewell.smpp.debug.Event;
//import zw.co.kewell.smpp.debug.FileDebug;
//import zw.co.kewell.smpp.debug.FileEvent;

/**
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 * Class <code>SmppObject</code> is the root of the SMPP Library
 * class hierarchy. Every class in the library has <code>SmppObject</code>
 * as a superclass except for classes in the <code>zw.co.kewell.smpp.debug</code>
 * package, exceptions and classes <code>Data</code>,
 * <code>OutbindListener</code> and <code>OutbindEvent</code>.
 * 
 */
public class SmppObject
{

    /**
     * Library-wide object used for writting debug information.
     * Every class derived from <code>SmppObject</code>
     * can use this object for logging debug information.<br>
     * It is initialised to an instance of <code>DefaultDebug</code> class
     * which only writes debug information to the screen.
     * <br>
     * For tracing purposes the library reserves tracing groups 1 - 15
     * for internal use, see below the description of the particular groups.
     * The implementation of <code>Debug</code> class is expeted to handle
     * these groups correctly (for example ignore groups at all).
     * 
     * @see Debug
     * @see DefaultDebug
     * @see FileDebug
     */
//    static protected Debug debug;

    /**
     * Library-wide object used for writting event information, basically
     * information about exceptions. Every class derived from
     * <code>SmppObject</code> can use this object for logging event
     * information.<br>
     * It is initialised to an instance of <code>DefaultEvent</code> class
     * which only writes event information to the screen.
     * 
     * @see Event
     * @see DefaultEvent
     * @see FileEvent
     */
//    static protected Event event;
    
    /** Debug tracing group for Receiver & Transmitter classes. */
    public static final int DRXTX = 1;

    /** Detailed debug tracing group for Receiver & Transmitter classes. */
    public static final int DRXTXD = 2;

    /** Event more detailed debug tracing group for Receiver & Transmitter classes. */
    public static final int DRXTXD2 = 3;

    /** Debug tracing group for Receiver & Transmitter classes. */
    public static final int DSESS = 4;

    /** Debug tracing group for PDU and derived classes. */
    public static final int DPDU = 5;

    /** Detailed debug tracing group for PDU and derived classes. */
    public static final int DPDUD = 6;

    /** Debug tracing group for communication (TCPIPConnection, Connection) classes. */
    public static final int DCOM = 7;

    /** Detailed debug tracing group for communication (TCPIPConnection, Connection) classes. */
    public static final int DCOMD = 8;

    /** Debug tracing group for utility classes. */
    public static final int DUTL = 9;

    /** Detailed debug tracing group for utility classes. */
    public static final int DUTLD = 10;

    /**
     * Static initialiser initialises library-wide debug and event
     * object to the default implementations.
     * 
     * @see DefaultDebug
     * @see DefaultEvent
     */
//    static {
//        debug = new DefaultDebug();
//        event = new DefaultEvent();
//    }
    
    /**
     * Sets the debug object, which is used for writting debugging info.
     * 
     * @param   dbg   the instance of the implementation of <code>Debug</code>
     * class
     * @see Debug
     * @see DefaultDebug
     * @see FileDebug
     */
//    static public void setDebug(Debug dbg) { debug = dbg; }

    /**
     * Sets the event object, which is used for writting debugging info.
     * 
     * @param   evt   the instance of the implementation of <code>Event</code>
     * class
     * @see Event
     * @see DefaultEvent
     * @see FileEvent
     */
//    static public void setEvent(Event evt) { event = evt; }
    
    /**
     * Returns current event object used for writting event info.
     *
     * @return the current event object
     */
//    static public Debug getDebug() { return debug; }
    
    /**
     * Returns current debug object used for writting debug info.
     *
     * @return the current debug object
     */
//    static public Event getEvent() { return event; }
    
}

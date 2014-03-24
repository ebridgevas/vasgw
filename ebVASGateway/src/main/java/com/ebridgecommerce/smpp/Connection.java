
package com.ebridgecommerce.smpp;

/*
 * Created on Aug 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import com.ebridgecommerce.smpp.util.ByteBuffer;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Abstract class defining necessary abstract methods for communication
 * over socket based network communication interface. It defines methods
 * for both client & server connection.<br>
 * <code>Session</code>, <code>Transmitter</code> and <code>Receiver</code>
 * classes use this class for sending and receiving data to and from SMSC.
 * 
 * @author David Tekeshe
 * @author david.tekeshe\@gmail.com
 * @version 1.0, 21 August 2006
 * 
 * @see TCPIPConnection
 * 
 */
public abstract class Connection extends SmppObject {

    private InetAddress inetAddress;
    /**
     * Timeout for underlying communication calls.
     */
    private long commsTimeout = Data.COMMS_TIMEOUT;
    /**
     * Timeout for receiving data from connection and for accepting
     * new connection.
     */
    private long receiveTimeout = Data.CONNECTION_RECEIVE_TIMEOUT;

    /**
     * This method should open the connection using communication parameters
     * set up by constructor of the <code>Connection</code> object.
     * 
     * @exception java.io.IOException Opening the connection can cause
     * IOException in the derived classes.
     */
    public abstract void open() throws IOException;

    /**
     * This method should close the connection previously opened by
     * the <code>open()</code> method.
     * 
     * @exception java.io.IOException Closing the connection can cause
     * IOException in the derived classes.
     */
    public abstract void close() throws IOException;

    /**
     * This method should send data over the connection.
     * The timeout for sending is commsTimeout. If the data couldn't
     * be sent, IOException should be thrown.
     * 
     * @param message the data which have to be sent
     * @exception java.io.IOException Sending a data over connection
     * can cause IOException in the derived classes.
     */
    public abstract void send(ByteBuffer data) throws IOException;

    /**
     * This method should receive data from the connection.
     * The timeout for receiving is receiveTimeout. If no data are
     * received within time specified by the timeout, null should be
     * returned.
     * 
     * @return the data received from the connection
     * @exception java.io.IOException Receiving data over connection
     * can cause IOException in the derived classes.
     */
    public abstract ByteBuffer receive() throws IOException;

    /**
     * This method should wait for connection. If the connection
     * is requested then this method should create new instance
     * of connection and return it.
     * 
     * @return the new accepted connection
     * @exception java.io.IOException waiting for connection can cause
     * an IOException
     */
    public abstract Connection accept() throws IOException;

    /**
     * Sets timeout used for calls to underlying communication
     * functions for an instance of the derived Connection class.
     *
     * @param timeout new communication timeout value
     */
    public synchronized void setCommsTimeout(long commsTimeout) {
        this.commsTimeout = commsTimeout;
    }

    /**
     * Sets timeout used for receiving data from connection using
     * <code>receive</code> method for an instance of the derived
     * Connection class.
     *
     * @param timeout new <code>receive</code> timeout value
     */
    public synchronized void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    /**
     * Returns currently set timeout used for calls to underlying
     * communication functions for an instance of the derived
     * Connection class.
     *
     * @return value of communication timeout
     */
    public synchronized long getCommsTimeout() {
        return commsTimeout;
    }

    /**
     * Returns timeout used for receiving data from connection using
     * <code>receive</code> method for an instance of the derived
     * Connection class.
     *
     * @return value of timeout for <code>receive</code>
     */
    public synchronized long getReceiveTimeout() {
        return receiveTimeout;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
    public abstract Boolean isConnected();
}

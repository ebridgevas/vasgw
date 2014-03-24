/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ebridgecommerce.exceptions;

/**
 *
 * @author DaTekeshe
 */
public class InvalidTransactionException extends Exception {
    public InvalidTransactionException( String message ) {
        super ( message );
    }
}

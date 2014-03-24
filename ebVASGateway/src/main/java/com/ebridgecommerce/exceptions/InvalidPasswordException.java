/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ebridgecommerce.exceptions;

/**
 *
 * @author DaTekeshe
 */
public class InvalidPasswordException extends Exception {
    public InvalidPasswordException( String message ) {
        super ( message );
    }
}

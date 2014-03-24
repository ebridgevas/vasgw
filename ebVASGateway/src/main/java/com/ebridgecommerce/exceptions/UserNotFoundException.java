/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ebridgecommerce.exceptions;

/**
 *
 * @author DaTekeshe
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException( String message ) {
        super ( message );
    }
}

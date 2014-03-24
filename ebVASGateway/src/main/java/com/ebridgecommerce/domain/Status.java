package com.ebridgecommerce.domain;

import java.io.Serializable;

/**
 *
 * @author DaTekeshe
 */
public enum Status implements Serializable {

    NEW,

    PROCESSED,

    REJECTED,

    DELETED,

    SENT,

    SAF;
    
}
package com.ebridgecommerce.sdp.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionDTO implements Serializable {

    private String msIsdn;

    public TransactionDTO() {
    }

    public TransactionDTO(String msIsdn) {
        this.msIsdn = msIsdn;
    }

    public String getMsIsdn() {
        return msIsdn;
    }

    public void setMsIsdn(String msIsdn) {
        this.msIsdn = msIsdn;
    }
}

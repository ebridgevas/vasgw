package com.ebridgecommerce.dto;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 7/1/12
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationStatsDTO {
    private Date txnDate;
    private Integer registered;
    private Integer pending;
    private Integer rejected;
    private String statusText;
    private String msisdn;

    public SimRegistrationStatsDTO(Date txnDate, Integer registered, Integer pending, Integer rejected) {
        this.txnDate = txnDate;
        this.registered = registered;
        this.pending = pending;
        this.rejected = rejected;
    }

    public SimRegistrationStatsDTO (Date txnDate, String msisdn, String statusText) {
        this.txnDate = txnDate;
        this.msisdn = msisdn;
        this.statusText = statusText;
    }

    public Date getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(Date txnDate) {
        this.txnDate = txnDate;
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }

    public Integer getPending() {
        return pending;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}

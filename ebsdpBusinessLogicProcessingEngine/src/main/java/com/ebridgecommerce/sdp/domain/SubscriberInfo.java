package com.ebridgecommerce.sdp.domain;

import javax.net.ssl.SSLEngineResult;

public class SubscriberInfo {

    private String msisdn;
    private String firstname;
    private String lastname;
    private String idNumber;
    private String physicalAddress;
    private StatusDTO status;

    public SubscriberInfo(String msisdn, StatusDTO status) {
        this.msisdn = msisdn;
        this.status = status;
    }

    public SubscriberInfo(String msisdn, String firstname, String lastname, String idNumber, String physicalAddress, StatusDTO status) {
        this.msisdn = msisdn;
        this.firstname = firstname;
        this.lastname = lastname;
        this.idNumber = idNumber;
        this.physicalAddress = physicalAddress;
        this.status = status;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }
}

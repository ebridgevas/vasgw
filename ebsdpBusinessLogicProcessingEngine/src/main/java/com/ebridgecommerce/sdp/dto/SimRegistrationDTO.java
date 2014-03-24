package com.ebridgecommerce.sdp.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/28/12
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationDTO implements Serializable {

    private String msIsdn;
    private String idNumber;
    private String firstname;
    private String lastname;
    private String physicalAddress;
    private String state;
    private Date dateCreated;
    private Date dateLastUpdated;

    public SimRegistrationDTO() {
    }

    public SimRegistrationDTO(String msIsdn, String state) {
        this.msIsdn = msIsdn;
        this.state = state;
    }

    public String getMsIsdn() {
        return msIsdn;
    }

    public void setMsIsdn(String msIsdn) {
        this.msIsdn = msIsdn;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
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

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }
}

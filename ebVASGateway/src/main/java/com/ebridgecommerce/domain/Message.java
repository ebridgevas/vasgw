package com.ebridgecommerce.domain;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DaTekeshe
 */

public class Message implements Serializable {

    private String id;

    private String uuid;
    private Date messageDate;

    private String sourceMsisdn;
    private String destinationMsisdn;

    private Status status;

    private String originalShortMessage;
    private String originalSmscId;
    private String forwardingSmscId;

    private String messageString;

    public Message(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getDestinationMsisdn() {
        return destinationMsisdn;
    }

    public void setDestinationMsisdn(String destinationMsisdn) {
        this.destinationMsisdn = destinationMsisdn;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getSourceMsisdn() {
        return sourceMsisdn;
    }

    public void setSourceMsisdn(String sourceMsisdn) {
        this.sourceMsisdn = sourceMsisdn;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getForwardingSmscId() {
        return forwardingSmscId;
    }

    public void setForwardingSmscId(String forwardingSmscId) {
        this.forwardingSmscId = forwardingSmscId;
    }

    public String getOriginalShortMessage() {
        return originalShortMessage;
    }

    public void setOriginalShortMessage(String originalShortMessage) {
        this.originalShortMessage = originalShortMessage;
    }

    public String getOriginalSmscId() {
        return originalSmscId;
    }

    public void setOriginalSmscId(String originalSmscId) {
        this.originalSmscId = originalSmscId;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }
    
}

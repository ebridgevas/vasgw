package com.ebridgecommerce.domain;

/**
 *
 * @author DaTekeshe
 */
public class ShortMessage extends Message {

    private String id;

    private String sourceImsi;
    private String sourceAddressText;

    private String destinationImsi;    
    private String text;

    private String smscId;
    private String smscReference;
    private Long smscSequence;

    private String serviceProviderId;
    private String jmsQueueName;

    public ShortMessage() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceAddressText() {
        return sourceAddressText;
    }

    public void setSourceAddressText(String sourceAddressText) {
        this.sourceAddressText = sourceAddressText;
    }

    public String getDestinationImsi() {
        return destinationImsi;
    }

    public void setDestinationImsi(String destinationImsi) {
        this.destinationImsi = destinationImsi;
    }

    public String getSourceImsi() {
        return sourceImsi;
    }

    public void setSourceImsi(String sourceImsi) {
        this.sourceImsi = sourceImsi;
    }


    public String getSmscId() {
        return smscId;
    }

    public void setSmscId(String smscId) {
        this.smscId = smscId;
    }

    public String getSmscReference() {
        return smscReference;
    }

    public void setSmscReference(String smscReference) {
        this.smscReference = smscReference;
    }

    public Long getSmscSequence() {
        return smscSequence;
    }

    public void setSmscSequence(Long smscSequence) {
        this.smscSequence = smscSequence;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getJmsQueueName() {
        return jmsQueueName;
    }

    public void setJmsQueueName(String jmsQueueName) {
        this.jmsQueueName = jmsQueueName;
    }

}

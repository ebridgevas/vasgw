package com.ebridgevas.internaltelecelservices.model;

/**
 * david@ebridgevas.com
 *
 */
public class ServiceCommandRequest {

    private String sourceId;
    private String destinationId;
    private String serviceCommand;
    private String sessionId;

    public ServiceCommandRequest() {
    }

    public ServiceCommandRequest( String sourceId,
                                  String destinationId,
                                  String serviceCommand,
                                  String sessionId) {
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.serviceCommand = serviceCommand;
        this.sessionId = sessionId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getServiceCommand() {
        return serviceCommand;
    }

    public void setServiceCommand(String serviceCommand) {
        this.serviceCommand = serviceCommand;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

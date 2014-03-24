package com.ebridgevas.model;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 3/15/13
 * Time: 4:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserSession {

    private PduType pduType;
    private ServiceCommand serviceCommand;

    public UserSession(PduType pduType, ServiceCommand serviceCommand) {
        this.pduType = pduType;
        this.serviceCommand = serviceCommand;
    }

    public PduType getPduType() {
        return pduType;
    }

    public void setPduType(PduType pduType) {
        this.pduType = pduType;
    }

    public ServiceCommand getServiceCommand() {
        return serviceCommand;
    }

    public void setServiceCommand(ServiceCommand serviceCommand) {
        this.serviceCommand = serviceCommand;
    }
}

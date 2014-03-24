package com.ebridgecommerce.prepaid.ws;

import com.comverse_in.prepaid.ccws.ServiceLocator;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.ServiceSoapStub;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

import javax.xml.rpc.ServiceException;
import java.rmi.Remote;

public class WebServiceConnector {

    public ServiceSoap getConnection() throws ServiceException {

        EngineConfiguration config = new FileProvider("/prod/prepaid/wsdd/client_deploy.wsdd");
        ServiceLocator locator = new ServiceLocator(config);
        Remote remote = locator.getPort(ServiceSoap.class);
        Stub axisPort = (Stub) remote;
        axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT);
        axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        axisPort._setProperty(WSHandlerConstants.USER, "vas");
        axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, "com.ebridgecommerce.prepaid.client.net.PasswordCallback");
        ServiceSoap service = (ServiceSoapStub) axisPort;
        return service;
    }
}

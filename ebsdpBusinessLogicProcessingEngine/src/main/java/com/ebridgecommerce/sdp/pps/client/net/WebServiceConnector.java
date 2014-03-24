package com.ebridgecommerce.sdp.pps.client.net;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 7/27/12
 * Time: 7:04 AM
 * To change this template use File | Settings | File Templates.
 */
import com.comverse_in.prepaid.ccws.ServiceLocator;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;

public class WebServiceConnector {

    private String pps;

    public WebServiceConnector(String pps) {
        this.pps = pps;
    }

    public ServiceSoap getConnection() throws ServiceException {

        try {
            EngineConfiguration config = new FileProvider(
                    "comverse".equalsIgnoreCase(pps) ? "/prod/ebridge/wsdd/comverse_client_deploy.wsdd" : "/prod/ebridge/wsdd/client_deploy.wsdd");
            ServiceLocator locator = new ServiceLocator(config);
            ServiceSoap service = null;
            if ("comverse".equalsIgnoreCase(pps)) {
                service = locator.getServiceSoap();
            } else {
               service = locator.getServiceSoap(new URL("http://172.17.1.15:8080/ocswebservices/services/zimbabweocsWebServices?wsdl"));
            }
//            Remote remote = locator.getPort(ServiceSoap.class);
            Stub axisPort = (Stub) service;
            axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT);
            axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
            axisPort._setProperty(WSHandlerConstants.USER, "vas");
            axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, "com.ebridgecommerce.sdp.pps.client.net.PasswordCallback");
//            ServiceSoap service = (ServiceSoapStub) axisPort;
            return service;
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }

    }
}

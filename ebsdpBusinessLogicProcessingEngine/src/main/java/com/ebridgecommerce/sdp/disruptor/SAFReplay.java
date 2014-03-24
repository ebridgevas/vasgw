package com.ebridgecommerce.sdp.disruptor;

import com.ebridgecommerce.sdp.dao.SimRegistrationDAO;
import com.ebridgecommerce.sdp.domain.StatusDTO;
import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import zw.co.ebridge.jms.JMSWriter;
import zw.co.telecel.akm.simreg.external.SubscriberRaw;

import javax.jms.*;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/20/12
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */

public class SAFReplay {

    private SimRegistrationService service;
    private JMSWriter jmsWriter;

    private Session session;
    private Destination destination;
    private Connection connection;
    public SAFReplay(String endpoint) {
        service = new SimRegistrationServiceImpl(endpoint);
    }

    public void process() {
        System.out.println("############### PROCESSING SAF REPLAY");
        for( SimRegistrationDTO registration : SimRegistrationDAO.getSimRegistrations(StatusDTO.CAPTURED) ) {

            if ( service.getSubscriber(registration.getMsIsdn()) == null) {
                if ( service.createSubscriber(registration) ) {
                    registration.setState("registered");
                    SimRegistrationDAO.updateRegistration(registration);
                }
            } else {
                registration.setState("registered");
                SimRegistrationDAO.updateRegistration(registration);
            }
        }
    }

    public static void main( String[] args ) {
        System.out.println("############### SAF REPLAY STARTED");
        new SAFReplay("http://10.10.4.28:8080/RegisterSubscriber/RegisterSubscriber").process();
    }

}
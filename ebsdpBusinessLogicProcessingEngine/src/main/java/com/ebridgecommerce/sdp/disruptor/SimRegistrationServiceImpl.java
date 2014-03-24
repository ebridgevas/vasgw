package com.ebridgecommerce.sdp.disruptor;

/**
 * WebServiceOutputDisruptor
 * User: David
 * Date: 6/20/12
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ebridgecommerce.sdp.disruptor.webserviceclient.simregistration.RegisterSubscriberStub;
import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import zw.co.telecel.akm.simreg.external.*;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;

public class SimRegistrationServiceImpl implements SimRegistrationService {

    private RegisterSubscriberStub stub;

    public SimRegistrationServiceImpl(String endpoint) {
        try {
            stub = new RegisterSubscriberStub( endpoint );
        } catch(Exception e){
            System.err.println("######### FATA ### " + e.getMessage());
        }
    }

    @Override
    public SubscriberRaw getSubscriber(String msisdn) {
        try {
            GetSubscriberByMobileNumberDocument requestDocument = GetSubscriberByMobileNumberDocument.Factory.newInstance();
            GetSubscriberByMobileNumber request = requestDocument.addNewGetSubscriberByMobileNumber();
            request.setMobileNumber( msisdn );
            GetSubscriberByMobileNumberResponseDocument responseDocument = stub.getSubscriberByMobileNumber(requestDocument);
            System.out.println("############### SAF REPLAY STARTED");
            GetSubscriberByMobileNumberResponse response = responseDocument.getGetSubscriberByMobileNumberResponse();
            SubscriberRaw result = response.getReturn();
            System.out.println("############## WEB SERVICE ########################### " + result);
            System.out.println("############## WEB SERVICE ########################### " + (result != null ? "xml Text = " + request.xmlText() + " --- isSetMobileNumber = " + request.isSetMobileNumber() + " - mobile number = " + request.getMobileNumber() : ">>>>"));
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("############## FATAL #### getSubscriber : 263733165588 FAILED :: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean createSubscriber(SimRegistrationDTO registrationDTO) {
        System.out.println("############### createSubscriber");
        try {

            CreateDocument requestDocument = CreateDocument.Factory.newInstance();
            Create request = requestDocument.addNewCreate();
            SubscriberRaw subscriber = request.addNewSubscriberRaw();
            subscriber.setMobileNumber(registrationDTO.getMsIsdn());
            subscriber.setFirstName(registrationDTO.getFirstname());
            subscriber.setLastName(registrationDTO.getLastname());
            subscriber.setIdNumber(registrationDTO.getIdNumber());
            subscriber.setAddress(registrationDTO.getPhysicalAddress());
            subscriber.setRegistrationDate(Calendar.getInstance());
            subscriber.setMedium("USSD");
            subscriber.setStatus("ACTIVE");
            subscriber.setModeration("FALSE");

            request.setSubscriberRaw(subscriber);
            System.out.println("############### createSubscriberXml \n=" + request.xmlText());

            stub.create(requestDocument);
            System.out.println("######### CHECKING SUBSCRIBER IS CREATED ...");
            getSubscriber( registrationDTO.getMsIsdn());
            return Boolean.TRUE;
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("############## FATAL #### createSubscriber : 263733165588 FAILED :: " + e.getMessage());
            return Boolean.FALSE;
        }
    }
}
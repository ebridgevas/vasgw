package com.ebridgecommerce.sdp.disruptor;

import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import zw.co.telecel.akm.simreg.external.SubscriberRaw;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/29/12
 * Time: 1:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationServiceStub implements SimRegistrationService {

    public SimRegistrationServiceStub(String endpoint) {

    }

    @Override
    public SubscriberRaw getSubscriber(String msisdn) {
        return null;
    }

    @Override
    public Boolean createSubscriber(SimRegistrationDTO pendingRegistration) {
        return Boolean.TRUE;
    }

}

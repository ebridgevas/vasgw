package com.ebridgecommerce.sdp.disruptor;

import com.ebridgecommerce.sdp.dto.SimRegistrationDTO;
import zw.co.telecel.akm.simreg.external.SubscriberRaw;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/29/12
 * Time: 1:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SimRegistrationService {

    SubscriberRaw getSubscriber(String msisdn);

    Boolean createSubscriber(SimRegistrationDTO pendingRegistration);
}

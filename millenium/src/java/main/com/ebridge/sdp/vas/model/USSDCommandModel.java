package com.ebridge.sdp.vas.model;

import com.ebridgevas.services.ServiceCommandProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * david@ebridgevas.com
 *
 */
public class USSDCommandModel {

    private static final Map<String, ServiceCommandProcessor> SERVICE_COMMAND_PROCESSORS;

    static {
        SERVICE_COMMAND_PROCESSORS = new HashMap <String, ServiceCommandProcessor>();
        SERVICE_COMMAND_PROCESSORS.put("2013", null);
    }

}



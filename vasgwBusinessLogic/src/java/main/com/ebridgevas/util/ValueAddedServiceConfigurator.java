package com.ebridgevas.util;

import com.ebridgevas.services.ValueAddedService;

import java.util.HashMap;
import java.util.Map;

/**
 * david@ebridgevas.com
 *
 */
public class ValueAddedServiceConfigurator {

    private static final Map<String, ValueAddedService> VALUE_ADDED_SERVICES;

    static {
        VALUE_ADDED_SERVICES = new HashMap<String, ValueAddedService>();
        VALUE_ADDED_SERVICES.put("2013", null);
    }
}

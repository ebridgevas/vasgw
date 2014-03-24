package com.ebridgevas.commands;

import com.ebridgevas.model.ServiceCommandRequest;
import com.ebridgevas.model.ServiceCommandResponse;

/**
 * david@ebridgevas.com
 *
 */
public interface ServiceCommandProcessor {

    public ServiceCommandResponse process(ServiceCommandRequest request);
}

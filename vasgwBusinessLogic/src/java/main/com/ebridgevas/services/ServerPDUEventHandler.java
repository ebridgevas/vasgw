package com.ebridgevas.services;

import com.ebridge.commons.dto.PduDto;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/23/12
 * Time: 3:17 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ServerPDUEventHandler {
    String handleEvent(PduDto pdu);
}

package com.ebridge.sdp.vas.smpp.events;

import zw.co.telecel.akm.millenium.dto.PduDto;



public interface ServerPDUEventHandler {
    String handleEvent(PduDto pdu);
}

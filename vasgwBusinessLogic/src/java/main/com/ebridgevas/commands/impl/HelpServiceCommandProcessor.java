package com.ebridgevas.commands.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.model.Message;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * david@ebridgevas.com
 *
 */
public class HelpServiceCommandProcessor implements ServiceCommandProcessor {

    /* TODO Add context sensitive help. */
    @Override
    public List<MTSM> process(PduDto pdu) {
        ArrayList<MTSM> result = new ArrayList<MTSM>();
        result.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), Message.HELP));
        result.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), Message.HELP2));
        return result;
    }

    @Override
    public void setPostpaidLimit(BigDecimal postpaidLimit) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTxnDao(TxnDAO txnDao) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

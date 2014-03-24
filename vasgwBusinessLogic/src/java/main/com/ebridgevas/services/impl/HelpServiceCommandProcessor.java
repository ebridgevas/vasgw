package com.ebridgevas.services.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.model.Message;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class HelpServiceCommandProcessor implements ServiceCommandProcessor {

    /* TODO Add context sensitive help. */
    @Override
    public List<MTSM> process(PduDto pdu) {
        ArrayList<MTSM> result = new ArrayList<MTSM>();
        if ("33350".equals(pdu.getDestinationId())) {
            result.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), Message.HELP_BALANCE_TRANSFER));
        } else if ("33073".equals(pdu.getDestinationId())) {
            result.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), Message.HELP_DATA_BUNDLE_PURCHASE));
        } else {
            result.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), "Please send requests to 33350 or 33073 only."));
        }
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

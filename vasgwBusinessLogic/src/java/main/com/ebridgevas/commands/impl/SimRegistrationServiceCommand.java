package com.ebridgevas.commands.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 3/16/13
 * Time: 3:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimRegistrationServiceCommand implements ServiceCommandProcessor {
    @Override
    public List<MTSM> process(PduDto pdu) {
        return null;
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

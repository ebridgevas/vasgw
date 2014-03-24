package com.ebridgevas.services.impl;

import com.ebridge.commons.dto.PduDto;

import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.services.ServiceCommandProcessor;
import com.ebridgevas.util.MobileNumberFormatter;

import static com.ebridgevas.services.ServerPDUEventListenerImpl.PREPAID_ACCOUNT_MANAGER;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class VoucherRechargeServiceCommandProcessor implements ServiceCommandProcessor {

    @Override
    public List<MTSM> process(PduDto pdu) {
        String[] tokens = pdu.getShortMessage().split("#");
        return PREPAID_ACCOUNT_MANAGER.voucherRecharge(
                pdu,
                MobileNumberFormatter.format(tokens[1]),
                tokens[0]);
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

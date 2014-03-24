package com.ebridgevas.services.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static com.ebridgevas.services.ServerPDUEventListenerImpl.PREPAID_ACCOUNT_MANAGER;
/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class BalanceEnquiryServiceCommandProcessor implements ServiceCommandProcessor {

    @Override
    public List<MTSM> process(PduDto pdu) {
        List<MTSM> result = new ArrayList<MTSM>();
        result.add(new MTSM(
                        pdu.getSourceId(),
                        pdu.getDestinationId(),
                        PREPAID_ACCOUNT_MANAGER.getAccountBalance(pdu.getSourceId())));
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

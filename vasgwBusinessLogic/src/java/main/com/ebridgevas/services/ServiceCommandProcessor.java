package com.ebridgevas.services;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ServiceCommandProcessor {
    List<MTSM> process(PduDto pdu);
    void setPostpaidLimit(BigDecimal postpaidLimit);
    void setTxnDao(TxnDAO txnDao);
}

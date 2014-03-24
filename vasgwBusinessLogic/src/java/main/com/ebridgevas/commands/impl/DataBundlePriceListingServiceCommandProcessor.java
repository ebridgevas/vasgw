package com.ebridgevas.commands.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.model.PduType;
import com.ebridgevas.model.ServiceCommand;
import com.ebridgevas.model.UserSession;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.ebridgevas.services.ServerPDUEventListenerImpl.USER_SESSIONS;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataBundlePriceListingServiceCommandProcessor
        extends AbstractServiceCommandProcessor
        implements ServiceCommandProcessor {

    @Override
    public List<MTSM> process(PduDto pdu) {

        USER_SESSIONS.put(pdu.getSourceId(), new UserSession(PduType.SMS, ServiceCommand.DATA_BUNDLE_PURCHASE));

        List<MTSM> result = new ArrayList<MTSM>();
        result.add(new MTSM(
                pdu.getSourceId(),
                pdu.getDestinationId(),
                getDataBundlePrices()));

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

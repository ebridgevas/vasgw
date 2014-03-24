package com.ebridgevas.commands.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.dto.TxnDto;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.model.PduType;
import com.ebridgevas.model.UserSession;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.ebridgevas.services.ServerPDUEventListenerImpl.PREPAID_ACCOUNT_MANAGER;
import static com.ebridgevas.services.ServerPDUEventListenerImpl.USER_SESSIONS;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataBundlePurchaseServiceCommandProcessor
        extends AbstractServiceCommandProcessor
        implements ServiceCommandProcessor {

    @Override
    public List<MTSM> process(PduDto pdu) {

        List<MTSM> result = new ArrayList<MTSM>();

        /* Get current user session. */
        UserSession userSession = USER_SESSIONS.get(pdu.getSourceId());
        Boolean isInitialDial = isInitialDial(userSession);

        /* Is it USSD or SMS. */
        PduType pduType = isInitialDial ? getPduTypeFrom(pdu.getShortMessage()) : userSession.getPduType();

        /* Create TxnDto. */
        TxnDto txn = createTxnDto(pdu);
        txn.setSessionId(getSessionId(pdu, pduType));
        txn.setProductCode(pdu.getShortMessage().trim());

        System.out.println("########### txn.getProductCode() : " + txn.getProductCode());

        /* Purchase data bundle. */
        result.add(new MTSM(
                pdu.getSourceId(),
                pdu.getDestinationId(),
                PREPAID_ACCOUNT_MANAGER.purchaseDataBundle(
                    txn, Boolean.FALSE, Boolean.FALSE
                )));

        /* Remove User Session. */
        USER_SESSIONS.remove(pdu.getSourceId());

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

//    public static void main(String[] args) {
//        DataBundlePurchaseServiceCommandProcessor processor = new DataBundlePurchaseServiceCommandProcessor();
//        PduDto pdu = new PduDto();
//        pdu.setSourceId("263733661588");
//        pdu.setShortMessage("2");
//        UserSession session = new UserSession(PduType.SMS, ServiceCommand.DATA_BUNDLE_PURCHASE);
//        USER_SESSIONS.put(pdu.getSourceId(), session);
//        processor.process(pdu);
//    }
}

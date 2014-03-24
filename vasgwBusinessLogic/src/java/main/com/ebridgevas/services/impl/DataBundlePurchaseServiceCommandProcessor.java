package com.ebridgevas.services.impl;

import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.dto.TxnDto;
import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.model.PduType;
import com.ebridgevas.model.UserSession;
import com.ebridgevas.services.ServiceCommandProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.ebridgevas.services.ServerPDUEventListenerImpl.PREPAID_ACCOUNT_MANAGER;
import static com.ebridgevas.services.ServerPDUEventListenerImpl.POSTPAID_ACCOUNT_MANAGER;
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

    private BigDecimal postpaidLimit;
    private TxnDAO txnDao;

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

        /* Is postpaid. */
        Boolean isPostpaid = isPostpaid(pdu.getSourceId());

        /* Check limits. */
        if (isPostpaid) {
            if ( txnDao.isLimitReached(txn, postpaidLimit ) ) {
                String errorMessage = "You have reached your data bundle credit limit of " +
                        postpaidLimit.setScale(2, RoundingMode.HALF_UP) +
                        " for this month. Contact our call centre team on 150 for any further enquiries.' " ;

                txn.setStatusCode("055");
                txn.setNarrative(errorMessage);
                txn.setShortMessage(errorMessage);
                txnDao.persist(txn);

                result.add(new MTSM(
                        pdu.getSourceId(),
                        pdu.getDestinationId(),
                        errorMessage
                ));

                return result;
            }
        }

        /* Purchase data bundle. */
        result.add(new MTSM(
                pdu.getSourceId(),
                pdu.getDestinationId(),
                isPostpaid ? POSTPAID_ACCOUNT_MANAGER.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE)
                                : PREPAID_ACCOUNT_MANAGER.purchaseDataBundle(txn, Boolean.FALSE, Boolean.FALSE)

            ));

        /* Remove User Session. */
        USER_SESSIONS.remove(pdu.getSourceId());

        return result;
    }

    public Boolean isPostpaid(String msisdn) {
        System.out.println("Checking if isPostpaid : " + msisdn );
        String cos = PREPAID_ACCOUNT_MANAGER.getClassOfService(msisdn);
        System.out.println("##### cos : " + cos );
        if (cos == null) {
            return true;
        }
        return "STAFF_COS".equalsIgnoreCase(cos);
    }

    @Override
    public void setPostpaidLimit(BigDecimal postpaidLimit) {
        this.postpaidLimit = postpaidLimit;
    }

    @Override
    public void setTxnDao(TxnDAO txnDao) {
        this.txnDao = txnDao;
    }
}

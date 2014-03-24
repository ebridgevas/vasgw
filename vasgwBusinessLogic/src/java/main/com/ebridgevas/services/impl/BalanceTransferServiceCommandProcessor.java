package com.ebridgevas.services.impl;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.dto.TxnDto;
import com.ebridgevas.model.MTSM;
import com.ebridgevas.services.ServiceCommandProcessor;
import com.ebridgevas.util.MobileNumberFormatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ebridgevas.services.ServerPDUEventListenerImpl.PREPAID_ACCOUNT_MANAGER;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 2/24/13
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class BalanceTransferServiceCommandProcessor implements ServiceCommandProcessor {

    @Override
    public List<MTSM> process(PduDto pdu) {

        TxnDto txn =
                new TxnDto(
                        new BigInteger(pdu.getUuid()),
                        pdu.getDestinationId(),
                        pdu.getSourceId(),
                        pdu.getChannel(),
                        new Date()
                        );

        List<MTSM> result = new ArrayList<MTSM>();
        String[] tokens = pdu.getShortMessage().split("#");
        String amountString = tokens[0].trim();
        BigDecimal amount = null;
        amountString = amountString.replaceAll("$", "");
        try {
            amount = new BigDecimal(amountString);
        } catch (Exception e) {
            e.printStackTrace();
            result.add(new MTSM(pdu.getSourceId(), pdu.getDestinationId(), amountString + " is invalid amount"));
            return result;
        }

        List<MTSM> response = PREPAID_ACCOUNT_MANAGER.transfer(
                                                        pdu,
                                                        MobileNumberFormatter.format(tokens[1]),
                                                        amount);

        txn.setTransactionType("AirtimeTransfer");
        txn.setProductCode("1");
        txn.setAmount(amount);
        txn.setNarrative(response.get(0).getShortMessage());
        txn.setShortMessage(response.get(0).getShortMessage());
        txn.setStatusCode((response.size() == 2) ? "000" : "096");

        PREPAID_ACCOUNT_MANAGER.getTxnDao().persist(txn);

        return response;
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

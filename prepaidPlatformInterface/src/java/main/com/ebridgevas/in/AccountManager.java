package com.ebridgevas.in;

import com.ebridge.commons.dto.PduDto;
import com.ebridgevas.dao.TxnDAO;
import com.ebridgevas.dto.TxnDto;
import com.ebridgevas.model.BalanceDTO;
import com.ebridgevas.model.MTSM;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/29/12
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AccountManager {
    String getClassOfService(String msisdn);
    String getAccountBalance(String msisdn);
    String purchaseDataBundle(TxnDto txn, Boolean creditOnly, Boolean debitOnly);
    String creditAccount(TxnDto txn);
    String debitAccount(TxnDto txn);

    List<BalanceDTO> getAccountBalances(String msisdn);
    List<MTSM> transfer(PduDto pdu, String beneficiaryMsisdn, BigDecimal airtimeTransferAmount);
    List<MTSM> voucherRecharge(PduDto pdu, String beneficiaryMsisdn, String rechargeVoucher);

    void setTxnDao(TxnDAO txnDao);
    TxnDAO getTxnDao();
//    void setRequestDao(CreditAccountRequestDao requestDao);
//    void setResponseDao(CreditAccountResponseDao responseDao);
}

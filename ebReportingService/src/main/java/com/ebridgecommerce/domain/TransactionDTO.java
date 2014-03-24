package com.ebridgecommerce.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/12/12
 * Time: 1:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionDTO {

    private Date transactionDate;
    private String subscriberMsisdn;
    private BigDecimal amount;

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getSubscriberMsisdn() {
        return subscriberMsisdn;
    }

    public void setSubscriberMsisdn(String subscriberMsisdn) {
        this.subscriberMsisdn = subscriberMsisdn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

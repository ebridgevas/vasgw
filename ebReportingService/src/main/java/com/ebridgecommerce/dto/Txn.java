package com.ebridgecommerce.dto;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by david on 3/18/14.
 */
public class Txn {

    private final Date txnDate;
    private final String sourceId;
    private final String statusCode;
    private final Integer txnCount;
    private final BigDecimal txnValue;
    private final BigDecimal txnCharge;

    public Txn( Date txnDate,
                String sourceId,
                String statusCode,
                Integer txnCount,
                BigDecimal txnValue,
                BigDecimal txnCharge) {
        this.txnDate = txnDate;
        this.sourceId = sourceId;
        this.statusCode = statusCode;
        this.txnCount = txnCount;
        this.txnValue = txnValue;
        this.txnCharge = txnCharge;
    }

    public Date getTxnDate() {
        return txnDate;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Integer getTxnCount() {
        return txnCount;
    }

    public BigDecimal getTxnValue() {
        return txnValue;
    }

    public BigDecimal getTxnCharge() {
        return txnCharge;
    }
}

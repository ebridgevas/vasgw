package com.ebridgevas.model;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 11/29/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataBundle {

    private String bundleType;
    private String bundleDescription;
    private BigDecimal bundleSize;
    private BigDecimal bundleRate;
    private BigDecimal debit;
    private BigDecimal credit;

    public DataBundle(String bundleType,
                      String bundleDescription,
                      BigDecimal bundleSize,
                      BigDecimal bundleRate,
                      BigDecimal debit,
                      BigDecimal credit) {
        this.bundleType = bundleType;
        this.bundleDescription = bundleDescription;
        this.bundleSize = bundleSize;
        this.bundleRate = bundleRate;
        this.debit = debit;
        this.credit = credit;
    }

    public String getBundleType() {
        return bundleType;
    }

    public String getBundleDescription() {
        return bundleDescription;
    }

    public BigDecimal getBundleSize() {
        return bundleSize;
    }

    public BigDecimal getBundleRate() {
        return bundleRate;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }
}

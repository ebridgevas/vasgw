package com.ebridgevas.model;

import java.math.BigDecimal;

/**
 * david@ebridgevas.com
 *
 */
public final class Product {

    private final String productId;
    private final String narrative;
    private final BigDecimal sellingPrice;
    private final BigDecimal debitAmount;
    private final BigDecimal creditAmount;
    private final String debitUnitOfMeasure;
    private final String creditUnitOfMeasure;

    public Product(String productId,
                   String narrative,
                   BigDecimal sellingPrice,
                   BigDecimal debitAmount,
                   BigDecimal creditAmount,
                   String debitUnitOfMeasure,
                   String creditUnitOfMeasure) {

        this.productId = productId;
        this.narrative = narrative;
        this.sellingPrice = sellingPrice;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
        this.debitUnitOfMeasure = debitUnitOfMeasure;
        this.creditUnitOfMeasure = creditUnitOfMeasure;
    }

    public String getProductId() {
        return productId;
    }

    public String getNarrative() {
        return narrative;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public String getDebitUnitOfMeasure() {
        return debitUnitOfMeasure;
    }

    public String getCreditUnitOfMeasure() {
        return creditUnitOfMeasure;
    }
}

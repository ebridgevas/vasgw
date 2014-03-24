package com.ebridgecommerce.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by david on 3/18/14.
 */
public class DailyTransactionStatsDTO {

    private Integer daySuccessfulTxns;
    private Integer dayFailedTxns;
    private BigDecimal dayTxnRevenue;
    private BigDecimal dayTxnCharges;
    private Integer daySubscribers;
    private Integer subscribersToDate;

    public DailyTransactionStatsDTO( Integer daySuccessfulTxns,
                                     Integer dayFailedTxns,
                                     BigDecimal dayTxnRevenue,
                                     BigDecimal dayTxnCharges,
                                     Integer daySubscribers,
                                     Integer subscribersToDate) {

        this.daySuccessfulTxns = daySuccessfulTxns;
        this.dayFailedTxns = dayFailedTxns;
        this.dayTxnRevenue = dayTxnRevenue;
        this.dayTxnCharges = dayTxnCharges;
        this.daySubscribers = daySubscribers;
        this.subscribersToDate = subscribersToDate;
    }

    public Integer getDaySuccessfulTxns() {
        return daySuccessfulTxns;
    }

    public Integer getDayFailedTxns() {
        return dayFailedTxns;
    }

    public BigDecimal getDayTxnRevenue() {
        return dayTxnRevenue;
    }

    public BigDecimal getDayTxnCharges() {
        return dayTxnCharges;
    }

    public Integer getDaySubscribers() {
        return daySubscribers;
    }

    public Integer getSubscribersToDate() {
        return subscribersToDate;
    }

    public void setDaySuccessfulTxns(Integer daySuccessfulTxns) {
        this.daySuccessfulTxns = daySuccessfulTxns;
    }

    public void setDayFailedTxns(Integer dayFailedTxns) {
        this.dayFailedTxns = dayFailedTxns;
    }

    public void setDayTxnRevenue(BigDecimal dayTxnRevenue) {
        this.dayTxnRevenue = dayTxnRevenue;
    }

    public void setDayTxnCharges(BigDecimal dayTxnCharges) {
        this.dayTxnCharges = dayTxnCharges;
    }

    public void setDaySubscribers(Integer daySubscribers) {
        this.daySubscribers = daySubscribers;
    }

    public void setSubscribersToDate(Integer subscribersToDate) {
        this.subscribersToDate = subscribersToDate;
    }
}

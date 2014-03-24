package com.ebridgecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@SuppressWarnings("serial")
public class StatsDTO implements Serializable {
	
	private Date transactionDate;
	private Long rejectedSubscriptionCount;
	private Long successfulSubscriptionCount;
	private BigDecimal failureRatio;
	private double bundleId;
	private BigDecimal revenue;
	private String narrative;
	
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Long getRejectedSubscriptionCount() {
		return rejectedSubscriptionCount;
	}
	public void setRejectedSubscriptionCount(Long rejectedSubscriptionCount) {
		this.rejectedSubscriptionCount = rejectedSubscriptionCount;
	}
	public Long getSuccessfulSubscriptionCount() {
		return successfulSubscriptionCount;
	}
	public void setSuccessfulSubscriptionCount(Long successfulSubscriptionCount) {
		this.successfulSubscriptionCount = successfulSubscriptionCount;
	}
	public BigDecimal getFailureRatio() {
		return failureRatio;
	}
	public void setFailureRatio(BigDecimal failureRatio) {
		this.failureRatio = failureRatio;
	}

	public double getBundleId() {
		return bundleId;
	}
	public void setBundleId(double bundleId) {
		this.bundleId = bundleId;
	}
	public BigDecimal getRevenue() {
		return revenue;
	}
	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	
	public int compareTo(StatsDTO o){
		return this.getTransactionDate().compareTo(o.getTransactionDate());
	}
}

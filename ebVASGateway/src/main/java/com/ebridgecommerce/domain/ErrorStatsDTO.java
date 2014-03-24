package com.ebridgecommerce.domain;

import java.sql.Date;

public class ErrorStatsDTO {
	
	private Date transactionDate;
	private Long rejectCount;
	private String narrative;
	
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Long getRejectCount() {
		return rejectCount;
	}
	public void setRejectCount(Long rejectCount) {
		this.rejectCount = rejectCount;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	
}

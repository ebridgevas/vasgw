package com.ebridgecommerce.domain;

import java.sql.Date;

public class StatsPK {
	
	private Date transactionDate;
	private double bundleId;
	
	public StatsPK(){
	}
	
	public StatsPK(Date transactionDate, double bundleId){
		this.transactionDate = transactionDate;
		this.bundleId = bundleId;
	}
	
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	
	public double getBundleId() {
		return bundleId;
	}

	public void setBundleId(double bundleId) {
		this.bundleId = bundleId;
	}

	public int compareTo(StatsPK o) {
		return this.transactionDate.compareTo(o.transactionDate);
	}
	
	public boolean equals(StatsPK o) {
		return this.transactionDate.equals(o.transactionDate) && (this.bundleId == bundleId);
	}
}

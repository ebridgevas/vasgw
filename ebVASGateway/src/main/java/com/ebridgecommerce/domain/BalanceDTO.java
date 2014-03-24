package com.ebridgecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
public class BalanceDTO implements Serializable {
	
	private String balanceType;
	private BigDecimal amount;
	private Date expiryDate;

	public BalanceDTO(){	
	}
	
	public BalanceDTO(String balanceType, BigDecimal amount, Date expiryDate){		
		this.balanceType = balanceType;
		this.amount = amount;
		this.expiryDate = expiryDate;
	}
	
	public String getBalanceType() {
		return balanceType;
	}
	public void setBalanceType(String balanceType) {
		this.balanceType = balanceType;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	
}

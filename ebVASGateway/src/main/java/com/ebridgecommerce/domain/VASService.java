package com.ebridgecommerce.domain;

import java.math.BigDecimal;

public class VASService {

	private String shortCode;
	private VASServiceProvider vasServiceProvider;
	private BigDecimal vasServiceCharge;

	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public VASServiceProvider getVasServiceProvider() {
		return vasServiceProvider;
	}
	public void setVasServiceProvider(VASServiceProvider vasServiceProvider) {
		this.vasServiceProvider = vasServiceProvider;
	}
	public BigDecimal getVasServiceCharge() {
		return vasServiceCharge;
	}
	public void setVasServiceCharge(BigDecimal vasServiceCharge) {
		this.vasServiceCharge = vasServiceCharge;
	}

}

package com.ebridgecommerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class VASServiceProvider implements Serializable {
	
	private static final long serialVersionUID = -3912599162628213121L;
	
	private String vasServiceProviderId;
  private String vasServiceProviderPassword;
  private String vasServiceProviderName;
  private String vasServiceProviderWallet;
  private String vasServiceProviderType;
  private State state;
  private BigDecimal charge;
  private String shortCode;
  
  public VASServiceProvider() {
  }
  
  public VASServiceProvider( String vasServiceProviderId, String vasServiceProviderPassword, String vasServiceProviderWallet, String vasServiceProviderType ) {
    this.vasServiceProviderId = vasServiceProviderId;
    this.vasServiceProviderPassword = vasServiceProviderPassword;
    this.vasServiceProviderType = vasServiceProviderType;
    this.vasServiceProviderWallet = vasServiceProviderWallet;
  }
  
	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getVasServiceProviderId() {
		return vasServiceProviderId;
	}

	public void setVasServiceProviderId(String vasServiceProviderId) {
		this.vasServiceProviderId = vasServiceProviderId;
	}

	public String getVasServiceProviderPassword() {
		return vasServiceProviderPassword;
	}

	public String getVasServiceProviderType() {
		return vasServiceProviderType;
	}

	public void setVasServiceProviderType(String vasServiceProviderType) {
		this.vasServiceProviderType = vasServiceProviderType;
	}

	public void setVasServiceProviderPassword(String vasServiceProviderPassword) {
		this.vasServiceProviderPassword = vasServiceProviderPassword;
	}

	public String getVasServiceProviderName() {
		return vasServiceProviderName;
	}

	public void setVasServiceProviderName(String vasServiceProviderName) {
		this.vasServiceProviderName = vasServiceProviderName;
	}

	public String getVasServiceProviderWallet() {
		return vasServiceProviderWallet;
	}

	public void setVasServiceProviderWallet(String vasServiceProviderWallet) {
		this.vasServiceProviderWallet = vasServiceProviderWallet;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public BigDecimal getCharge() {
		return charge;
	}

	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}
  
}
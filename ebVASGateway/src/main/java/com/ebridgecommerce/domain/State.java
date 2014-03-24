package com.ebridgecommerce.domain;

import java.io.Serializable;

public enum State implements Serializable {
	ACTIVE("Active"),
	INACTIVE("InActive"),
	SUSPENDED("Suspended");
	
	String description;
	
	State(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}

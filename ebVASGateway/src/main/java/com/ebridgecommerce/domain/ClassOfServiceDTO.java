package com.ebridgecommerce.domain;

public class ClassOfServiceDTO {
	
	private String promotionId;
	private String uuid;
	private String subscriberMsisdn;
	private String originalCosName;
	private String newCosName;
	
	public ClassOfServiceDTO(){	
	}
	
	public ClassOfServiceDTO(String promotionId, String uuid, String subscriberMsisdn, String originalCosName, String newCosName){
		this.promotionId = promotionId;
		this.uuid = uuid;
		this.subscriberMsisdn = subscriberMsisdn;
		this.originalCosName = originalCosName;
		this.newCosName = newCosName;
	}
	
	public String getPromotionId() {
		return promotionId;
	}
	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}
	
	public String getSubscriberMsisdn() {
		return subscriberMsisdn;
	}

	public void setSubscriberMsisdn(String subscriberMsisdn) {
		this.subscriberMsisdn = subscriberMsisdn;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOriginalCosName() {
		return originalCosName;
	}
	public void setOriginalCosName(String originalCosName) {
		this.originalCosName = originalCosName;
	}
	public String getNewCosName() {
		return newCosName;
	}
	public void setNewCosName(String newCosName) {
		this.newCosName = newCosName;
	}
	
	
}

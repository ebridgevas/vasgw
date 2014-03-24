package com.ebridgecommerce.domain;

import java.io.Serializable;

public class SmppBindParametersDTO implements Serializable {
	
	private static final long serialVersionUID = -3359329504793161368L;

	private String smppIPAdress;
	private int smppPort;
	private String systemId;
	private String systemPassword;
	private String smppVersion;
	private Long enquireLinkRate;
	private String systemType;
	
	public SmppBindParametersDTO(String smppIPAdress, int smppPort,String systemId, String systemPassword, String smppVersion, Long enquireLinkRate, String systemType ) {
		this.smppIPAdress = smppIPAdress;
		this.smppPort = smppPort;
		this.systemId = systemId;
		this.systemPassword = systemPassword;
		this.smppVersion = smppVersion;
		this.enquireLinkRate = enquireLinkRate;
		this.systemType = systemType;
	}

	public String getSystemType() {
		return systemType;
	}

	public String getSmppIPAdress() {
		return smppIPAdress;
	}
	public int getSmppPort() {
		return smppPort;
	}
	public String getSystemId() {
		return systemId;
	}
	public String getSystemPassword() {
		return systemPassword;
	}
	public String getSmppVersion() {
		return smppVersion;
	}
	public Long getEnquireLinkRate() {
		return enquireLinkRate;
	}
	
	public String toString() {
		String s = "";
		s += "<smpp-params>\n";
		s += "  <systemType>" + systemType + "</systemType>\n";
		s += "  <smppIPAdress>" + smppIPAdress + "</smppIPAdress>\n";
		s += "  <smppPort>" + smppPort + "</smppPort>\n";
		s += "  <systemId>" + systemId + "</systemId>\n";
		s += "  <systemPassword>" + systemPassword + "</systemPassword>\n";
		s += "  <smppVersion>" + smppVersion + "</smppVersion>\n";
		s += "  <enquireLinkRate>" + enquireLinkRate + "</enquireLinkRate>\n";
		s += "</smpp-params>\n";
		return s;
	}
}

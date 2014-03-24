package com.ebridgecommerce.sdp.domain;

import java.util.List;

public class USSDSession {
	
	private String msisdn;
	private String sessionId;
	private List<String> answers;
	private TxnType txnType;

    public USSDSession(String msisdn, String sessionId, TxnType txnType) {
        this.msisdn = msisdn;
        this.sessionId = sessionId;
        this.txnType = txnType;
    }

    public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getAnswers() {
		return answers;
	}
	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

    public TxnType getTxnType() {
        return txnType;
    }

    public void setTxnType(TxnType txnType) {
        this.txnType = txnType;
    }
}

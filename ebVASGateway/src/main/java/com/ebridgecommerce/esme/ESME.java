package com.ebridgecommerce.esme;

import com.ebridgecommerce.domain.SmppBindParametersDTO;
import com.ebridgecommerce.smppgw.SmppPDUProcessor;
import com.ebridgecommerce.exceptions.Util;

public class ESME {

	public static final String SYSTEM_TYPE = "Kingdom";
	private SmppBindParametersDTO smppBindParametersDTO;
	private SmppPDUProcessor smppPDUProcessor;
	public static String linkState;

	public ESME(SmppBindParametersDTO smppBindParametersDTO) {
		this.smppBindParametersDTO = smppBindParametersDTO;
		smppPDUProcessor = new SmppPDUProcessor(smppBindParametersDTO, new MosmPDUListener(this));
		linkState = "UNKNOWN";
		System.out.println(smppBindParametersDTO);
	}

	public SmppPDUProcessor getSmppPDUProcessor() {
		return smppPDUProcessor;
	}

	private boolean bind() {
		if (smppPDUProcessor.bind() ) {
			new Thread(new MTSMSender(smppPDUProcessor)).start();
			return true;
		} else {
			return false;
		}
	}

	public void linkMonitor() {
		while (true) {
			linkState = "UNKNOWN";
			smppPDUProcessor.enquireLink();
			int count = 0;
			while ("UNKNOWN".equals(linkState) && count < 5) {
				new Util().sleep(1000L);
				++count;
			}
			if (!"UP".equals(linkState)) {
				System.out.print("-");
				bind();
			} else {
					new Util().sleep(smppBindParametersDTO.getEnquireLinkRate());
			}
		}
	}

	public static void main(String[] args) {

		if (args.length < 6) {
			System.out.println("Usage: esme <ebridgeSmppIPAdress> <ebridgeSmppPort> <ebridgeSystemId> <ebridgeSystemPassword> ebridgeSmppVersion> <enquireLinkRate>");
			System.exit(1);
		} else {
			SmppBindParametersDTO params = new SmppBindParametersDTO(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], Long.parseLong(args[5]),SYSTEM_TYPE);
			new ESME(params).linkMonitor();
		}
	}

}
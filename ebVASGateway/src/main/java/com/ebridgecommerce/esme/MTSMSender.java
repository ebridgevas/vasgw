package com.ebridgecommerce.esme;

import com.ebridgecommerce.smppgw.SmppPDUProcessor;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MTSMSender implements Runnable {

  /* TODO Make this class a Singleton */
	private SmppPDUProcessor smppPDUProcessor;

	public MTSMSender(SmppPDUProcessor smppPDUProcessor) {
		this.smppPDUProcessor = smppPDUProcessor;
	}

	@Override
	public void run() {
		System.out.println("To send an sms type: send>theSubscriber:yourShortMessage");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		do {
			try {
				System.out.println("send> ");
				line = in.readLine();
				String[] tokens = line.split(":");
				if (tokens[0].length() > 1) {
					smppPDUProcessor.submit("Kingdom", MobileNumberFormatter.format(tokens[0]), tokens[1]);
				} else {
					System.out.println(tokens[0] + " is an invalid command. To send an sms type: send|theSubscriber|yourShortMessage");
				}
			} catch (Exception e) {
				System.out.println("Error Sending message: " + e.getMessage());
			}
		} while (line != null);

		smppPDUProcessor.submit("Kingdom", "0733435854", "Test Message");
	}
}
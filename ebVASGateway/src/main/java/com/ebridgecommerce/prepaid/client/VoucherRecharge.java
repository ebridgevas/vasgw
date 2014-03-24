package com.ebridgecommerce.prepaid.client;

import com.comverse_in.prepaid.ccws.BalanceEntity;
import com.comverse_in.prepaid.ccws.ServiceSoap;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.comverse_in.prepaid.ccws.VoucherEntity;
import com.ebridgecommerce.exceptions.InvalidMobileNumberException;
import com.ebridgecommerce.exceptions.MobileNumberFormatter;
import com.ebridgecommerce.exceptions.TransactionFailedException;
import com.ebridgecommerce.exceptions.XMLParser;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author DaTekeshe
 */
public class VoucherRecharge {
	
	private ServiceSoap soapService;
	private String identity;

	public VoucherRecharge(ServiceSoap soapService, String identity) {
		this.soapService = soapService;
		this.identity = identity;
	}
	
	public List<Map<String, String>> voucherRecharge(String uuid, String sourceAccountId, String destinationAccountId, String rechargeVoucher)
			throws TransactionFailedException {
		
		List<Map<String, String>> responses = new ArrayList<Map<String, String>>();
		
		try {

			SubscriberRetrieve destinationAccountRetrieve = null;
			try {
				destinationAccountRetrieve = soapService.retrieveSubscriberWithIdentityNoHistory(MobileNumberFormatter.format(destinationAccountId).substring(3), identity, 1);
			} catch (RemoteException e) {
				throw new TransactionFailedException("Failed to retrieve subscriber : " + MobileNumberFormatter.format(destinationAccountId).substring(3) + " - " + XMLParser.getError(e.getMessage()));
			} catch (InvalidMobileNumberException e) {
				throw new TransactionFailedException("Source account is invalid");
			}
			
			/* Verify subscriber status */
			if (!"Active".equalsIgnoreCase(destinationAccountRetrieve.getSubscriberData().getCurrentState())) {
				throw new TransactionFailedException("Subscriber account is " + destinationAccountRetrieve.getSubscriberData().getCurrentState());
			}

			/* Retrieve the voucher */
			VoucherEntity voucherEntity = soapService.retrieveVoucherBySecretCode(rechargeVoucher);
			if (voucherEntity == null) {
				throw new TransactionFailedException("Failed to retrieve voucher : " + rechargeVoucher);
			}

			/* Check voucher state */
			if (!"Active".equalsIgnoreCase(voucherEntity.getState())) {
				throw new TransactionFailedException("Voucher is " + voucherEntity.getState());
			}

			/* Validate transaction */

			/* Maximum balance check */

			/* Get current balance for this subscriber */
			BigDecimal beneficiaryAccountBalance = null;
			for (BalanceEntity balanceEntity : destinationAccountRetrieve.getSubscriberData().getBalances()) {
				if ("Core".equalsIgnoreCase(balanceEntity.getBalanceName())) {
					beneficiaryAccountBalance = new BigDecimal(balanceEntity.getBalance());
					break;
				}
			}

			/* TODO Check for maximum balance violation */

			String balanceChangeComments = "Voucher Recharge # " + rechargeVoucher + " for subscriber # " + MobileNumberFormatter.format(destinationAccountId).substring(3);
			soapService.rechargeAccountBySubscriber(MobileNumberFormatter.format(destinationAccountId).substring(3), identity, rechargeVoucher, balanceChangeComments);
			if (MobileNumberFormatter.format(destinationAccountId).equals(MobileNumberFormatter.format(sourceAccountId))) {
				responses.add(getResponse(sourceAccountId, voucherEntity.getFaceValue() + " recharge voucher accepted. Your balance is now " + new DecimalFormat("###,##.00").format(beneficiaryAccountBalance.add(voucherEntity.getFaceValue()).doubleValue())));
			} else {
				responses.add(getResponse(sourceAccountId, voucherEntity.getFaceValue() + " recharge voucher for " + MobileNumberFormatter.shortFormat(destinationAccountId) + " accepted."));
				responses.add(getResponse(destinationAccountId, voucherEntity.getFaceValue() + " recharge voucher from" + MobileNumberFormatter.shortFormat(sourceAccountId) + " accepted. Your balance is now " + new DecimalFormat("###,##.00").format(beneficiaryAccountBalance.add(voucherEntity.getFaceValue()).doubleValue())));
			}
			
		} catch (RemoteException ex) {
			throw new TransactionFailedException(XMLParser.getError(ex.getMessage()));
		} catch(Exception ex) {
			throw new TransactionFailedException(ex.getMessage());
		}
		return responses;
	}

	private Map<String, String> getResponse(String subscriberId, String shortMessage) {
		Map<String, String> response = new HashMap<String, String>();
		try {
			response.put("subscriberId", MobileNumberFormatter.format(subscriberId));
		} catch (InvalidMobileNumberException e) {
			response.put("subscriberId", subscriberId);
		}
		response.put("shortMessage", shortMessage);
		return response;
	}
}
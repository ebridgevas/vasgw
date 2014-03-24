package com.ebridgevas.model;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 12/4/12
 * Time: 4:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class BalanceDTO {
    private String walletId;
    private String walletDescription;
    private String balance;
    private String expiryDate;

    public BalanceDTO(String walletId, String walletDescription, String balance, String expiryDate) {
        this.walletId = walletId;
        this.walletDescription = walletDescription;
        this.balance = balance;
        this.expiryDate = expiryDate;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getWalletDescription() {
        return walletDescription;
    }

    public String getBalance() {
        return balance;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}

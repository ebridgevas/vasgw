package com.ebridgevas.model;



import java.util.Map;

/**
 * david@ebridgevas.com
 *
 */
public class MobileAccount {

    private final String mobileNumber;
    private final String dataBalanceName;
    private Map<String, BalanceDTO> balances;

    public MobileAccount(
            String mobileNumber,
            String dataBalanceName) {

        this.mobileNumber = mobileNumber;
        this.dataBalanceName = dataBalanceName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getDataBalanceName() {
        return dataBalanceName;
    }

    public Map<String, BalanceDTO> getBalances() {
        return balances;
    }

    public void setBalances(Map<String, BalanceDTO> balances) {
        this.balances = balances;
    }
}

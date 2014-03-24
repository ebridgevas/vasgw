/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import com.comverse_in.prepaid.ccws.BalanceCreditAccount;
import com.comverse_in.prepaid.ccws.BalanceInformationClient;
import com.comverse_in.prepaid.ccws.ZSmartClient;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.rpc.ServiceException;

/**
 *
 * @author matsaudzaa
 */
public class CreditAccount {
    
    private final static String CORE_BALANCE_NAME = "Core";
    
     private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      CreditAccount.class.getName());
    
    /*public Double getSubscriberBalance(String msisdn) throws ServiceException, RemoteException{
        try{
           
            wseclient ocsClient = new wseclient();
            BalanceInformationClient balance = new BalanceInformationClient();
            
           balance  = ocsClient.getSubscriberBalanceBefore(msisdn);
            
            
           return balance.getCoreBalanceBefore();
        }catch(Exception ex){
            //System.out.println("##### ERROR RETRIEVING BALANCE msisdn:"+msisdn+", date : "+new Date());
            _log.error("##### ERROR RETRIEVING BALANCE FROM CREDITACCOUNT msisdn:"+msisdn+", date : "+new Date(), ex);
            
            return 0.0;
        }
    }*/
    
   /* public boolean subtractAmount(String msisdn, Double amount){
        try{
             wseclient ocsClient = new wseclient();
             List<BalanceCreditAccount> walletAccounts = new ArrayList<BalanceCreditAccount>();
             BalanceCreditAccount coreWallet = new BalanceCreditAccount();
             BalanceInformationClient bal = ocsClient.getSubscriberBalanceBefore(msisdn);
            
             coreWallet.setBalanceName(CORE_BALANCE_NAME);
             Calendar expDate = Calendar.getInstance();
             expDate.setTime(bal.getCoreExpiryDateBefore());
             coreWallet.setExpirationDate(expDate);
             coreWallet.setCreditValue(amount);
        
             walletAccounts.add(coreWallet);
          
           //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccounts.size()];
           walletAccounts.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, msisdn);
          
            return ocsResult; 
        }catch(Exception ex){
            System.out.println("##### FAILED TO CREDIT AIRTIME, msisdn:"+msisdn+", date : "+new Date());
          // _log.error("##### FAILED TO CREDIT AIRTIME FROM CREDIT ACCOUNT, msisdn:"+msisdn+", date : "+new Date(), ex);
            //ex.printStackTrace();
            return false;
        }
    } */
    
}

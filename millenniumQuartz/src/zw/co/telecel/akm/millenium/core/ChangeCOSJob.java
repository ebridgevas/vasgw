/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import com.comverse_in.prepaid.ccws.BalanceCreditAccount;
import com.comverse_in.prepaid.ccws.BalanceInformationClient;
import com.comverse_in.prepaid.ccws.ZSmartClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import smsgateway.SMSGateway;
import zw.co.telecel.akm.millenium.dao.BundleDao;
import zw.co.telecel.akm.millenium.dao.ErrorLogDao;
import zw.co.telecel.akm.millenium.dao.MTransactionDao;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dto.BundleDto;
import zw.co.telecel.akm.millenium.dto.ERRORLOG;
import zw.co.telecel.akm.millenium.dto.MTransaction;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.utils.ExpiryDateGenerator;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;
import zw.co.telecel.akm.webservice.ccb.client.ChangeCOS;
import zw.co.telecel.akm.webservice.ccb.client.CheckNumber;
//import zw.co.telecel.akm.webservices.zte.trigger.RegisterEvent;

/**
 *
 * @author matsaudzaa
 */
public class ChangeCOSJob implements Job {
   
    //private static Logger _log = LoggerFactory.getLogger(ChangeCOSJob.class);
     /* Get actual class name to be printed on */
  private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      ChangeCOSJob.class.getName());
    
    private final static String MILLENIUM_COS_NAME = "4007";
     private final static String TEL_COS_NAME = "TEL_COS";
     private DataManager dataManager = new DataManager();

    public ChangeCOSJob() {
    }
    
     public void execute(JobExecutionContext context)
        throws JobExecutionException {
          try{
        // Say Hello to the World and display the date/time
      
        
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String msisdn = dataMap.getString("msisdn");
        String bundleType = dataMap.getString("bundleType");
        //String dateOfPurchase = dataMap.getString("dateOfPurchase");
      
        
       // Date purchaseDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").parse(dateOfPurchase);
        Date purchaseDate = ExpiryDateGenerator.purchaseDate();
        System.out.println("###### JOB EXECUTING MSISDN FROM CHANGECOSJOB : "+msisdn +" ,date :"+ new Date());
       //  _log.info("###### JOB EXECUTING MSISDN FROM CHANGECOSJOB : "+msisdn +" ,date :"+ new Date());
        //.out.println("JOB EXECUTING MSISDN : "+msisdn);
       
        //if unable to remove quartz job on opt out
        try{
            RegisterDao rDAO = new RegisterDao();
            rDAO.initEntityManager();
          Register entry = rDAO.findByMobileNumber(msisdn);
          if(entry == null){
              //sub opted out, do not execute job
              System.out.println("####### Sub opted out do not excute change cos job, msisdn:"+msisdn+", date:"+new Date());
             // _log.info("####### Sub opted out do not excute change cos job, msisdn:"+msisdn+", date:"+new Date());
              return;
          }else{
              //doest work anymore, register now being removed on migration
           /* if(entry.getDateOfPurchase().after(purchaseDate)){
                //bundle migration occured
                System.out.println("####### Bundle Migration occured do not excute change cos job, msisdn:"+msisdn+", date:"+new Date());
                return;
            }  */
              
        //zte ocs check core > $2 
   ZSmartClient ocsClient2 = new ZSmartClient();
        Double amount = 0.0;
        try{
            if("1".equals(bundleType)){
                 //amount = 
                         BalanceInformationClient balance =ocsClient2.getSubscriberBalanceBefore(msisdn, "TeleBonus_LV");  //credit.getSubscriberBalance(msisdn);
                         amount = balance.getCoreBalanceBefore();
            }else{
                BalanceInformationClient balance =ocsClient2.getSubscriberBalanceBefore(msisdn, "TeleBonus_HV");  //credit.getSubscriberBalance(msisdn);
                         amount = balance.getCoreBalanceBefore();
            }
          
        }catch(Exception ex){
            System.out.println("##### Possible error on checking balance on Change COS");
            //remove from register
             RegisterDao rDAO6 = new RegisterDao();
            rDAO6.initEntityManager();
          Register entry6 = rDAO6.findByMobileNumber(msisdn);
          rDAO6.delete(entry6);
          
            //send sms
           //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the Telecel Daily Package has expired, please dial *146# to subscribe again");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex6){
              System.out.println("#### ERROR SENDING A SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
            
            return;
        }
        
        // _log.info("###### CHECKING SUBSCRIBER BALANCE ON CHANGE COS JOB, MSISDN"+msisdn+", DATE:"+new Date()+", balance:"+amount);
       // System.out.println("###### Balance is : "+ amount);
         Double bundleAmount = 0.0;
         Double duration = 0.0;
         String bt = "0";
         String walletName = "";
         if(bundleType.equals("1")){
             bundleAmount = 0.50;
             duration = 0.006*90; // 5mins test
             bt = "1";
             walletName ="TeleBonus_LV";
             
         }else{
             bundleAmount = 1.00;
              duration = 0.005*200;  //10mins test
              bt = "2";
              walletName ="TeleBonus_HV";
         }
        if(amount >= bundleAmount ){
            // sub is auto renewed
            // credit account --$2
           
                  BundleDto b2 = new BundleDto();
            Date exp = ExpiryDateGenerator.generateExpiryDate(24);      
            b2.setExpiryDate(exp);
            b2.setExpiryDateTimestamp(exp);
            //b2.setExpiryDays(2); //test purposes
            b2.setAmount(bundleAmount);
            b2.setDuration(duration);  // 120mins
            b2.setBundleType(bt);
            b2.setId(KeyGenerator.generateEntityId());
            b2.setMobileNumber(msisdn);
            Date transactionDate = new Date();
            b2.setTransactionDate(transactionDate);
            b2.setTransactionDateTimestamp(transactionDate);
            b2.setTransactionType("RENEWAL");
            
            
            
            
            
             try{
            //call zte ocs and credit international wallet
            ZSmartClient ocsClient = new ZSmartClient();
            CheckNumber check = new CheckNumber();
           
            BalanceInformationClient balance = new BalanceInformationClient();
            balance  = ocsClient.getSubscriberBalanceBefore(msisdn,walletName);
            
            //deplete daily wallet to zero
            Double expiredBalance = balance.getInternationalBundleBefore();
            
    
        
            
            
         // populate wallets
        List<BalanceCreditAccount> walletAccounts = new ArrayList<BalanceCreditAccount>();
            
        //depletion first
            BalanceCreditAccount internationalWalletE = new BalanceCreditAccount();
           internationalWalletE.setBalanceName(walletName); //Daily Wallet
           internationalWalletE.setCreditValue(-expiredBalance);
           Calendar dailyExpiryE = Calendar.getInstance();
           Date bExpE = b2.getExpiryDateTimestamp();
           dailyExpiryE.setTime(bExpE);
          
           internationalWalletE.setExpirationDate(dailyExpiryE);
           walletAccounts.add(internationalWalletE);
           
     
           BalanceCreditAccount internationalWallet = new BalanceCreditAccount();
           internationalWallet.setBalanceName(walletName); //Daily Wallet
           internationalWallet.setCreditValue(duration);
           Calendar dailyExpiry = Calendar.getInstance();
           Date bExp = b2.getExpiryDateTimestamp();
           dailyExpiry.setTime(bExp);
          
           internationalWallet.setExpirationDate(dailyExpiry);
           walletAccounts.add(internationalWallet);
           
           
           BalanceCreditAccount coreWallet = new BalanceCreditAccount();
           coreWallet.setBalanceName("Core");
           coreWallet.setCreditValue(-bundleAmount);
           Calendar coreExpiryBefore = Calendar.getInstance();
           Date cdate =  balance.getCoreExpiryDateBefore();   
           if(cdate == null){
               Calendar cal = Calendar.getInstance(); //default Date : [current Date]  //reason wallet might not exist before recharge zte ocs
              
               cdate = cal.getTime();
               balance.setCoreExpiryDateBefore(cdate);
           }
       
        coreExpiryBefore.setTime(cdate);
        coreWallet.setExpirationDate(coreExpiryBefore);
        walletAccounts.add(coreWallet);
          
           //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccounts.size()];
           walletAccounts.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, msisdn);
           if(ocsResult){
               //sucess
               BundleDao dao = new BundleDao();
               dao.initEntityManager();
               dao.persist(b2);
               
               
               //register new window period
                // register new window period
            RegisterTrigger trigger = new RegisterTrigger();
            boolean status = trigger.registerWindowPeriod(msisdn,false, true,exp,bt,transactionDate.toString(),bundleAmount.toString(),true); //returns boolean might save output for debugging purposes
            
             if(status == false){
                     //System.out.println("##### FAILED TO REGISTER NEW PERIOD ON CHANGE COS JOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                     _log.error("##### FAILED TO REGISTER NEW PERIOD ON CHANGE COS JOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                     //log to DB
                     try{
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REGISTER NEW PERIOD ON CHANGE COS JOB, msisdn:"+msisdn+", must expire on : "+exp);
                          error.setEventType("WINDOW_PERIOD");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                     }
                          catch(Exception ex){
                             // System.out.println("##### CHECK DB, ERROR SAVING REGISTER NEW PERIOD LOG FROM CHANGECOSJOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                              _log.error("##### CHECK DB, ERROR SAVING REGISTER NEW PERIOD LOG FROM CHANGECOSJOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp, ex);
                          }
                 }
               
               //////************** SEND SMS TO SUB
               
               
                     //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the USD"+bundleAmount+" bundle has been renewed, it expires on "+exp);
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING AUTO RENEWAL SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
             
           }else{
               //fail
               BundleDto errorBundle = new BundleDto();
               ERRORLOG errorLog = new ERRORLOG();
               errorLog.setComment("BUNDLE FOR : "+bundleAmount);
               errorLog.setEventType("AUTORENEW_PURCHASE_ERROR");
               errorLog.setLogDate(new Date());
               errorLog.setLogDateTimestamp(new Date());
               errorLog.setMsisdn(msisdn);
               errorLog.setId(KeyGenerator.generateEntityId());
            
           
               ErrorLogDao dao = new ErrorLogDao();
               dao.initEntityManager();
               dao.persist(errorLog);
               
               System.out.println("#####  error on crediting account on auto renewal Change COS");
            //remove from register
             RegisterDao rDAO7 = new RegisterDao();
            rDAO7.initEntityManager();
          Register entry7 = rDAO7.findByMobileNumber(msisdn);
          rDAO7.delete(entry7);
          
            //send sms
           //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the Telecel Daily Package has expired, please dial *146# to subscribe again");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex6){
              System.out.println("#### ERROR SENDING A SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
             
               
           }
           
            
            
            
            
        }catch(Exception ex){
            //RegisterDao rDAO2 = new RegisterDao();
           // rDAO2.initEntityManager();
         // Register entry2 = rDAO2.findByMobileNumber(msisdn);
         // rDAO2.delete(entry2);
           System.out.println("##### ERROR crediting and debiting amount on auto renewal, misdn:"+msisdn+", date: "+new Date());
           
           //send sms
           //notfiy subscriber of de-activation
                    BundleDto errorBundle = new BundleDto();
               ERRORLOG errorLog = new ERRORLOG();
               errorLog.setComment("ERROR crediting and debiting amount on auto renewal");
               errorLog.setEventType("CREDITING_DEBITING_ONAUTO RENEWAL_ERROR");
               errorLog.setLogDate(new Date());
               errorLog.setLogDateTimestamp(new Date());
               errorLog.setMsisdn(msisdn);
               errorLog.setId(KeyGenerator.generateEntityId());
            
           
               ErrorLogDao dao2 = new ErrorLogDao();
               dao2.initEntityManager();
               dao2.persist(errorLog);
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the Telecel Daily Package has expired, please dial *146# to subscribe again");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex6){
              System.out.println("#### ERROR SENDING A SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
           
        }
            
            /////////////
        }else{
         //no money clause  
            
            
            
             try{
            //call zte ocs and credit international wallet
            ZSmartClient ocsClient = new ZSmartClient();
            CheckNumber check = new CheckNumber();
           
            BalanceInformationClient balance = new BalanceInformationClient();
            balance  = ocsClient.getSubscriberBalanceBefore(msisdn,walletName);
            
            //deplete daily wallet to zero
            Double expiredBalance = balance.getInternationalBundleBefore();
            
    
        
            
            
         // populate wallets
        List<BalanceCreditAccount> walletAccounts = new ArrayList<BalanceCreditAccount>();
            
        //depletion first
            BalanceCreditAccount internationalWalletE = new BalanceCreditAccount();
           internationalWalletE.setBalanceName(walletName); //Daily Wallet
           internationalWalletE.setCreditValue(-expiredBalance);
           Calendar dailyExpiryE = Calendar.getInstance();
           Date bExpE = new Date();
           dailyExpiryE.setTime(bExpE);
          
           internationalWalletE.setExpirationDate(dailyExpiryE);
           walletAccounts.add(internationalWalletE);
           
     
           
           //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccounts.size()];
           walletAccounts.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, msisdn);
           if(ocsResult){
               //sucess
               BundleDao dao = new BundleDao();
               BundleDto b2 = new BundleDto();
            Date exp = ExpiryDateGenerator.generateExpiryDate(24);      
            b2.setExpiryDate(exp);
            b2.setExpiryDateTimestamp(exp);
            //b2.setExpiryDays(2); //test purposes
            b2.setAmount(bundleAmount);
            b2.setDuration(duration);  // 120mins
            b2.setBundleType(bt);
            b2.setId(KeyGenerator.generateEntityId());
            b2.setMobileNumber(msisdn);
            Date transactionDate = new Date();
            b2.setTransactionDate(transactionDate);
            b2.setTransactionDateTimestamp(transactionDate);
            b2.setTransactionType("DEACTIVATION");
               dao.initEntityManager();
               dao.persist(b2);
               
               //////************** SEND SMS TO SUB
               try{
                    RegisterDao rDAO2 = new RegisterDao();
            rDAO2.initEntityManager();
          Register entry2 = rDAO2.findByMobileNumber(msisdn);
          rDAO2.delete(entry2);
               }catch(Exception ex){
                   
                      //fail
               BundleDto errorBundle = new BundleDto();
               ERRORLOG errorLog = new ERRORLOG();
               errorLog.setComment("REGISTER REMOVAL NO MONEY");
               errorLog.setEventType("REMOVE_REGISTER_ERROR");
               errorLog.setLogDate(new Date());
               errorLog.setLogDateTimestamp(new Date());
               errorLog.setMsisdn(msisdn);
               errorLog.setId(KeyGenerator.generateEntityId());
            
           
               ErrorLogDao dao2 = new ErrorLogDao();
               dao2.initEntityManager();
               dao2.persist(errorLog);
                   
                   
               }
               
                     //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the USD"+bundleAmount+" bundle has been deactivated,please dial *146# to renew");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING DEACTIVATION SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
             
           }else{
               //fail
               BundleDto errorBundle = new BundleDto();
               ERRORLOG errorLog = new ERRORLOG();
               errorLog.setComment("BUNDLE FOR : "+bundleAmount);
               errorLog.setEventType("DEPLETION_DEACTIVATION_ERROR");
               errorLog.setLogDate(new Date());
               errorLog.setLogDateTimestamp(new Date());
               errorLog.setMsisdn(msisdn);
               errorLog.setId(KeyGenerator.generateEntityId());
            
           
               ErrorLogDao dao = new ErrorLogDao();
               dao.initEntityManager();
               dao.persist(errorLog);
               
               
               
               
                try{
                    RegisterDao rDAO2 = new RegisterDao();
            rDAO2.initEntityManager();
          Register entry2 = rDAO2.findByMobileNumber(msisdn);
          rDAO2.delete(entry2);
               }catch(Exception ex){
                   
                      //fail
               BundleDto errorBundle2 = new BundleDto();
               ERRORLOG errorLog2 = new ERRORLOG();
               errorLog2.setComment("REGISTER REMOVAL NO MONEY");
               errorLog2.setEventType("REMOVE_REGISTER_ERROR");
               errorLog2.setLogDate(new Date());
               errorLog2.setLogDateTimestamp(new Date());
               errorLog2.setMsisdn(msisdn);
               errorLog2.setId(KeyGenerator.generateEntityId());
            
           
               ErrorLogDao dao10 = new ErrorLogDao();
               dao10.initEntityManager();
               dao10.persist(errorLog2);
                   
                   
               }
               
                     //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the USD"+bundleAmount+" bundle has been deactivated,please dial *146# to renew");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING DEACTIVATION SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
               
               
               
             
               
           }
           
            
            
            
            
        }catch(Exception ex){
        
            System.out.println("################################ERROR crediting and debiting amount on auto renewal ");
            BundleDto errorBundle = new BundleDto();
               ERRORLOG errorLog = new ERRORLOG();
               errorLog.setComment("ERROR crediting and debiting amount on auto renewal");
               errorLog.setEventType("CREDITING_DEBITING_ONAUTO RENEWAL_ERROR");
               errorLog.setLogDate(new Date());
               errorLog.setLogDateTimestamp(new Date());
               errorLog.setMsisdn(msisdn);
               errorLog.setId(KeyGenerator.generateEntityId());
            
           
               ErrorLogDao dao2 = new ErrorLogDao();
               dao2.initEntityManager();
               dao2.persist(errorLog);
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to the Telecel Daily Package has expired, please dial *146# to subscribe again");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex6){
              System.out.println("#### ERROR SENDING A SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING DEACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
            
                          
            }
        }
            
            
            
        }
                ////////////////////////////////////////////
                
            
            
                      
                      
                      
             
             
             
           
                      
                  }
            
          
         
        
     }catch(Exception ex){
         System.out.println("##### Error retrieving balance on change cos");
         RegisterDao rDAO4 = new RegisterDao();
            rDAO4.initEntityManager();
          Register entry4 = rDAO4.findByMobileNumber(msisdn);
          rDAO4.delete(entry4);
         //ex.printStackTrace();
     }
    
}catch(Exception ex){
    System.out.println("FAILED TO EXECUTE JOB");
     
}
     
          
     }//end of job exe
     
}//end of class
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebridge.sdp.vas.services;


import com.comverse_in.prepaid.ccws.BalanceCreditAccount;
import com.comverse_in.prepaid.ccws.BalanceInformationClient;
import com.comverse_in.prepaid.ccws.ZSmartClient;
import com.ebridge.sdp.vas.utils.KeyGenerator;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.rpc.ServiceException;
import milleniumregister.MilleniumRegister;
import smsgateway.SMSGateway;
import test.CheckCOS;
import zw.co.telecel.akm.millenium.core.ChangeCOSJob;
import zw.co.telecel.akm.millenium.core.CreditAccount;
import zw.co.telecel.akm.millenium.core.DataManager;
import zw.co.telecel.akm.millenium.core.RegisterTrigger;
import zw.co.telecel.akm.millenium.dao.BundleDao;
import zw.co.telecel.akm.millenium.dao.ErrorLogDao;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dto.BundleDto;
import zw.co.telecel.akm.millenium.dto.ERRORLOG;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.utils.ExpiryDateGenerator;
import zw.co.telecel.akm.webservice.ccb.client.ChangeCOS;
import zw.co.telecel.akm.webservice.ccb.client.CheckNumber;
//import zw.co.telecel.akm.webservices.zte.trigger.RegisterEvent;

/**
 *
 * @author matsaudzaa
 */
public class MilleniumlModel {
    
    private String msisdn;
    private int level;
    private Operation operation;
    //private BundleDao dao;
     private final static String INTERNATIONAL_BALANCE_NAME = "TeleBonus";
     private final static String CORE_BALANCE_NAME = "Core";
     private final static String STATUS_SUCCESS = "success";
     private final static String STATUS_FAIL = "fail";
     private final static String STATUS_INSUFFICIENT = "insufficient";
     private final static String NOT_TEL_COS = "notPrepaid";
      private final static String BUNDLE_EXISTS = "bundleexists";
      private final static String MILLENIUM_COS_NAME = "4007";
     private final static String TEL_COS_NAME = "TEL_COS";
     
     DataManager dataManager = new DataManager();
     
     private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      MilleniumlModel.class.getName());

    public MilleniumlModel() {
        
    }
    
    MilleniumlModel(String msisdn, int level) {
        this.msisdn = msisdn;
        this.level = level;
       
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

   /* public BundleDao getDao() {
        return dao;
    }

    public void setDao(BundleDao dao) {
        this.dao = dao;
    }*/
    
    
    public String purchaseBundle(String mobileNumber) {
        
        
        if (operation == Operation.ONE) {
            BundleDto b1 = new BundleDto();
             BundleDao dao = new BundleDao();
               dao.initEntityManager();
               if((dao.findByMobileNumber(mobileNumber).isEmpty())){
                   b1.setTransactionType("SUBSCRIPTION");
               }else{
                    b1.setTransactionType("RENEWAl");
               }
            
            b1.setExpiryDate(ExpiryDateGenerator.generateExpiryDate(24));
            b1.setExpiryDateTimestamp(ExpiryDateGenerator.generateExpiryDate(24)); //purchase date + 24hrs
            //b1.setExpiryDays(1); //test purposes
            b1.setAmount(0.50);
            b1.setDuration(0.006*90); // test 5mins    //0.008*90 real value
            b1.setBundleType("1");
            b1.setId(KeyGenerator.generateEntityId());
            b1.setMobileNumber(mobileNumber);
            Date transactionDate = new Date();
            b1.setTransactionDate(transactionDate);
            b1.setTransactionDateTimestamp(transactionDate);
             RegisterDao rdao = new RegisterDao();
               rdao.initEntityManager();
              Register register = rdao.findByMobileNumber(msisdn);
              String status = "";
              if(register == null){
                    status = this.executeTransaction(b1,false,"","TeleBonus_LV",false);
              }else{
                   Date expiry = register.getExpiryDate();
                   if(expiry.after(new Date())){
                       //purchase another bundle before original bundle expiry
                       //check if bundle bance is greater than 0
                       if(this.balanceUponPurchase(msisdn)>0.018){
                       status = BUNDLE_EXISTS;
                       }else{
                       status = this.executeTransaction(b1,true,register.getBundleType(),"TeleBonus_LV",false);  
                       }
                   }else{
                       
                       status = this.executeTransaction(b1,true,register.getBundleType(),"TeleBonus_LV",true);  
                   }
                   
              }
          
            switch (status) {
                case STATUS_SUCCESS:
                    return "Your subscription to the $0.50 bundle of 90mins valid for 24hours was successful";
                case STATUS_INSUFFICIENT:
                    return "You have insufficient credit to subscribe to the bundle";
                case NOT_TEL_COS:
                    return "This service is for prepaid subscribers only";
                    case BUNDLE_EXISTS:
                    return "You still have another valid bundle,Please use up the existing bundle first before buying a new one";
                default:
                    return "Error Processing Transaction";
            }
            
        } else if (operation == Operation.TWO) {
            
            BundleDto b2 = new BundleDto();
             BundleDao dao = new BundleDao();
               dao.initEntityManager();
               if((dao.findByMobileNumber(mobileNumber).isEmpty())){
                   b2.setTransactionType("SUBSCRIPTION");
               }else{
                    b2.setTransactionType("RENEWAL");
               }
            b2.setExpiryDate(ExpiryDateGenerator.generateExpiryDate(24));
            b2.setExpiryDateTimestamp(ExpiryDateGenerator.generateExpiryDate(24));
            //b2.setExpiryDays(2); //test purposes
            b2.setAmount(1.00);
            b2.setDuration(0.005*200);  // 10mins for test real value 200mins
            b2.setBundleType("2");
            b2.setId(KeyGenerator.generateEntityId());
            b2.setMobileNumber(mobileNumber);
            Date transactionDate = new Date();
            b2.setTransactionDate(transactionDate);
            b2.setTransactionDateTimestamp(transactionDate);
            RegisterDao rdao = new RegisterDao();
               rdao.initEntityManager();
              Register register = rdao.findByMobileNumber(msisdn);
              String status = "";
              if(register == null){
                    status = this.executeTransaction(b2,false,"","TeleBonus_HV",false);
              }else{
                   Date expiry = register.getExpiryDate();
                   if(expiry.after(new Date())){
                       //purchase another bundle before original bundle expiry
                        if(this.balanceUponPurchase(msisdn)>0.015){
                       status = BUNDLE_EXISTS;
                       }else{
                       status = this.executeTransaction(b2,true,register.getBundleType(),"TeleBonus_HV",false);  
                        }
                   }else{
                       status = this.executeTransaction(b2,true,register.getBundleType(),"TeleBonus_HV",true);  
                   }
              }
            switch (status) {
                case STATUS_SUCCESS:
                    return "Your subscription to the $1 bundle of 200mins valid for 24hours was successful";
                case STATUS_INSUFFICIENT:
                    return "You have insufficient credit to purchase the bundle";
                case NOT_TEL_COS:
                    return "This service is for prepaid subscribers only";
                     case BUNDLE_EXISTS:
                    return "You still have another valid bundle,Please use up the existing bundle first before buying a new one";
                default:
                    return "Error Processing Transaction";
            }
            
            
        }  else {
            return null;
        }
    }
    
    
    public String executeTransaction(BundleDto bundle, boolean existingBundle,String bundleName,String bundleType, boolean bundleExpiry){
        
        try{
            //call zte ocs and credit international wallet
            ZSmartClient ocsClient = new ZSmartClient();
            CheckNumber check = new CheckNumber();
            CheckCOS coscheck = new CheckCOS();
            
            if(!(coscheck.IsSubscriberCOSAllowed(bundle.getMobileNumber()))){
            return NOT_TEL_COS;
            }
            
            if( !(check.isPrepaid(bundle.getMobileNumber())) ){
                //msisdn not prepaid
                return NOT_TEL_COS;
            }
            
            //are you in Mo'Fire
            boolean status = MilleniumRegister.isSubscriberInMillenium(bundle.getMobileNumber());
            if(status){
                try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(bundle.getMobileNumber(), "Telecel", "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING SMS, msisdn: "+msisdn+" at : "+new Date());
             // _log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex);
              
             
            }
                         return NOT_TEL_COS;
            }
            
            //Check Number IF IT IS IN MOFIRE JAR
            
            
            BalanceInformationClient balance = new BalanceInformationClient();
            balance  = ocsClient.getSubscriberBalanceBefore(bundle.getMobileNumber(),bundleName);
            if(bundle.getAmount() > balance.getCoreBalanceBefore()){
                return STATUS_INSUFFICIENT;
            }else{
                //populate wallets
                
                List<BalanceCreditAccount> walletAccounts = new ArrayList<BalanceCreditAccount>();
                 List<BalanceCreditAccount> walletAccountsB4 = new ArrayList<BalanceCreditAccount>();
                
                if(existingBundle){
                    //renewal
                 //BUNDLE MIGRATION
                    //check if there is migration occuring
                    if(bundleName.equals(bundleType)){    //bundleName previous subscription and bundleType is current subscription
                        //no migration 
                         //purchase after expiry or before expiry
                        //purchase after expiry - no jobs to run
                        
                        if(bundleExpiry == false){
                            //purchase before expiry
                            //remove the jobs
                            DataManager dataManager = new DataManager();
                            dataManager.removeJobs(msisdn);
                        }
                        try{
                           BalanceCreditAccount internationalWallet2 = new BalanceCreditAccount();
           internationalWallet2.setBalanceName(bundleName); //Daily Wallet
           
           internationalWallet2.setCreditValue(-balance.getInternationalBundleBefore());
           Calendar dailyExpiry2 = Calendar.getInstance();
          
          
           internationalWallet2.setExpirationDate(dailyExpiry2);
           if(balance.getInternationalBundleBefore() == 0.00){
               //do not attempt to deplete
           }else{
                walletAccountsB4.add(internationalWallet2); 
           }
           
            //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccountsB4.size()];
           walletAccountsB4.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, bundle.getMobileNumber());
           
                        }catch(Exception ee){
                        //accumulation occurs
                        }
           //end of try      
     
           BalanceCreditAccount internationalWallet = new BalanceCreditAccount();
           internationalWallet.setBalanceName(bundleName); //Daily Wallet
           internationalWallet.setCreditValue(bundle.getDuration());
           Calendar dailyExpiry = Calendar.getInstance();
           Date bExp = bundle.getExpiryDateTimestamp();
           dailyExpiry.setTime(bExp);
          
           internationalWallet.setExpirationDate(dailyExpiry);
           walletAccounts.add(internationalWallet);
           
           
           BalanceCreditAccount coreWallet = new BalanceCreditAccount();
           coreWallet.setBalanceName(CORE_BALANCE_NAME);
           coreWallet.setCreditValue(-bundle.getAmount());
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
                        
                        
                    }else{
                        //migration
                        //migrate after expiry or migrate while before expiry
                        
                         if(bundleExpiry == false){
                            //purchase before expiry
                            //remove the jobs
                            DataManager dataManager = new DataManager();
                            dataManager.removeJobs(msisdn);
                        }
                         
                        
                        
                         //Add depletion for the previous bundle
                          try{
                        BalanceCreditAccount internationalWalletB4 = new BalanceCreditAccount();
           internationalWalletB4.setBalanceName(bundleName); //Daily Wallet
           
           internationalWalletB4.setCreditValue(-balance.getInternationalBundleBefore()); //bundle name
           Calendar dailyExpiryB4 = Calendar.getInstance();
           internationalWalletB4.setExpirationDate(dailyExpiryB4);
           
            if(balance.getInternationalBundleBefore() == 0.000){
                //do not attempt to deplete
            }else{
                 walletAccountsB4.add(internationalWalletB4); 
            }
            //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccountsB4.size()];
           walletAccountsB4.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, bundle.getMobileNumber());
                          }catch(Exception e){
                          // previous bundle remain with money saka ma1
                          }
           //end of try
         
          
          BalanceInformationClient balanceTo = new BalanceInformationClient();
        //  System.out.println("#################### balanceTo obj created");
            balanceTo  = ocsClient.getSubscriberBalanceBefore(bundle.getMobileNumber(),bundleType);
          //  System.out.println("#################### after trying to get balanceTo");
            if(balanceTo.getInternationalBundleBefore() == null){
                // dont deplete
            }else{
                
               // System.out.println("#################### balanceTo obj not null "+balanceTo.getInternationalBundleBefore());
                BalanceCreditAccount internationalWallet2 = new BalanceCreditAccount();
           internationalWallet2.setBalanceName(bundleType); //Daily Wallet
           
           internationalWallet2.setCreditValue(-balanceTo.getInternationalBundleBefore()); //it has to be bundle type
           Calendar dailyExpiry2 = Calendar.getInstance();
          
          
           internationalWallet2.setExpirationDate(dailyExpiry2);
           if(balanceTo.getInternationalBundleBefore() == 0.00){
               //do not attempt to deplete
           }else{
                walletAccounts.add(internationalWallet2); 
           }
            }
                         
                        
                  
     
           BalanceCreditAccount internationalWallet = new BalanceCreditAccount();
           internationalWallet.setBalanceName(bundleType); //Daily Wallet
           internationalWallet.setCreditValue(bundle.getDuration());
           Calendar dailyExpiry = Calendar.getInstance();
           Date bExp = bundle.getExpiryDateTimestamp();
           dailyExpiry.setTime(bExp);
          
           internationalWallet.setExpirationDate(dailyExpiry);
           walletAccounts.add(internationalWallet);
           
           
           BalanceCreditAccount coreWallet = new BalanceCreditAccount();
           coreWallet.setBalanceName(CORE_BALANCE_NAME);
           coreWallet.setCreditValue(-bundle.getAmount());
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
                    }
                    
              
              
              //remove jobs
                   
                    
        
        
        
        //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccounts.size()];
           walletAccounts.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, bundle.getMobileNumber());
           if(ocsResult){
               //sucess
               BundleDao dao = new BundleDao();
               dao.initEntityManager();
               dao.persist(bundle);
               
                //register window period
                 RegisterTrigger window = new RegisterTrigger();
                 // public boolean registerWindowPeriod(String msisdn,boolean isAutoRenewal, Date expiryDate, String bundleType, String dateOfPurchase,String bundleAmount)
                 boolean registered = window.registerWindowPeriod(msisdn,true, false,bundle.getExpiryDateTimestamp(),bundle.getBundleType(),new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(bundle.getTransactionDateTimestamp()),bundle.getAmount().toString(),true);   //catch return value success/fail
                 Date exp = ExpiryDateGenerator.generateExpiryDate(24);
                 if(registered == false){
                     System.out.println("##### FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                     //_log.error("##### FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                     //log to DB
                     try{
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REGISTER NEW PERIOD ON OPT IN");
                          error.setEventType("WINDOW_PERIOD");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                     }
                          catch(Exception ex){
                              System.out.println("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                             // _log.error("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp, ex);
                          }
               
               return STATUS_SUCCESS;
           }
               return STATUS_SUCCESS;
            }else{
               
                //log to DB
                     try{
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO CREDIT_ACCOUNT ON SUBSCRIPTION");
                          error.setEventType("CREDIT_ACCOUNT");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                     }
                          catch(Exception ex){
                              System.out.println("##### CHECK DB, ERROR SAVING LOG ,FAILED TO CREDIT ACCOUNT ON ZTE, msisdn:"+msisdn+", date : "+new Date());
                             // _log.error("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp, ex);
                          }
               
               return STATUS_FAIL;
           }
        
        
                }else{
                    
                    // subscription 
                       
     
           BalanceCreditAccount internationalWallet = new BalanceCreditAccount();
           internationalWallet.setBalanceName(bundleType); //Daily Wallet
           internationalWallet.setCreditValue(bundle.getDuration());
           Calendar dailyExpiry = Calendar.getInstance();
           Date bExp = bundle.getExpiryDateTimestamp();
           dailyExpiry.setTime(bExp);
          
           internationalWallet.setExpirationDate(dailyExpiry);
           walletAccounts.add(internationalWallet);
           
           
           BalanceCreditAccount coreWallet = new BalanceCreditAccount();
           coreWallet.setBalanceName(CORE_BALANCE_NAME);
           coreWallet.setCreditValue(-bundle.getAmount());
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
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, bundle.getMobileNumber());
           if(ocsResult){
               //sucess
               BundleDao dao = new BundleDao();
               dao.initEntityManager();
               dao.persist(bundle);
               
                //register window period
                 RegisterTrigger window = new RegisterTrigger();
                 // public boolean registerWindowPeriod(String msisdn,boolean isAutoRenewal, Date expiryDate, String bundleType, String dateOfPurchase,String bundleAmount)
                 boolean registered = window.registerWindowPeriod(msisdn,false, false,bundle.getExpiryDateTimestamp(),bundle.getBundleType(),new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(bundle.getTransactionDateTimestamp()),bundle.getAmount().toString(),false);   //catch return value success/fail
                 Date exp = ExpiryDateGenerator.generateExpiryDate(24);
                 if(registered == false){
                     System.out.println("##### FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                     //_log.error("##### FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                     //log to DB
                     try{
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REGISTER NEW PERIOD ON OPT IN");
                          error.setEventType("WINDOW_PERIOD");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                     }
                          catch(Exception ex){
                              System.out.println("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp);
                             // _log.error("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp, ex);
                          }
               
               return STATUS_SUCCESS;
           }
               return STATUS_SUCCESS;
            }else{
               
                //log to DB
                     try{
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO CREDIT_ACCOUNT ON SUBSCRIPTION");
                          error.setEventType("CREDIT_ACCOUNT");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                     }
                          catch(Exception ex){
                              System.out.println("##### CHECK DB, ERROR SAVING LOG ,FAILED TO CREDIT ACCOUNT ON ZTE, msisdn:"+msisdn+", date : "+new Date());
                             // _log.error("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER NEW PERIOD ON OPT IN, msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+exp, ex);
                          }
               
               return STATUS_FAIL;
           }
                    
                    
                    
                }
     
          
           
           
            
            
            } 
            
        }catch(Exception ex){
            ex.printStackTrace();
            return STATUS_FAIL;
        }
      
    }
    
    public String balanceEnquiry(String msisdn){
        try{
            
            if( !(this.isPrepaidMsisdn(msisdn)) ){
                //not prepaid
                //send sms
                 //notfiy subscriber of de-activation
               
                         return "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details";
                //return "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details";
            }
            
            //are you in Mo'Fire
            boolean status = MilleniumRegister.isSubscriberInMillenium(msisdn);
            if(status){
               
                         return  "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details";
            }
            
            RegisterDao registerDao = new RegisterDao();
            registerDao.initEntityManager();
            Register register = registerDao.findByMobileNumber(msisdn);
            if(register == null){ //not part of the promotion
                return "Dear Customer, you are not part of this promotion. To subscribe to the Telecel Daily Package dial *146#";
            }
            String bundleType = register.getBundleType();
            ZSmartClient ocsClient = new ZSmartClient();
            CheckNumber check = new CheckNumber();
            if(check.isPrepaid(msisdn)){
            BalanceInformationClient balance = ocsClient.getSubscriberBalanceBefore(msisdn,bundleType);
            Double internationalBalance = balance.getInternationalBundleBefore();
            Date internationalExpiry = balance.getInternationalBundleExpiryBefore();
            //0.005 is 1minute
            Double minutes = 0.0;
            if("TeleBonus_HV".equals(bundleType)){
               minutes = (internationalBalance /0.005);
            }else{
                minutes = (internationalBalance /0.006);
            }
             
           // Double minutes = (seconds/60);
            return "You have "+minutes+"mins, exp "+internationalExpiry;
            }else{
                return "This service is for prepaid subscribers only";
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return "Error processing request";
        }
    }
    
    
        public double balanceUponPurchase(String msisdn){
        try{
            
            RegisterDao registerDao = new RegisterDao();
            registerDao.initEntityManager();
            Register register = registerDao.findByMobileNumber(msisdn);
            if(register == null){ //not part of the promotion
                return 0.0;
            }
            String bundleType = register.getBundleType();
            ZSmartClient ocsClient = new ZSmartClient();
            
            BalanceInformationClient balance = ocsClient.getSubscriberBalanceBefore(msisdn,bundleType);
            Double internationalBalance = balance.getInternationalBundleBefore();
           
            return internationalBalance;
        }catch(Exception ex){
            ex.printStackTrace();
           return 0.0;
        }
    }
    
    
    public boolean isPrepaidMsisdn(String msidn){
        try{
            CheckNumber check = new CheckNumber();
            boolean result = check.isPrepaid(msisdn);
            return result;
        }catch(Exception ex){
            //System.out.println("Error accessing CCB Millenium from milleniumModel, date: "+new Date());
            _log.error("Error accessing CCB Millenium from milleniumModel, date: "+new Date()+",msisdn:"+msisdn, ex);
            //log to DB
                          try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO CHECK NUM IF PREPAID OR POST PAID");
                          error.setEventType("PREPAID_POSTPAID");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);}
                          catch(Exception e){
                              _log.error("##### Check DB Error Saving LOG,Error accessing CCB Millenium from milleniumModel, date: "+new Date()+",msisdn:"+msisdn, ex);
                          }
            return true;
        }
    }
    
    public void unSubscribe(String msisdn){
        try{
            
            if( !(this.isPrepaidMsisdn(msisdn)) ){
                //not prepaid
                //send sms
                 //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING SMS, msisdn: "+msisdn+" at : "+new Date());
             // _log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex);
              
             
            }
                         return;
                //return "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details";
            }
            
            //are you in Mo'Fire
            boolean status = MilleniumRegister.isSubscriberInMillenium(msisdn);
            if(status){
                try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Dear Customer, we are sorry to inform you that you are not qualified to avail this promotion. Please call 150 for details");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING SMS, msisdn: "+msisdn+" at : "+new Date());
             // _log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex);
              
             
            }
                         return;
            }
            
            
            BundleDao bDao = new BundleDao();
            bDao.initEntityManager();
            List<BundleDto> bundleDtoList = bDao.findByMobileNumber(msisdn);
            if(bundleDtoList.isEmpty()){
                //msisdn never subscribed before
                
                //send SMS    
            //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "You are not part of the promotion, to subscribe please dial *146#");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING UNSUBSCRIBE SMS, msisdn: "+msisdn+" at : "+new Date());
             // _log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex);
              
             
            } 
                return;
            }else{
                 //remove register
             RegisterDao rdao = new RegisterDao();
               rdao.initEntityManager();
               Register register = rdao.findByMobileNumber(msisdn);
               if(register == null){
                   //send message
                        
                
                   
           //send SMS    
            //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "You have unsubscribed from your Telecel Daily Package. Stay tuned for more offers from Telecel");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING UNSUBSCRIBE SMS, msisdn: "+msisdn+" at : "+new Date());
             // _log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex);
              
             
            }    
                   return;
               }
               //rdao.delete(register);
               
                 //create opt out transaction
                   BundleDao bundleDao = new BundleDao();
                   bundleDao.initEntityManager();
                   BundleDto bundleDto = new BundleDto();
                   bundleDto.setMobileNumber(msisdn);
                   bundleDto.setTransactionDate(new Date());
                   bundleDto.setTransactionDateTimestamp(new Date());
                   bundleDto.setBundleType(register.getBundleType());
                   bundleDto.setId(KeyGenerator.generateEntityId());
                   bundleDto.setTransactionType("OPTOUT");
                  
               
              
             
               
           //deplete wallet
                          ZSmartClient ocsClient = new ZSmartClient();
           
            
            BalanceInformationClient balance = new BalanceInformationClient();
            balance  = ocsClient.getSubscriberBalanceBefore(msisdn,register.getBundleType());
            //populate wallets
            List<BalanceCreditAccount> walletAccounts = new ArrayList<BalanceCreditAccount>();
                        
           BalanceCreditAccount internationalWallet2 = new BalanceCreditAccount();
          
           internationalWallet2.setBalanceName(register.getBundleType()); //Daily Wallet
           internationalWallet2.setCreditValue(-balance.getInternationalBundleBefore());
           Calendar dailyExpiry2 = Calendar.getInstance();
          
          
           internationalWallet2.setExpirationDate(dailyExpiry2);
           walletAccounts.add(internationalWallet2);         
    
           //execute rechargeTransaction
           BalanceCreditAccount[] balanceCreditAccounts  = new BalanceCreditAccount[walletAccounts.size()];
           walletAccounts.toArray(balanceCreditAccounts);
           boolean ocsResult = ocsClient.creditWallets(balanceCreditAccounts, msisdn);

           
            //remove register and jobs
               DataManager dataM = new DataManager();
               dataM.removeRegister(msisdn);
               
                bundleDao.persist(bundleDto);
           //send SMS    
            //notfiy subscriber of de-activation
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "You have unsubscribed from your Telecel Daily Package. Stay tuned for more offers from Telecel");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex){
              System.out.println("#### ERROR SENDING UNSUBSCRIBE SMS, msisdn: "+msisdn+" at : "+new Date());
             // _log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex);
              
             
            }    
            }
            
           
               
           
                     
            
        }catch(Exception ex){
            // ex.printStackTrace();
            _log.error("####### Error processing unsubscribe request, msisdn:"+msisdn+", date:"+new Date(), ex);
             try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Error Processing unsubcribe request , please try again");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            
            }catch(Exception ex2){
              System.out.println("#### ERROR SENDING SMS, msisdn: "+msisdn+" at : "+new Date());
              //_log.error("#### ERROR SENDING PREPAID OR NOT SMS ON USSD, msisdn: "+msisdn+" at : "+new Date(), ex2);
              
             
            }
             return;
            //return "Error processing request";
           
        }
    }
    
   
   
   
  
    
   }
   
   
    


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import static org.quartz.TriggerBuilder.newTrigger;


import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import smsgateway.SMSGateway;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import zw.co.telecel.akm.millenium.dao.MTransactionDao;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dao.ScheduleDao;
import zw.co.telecel.akm.millenium.dto.ERRORLOG;
import zw.co.telecel.akm.millenium.dto.MTransaction;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.dto.Schedule;
import zw.co.telecel.akm.millenium.utils.ExpiryDateGenerator;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;
import zw.co.telecel.akm.webservice.ccb.client.ChangeCOS;
//import zw.co.telecel.akm.webservices.zte.trigger.RegisterEvent;

/**
 *
 * @author matsaudzaa
 */
public class ServerJOB implements Job {
    
    
     private final static String MILLENIUM_COS_NAME = "4007";
     private final static String TEL_COS_NAME = "TEL_COS";
     private DataManager dataManager = new DataManager();
     
      private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      ServerJOB.class.getName());

    public ServerJOB() {
    }
    
     public void execute(JobExecutionContext context)
        throws JobExecutionException {
          try{
        // Say Hello to the World and display the date/time
        
       
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String msisdn = dataMap.getString("msisdn");
        //System.out.println("JOB SERVER EXECUTING MSISDN : "+msisdn+", date: "+new Date());
        _log.info("JOB SERVER EXECUTING MSISDN : "+msisdn+", date: "+new Date());
        
        try{
        RegisterDao r2DAO = new RegisterDao();
            r2DAO.initEntityManager();
          Register entry2 = r2DAO.findByMobileNumber(msisdn);
          if( (entry2 == null) || (entry2.getStatus().equals("ACTIVE")) ){
              //sub opted out, do not execute job
                System.out.println("####### : sub opted out or already active, do not execute job, msisdn:"+msisdn+", date:"+new Date());
               _log.info("####### : sub opted out or already active, do not execute job, msisdn:"+msisdn+", date:"+new Date());
              return;
          }else{
              
              
              
        //zte ocs check core > $2 
        CreditAccount credit = new CreditAccount();
        Double amount =  0.0;// credit.getSubscriberBalance(msisdn);
        if(amount >= 2){
            //System.out.println("#### Amount Greater than 2");
             RegisterDao rDAO = new RegisterDao();
            rDAO.initEntityManager();
            Register entry = null;
          try{  
           entry = rDAO.findByMobileNumber(msisdn);
          }catch(Exception ex){
              //System.out.println("###### CHECK DB, unable to search for register from server job, msisdn:"+msisdn+", date:"+new Date());
              _log.error("###### CHECK DB, unable to search for register from server job, msisdn:"+msisdn+", date:"+new Date(), ex);
          }
          if(entry == null){
              //System.out.println("###### TRIGGER FIRED BUT JOB NOT EXECUTED BECAUSE OF DB FAILURE TO FIND REGISTER ENTRY, MSISDN : "+msisdn+", date : "+new Date());
              _log.error("###### SERVER JOB TRIGGER FIRED BUT JOB NOT EXECUTED BECAUSE OF DB FAILURE TO FIND REGISTER ENTRY, MSISDN : "+msisdn+", date : "+new Date());
              return;
          }
          //gamble might not work if problem with db
          if("ACTIVE".equals(entry.getStatus())){
              //aready active in promo
              //do nothing
              System.out.println("####### : //Subscriber Active Already, sub status server job : "+entry.getStatus()+", msisdn:"+msisdn);
              _log.info("####### : //Subscriber Active Already, sub status server job : "+entry.getStatus()+", msisdn:"+msisdn);
              return;
          }else{
             
              boolean result = true;//credit.subtractAmount(msisdn, -2.00); 
              
                if(!result){
                //System.out.println("##### FAILED TO CREDIT AIRTIME ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date());
                _log.error("##### FAILED TO CREDIT AIRTIME ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date());
                //log to DB
                         try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO CREDIT AMOUNT ON SERVER JOB");
                          error.setEventType("CREDIT_ACCOUNT");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                         }
                          catch(Exception ex){
                              //System.out.println("##### CHECK DB, ERROR SAVING LOG , FAILED TO CREDIT AIRTIME ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date());
                              _log.error("##### CHECK DB, ERROR SAVING LOG , FAILED TO CREDIT AIRTIME ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date(), ex);
                          }
                         
                         
                         
                         
                         
                         
                         //notfiy subscriber of how to re-enter promotion
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "To re-activate your 2013 Mo Fire promotion, please dial *2013# and select option 3 for re-activation");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex){
              //System.out.println("#### ERROR SENDING SMS, msisdn: "+msisdn+" at : "+new Date());
              _log.error("#### ERROR SENDING RE_ACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), ex);
              //log to DB
                          try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment("ERROR SENDING RE_ACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT");
                          error.setEventType("SMS");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                          }
                          catch(Exception e){
                              //System.out.println("##### CHECK DB, ERROR SAVING SMS LOG FROM CHANGECOSJOB, ERROR SENDING DEACTIVATION SMS, msisdn: "+msisdn+" at : "+new Date());
                              _log.error("##### CHECK DB, ERROR SAVING SMS LOG FROM CHANGECOSJOB, ERROR SENDING RE_ACTIVATION SMS ON FAILURE TO CREDIT ACCOUNT, msisdn: "+msisdn+" at : "+new Date(), e);
                          }
                         
            }    
                         
                         
                         
               
                         
                }else{
                    //credit successfull
                    
                     //change sub to 2777 COS
         
            ChangeCOS change = new ChangeCOS();
           boolean cosChange = change.changeCOS(MILLENIUM_COS_NAME, msisdn);
          
           if(cosChange ==  false){
                          //System.out.println("##### FAILED TO CHANGE TO MELLENIUM COS ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date());
                          _log.error("##### FAILED TO CHANGE TO MELLENIUM COS ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date());
                          //log to DB
                          try{
                              
                              
                              
                          //reversal of $2
                           boolean reversalResult = true;//credit.subtractAmount(msisdn, -2.00); 
                           if(reversalResult == true){
                               _log.info("#### Reversal Successful from server job, msisdn:"+msisdn+", date:"+new Date());
                           }
                          
                          //log to DB
                         
                              if(reversalResult == false){
                                  _log.error("#### Reversal Failed from server job, msisdn:"+msisdn+", date:"+new Date());
                                  ERRORLOG error2 = new ERRORLOG();
                          error2.setComment("Reversal Failed From Server Job");
                          error2.setEventType("REVERSAL");
                          error2.setId(KeyGenerator.generateEntityId());
                          error2.setLogDate(new Date());
                          error2.setLogDateTimestamp(new Date());
                          error2.setMsisdn(msisdn);
                          dataManager.logToDB(error2); 
                              }
                              
                              
                             ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO CHANGE TO MELLENIUM COS ON SERVER JOB");
                          error.setEventType("COS_CHANGE");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error); 
                          }catch(Exception ex){
                              //System.out.println("##### CHECK DB, ERROR SAVING LOG , DATE : "+ new Date());
                              _log.error("##### CHECK DB, ERROR SAVING LOG ,FAILED TO CHANGE TO MELLENIUM COS ON SERVER JOB, msisdn:"+msisdn+", date : "+new Date(), ex);
                          }
                          
                          
                           //notfiy subscriber of how to re-enter promotion
                          try{
               SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "To re-activate your 2013 Mo Fire promotion, please dial *2013# and select option 3 for re-activation");
               //System.out.println("###### Line after sending de-activation message..."+msisdn);
            }catch(Exception ex){
              //System.out.println("#### ERROR SENDING SMS, msisdn: "+msisdn+" at : "+new Date());
              _log.error("#### ERROR SENDING RE_ACTIVATION SMS ON FAILURE TO CHANGE COS, msisdn: "+msisdn+" at : "+new Date(), ex);
              //log to DB
                          try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment("ERROR SENDING RE_ACTIVATION SMS ON FAILURE TO CHANGE COS");
                          error.setEventType("SMS");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                          }
                          catch(Exception e){
                              //System.out.println("##### CHECK DB, ERROR SAVING SMS LOG FROM CHANGECOSJOB, ERROR SENDING DEACTIVATION SMS, msisdn: "+msisdn+" at : "+new Date());
                              _log.error("##### CHECK DB, ERROR SAVING SMS LOG FROM CHANGECOSJOB, ERROR SENDING RE_ACTIVATION SMS ON FAILURE TO CHANGE COS, msisdn: "+msisdn+" at : "+new Date(), e);
                          }
                         
            }    
                          
                          
                     }else{  //cos change sucessfull
               
               
               
                              //System.out.println("####### start of register new window period");
             // register new window period
            Date expiryDate = ExpiryDateGenerator.generateExpiryDate(7);
            RegisterTrigger trigger = new RegisterTrigger();
         boolean r = true;//trigger.registerWindowPeriod(msisdn, false); //returns boolean might save output for debugging purposes
             if(r == false){
                     //System.out.println("##### FAILED TO REGISTER NEW PERIOD ON SERVER JOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+expiryDate);
                     _log.error("##### FAILED TO REGISTER NEW PERIOD ON SERVER JOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+expiryDate);
                     //log to DB
                     try{
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REGISTER NEW PERIOD ON SERVER JOB");
                          error.setEventType("WINDOW_PERIOD");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                     }
                          catch(Exception ex){
                              //System.out.println("##### CHECK DB, ERROR SAVING LOG , DATE : "+ new Date());
                              _log.error("##### CHECK DB, ERROR SAVING LOG,FAILED TO REGISTER NEW PERIOD ON SERVER JOB , msisdn:"+msisdn+", date : "+new Date()+ ", must expire on : "+expiryDate , ex);
                          }
                 }
            
           
             //update register to active
             try{
            
            RegisterDao rDao = new RegisterDao();
            rDao.initEntityManager();
            Register registerDto = rDao.findByMobileNumber(msisdn);
            registerDto.setStatus("ACTIVE");
            rDao.edit(registerDto);
            
             }catch(Exception ex){
                 //System.out.println("##### CHECK DB, ERROR EDITING REGISTER TO ACTIVE FOR MSISDN ON SERVER JOB: "+msisdn+ ", DATE:"+ new Date());
                 _log.error("##### CHECK DB, ERROR EDITING REGISTER TO ACTIVE FOR MSISDN ON SERVER JOB: "+msisdn+ ", DATE:"+ new Date(), ex);
             }
             
           
            
            
            //remove zte trigger set large amount   
             try{
//                 RegisterEvent registerEvent = new RegisterEvent();
  //          registerEvent.registerTrigger(msisdn, 2000.00); //check max value //consider return value
            }catch(Exception ex){
                //System.out.println("FAILED TO REGISTER MAXIMUM TRIGGER, msisdn"+msisdn+", date:"+new Date());
                _log.error("FAILED TO REGISTER MAXIMUM TRIGGER SERVER JOB, msisdn"+msisdn+", date:"+new Date(), ex);
                
                 //log to DB
                          try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO SET MAXIMUM TRIGGER VALUE ON SERVER JOB");
                          error.setEventType("ZTE_TRIGGER");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                          }
                          catch(Exception e){
                              //System.out.println("##### CHECK DB, ERROR SAVING LOG , DATE : "+ new Date());
                              _log.error("##### CHECK DB, ERROR SAVING LOG ,FAILED TO REGISTER MAXIMUM TRIGGER SERVER JOB, msisdn"+msisdn+", date:"+new Date(), e);
                          }
            }
            
            
            //notify subscriber
           
            try{
                SMSGateway smsClient = new SMSGateway();
               smsClient.sendMessage(msisdn, "Telecel", "Your subscription to 2013 Mo Fire promo has been renewed. Get discounted rates of 1c/min, free SMS and internet at 3c/Mb from 9pm-12 noon daily until:"+expiryDate);
              
            }catch(Exception ex){
                 //System.out.println("#### ERROR SENDING SMS at : "+new Date());
                 _log.error("#### ERROR SENDING RENEWAL SMS FROM SERVER JOB at : "+new Date()+", msisdn:"+msisdn, ex);
                 //log to DB
                          try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment(" ERROR SENDING RENEWAL SMS FROM SERVER JOB, exp on :"+expiryDate);
                          error.setEventType("SMS");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                          }
                          catch(Exception e){
                              //System.out.println("##### CHECK DB, ERROR SAVING LOG , DATE : "+ new Date());
                              _log.error("##### CHECK DB, ERROR SAVING LOG ,ERROR SENDING RENEWAL SMS FROM SERVER JOB at : "+new Date()+", msisdn:"+msisdn, e);
                          }
            }
               
               
               
                          } // end of change cos successfull
                    
                    
                }//else for credit successful
          
          
           
           
           
        
             
            
              
              
          }//else for INACTIVE
           
           
             
             
             
        }//else for amount > $2            
          _log.info("####### SERVER JOB, msisdn: "+msisdn +" ,has less than $2");
         // System.out.println("####### SERVER JOB, msisdn: "+msisdn +" ,has less than $2") ;
              
              
          }
          
          
          }catch(Exception ex){
           // System.out.println("###### CHECK DB, unable to search for register from server job, msisdn:"+msisdn+", date:"+new Date());
            _log.error("###### CHECK DB, unable to search for register from server job, msisdn:"+msisdn+", date:"+new Date(), ex);
        }
       
                
    }catch(Exception ex){
       ex.printStackTrace();
}
    
     }
     
     
}

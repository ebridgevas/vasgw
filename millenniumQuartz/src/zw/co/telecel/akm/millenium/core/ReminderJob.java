/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smsgateway.SMSGateway;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dto.ERRORLOG;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;

/**
 *
 * @author matsaudzaa
 */
public class ReminderJob implements Job {

      private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      ReminderJob.class.getName());
      private DataManager dataManager = new DataManager();

    public ReminderJob() {
    }
    
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
          
        // Say Hello to the World and display the date/time
       
       
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String expiry = dataMap.getString("expiry");
        String msisdn = dataMap.getString("msisdn");
        String bundleAmount = dataMap.getString("bundleAmount");
       
        String day = dataMap.getString("day");
       // System.out.println(" REMINDER JOB EXECUTING, MSISDN : "+msisdn);
        _log.info(" REMINDER JOB EXECUTING, MSISDN : "+msisdn+", date :"+new Date());
        
         
            //notfy subscriber
            try{
                //if unable to remove quartz job on opt out
       
          
           try{
            RegisterDao rDAO = new RegisterDao();
            rDAO.initEntityManager();
          Register entry = rDAO.findByMobileNumber(msisdn);
          if(entry == null){
              //sub opted out, do not execute job
              //System.out.println("####### : //sub opted out, do not execute job");
              System.out.println("####### Sub opted out do not excute job, msisdn:"+msisdn+", date:"+new Date());
             // _log.info("####### Sub opted out do not excute job, msisdn:"+msisdn+", date:"+new Date());
              return;
          }else{
              
               SMSGateway smsClient = new SMSGateway();
               if("6".equals(day)){  //day 6 sms
                   try{
                      smsClient.sendMessage(msisdn, "Telecel", "Your Telecel Daily USD"+bundleAmount+" subscription will expire on "+expiry+"for automatic subscription please have at least USD"+bundleAmount); 
               
                   }catch(Exception ex){
                      // System.out.println("#### ERROR SENDING DAY 6 SMS FROM REMINDER JOB, msisdn: "+msisdn+" at : "+new Date());
                       _log.error("#### ERROR SENDING DAY 6 SMS FROM REMINDER JOB, msisdn: "+msisdn+" at : "+new Date(), ex);
              //log to DB
                          try{
                          ERRORLOG error = new ERRORLOG();
                          error.setComment(" ERROR SENDING DAY 6 SMS, exp on :"+expiry);
                          error.setEventType("SMS");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          dataManager.logToDB(error);
                          }
                          catch(Exception e){
                             // System.out.println("##### CHECK DB, ERROR SAVING LOG , DATE : "+ new Date());
                              _log.error("##### CHECK DB, ERROR SAVING SMS DAY 6 LOG FROM REMINDERJOB, msisdn:"+msisdn+" , DATE : "+ new Date(), e);
                          }
                   }
                    }
              
          }
        }catch(Exception ex){
            //System.out.println("###### CHECK DB, unable to search for register from reminder job, msisdn:"+msisdn+", date:"+new Date());
            _log.error("###### CHECK DB, unable to search for register from reminder job, msisdn:"+msisdn+", date:"+new Date(), ex);
        }
        
              
               //System.out.println("###### Line after sending  reminder message..."+msisdn);
            
         
    }catch(Exception ex){
         //System.out.println("#### ERROR EXECUTING SMS JOB FROM REMINDER JOB at : "+new Date());
         _log.error("#### ERROR EXECUTING SMS JOB FROM REMINDER JOB at : "+new Date(), ex);
      
}
    
     }
    
    
    
}

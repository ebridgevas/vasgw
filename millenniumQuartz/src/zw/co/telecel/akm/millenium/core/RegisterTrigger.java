/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import static org.quartz.DateBuilder.*;
import org.quartz.JobDetail;
import static org.quartz.JobBuilder.newJob;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zw.co.telecel.akm.millenium.dao.MTransactionDao;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dao.ScheduleDao;
import zw.co.telecel.akm.millenium.dto.MTransaction;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.dto.Schedule;
import zw.co.telecel.akm.millenium.utils.ExpiryDateGenerator;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;
import zw.co.telecel.akm.webservice.ccb.client.ChangeCOS;

/**
 *
 * @author matsaudzaa
 */
public class RegisterTrigger {
    private final static String MILLENIUM_COS_NAME = "4007";
    
     private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      RegisterTrigger.class.getName());
    
    public boolean registerWindowPeriod(String msisdn,boolean deleteRegister,boolean isAutoRenewal, Date expiryDate, String bundleType, String dateOfPurchase,String bundleAmount,boolean isManualRenewal){
       
        try{
            
            //log.info("------- Initializing ----------------------");
            //System.out.println("------- Initializing ----------------------");

            // First we must get a reference to a scheduler
             SchedulerFactory sf = new StdSchedulerFactory("/usr/local/dailyPromo/quartz.properties");
            Scheduler sched = sf.getScheduler();
            
            
            
            
            
            //expiry dates
       
        Date reminder6ExpiryDate = ExpiryDateGenerator.timeBeforeExpiryDate(expiryDate);
        
            
            // define the job and tie it to our ChangeCOSJob class
        String cosJobID = KeyGenerator.generateJobId(msisdn.substring(3));
        JobDetail job = org.quartz.JobBuilder.newJob(ChangeCOSJob.class)
            .withIdentity(cosJobID, "group1")
            .usingJobData("msisdn", msisdn)
            .usingJobData("bundleType",bundleType)     
            .build();
        
        //defining day 6 reminder job
        String rem6JobID = KeyGenerator.generateReminder6JobId(msisdn.substring(3));
        JobDetail reminder6Job = org.quartz.JobBuilder.newJob(ReminderJob.class)
        .withIdentity(rem6JobID, "group2")
            .usingJobData("msisdn", msisdn)
            .usingJobData("expiry",""+expiryDate)
            .usingJobData("bundleAmount",bundleAmount)  
            .usingJobData("day","6")    
            .build();  
        
      
        
        
        
        
        // Trigger the job to run on the next round minute
        Trigger trigger = newTrigger()
            .withIdentity(KeyGenerator.generateTriggerId(msisdn.substring(3)), "group1")
            .startAt(expiryDate)
            .build();
        
        //defining reminder day6 trigger
        Trigger reminder6Trigger = newTrigger()
            .withIdentity(KeyGenerator.generateReminder6TriggerId(msisdn.substring(3)), "group2")
            .startAt(reminder6ExpiryDate)
            .build();
        
        
        
        
         // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job, trigger);
        sched.scheduleJob(reminder6Job, reminder6Trigger);
         
      
        
       
             if(isAutoRenewal){
                 
                  //update register with new job ID's
                                try{
            RegisterDao rDAO = new RegisterDao();
            rDAO.initEntityManager();
          Register entry = rDAO.findByMobileNumber(msisdn);
          if(entry == null){
              //sub opted out, do not execute job
             
          }else{
              entry.setCosJobId(cosJobID);
              entry.setRem6JobId(rem6JobID);
              entry.setDateOfPurchase(new Date());
              entry.setExpiryDate(expiryDate);
              
              rDAO.edit(entry);
          }
        }catch(Exception ex){
            System.out.println("###### CHECK DB, unable to update register with new job id's FROM RegisterTrigger, msisdn:"+msisdn+", date:"+new Date());
            //_log.error("###### CHECK DB, unable to update register with new job id's FROM RegisterTrigger, msisdn:"+msisdn+", date:"+new Date(), ex);
        }
                    
             }else{
                 
              if(deleteRegister){
                  //DataManager remove jobs and delete register
                  DataManager dbManager = new DataManager();
                  dbManager.removeRegister(msisdn);
              }     
        //create db Register   
         Register registerDto = new Register();
         Date datePurchased = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").parse(dateOfPurchase);
         registerDto.setMsisdn(msisdn);
         registerDto.setStatus("ACTIVE");
         registerDto.setCosJobId(cosJobID);
         registerDto.setRem6JobId(rem6JobID);
         registerDto.setDateOfPurchase(datePurchased); //parse
         registerDto.setExpiryDate(expiryDate);
         if(bundleType.equals("1")){
             registerDto.setBundleType("TeleBonus_LV");
         }else{
             registerDto.setBundleType("TeleBonus_HV");
         }
         RegisterDao rDao = new RegisterDao();
         
         rDao.initEntityManager();
         rDao.persist(registerDto);
             }
           
               
                        
          /* //subtract
            CreditAccount credit = new CreditAccount();
             // credit account --$2
          // boolean result = credit.subtractAmount(msisdn, -2.00);  //catch return value success/fail
          // System.out.println("Credit Account status : "+result);             
        //change COS to 2777
           ChangeCOS change = new ChangeCOS();
            change.changeCOS(MILLENIUM_COS_NAME, msisdn); */
                        
        
           
           return true;  
        }catch(Exception ex){
            System.out.println("##### , "+new Date()+" Error registering window period, msisdn : "+msisdn+", date : "+new Date());
            //_log.error("##### , "+new Date()+" Error registering window period, msisdn : "+msisdn+", date : "+new Date(), ex);
            //creation register
            if((isAutoRenewal) || (isManualRenewal)){
               //edit register 
                 RegisterDao rDAO = new RegisterDao();
            rDAO.initEntityManager();
          Register entry = rDAO.findByMobileNumber(msisdn);
          
          
              entry.setDateOfPurchase(new Date());
              entry.setExpiryDate(expiryDate);
              
              rDAO.edit(entry);
          
            }else{
                //create register
                //create db Register   
         Register registerDto = new Register();
         registerDto.setDateOfPurchase(new Date());
         registerDto.setMsisdn(msisdn);
         registerDto.setStatus("ACTIVE");
         registerDto.setExpiryDate(expiryDate);
         if(bundleType.equals("1")){
             registerDto.setBundleType("TeleBonus_LV");
         }else{
             registerDto.setBundleType("TeleBonus_HV");
         }
         RegisterDao rDao = new RegisterDao();
         
         rDao.initEntityManager();
         rDao.persist(registerDto);
            }
            ex.printStackTrace();
            return false;
        }

        
    }
    
}

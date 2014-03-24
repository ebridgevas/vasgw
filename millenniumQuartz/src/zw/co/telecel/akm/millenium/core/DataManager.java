/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import java.util.Date;
//import org.GNOME.Accessibility._LoginHelper;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import zw.co.telecel.akm.millenium.dao.ErrorLogDao;
import zw.co.telecel.akm.millenium.dao.MTransactionDao;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dto.ERRORLOG;
import zw.co.telecel.akm.millenium.dto.MTransaction;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;

/**
 *
 * @author matsaudzaa
 */
public class DataManager {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      DataManager.class.getName());
    
   public void logToDB(ERRORLOG error){
       try{
           ErrorLogDao tDao = new ErrorLogDao();
                        tDao.initEntityManager();
                        tDao.persist(error);
       }catch(Exception ex){
           //System.out.println("##### CHECK DB, error logging to DB , date : "+new Date()+"error msg :"+ex.getMessage());
           _log.error("##### CHECK DB, error logging to DB from DataManager , date : "+new Date(), ex);
       }
   }
    
    public boolean createOptOutTransaction(String msisdn){
        try{
            //create db transaction : opt out
        MTransaction transactionDto = new MTransaction();
        transactionDto.setId(KeyGenerator.generateEntityId());
        Date currentDate = new Date();
        transactionDto.setTransactionDate(currentDate);
        transactionDto.setTransactionDateTimestamp(currentDate);
        transactionDto.setMsisdn(msisdn);
        transactionDto.setTransactionType("OPTOUT");
        MTransactionDao tDao = new MTransactionDao();
                        tDao.initEntityManager();
                        tDao.persist(transactionDto);
         return true;               
        }catch(Exception ex){
            //System.out.println("##### CHECK DB, error creating opt out transaction to DB , date : "+new Date()+"error msg :"+ex.getMessage());
            _log.error("##### CHECK DB, error creating opt out transaction to DB from DataManager , date : "+new Date(), ex);
            return false;
        }
    }
    
    public Register findRegister(String msisdn){
        
            //implement delete method in register
            
            RegisterDao rDao = new RegisterDao();
                        rDao.initEntityManager();
            Register register =  rDao.findByMobileNumber(msisdn);
                        
            return register;
        
    }
    
    public boolean removeRegister(String msisdn){
        try{
            //implement delete method in register
            
            RegisterDao rDao = new RegisterDao();
                        rDao.initEntityManager();
            Register register =  rDao.findByMobileNumber(msisdn);
            String cosJobID = register.getCosJobId();
            String rem6JobID = register.getRem6JobId();
           // String rem7JobID = register.getRem7JobId(); 
             try{
                 SchedulerFactory sf = new StdSchedulerFactory("/usr/local/dailyPromo/quartz.properties");
                 Scheduler sched = sf.getScheduler();
                 JobKey job1 = new JobKey(cosJobID, "group1");
                 JobKey job2 = new JobKey(rem6JobID, "group2");
                // JobKey job3 = new JobKey(rem7JobID, "group3");
                 
                 
                      boolean status1 = sched.deleteJob(job1);
                       if( (status1 == false) ){
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REMOVE CHANGE COS JOB ID ON OPT OUT, status1:"+status1);
                          error.setEventType("JOB_REMOVAL");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          this.logToDB(error);
                 }
                 
                
                      boolean status2 = sched.deleteJob(job2);
                      if( (status2 == false) ){
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REMOVE REMINDER JOB 6 JOB ID ON OPT OUT, status2:"+status2);
                          error.setEventType("JOB_REMOVAL");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          this.logToDB(error);
                 }
                 
                
                
                 
                
                 
                
                 
             }catch(Exception ex){
                System.out.println("##### Error removing jobs on opt out, msisdn : "+msisdn+", at : "+new Date()) ;
                 
              //  _log.error("##### Error removing jobs on opt out(DataManager), msisdn : "+msisdn+", at : "+new Date()+", cosJobID:"+cosJobID+", rem6JobID:"+rem6JobID+", rem7JobID:"+rem7JobID, ex);
             }  
             rDao.delete(register);
            return true;
        }catch(Exception ex){
            System.out.println("##### CHECK DB, error removing register from DB , date : "+new Date()+"error msg :"+ex.getMessage());
           // _log.error("##### CHECK DB, error removing register from DB , date : "+new Date()+", msisdn:"+msisdn, ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    
    
    
    public boolean removeJobs(String msisdn){
        try{
            //implement delete method in register
            
            RegisterDao rDao = new RegisterDao();
                        rDao.initEntityManager();
            Register register =  rDao.findByMobileNumber(msisdn);
            String cosJobID = register.getCosJobId();
            String rem6JobID = register.getRem6JobId();
           // String rem7JobID = register.getRem7JobId(); 
             try{
                 SchedulerFactory sf = new StdSchedulerFactory("/usr/local/dailyPromo/quartz.properties");
                 Scheduler sched = sf.getScheduler();
                 JobKey job1 = new JobKey(cosJobID, "group1");
                 JobKey job2 = new JobKey(rem6JobID, "group2");
                // JobKey job3 = new JobKey(rem7JobID, "group3");
                 
                 
                      boolean status1 = sched.deleteJob(job1);
                       if( (status1 == false) ){
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REMOVE CHANGE COS JOB ID ON PURCHASE BEFORE EXPIRY, status1:"+status1);
                          error.setEventType("JOB_REMOVAL");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          this.logToDB(error);
                 }
                 
                
                      boolean status2 = sched.deleteJob(job2);
                      if( (status2 == false) ){
                     ERRORLOG error = new ERRORLOG();
                          error.setComment("FAILED TO REMOVE REMINDER JOB 6 JOB ID ON PURCHASE BEFORE EXPIRY, status2:"+status2);
                          error.setEventType("JOB_REMOVAL");
                          error.setId(KeyGenerator.generateEntityId());
                          error.setLogDate(new Date());
                          error.setLogDateTimestamp(new Date());
                          error.setMsisdn(msisdn);
                          this.logToDB(error);
                 }
                 
                
                
                 
                
                 
                
                 
             }catch(Exception ex){
                System.out.println("##### Error removing jobs on purchase before expiry, msisdn : "+msisdn+", at : "+new Date()) ;
                 
              //  _log.error("##### Error removing jobs on opt out(DataManager), msisdn : "+msisdn+", at : "+new Date()+", cosJobID:"+cosJobID+", rem6JobID:"+rem6JobID+", rem7JobID:"+rem7JobID, ex);
             }  
            
            return true;
        }catch(Exception ex){
            System.out.println("##### CHECK DB, error finding register from DB , date : "+new Date()+"error msg :"+ex.getMessage());
           // _log.error("##### CHECK DB, error removing register from DB , date : "+new Date()+", msisdn:"+msisdn, ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    
    
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

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
import zw.co.telecel.akm.millenium.utils.ExpiryDateGenerator;
import zw.co.telecel.akm.millenium.utils.KeyGenerator;

/**
 *
 * @author matsaudzaa
 */
public class RegisterServerTrigger {

     private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      RegisterServerTrigger.class.getName());
    
    public boolean register(String msisdn){
         
        try{
            
           // log.info("------- Initializing ----------------------");
            //System.out.println("------- Initializing ----------------------");

            // First we must get a reference to a scheduler
             SchedulerFactory sf = new StdSchedulerFactory("/usr/local/milleniumPromo/quartz.properties");
            Scheduler sched = sf.getScheduler();
            
            
            // define the job and tie it to our HelloJob class
        JobDetail job = org.quartz.JobBuilder.newJob(ServerJOB.class)
            .withIdentity(KeyGenerator.generateJobId(msisdn.substring(3)), "group0")
            .usingJobData("msisdn", msisdn)
            .build();
        
        Date entryDate = new Date();
        Date expiryDate = ExpiryDateGenerator.generateServerExpiryDate();
        // Trigger the job to run on the next round minute
        Trigger trigger = newTrigger()
            .withIdentity(KeyGenerator.generateTriggerId(msisdn.substring(3)), "group0")
            .startAt(expiryDate)
            .build();
        
         // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job, trigger);
        return true;
        }catch(Exception ex){
            _log.error("Error registering jobs initiated by server, msisdn:"+msisdn+", date at"+new Date(), ex);
          
            return false;
        }
        
    }
}

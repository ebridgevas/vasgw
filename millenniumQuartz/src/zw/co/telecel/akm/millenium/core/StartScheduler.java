/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import static org.quartz.DateBuilder.*;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author matsaudzaa
 */
public class StartScheduler {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      StartScheduler.class.getName());
    
    public static void main(String [] args){
        
        try{
            // _log.info("####### SCHEDULER INSTANCE 1 STARTED ####### at Date : "+new Date());
            
            SchedulerFactory sf = new StdSchedulerFactory("/usr/local/dailyPromo/quartz.properties");
            
            Scheduler sched = sf.getScheduler();
            
            sched.start();
           
             System.out.println("####### SCHEDULER INSTANCE 1 STARTED ####### at Date : "+new Date());
           // _log.info("####### SCHEDULER INSTANCE 1 STARTED ####### at Date : "+new Date());
        }catch(Exception ex){
            System.out.println("ERROR");
            _log.error("Error Starting scheduler instance 1 at Date:"+new Date(), ex);
            //ex.printStackTrace();
        }
        
    }
    
}

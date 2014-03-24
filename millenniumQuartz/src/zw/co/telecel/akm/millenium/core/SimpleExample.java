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

/**
 * This Example will demonstrate how to start and shutdown the Quartz 
 * scheduler and how to schedule a job to run in Quartz.
 * 
 * @author Bill Kratzer
 */
public class SimpleExample {

    
    public void run() throws Exception {
        Logger log = LoggerFactory.getLogger(SimpleExample.class);

        log.info("------- Initializing ----------------------");
        System.out.println("------- Initializing ----------------------");

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory("/usr/local/milleniumPromo/quartz.properties");
        Scheduler sched = sf.getScheduler();
        
        System.out.println("Thread Pool Size : "+sched.getMetaData().getThreadPoolSize());
        System.out.println("Instance ID : "+sched.getMetaData().getSchedulerInstanceId());
        System.out.println("Scheduler Name : "+sched.getMetaData().getSchedulerName());
        System.out.println("Summary : "+sched.getMetaData().getSummary());

        log.info("------- Initialization Complete -----------");
        System.out.println("------- Initialization Complete -----------");

        // computer a time that is on the next round minute
        Date runTime = evenMinuteDate(new Date());

        log.info("------- Scheduling Job  -------------------");
        System.out.println("------- Scheduling Job  -------------------");
       
        // define the job and tie it to our HelloJob class
        JobDetail job = org.quartz.JobBuilder.newJob(HelloJob.class)
            .withIdentity("job1", "group1")
            .build();
        
        JobDetail job2 = org.quartz.JobBuilder.newJob(HelloJob2.class)
            .withIdentity("job2", "group1")
            .build();
        
        // Trigger the job to run on the next round minute
        Trigger trigger = newTrigger()
            .withIdentity("trigger1", "group1")
            .startAt(futureDate(5, IntervalUnit.MINUTE))
            .build();
        
         Trigger trigger2 = newTrigger()
            .withIdentity("trigger2", "group1")
           .startAt(futureDate(6, IntervalUnit.MINUTE))
            .build();
        
        // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job, trigger);
         sched.scheduleJob(job2, trigger2);
        log.info(job.getKey() + " will run at: " + runTime);  
     //  System.out.println(job.getKey() + " will run at: " + runTime);
      // System.out.println(job2.getKey() + " will run at: " + runTime);

        // Start up the scheduler (nothing can actually run until the 
        // scheduler has been started)
        sched.start();

       // log.info("------- Started Scheduler -----------------");
         System.out.println("------- Started Scheduler -----------------");
//
//        // wait long enough so that the scheduler as an opportunity to 
//        // run the job!
//        log.info("------- Waiting 65 seconds... -------------");
    System.out.println("------- Waiting 65 seconds... -------------");
       try {
            // wait 65 seconds to show job
            Thread.sleep(450L * 1000L); 
            // executing...
        } catch (Exception e) {
        }
//
//        // shut down the scheduler
//        log.info("------- Shutting Down ---------------------");
       System.out.println("------- Shutting Down ---------------------");
       sched.shutdown(true);
//        log.info("------- Shutdown Complete -----------------");
       System.out.println("------- Shutdown Complete -----------------");
    }

    public static void main(String[] args) throws Exception {

        SimpleExample example = new SimpleExample();
        example.run();

    }

}

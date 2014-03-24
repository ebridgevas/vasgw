/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author matsaudzaa
 */
public class RemoteScheduler {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      RemoteScheduler.class.getName());
    
    public static void main(String [] args){
        try{
            // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory("/usr/local/milleniumPromo/quartz.properties");
        Scheduler sched = sf.getScheduler();
        
        _log.info("Remote Scheduler Started : "+sched.getMetaData().getThreadPoolSize());
        _log.info("Thread Pool Size : "+sched.getMetaData().getThreadPoolSize());
        _log.info("Instance ID : "+sched.getMetaData().getSchedulerInstanceId());
        _log.info("Scheduler Name : "+sched.getMetaData().getSchedulerName());
        _log.info("Summary : "+sched.getMetaData().getSummary());
        _log.info("Number of Jobs Excecuted : "+sched.getMetaData().getNumberOfJobsExecuted());
        _log.info("Running Since : "+sched.getMetaData().getRunningSince());
       
          
          

       
       
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}

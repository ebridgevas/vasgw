/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import java.util.Date;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author matsaudzaa
 */
public class StartScheduler7 {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      StartScheduler7.class.getName());
    
    public static void main(String [] args){
        
        try{
            SchedulerFactory sf = new StdSchedulerFactory("/usr/local/dailyPromo/quartz.properties");
            Scheduler sched = sf.getScheduler();
            sched.start();
             //_log.info("####### SCHEDULER INSTANCE 5 STARTED ####### at Date : "+new Date());
            System.out.println("####### SCHEDULER INSTANCE 7 STARTED ####### at Date : "+new Date());
        }catch(Exception ex){
           _log.error("Error Starting scheduler instance 7 at Date:"+new Date(), ex);
            ex.printStackTrace();
        }
        
    }
    
}

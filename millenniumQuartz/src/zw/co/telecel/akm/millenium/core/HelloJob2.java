/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author matsaudzaa
 */
public class HelloJob2 implements Job {
    
    private static Logger _log = LoggerFactory.getLogger(HelloJob2.class);

    public HelloJob2() {
    }
    
      public void execute(JobExecutionContext context)
        throws JobExecutionException {

        // Say Hello to the World and display the date/time
        _log.info("Hello World! Job 2 - " + new Date());
        System.out.println("Hello World! Job 2 - " + new Date());
    }
    
}

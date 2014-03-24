/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.utils;

import java.util.Calendar;
import java.util.Date;
import zw.co.telecel.akm.millenium.core.ChangeCOSJob;

/**
 *
 * @author matsaudzaa
 */
public class ExpiryDateGenerator {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      ExpiryDateGenerator.class.getName());
    
     public static Date timeBeforeExpiryDate(Date expiryDate){
        
        try{
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(expiryDate);
            
            System.out.println("Current Date : "+ currentDate.getTime());
           // currentDate.add(Calendar.HOUR_OF_DAY, -2); //minus two hours
            currentDate.add(Calendar.MINUTE, -30);  // minus thirty minutes 
            System.out.println("New Date : "+ currentDate.getTime());
            return currentDate.getTime();
        }catch(Exception ex){
           // _log.error("####### Error Generating Expiry Date #######, Date:"+new Date(), ex);
            System.out.println("####### Error Generating Expiry Date #######");
            return null;
        }
        
        
    }
     
      public static Date purchaseDate(){
        
        try{
            Calendar currentDate = Calendar.getInstance();
            
            
            System.out.println("Current Date : "+ currentDate.getTime());
           // currentDate.add(Calendar.HOUR_OF_DAY, -24); //minus two hours
            currentDate.add(Calendar.MINUTE, -2);  // minus two minutes 
            System.out.println("New Date : "+ currentDate.getTime());
            return currentDate.getTime();
        }catch(Exception ex){
           // _log.error("####### Error Generating Expiry Date #######, Date:"+new Date(), ex);
            System.out.println("####### Error Generating Expiry Date #######");
            return null;
        }
        
        
    }
     
     public static Date onDayExpiryDate(Date expiryDate){
        
        try{
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(expiryDate);
            
            System.out.println("Current Date : "+ currentDate.getTime());
           //currentDate.add(Calendar.MINUTE, -120); //minus 2hours
            currentDate.add(Calendar.MINUTE, -240);  // minus 4hours
            System.out.println("New Date : "+ currentDate.getTime());
            return currentDate.getTime();
        }catch(Exception ex){
            System.out.println("####### Error Generating Expiry Date #######");
            return null;
        }
        
        
    }
    
     public static Date generateExpiryDate(int days){
        
        try{
            Calendar currentDate = Calendar.getInstance();
            
              currentDate.add(Calendar.HOUR, 24);
            //currentDate.add(Calendar.MINUTE, 10);
           
            return currentDate.getTime();
        }catch(Exception ex){
            System.out.println("####### Error Generating Expiry Date #######");
            return null;
        }
        
        
    }
     
     public static Date generateServerExpiryDate(){
        
        try{
            Calendar currentDate = Calendar.getInstance();
           
             
            //currentDate.add(Calendar.DAY_OF_MONTH, days);
            currentDate.add(Calendar.MILLISECOND, 10000);
           
            return currentDate.getTime();
        }catch(Exception ex){
            System.out.println("####### Error Generating Expiry Date #######");
            return null;
        }
        
        
    }
    
    
    
}

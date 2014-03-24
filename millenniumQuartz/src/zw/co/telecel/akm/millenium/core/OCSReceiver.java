/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.core;

import java.util.Date;
import smsgateway.SMSGateway;
import zw.co.telecel.akm.millenium.dao.RegisterDao;
import zw.co.telecel.akm.millenium.dto.Register;
import zw.co.telecel.akm.millenium.utils.ExpiryDateGenerator;
import zw.co.telecel.akm.webservice.ccb.client.ChangeCOS;
//import zw.co.telecel.akm.webservices.zte.trigger.RegisterEvent;

/**
 *
 * @author matsaudzaa
 */
public class OCSReceiver {
    
      private final static String MILLENIUM_COS_NAME = "4007";
     private final static String TEL_COS_NAME = "Core";
    
    public void receiverListener(String msisdn){
        try{
        //zte ocs check core > $2 
        CreditAccount credit = new CreditAccount();
        Double amount = 0.0 ;// credit.getSubscriberBalance(msisdn);
        if(amount >= 2){
            
             RegisterDao rDAO = new RegisterDao();
            rDAO.initEntityManager();
          Register entry = rDAO.findByMobileNumber(msisdn);
          if(entry == null){
              //sub opted out, do not execute job
              System.out.println("####### : //sub opted out, do not execute job");
              return;
          }
          if("INACTIVE".equals(entry.getStatus())){
              //aready active in promo
              //do nothing
              return;
          }
           
            //change sub to 2777 COS
            ChangeCOS change = new ChangeCOS();
            change.changeCOS(MILLENIUM_COS_NAME, msisdn);
            // credit account --$2
//             credit.subtractAmount(msisdn, -2.00);  //catch return value success/fail
            
            // register new window period
            Date expiryDate = ExpiryDateGenerator.generateExpiryDate(7);
            RegisterTrigger trigger = new RegisterTrigger();
//            trigger.registerWindowPeriod(msisdn, false); //returns boolean might save output for debugging purposes
            
            // create new schedule  ..already done in register window period
            
            //update register to active
            RegisterDao rDao = new RegisterDao();
            rDao.initEntityManager();
            Register registerDto = rDao.findByMobileNumber(msisdn);
            registerDto.setStatus("ACTIVE");
            rDao.edit(registerDto);
            
            //create new transaction : renewal ..already done in register window period
            
            //remove zte trigger set large amount
//            RegisterEvent registerEvent = new RegisterEvent();
  //          registerEvent.registerTrigger(msisdn, 2000.00); //check max value //consider return value
           
            //notify subscriber
            try{
                SMSGateway smsClient = new SMSGateway();
                smsClient.sendMessage(msisdn, "Telecel", "Your subscription to 2013 has been renewed. Enjoy discounted rates until "+expiryDate+" : 1c per min from 9PM to 12PM daily; SMS at 0c and internet at 3c/ MB");
              
            }catch(Exception ex){
                 System.out.println("#### ERROR SENDING SMS at : "+new Date());
            }
             
        }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}

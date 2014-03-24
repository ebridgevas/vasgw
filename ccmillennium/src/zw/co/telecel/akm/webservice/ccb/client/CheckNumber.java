/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.webservice.ccb.client;

import com.ztesoft.zsmart.bss.ws.customization.zimbabwe.CellPhoneNumberIdentifyReqDto;
import com.ztesoft.zsmart.bss.ws.customization.zimbabwe.CellPhoneNumberIdentifyRetDto;
import com.ztesoft.zsmart.bss.ws.customization.zimbabwe.ChangeUserBrandReqDto;
import com.ztesoft.zsmart.bss.ws.customization.zimbabwe.WebServicesService;
import java.util.Date;

/**
 *
 * @author matsaudzaa
 */
public class CheckNumber {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      CheckNumber.class.getName());
    
    public boolean isPrepaid(String msisdn){
        try{
            // 2prepaid 1postpaid
            WebServicesService web = new WebServicesService();
            CellPhoneNumberIdentifyReqDto req = new CellPhoneNumberIdentifyReqDto();
             //req.setMSISDN("0737730520");
            String num = "0"+msisdn.substring(3);
            req.setMSISDN(num);
             
           CellPhoneNumberIdentifyRetDto result = web.getWebServices().cellPhoneNumberIdentify(req);
           String count = result.getUserFlag();
           //System.out.println("Count : "+count);
          
          
          
          
          if("1".equals(count)){ //postpaid
              return false;
          }else{
              return true;
          }
          
            
        }catch(Exception ex){
            _log.error("##### CCB Millenium, failed to check msisdn if prepaid /postpaid, msisdn:"+msisdn+", date:"+new Date(), ex);
            //ex.printStackTrace();
           return true;
            
        }
        
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.webservice.ccb.client;

import com.ztesoft.zsmart.bss.ws.customization.zimbabwe.ChangeUserBrandReqDto;
import com.ztesoft.zsmart.bss.ws.customization.zimbabwe.WebServicesService;
import java.util.Date;

/**
 *
 * @author matsaudzaa
 */
public class ChangeCOS {
    
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      ChangeCOS.class.getName());
    
    public boolean changeCOS(String cosName, String msisdn){
        try{
            WebServicesService web = new WebServicesService();
            ChangeUserBrandReqDto user = new ChangeUserBrandReqDto();
            user.setBrandIndex(cosName);
            user.setMSISDN(msisdn);
             
            web.getWebServices().changeUserBrand(user);
            //System.out.println("##### SUCCESSFUL COS CHANGED TO : "+cosName+ ", msisdn:"+msisdn+", date : "+new Date()); 
            _log.info("##### CCBMillenium SUCCESSFUL COS CHANGED TO : "+cosName+ ", msisdn:"+msisdn+", date : "+new Date());
            return true;
        }catch(Exception ex){
            //System.out.println("##### FAILED TO CHANGE COS TO : "+cosName+ ", msisdn:"+msisdn+", date : "+new Date());     
            _log.error("##### CCBMillenium FAILED TO CHANGE COS TO : "+cosName+ ", msisdn:"+msisdn+", date : "+new Date(), ex);
            ex.printStackTrace();
            return false;
        }
    }
}

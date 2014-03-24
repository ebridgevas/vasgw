/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.webservice.ccb.client;

/**
 *
 * @author matsaudzaa
 */
public class TestClass {
    
    public static void main(String [] args){
        try{
           
               //4007  263738836147  738837706
            ChangeCOS change = new ChangeCOS(); //0738836137  737730520
             boolean result = change.changeCOS("TEL_COS", "263739108878");
             
             System.out.println("State : "+result);
            //change.changeCOS(null, null)
            // change.changeCOS("TEL_COS", "263733099419"); // ok
          //  change.changeCOS("TEL_COS", "263733037621");
           //change.changeCOS("TEL_COS", "263738948338");
          // change.changeCOS("TEL_COS", "263738948338");  //ok
          // change.changeCOS("TEL_COS", "263737730520"); //not ok
         // change.changeCOS("CONT_COS", "263738836139");
          // change.changeCOS("TEL_COS", "263737379786");
          // change.changeCOS("TEL_COS", "263737730522");
          // change.changeCOS("TEL_COS", "263738836150"); // not ok
           // change.changeCOS("TEL_COS", "263738836143"); // does not work
           //738948339
            //System.out.println("success");
        }catch(Exception ex){
            ex.printStackTrace();
        }
                
    }
    
}

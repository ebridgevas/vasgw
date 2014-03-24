/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.webservice.ccb.client;

/**
 *
 * @author matsaudzaa
 */
public class TestNum {
      public static void main(String [] args){
        try{
           

            ChangeCOS change = new ChangeCOS(); //0738836137  737730520
            CheckNumber check = new CheckNumber();
            boolean result = check.isPrepaid("263734266605");
           // change.changeCOS("2777Millenium", "263737730520");
            //change.changeCOS("TEL_COS", "263738836139");
            System.out.println("state : "+result);
        }catch(Exception ex){
            ex.printStackTrace();
        }
                
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.utils;

import java.util.Date;

/**
 *
 * @author matsaudzaa
 */
public class NewClass {
    
    public static void main(String [] args){
        try{
            ExpiryDateGenerator.onDayExpiryDate(new Date());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}

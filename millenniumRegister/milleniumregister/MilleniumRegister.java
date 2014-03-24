/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package milleniumregister;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import zw.co.telecel.model.Register;

/**
 *
 * @author madziwal
 */
public class MilleniumRegister {

    /**
     * @param args the command line arguments
     */
    public static  EntityManagerFactory emf = Persistence.createEntityManagerFactory("MilleniumRegisterPU");
    
    public static boolean isSubscriberInMillenium(String msisdn){
    Register reg = null;
    reg = new RegisterJpaController(emf).findRegisterByMsisdn(msisdn);
    if(reg == null){
    return false;
    }
    else return true;
    }
    
    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
       System.out.println("<<<<<<<<<<<<<<<<<, result is :"+isSubscriberInMillenium("263733001617"));
    }
}

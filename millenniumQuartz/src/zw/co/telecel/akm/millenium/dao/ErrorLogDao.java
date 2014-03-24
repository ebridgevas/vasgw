/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import zw.co.telecel.akm.millenium.dto.ERRORLOG;
import zw.co.telecel.akm.millenium.dto.MTransaction;

/**
 *
 * @author matsaudzaa
 */
public class ErrorLogDao {
    
    
     private EntityManagerFactory emf;
    private EntityManager em;
    private String PERSISTENCE_UNIT_NAME = "MilleniumQuartzPU";

    public void initEntityManager() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
    }

    public void closeEntityManager() {
        em.close();
        emf.close();
    }

    public void persist(ERRORLOG error) {
        em.getTransaction().begin();
        em.persist(error);
        em.getTransaction().commit();
    }

   
    
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import zw.co.telecel.akm.millenium.core.ChangeCOSJob;
import zw.co.telecel.akm.millenium.dto.MTransaction;
import zw.co.telecel.akm.millenium.dto.Register;

/**
 *
 * @author matsaudzaa
 */
public class RegisterDao {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private String PERSISTENCE_UNIT_NAME = "MilleniumQuartzPU";
    
     private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(
                      RegisterDao.class.getName());

    public void initEntityManager() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
    }

    public void closeEntityManager() {
        em.close();
        emf.close();
    }

    public void persist(Register register) {
        em.getTransaction().begin();
        em.persist(register);
        em.getTransaction().commit();
    }
    
    public void edit(Register register) {
        em.getTransaction().begin();
        em.merge(register);
        em.getTransaction().commit();
    }
    
      public void delete(Register register) {
        em.getTransaction().begin();
        em.remove(register);
        em.getTransaction().commit();
    }

    public List<Register> findByState(String status) {
        List<Register> t = (List<Register>)em.createQuery(
                "select r from Register r where r.status = :status")
                .setParameter("status", status).getResultList();
        return t;
    }
    
    public Register findByMobileNumber(String msisdn) {
        try{
        Register r = (Register)em.createQuery(
                "select r from Register r where r.msisdn = :msisdn")
                .setParameter("msisdn", msisdn).getSingleResult();
        return r;
        }
        catch(NoResultException nre){
            return null;
        }
        catch(Exception ex){
            //System.out.println("##### CHECK DB, error searching register from DB , date : "+new Date()+"error msg :"+ex.getMessage());
            _log.error("##### CHECK DB, error searching register from DB from RegisterDao , date : "+new Date(), ex);
            Register register = new Register();
            register.setMsisdn("0");
            return register;
        }
    }
    
    
}

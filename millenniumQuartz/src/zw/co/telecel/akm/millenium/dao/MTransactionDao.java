/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import zw.co.telecel.akm.millenium.dto.MTransaction;

/**
 *
 * @author matsaudzaa
 */
public class MTransactionDao {
    
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

    public void persist(MTransaction transaction) {
        em.getTransaction().begin();
        em.persist(transaction);
        em.getTransaction().commit();
    }

    public List<MTransaction> findByMobileNumber(String msisdn) {
        List<MTransaction> t = (List<MTransaction>)em.createQuery(
                "select t from MTransaction t where t.msisdn = :msisdn")
                .setParameter("msisdn", msisdn).getResultList();
        return t;
    }
    
    
}

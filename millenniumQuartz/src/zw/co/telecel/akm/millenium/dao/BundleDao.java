/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dao;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import zw.co.telecel.akm.millenium.dto.BundleDto;

/**
 *
 * @author matsaudzaa
 */
public class BundleDao {
    
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

    public void persist(BundleDto bundle) {
        em.getTransaction().begin();
        em.persist(bundle);
        em.getTransaction().commit();
    }

    public List<BundleDto> findByMobileNumber(String mobileNumber) {
        try{
        List<BundleDto> b = (List<BundleDto>)em.createQuery(
                "select b from BundleDto b where b.mobileNumber = :mobileNumber")
                .setParameter("mobileNumber", mobileNumber).getResultList();
        return b;
        }catch(Exception ex){
            return new ArrayList<BundleDto>();
        }
        
    }
    
}

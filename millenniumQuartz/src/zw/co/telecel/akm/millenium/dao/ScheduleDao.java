/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import zw.co.telecel.akm.millenium.dto.Schedule;

/**
 *
 * @author matsaudzaa
 */
public class ScheduleDao {
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

    public void persist(Schedule schedule) {
        em.getTransaction().begin();
        em.persist(schedule);
        em.getTransaction().commit();
    }

    public List<Schedule> findByMobileNumber(String msisdn) {
        List<Schedule> t = (List<Schedule>)em.createQuery(
                "select s from Schedule s where s.msisdn = :msisdn")
                .setParameter("msisdn", msisdn).getResultList();
        return t;
    }
    
}

package zw.co.telecel.akm.millenium.dao;



import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import zw.co.telecel.akm.millenium.dto.PduDto;

public class PduDao {

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

    public void persist(PduDto pdu) {
        em.getTransaction().begin();
        em.persist(pdu);
        em.getTransaction().commit();
    }

    public PduDto findById(String uuid) {
        PduDto p = (PduDto)em.createQuery(
                "select p from PduDto p where p.uuid = :uuid")
                .setParameter("uuid", uuid).getSingleResult();
        return p;
    }
}

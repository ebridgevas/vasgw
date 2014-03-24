/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package milleniumregister;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import milleniumregister.exceptions.NonexistentEntityException;
import milleniumregister.exceptions.PreexistingEntityException;
import zw.co.telecel.model.Register;

/**
 *
 * @author madziwal
 */
public class RegisterJpaController implements Serializable {

    public RegisterJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Register register) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(register);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRegister(register.getMsisdn()) != null) {
                throw new PreexistingEntityException("Register " + register + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    
    public Register findRegisterByMsisdn(String msisdn){
     EntityManager em = getEntityManager();
     Register reg = null;
        try {
            Query q = em.createQuery("SELECT r FROM Register r WHERE r.msisdn = :msisdn AND r.status = :status");
            q.setParameter("msisdn", msisdn);
            q.setParameter("status", "ACTIVE");
            reg = (Register)q.getSingleResult();
            return reg;
        } catch (Exception e) {
            return null;
        }
     }
    
    public void edit(Register register) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            register = em.merge(register);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = register.getMsisdn();
                if (findRegister(id) == null) {
                    throw new NonexistentEntityException("The register with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Register register;
            try {
                register = em.getReference(Register.class, id);
                register.getMsisdn();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The register with id " + id + " no longer exists.", enfe);
            }
            em.remove(register);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Register> findRegisterEntities() {
        return findRegisterEntities(true, -1, -1);
    }

    public List<Register> findRegisterEntities(int maxResults, int firstResult) {
        return findRegisterEntities(false, maxResults, firstResult);
    }

    private List<Register> findRegisterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Register.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Register findRegister(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Register.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegisterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Register> rt = cq.from(Register.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

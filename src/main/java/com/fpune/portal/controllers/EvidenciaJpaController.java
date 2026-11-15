/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.controllers;

import com.fpune.portal.controllers.exceptions.NonexistentEntityException;
import com.fpune.portal.entity.Evidencia;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.fpune.portal.entity.Solicitud;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

/**
 *
 * @author marti
 */
public class EvidenciaJpaController implements Serializable {

    public EvidenciaJpaController() {
      this.emf = JpaUtil.getEntityManagerFactory();

    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Evidencia evidencia) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Solicitud solicitud = evidencia.getSolicitud();
            if (solicitud != null) {
                solicitud = em.getReference(solicitud.getClass(), solicitud.getId());
                evidencia.setSolicitud(solicitud);
            }
            em.persist(evidencia);
            if (solicitud != null) {
                solicitud.getEvidenciaList().add(evidencia);
                solicitud = em.merge(solicitud);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Evidencia evidencia) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Evidencia persistentEvidencia = em.find(Evidencia.class, evidencia.getId());
            Solicitud solicitudOld = persistentEvidencia.getSolicitud();
            Solicitud solicitudNew = evidencia.getSolicitud();
            if (solicitudNew != null) {
                solicitudNew = em.getReference(solicitudNew.getClass(), solicitudNew.getId());
                evidencia.setSolicitud(solicitudNew);
            }
            evidencia = em.merge(evidencia);
            if (solicitudOld != null && !solicitudOld.equals(solicitudNew)) {
                solicitudOld.getEvidenciaList().remove(evidencia);
                solicitudOld = em.merge(solicitudOld);
            }
            if (solicitudNew != null && !solicitudNew.equals(solicitudOld)) {
                solicitudNew.getEvidenciaList().add(evidencia);
                solicitudNew = em.merge(solicitudNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = evidencia.getId();
                if (findEvidencia(id) == null) {
                    throw new NonexistentEntityException("The evidencia with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Evidencia evidencia;
            try {
                evidencia = em.getReference(Evidencia.class, id);
                evidencia.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The evidencia with id " + id + " no longer exists.", enfe);
            }
            Solicitud solicitud = evidencia.getSolicitud();
            if (solicitud != null) {
                solicitud.getEvidenciaList().remove(evidencia);
                solicitud = em.merge(solicitud);
            }
            em.remove(evidencia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Evidencia> findEvidenciaEntities() {
        return findEvidenciaEntities(true, -1, -1);
    }

    public List<Evidencia> findEvidenciaEntities(int maxResults, int firstResult) {
        return findEvidenciaEntities(false, maxResults, firstResult);
    }

    private List<Evidencia> findEvidenciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Evidencia.class));
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

    public Evidencia findEvidencia(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Evidencia.class, id);
        } finally {
            em.close();
        }
    }

    public int getEvidenciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Evidencia> rt = cq.from(Evidencia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

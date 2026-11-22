/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.controllers;

import com.fpune.portal.controllers.exceptions.NonexistentEntityException;
import com.fpune.portal.entity.HistorialEstados;
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
public class HistorialEstadosJpaController implements Serializable {
    

    public HistorialEstadosJpaController() {
        this.emf = JpaUtil.getEntityManagerFactory();
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(HistorialEstados historialEstados) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Solicitud solicitud = historialEstados.getSolicitud();
            if (solicitud != null) {
                solicitud = em.getReference(solicitud.getClass(), solicitud.getId());
                historialEstados.setSolicitud(solicitud);
            }
            em.persist(historialEstados);
            if (solicitud != null) {
                solicitud.getHistorialEstadosList().add(historialEstados);
                solicitud = em.merge(solicitud);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(HistorialEstados historialEstados) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HistorialEstados persistentHistorialEstados = em.find(HistorialEstados.class, historialEstados.getId());
            Solicitud solicitudOld = persistentHistorialEstados.getSolicitud();
            Solicitud solicitudNew = historialEstados.getSolicitud();
            if (solicitudNew != null) {
                solicitudNew = em.getReference(solicitudNew.getClass(), solicitudNew.getId());
                historialEstados.setSolicitud(solicitudNew);
            }
            historialEstados = em.merge(historialEstados);
            if (solicitudOld != null && !solicitudOld.equals(solicitudNew)) {
                solicitudOld.getHistorialEstadosList().remove(historialEstados);
                solicitudOld = em.merge(solicitudOld);
            }
            if (solicitudNew != null && !solicitudNew.equals(solicitudOld)) {
                solicitudNew.getHistorialEstadosList().add(historialEstados);
                solicitudNew = em.merge(solicitudNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = historialEstados.getId();
                if (findHistorialEstados(id) == null) {
                    throw new NonexistentEntityException("The historialEstados with id " + id + " no longer exists.");
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
            HistorialEstados historialEstados;
            try {
                historialEstados = em.getReference(HistorialEstados.class, id);
                historialEstados.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The historialEstados with id " + id + " no longer exists.", enfe);
            }
            Solicitud solicitud = historialEstados.getSolicitud();
            if (solicitud != null) {
                solicitud.getHistorialEstadosList().remove(historialEstados);
                solicitud = em.merge(solicitud);
            }
            em.remove(historialEstados);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<HistorialEstados> findHistorialEstadosEntities() {
        return findHistorialEstadosEntities(true, -1, -1);
    }

    public List<HistorialEstados> findHistorialEstadosEntities(int maxResults, int firstResult) {
        return findHistorialEstadosEntities(false, maxResults, firstResult);
    }

    private List<HistorialEstados> findHistorialEstadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HistorialEstados.class));
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

    public HistorialEstados findHistorialEstados(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HistorialEstados.class, id);
        } finally {
            em.close();
        }
    }

    public int getHistorialEstadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HistorialEstados> rt = cq.from(HistorialEstados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
     public List<HistorialEstados> findBySolicitud(Long idSolicitud) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT h FROM HistorialEstados h JOIN FETCH h.admin "
                    + "WHERE h.solicitud.id = :idSolicitud ORDER BY h.fechaCambio, h.id",
                    HistorialEstados.class)
                    .setParameter("idSolicitud", idSolicitud)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
}

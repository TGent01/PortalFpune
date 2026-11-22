/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.controllers;

import com.fpune.portal.controllers.exceptions.NonexistentEntityException;
import com.fpune.portal.entity.Prioridad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author marti
 */
public class PrioridadJpaController implements Serializable {

    public PrioridadJpaController() {
        this.emf = JpaUtil.getEntityManagerFactory();

    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prioridad prioridad) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(prioridad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prioridad prioridad) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            prioridad = em.merge(prioridad);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = prioridad.getId();
                if (findPrioridad(id) == null) {
                    throw new NonexistentEntityException("The prioridad with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prioridad prioridad;
            try {
                prioridad = em.getReference(Prioridad.class, id);
                prioridad.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prioridad with id " + id + " no longer exists.", enfe);
            }
            em.remove(prioridad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prioridad> findPrioridadEntities() {
        return findPrioridadEntities(true, -1, -1);
    }

    public List<Prioridad> findPrioridadEntities(int maxResults, int firstResult) {
        return findPrioridadEntities(false, maxResults, firstResult);
    }

    private List<Prioridad> findPrioridadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Prioridad p ORDER BY p.nivel, p.id", Prioridad.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Prioridad findPrioridad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prioridad.class, id);
        } finally {
            em.close();
        }
    }
    public Prioridad findPrioridadByNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Prioridad.findByNombre", Prioridad.class)
                    .setParameter("nombre", nombre)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
    
    public Prioridad findPrioridadByNivel(int nivel) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Prioridad.findByNivel", Prioridad.class)
                    .setParameter("nivel", nivel)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public int getPrioridadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prioridad> rt = cq.from(Prioridad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

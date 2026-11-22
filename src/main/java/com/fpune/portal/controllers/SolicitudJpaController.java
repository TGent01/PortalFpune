/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.controllers;

import com.fpune.portal.controllers.exceptions.IllegalOrphanException;
import com.fpune.portal.controllers.exceptions.NonexistentEntityException;
import com.fpune.portal.entity.Estado;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.fpune.portal.entity.Evidencia;
import java.util.ArrayList;
import java.util.List;
import com.fpune.portal.entity.HistorialEstados;
import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.Date;

/**
 *
 * @author marti
 */
public class SolicitudJpaController implements Serializable {

    public SolicitudJpaController() {
        this.emf = JpaUtil.getEntityManagerFactory();

    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Solicitud solicitud) {
        if (solicitud.getEvidenciaList() == null) {
            solicitud.setEvidenciaList(new ArrayList<Evidencia>());
        }
        if (solicitud.getHistorialEstadosList() == null) {
            solicitud.setHistorialEstadosList(new ArrayList<HistorialEstados>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Evidencia> attachedEvidenciaList = new ArrayList<Evidencia>();
            for (Evidencia evidenciaListEvidenciaToAttach : solicitud.getEvidenciaList()) {
                evidenciaListEvidenciaToAttach = em.getReference(evidenciaListEvidenciaToAttach.getClass(), evidenciaListEvidenciaToAttach.getId());
                attachedEvidenciaList.add(evidenciaListEvidenciaToAttach);
            }
            solicitud.setEvidenciaList(attachedEvidenciaList);
            List<HistorialEstados> attachedHistorialEstadosList = new ArrayList<HistorialEstados>();
            for (HistorialEstados historialEstadosListHistorialEstadosToAttach : solicitud.getHistorialEstadosList()) {
                historialEstadosListHistorialEstadosToAttach = em.getReference(historialEstadosListHistorialEstadosToAttach.getClass(), historialEstadosListHistorialEstadosToAttach.getId());
                attachedHistorialEstadosList.add(historialEstadosListHistorialEstadosToAttach);
            }
            solicitud.setHistorialEstadosList(attachedHistorialEstadosList);
            em.persist(solicitud);
            for (Evidencia evidenciaListEvidencia : solicitud.getEvidenciaList()) {
                Solicitud oldSolicitudOfEvidenciaListEvidencia = evidenciaListEvidencia.getSolicitud();
                evidenciaListEvidencia.setSolicitud(solicitud);
                evidenciaListEvidencia = em.merge(evidenciaListEvidencia);
                if (oldSolicitudOfEvidenciaListEvidencia != null) {
                    oldSolicitudOfEvidenciaListEvidencia.getEvidenciaList().remove(evidenciaListEvidencia);
                    oldSolicitudOfEvidenciaListEvidencia = em.merge(oldSolicitudOfEvidenciaListEvidencia);
                }
            }
            for (HistorialEstados historialEstadosListHistorialEstados : solicitud.getHistorialEstadosList()) {
                Solicitud oldSolicitudOfHistorialEstadosListHistorialEstados = historialEstadosListHistorialEstados.getSolicitud();
                historialEstadosListHistorialEstados.setSolicitud(solicitud);
                historialEstadosListHistorialEstados = em.merge(historialEstadosListHistorialEstados);
                if (oldSolicitudOfHistorialEstadosListHistorialEstados != null) {
                    oldSolicitudOfHistorialEstadosListHistorialEstados.getHistorialEstadosList().remove(historialEstadosListHistorialEstados);
                    oldSolicitudOfHistorialEstadosListHistorialEstados = em.merge(oldSolicitudOfHistorialEstadosListHistorialEstados);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Solicitud solicitud) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Solicitud persistentSolicitud = em.find(Solicitud.class, solicitud.getId());
            List<Evidencia> evidenciaListOld = persistentSolicitud.getEvidenciaList();
            List<Evidencia> evidenciaListNew = solicitud.getEvidenciaList();
            List<HistorialEstados> historialEstadosListOld = persistentSolicitud.getHistorialEstadosList();
            List<HistorialEstados> historialEstadosListNew = solicitud.getHistorialEstadosList();
            List<String> illegalOrphanMessages = null;
            for (Evidencia evidenciaListOldEvidencia : evidenciaListOld) {
                if (!evidenciaListNew.contains(evidenciaListOldEvidencia)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Evidencia " + evidenciaListOldEvidencia + " since its solicitud field is not nullable.");
                }
            }
            for (HistorialEstados historialEstadosListOldHistorialEstados : historialEstadosListOld) {
                if (!historialEstadosListNew.contains(historialEstadosListOldHistorialEstados)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain HistorialEstados " + historialEstadosListOldHistorialEstados + " since its solicitud field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Evidencia> attachedEvidenciaListNew = new ArrayList<Evidencia>();
            for (Evidencia evidenciaListNewEvidenciaToAttach : evidenciaListNew) {
                evidenciaListNewEvidenciaToAttach = em.getReference(evidenciaListNewEvidenciaToAttach.getClass(), evidenciaListNewEvidenciaToAttach.getId());
                attachedEvidenciaListNew.add(evidenciaListNewEvidenciaToAttach);
            }
            evidenciaListNew = attachedEvidenciaListNew;
            solicitud.setEvidenciaList(evidenciaListNew);
            List<HistorialEstados> attachedHistorialEstadosListNew = new ArrayList<HistorialEstados>();
            for (HistorialEstados historialEstadosListNewHistorialEstadosToAttach : historialEstadosListNew) {
                historialEstadosListNewHistorialEstadosToAttach = em.getReference(historialEstadosListNewHistorialEstadosToAttach.getClass(), historialEstadosListNewHistorialEstadosToAttach.getId());
                attachedHistorialEstadosListNew.add(historialEstadosListNewHistorialEstadosToAttach);
            }
            historialEstadosListNew = attachedHistorialEstadosListNew;
            solicitud.setHistorialEstadosList(historialEstadosListNew);
            solicitud = em.merge(solicitud);
            for (Evidencia evidenciaListNewEvidencia : evidenciaListNew) {
                if (!evidenciaListOld.contains(evidenciaListNewEvidencia)) {
                    Solicitud oldSolicitudOfEvidenciaListNewEvidencia = evidenciaListNewEvidencia.getSolicitud();
                    evidenciaListNewEvidencia.setSolicitud(solicitud);
                    evidenciaListNewEvidencia = em.merge(evidenciaListNewEvidencia);
                    if (oldSolicitudOfEvidenciaListNewEvidencia != null && !oldSolicitudOfEvidenciaListNewEvidencia.equals(solicitud)) {
                        oldSolicitudOfEvidenciaListNewEvidencia.getEvidenciaList().remove(evidenciaListNewEvidencia);
                        oldSolicitudOfEvidenciaListNewEvidencia = em.merge(oldSolicitudOfEvidenciaListNewEvidencia);
                    }
                }
            }
            for (HistorialEstados historialEstadosListNewHistorialEstados : historialEstadosListNew) {
                if (!historialEstadosListOld.contains(historialEstadosListNewHistorialEstados)) {
                    Solicitud oldSolicitudOfHistorialEstadosListNewHistorialEstados = historialEstadosListNewHistorialEstados.getSolicitud();
                    historialEstadosListNewHistorialEstados.setSolicitud(solicitud);
                    historialEstadosListNewHistorialEstados = em.merge(historialEstadosListNewHistorialEstados);
                    if (oldSolicitudOfHistorialEstadosListNewHistorialEstados != null && !oldSolicitudOfHistorialEstadosListNewHistorialEstados.equals(solicitud)) {
                        oldSolicitudOfHistorialEstadosListNewHistorialEstados.getHistorialEstadosList().remove(historialEstadosListNewHistorialEstados);
                        oldSolicitudOfHistorialEstadosListNewHistorialEstados = em.merge(oldSolicitudOfHistorialEstadosListNewHistorialEstados);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = solicitud.getId();
                if (findSolicitud(id) == null) {
                    throw new NonexistentEntityException("The solicitud with id " + id + " no longer exists.");
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
        Solicitud solicitud = em.find(Solicitud.class, id);
        if (solicitud == null) {
            throw new NonexistentEntityException("The solicitud with id " + id + " no longer exists.");
        }
        em.remove(solicitud);   // CascadeType.ALL borra también sus evidencias e historial
        em.getTransaction().commit();
    } catch (RuntimeException ex) {
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        throw ex;
    } finally {
        if (em != null) {
            em.close();
        }
    }
}

    public List<Solicitud> findSolicitudEntities() {
        return findSolicitudEntities(true, -1, -1);
    }

    public List<Solicitud> findSolicitudEntities(int maxResults, int firstResult) {
        return findSolicitudEntities(false, maxResults, firstResult);
    }

    private List<Solicitud> findSolicitudEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Solicitud.class));
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
    
    public List<Solicitud> findSolicitudesFiltradas(Integer idEstado, Integer idCategoria,
            Integer idTipo, Integer idPrioridad) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT s FROM Solicitud s WHERE 1 = 1");
            if (idEstado != null) {
                jpql.append(" AND s.estado.id = :idEstado");
            }
            if (idCategoria != null) {
                jpql.append(" AND s.categoria.id = :idCategoria");
            }
            if (idTipo != null) {
                jpql.append(" AND s.tipoSolicitud.id = :idTipo");
            }
            if (idPrioridad != null) {
                jpql.append(" AND s.prioridad.id = :idPrioridad");
            }
            jpql.append(" ORDER BY s.fechaCreacion DESC, s.id DESC");

            TypedQuery<Solicitud> q = em.createQuery(jpql.toString(), Solicitud.class);
            if (idEstado != null) {
                q.setParameter("idEstado", idEstado);
            }
            if (idCategoria != null) {
                q.setParameter("idCategoria", idCategoria);
            }
            if (idTipo != null) {
                q.setParameter("idTipo", idTipo);
            }
            if (idPrioridad != null) {
                q.setParameter("idPrioridad", idPrioridad);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cambia estado y/o prioridad de una solicitud y registra el cambio en el
     * historial, TODO en una sola transacción (o se guarda todo o nada).
     *
     * Las validaciones (estado final, existencia, comentario obligatorio...) se
     * hacen antes en SolicitudServiceMysqlImpl.cambiarEstado().
     *
     * @param idPrioridad null = mantener la prioridad actual
     * @param idAdmin     usuario administrador que hace el cambio
     */
    public void cambiarEstado(Long idSolicitud, Integer idEstadoNuevo, Integer idPrioridad,
            String comentario, Long idAdmin) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            Solicitud solicitud = em.find(Solicitud.class, idSolicitud);
            if (solicitud == null) {
                throw new NonexistentEntityException("The solicitud with id " + idSolicitud + " no longer exists.");
            }
            Estado estadoAnterior = solicitud.getEstado();
            Estado estadoNuevo = em.find(Estado.class, idEstadoNuevo);

            // La prioridad solo se guarda en el historial si en este cambio se asignó una distinta
            Prioridad prioridadAsignada = null;
            if (idPrioridad != null) {
                Prioridad nueva = em.find(Prioridad.class, idPrioridad);
                Prioridad actual = solicitud.getPrioridad();
                if (actual == null || !actual.getId().equals(nueva.getId())) {
                    prioridadAsignada = nueva;
                }
                solicitud.setPrioridad(nueva);
            }

            solicitud.setEstado(estadoNuevo);
            // Fecha de cierre: se completa al llegar a un estado final
            solicitud.setFechaCierre(estadoNuevo.getEsFinal() ? new Date() : null);
            // "solicitud" está administrada por el EntityManager: el UPDATE se hace solo en el commit

            HistorialEstados historial = new HistorialEstados(solicitud, estadoAnterior, estadoNuevo,
                    prioridadAsignada, comentario, em.getReference(Usuario.class, idAdmin));
            em.persist(historial); // fechaCambio la pone el @PrePersist

            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Solicitud findSolicitud(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Solicitud.class, id);
        } finally {
            em.close();
        }
    }
     public Solicitud findSolicitudByCodigoTicket(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Solicitud.findByCodigoTicket", Solicitud.class)
                    .setParameter("codigoTicket", com.fpune.portal.util.TicketGenerator.normalizar(codigo))
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public int getSolicitudCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Solicitud> rt = cq.from(Solicitud.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public boolean existeTicket(String codigo) {
    EntityManager em = getEntityManager();
    try {
        return !em.createNamedQuery("Solicitud.findByCodigoTicket", Solicitud.class)
                  .setParameter("codigoTicket", codigo)
                  .setMaxResults(1)
                  .getResultList()
                  .isEmpty();
    } finally {
        em.close();
    }
    }
    
}

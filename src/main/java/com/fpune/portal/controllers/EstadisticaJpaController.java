/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.controllers;

import com.fpune.portal.dtos.SolicitudResumenDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consultas de solo lectura para el módulo de estadísticas.
 * Traen únicamente los campos necesarios (nada de asunto, descripción ni usuarios).
 *
 * @author marti
 */
public class EstadisticaJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public EstadisticaJpaController() {
        this.emf = JpaUtil.getEntityManagerFactory();
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Resumen de las solicitudes creadas entre desde y hasta (ambos incluidos).
     * Si desde o hasta son null, ese límite no se aplica.
     */
    public List<SolicitudResumenDTO> findResumenSolicitudes(LocalDate desde, LocalDate hasta) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT s.id, e.nombre, e.esFinal, c.nombre, t.nombre, p.nombre, s.fechaCreacion, s.fechaCierre "
                    + "FROM Solicitud s JOIN s.estado e JOIN s.categoria c JOIN s.tipoSolicitud t "
                    + "LEFT JOIN s.prioridad p WHERE 1 = 1");
            if (desde != null) {
                jpql.append(" AND s.fechaCreacion >= :desde");
            }
            if (hasta != null) {
                jpql.append(" AND s.fechaCreacion <= :hasta");
            }
            TypedQuery<Object[]> q = em.createQuery(jpql.toString(), Object[].class);
            if (desde != null) {
                q.setParameter("desde", java.sql.Date.valueOf(desde));
            }
            if (hasta != null) {
                q.setParameter("hasta", java.sql.Date.valueOf(hasta));
            }

            List<SolicitudResumenDTO> lista = new ArrayList<>();
            for (Object[] f : q.getResultList()) {
                lista.add(new SolicitudResumenDTO(
                        (Long) f[0],
                        (String) f[1],
                        Boolean.TRUE.equals(f[2]),
                        (String) f[3],
                        (String) f[4],
                        (String) f[5],
                        aLocalDate(f[6]),
                        aLocalDate(f[7])));
            }
            return lista;
        } finally {
            em.close();
        }
    }

    /** id de solicitud → fecha del PRIMER cambio que hizo un admin (para el tiempo de respuesta). */
    public Map<Long, LocalDate> findPrimerCambioPorSolicitud() {
        EntityManager em = getEntityManager();
        try {
            Map<Long, LocalDate> mapa = new HashMap<>();
            List<Object[]> filas = em.createQuery(
                    "SELECT h.solicitud.id, MIN(h.fechaCambio) FROM HistorialEstados h GROUP BY h.solicitud.id",
                    Object[].class).getResultList();
            for (Object[] f : filas) {
                mapa.put((Long) f[0], aLocalDate(f[1]));
            }
            return mapa;
        } finally {
            em.close();
        }
    }

    /** id de solicitud → cantidad de evidencias. */
    public Map<Long, Long> findCantidadEvidenciasPorSolicitud() {
        EntityManager em = getEntityManager();
        try {
            Map<Long, Long> mapa = new HashMap<>();
            List<Object[]> filas = em.createQuery(
                    "SELECT e.solicitud.id, COUNT(e) FROM Evidencia e GROUP BY e.solicitud.id",
                    Object[].class).getResultList();
            for (Object[] f : filas) {
                mapa.put((Long) f[0], (Long) f[1]);
            }
            return mapa;
        } finally {
            em.close();
        }
    }

    /** Usuarios habilitados (KPI de adopción digital). */
    public long countUsuariosHabilitados() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.isEnable = true", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Hibernate devuelve DATE como java.sql.Date (su toInstant() falla) y
     * TIMESTAMP como java.sql.Timestamp. Esta conversión sirve para los dos.
     */
    private static LocalDate aLocalDate(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof LocalDate) {
            return (LocalDate) valor;
        }
        return new java.sql.Date(((Date) valor).getTime()).toLocalDate();
    }
}
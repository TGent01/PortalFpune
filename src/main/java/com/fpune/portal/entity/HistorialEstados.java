/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Registro de cada cambio de estado y/o prioridad que un administrador
 * realiza sobre una solicitud (módulo de trazabilidad).
 *
 * @author marti
 */
@Entity
@Table(name = "historial_estados")
@NamedQueries({
    @NamedQuery(name = "HistorialEstados.findAll", query = "SELECT h FROM HistorialEstados h ORDER BY h.fechaCambio"),
    @NamedQuery(name = "HistorialEstados.findById", query = "SELECT h FROM HistorialEstados h WHERE h.id = :id"),
    @NamedQuery(name = "HistorialEstados.findBySolicitud", query = "SELECT h FROM HistorialEstados h WHERE h.solicitud.id = :idSolicitud ORDER BY h.fechaCambio"),
    @NamedQuery(name = "HistorialEstados.findByAdmin", query = "SELECT h FROM HistorialEstados h WHERE h.admin.id = :idAdmin ORDER BY h.fechaCambio DESC"),
    @NamedQuery(name = "HistorialEstados.findByFechaCambio", query = "SELECT h FROM HistorialEstados h WHERE h.fechaCambio = :fechaCambio")})
public class HistorialEstados implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @Basic(optional = false)
    @Column(name = "fecha_cambio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCambio;

    // ---- Relaciones que SÍ se muestran en la línea de tiempo (EAGER) ----

    // Null en el primer registro (cuando se crea la solicitud no hay estado anterior).
    @JoinColumn(name = "id_estado_anterior", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Estado estadoAnterior;

    @JoinColumn(name = "id_estado_nuevo", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Estado estadoNuevo;

    // Prioridad asignada en este cambio (opcional).
    @JoinColumn(name = "id_prioridad", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Prioridad prioridad;

    // ---- Relaciones ocultas en el JSON (LAZY + @JsonIgnore) ----

    // Administrador que hizo el cambio. Oculto: el historial lo ve cualquiera
    // con el ticket y no debe exponer CI ni correo. Si el panel admin necesita
    // el nombre, devolverlo con un DTO.
    @JsonIgnore
    @JoinColumn(name = "id_admin", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario admin;

    // Solicitud dueña del registro. Oculto para evitar el bucle
    // Solicitud -> historial -> Solicitud en el JSON.
    @JsonIgnore
    @JoinColumn(name = "id_solicitud", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Solicitud solicitud;

    public HistorialEstados() {
    }

    public HistorialEstados(Long id) {
        this.id = id;
    }

    public HistorialEstados(Solicitud solicitud, Estado estadoAnterior, Estado estadoNuevo,
            Prioridad prioridad, String comentario, Usuario admin) {
        this.solicitud = solicitud;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.prioridad = prioridad;
        this.comentario = comentario;
        this.admin = admin;
    }

    // Hibernate inserta NULL explícito si el campo está vacío, por eso el
    // DEFAULT current_timestamp() de MySQL no se aplica: lo ponemos aquí.
    @PrePersist
    protected void prePersist() {
        if (fechaCambio == null) {
            fechaCambio = new Date();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public Estado getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(Estado estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public Estado getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(Estado estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HistorialEstados)) {
            return false;
        }
        HistorialEstados other = (HistorialEstados) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.fpune.portal.entity.HistorialEstados[ id=" + id + " ]";
    }

}
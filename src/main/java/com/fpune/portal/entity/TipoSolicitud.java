/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author marti
 */
@Entity
@Table(name = "tipos_solicitud")
@NamedQueries({
    @NamedQuery(name = "TipoSolicitud.findAll", query = "SELECT t FROM TipoSolicitud t"),
    @NamedQuery(name = "TipoSolicitud.findById", query = "SELECT t FROM TipoSolicitud t WHERE t.id = :id"),
    @NamedQuery(name = "TipoSolicitud.findByNombre", query = "SELECT t FROM TipoSolicitud t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "TipoSolicitud.findByDescripcion", query = "SELECT t FROM TipoSolicitud t WHERE t.descripcion = :descripcion")})
public class TipoSolicitud implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoSolicitud", fetch = FetchType.LAZY)
    private List<Solicitud> solicitudList;

    

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;

    public TipoSolicitud() {
    }

    public TipoSolicitud(Integer id) {
        this.id = id;
    }

    public TipoSolicitud(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoSolicitud)) {
            return false;
        }
        TipoSolicitud other = (TipoSolicitud) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.fpune.portal.entity.TipoSolicitud[ id=" + id + " ]";
    }

    public List<Solicitud> getSolicitudList() {
        return solicitudList;
    }

    public void setSolicitudList(List<Solicitud> solicitudList) {
        this.solicitudList = solicitudList;
    }

   

    
}

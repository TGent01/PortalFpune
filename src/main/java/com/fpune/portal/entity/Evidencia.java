/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author marti
 */
@Entity
@Table(name = "evidencias")
@NamedQueries({
    @NamedQuery(name = "Evidencia.findAll", query = "SELECT e FROM Evidencia e"),
    @NamedQuery(name = "Evidencia.findById", query = "SELECT e FROM Evidencia e WHERE e.id = :id"),
    @NamedQuery(name = "Evidencia.findByNombreArchivo", query = "SELECT e FROM Evidencia e WHERE e.nombreArchivo = :nombreArchivo"),
    @NamedQuery(name = "Evidencia.findByRutaArchivo", query = "SELECT e FROM Evidencia e WHERE e.rutaArchivo = :rutaArchivo"),
    @NamedQuery(name = "Evidencia.findByTipoMime", query = "SELECT e FROM Evidencia e WHERE e.tipoMime = :tipoMime"),
    @NamedQuery(name = "Evidencia.findByTamanoBytes", query = "SELECT e FROM Evidencia e WHERE e.tamanoBytes = :tamanoBytes")})
public class Evidencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "nombre_archivo")
    private String nombreArchivo;
    @Basic(optional = false)
    @Column(name = "ruta_archivo")
    private String rutaArchivo;
    @Basic(optional = false)
    @Column(name = "tipo_mime")
    private String tipoMime;
    @Basic(optional = false)
    @Column(name = "tamano_bytes")
    private int tamanoBytes;
    @JoinColumn(name = "id_solicitud", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Solicitudes solicitudes;

    public Evidencia() {
    }

    public Evidencia(Long id) {
        this.id = id;
    }

    public Evidencia(Long id, String nombreArchivo, String rutaArchivo, String tipoMime, int tamanoBytes) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.tipoMime = tipoMime;
        this.tamanoBytes = tamanoBytes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public int getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(int tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public Solicitudes getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(Solicitudes solicitudes) {
        this.solicitudes = solicitudes;
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
        if (!(object instanceof Evidencia)) {
            return false;
        }
        Evidencia other = (Evidencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.fpune.portal.entity.Evidencia[ id=" + id + " ]";
    }
    
}

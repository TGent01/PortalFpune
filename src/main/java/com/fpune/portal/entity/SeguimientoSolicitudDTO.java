package com.fpune.portal.dtos;

import com.fpune.portal.entity.Solicitud;
import java.time.LocalDate;
import java.util.Date;

/**
 * Lo que ve el solicitante al consultar su solicitud por ticket.
 * Solo datos relevantes para él: sin id interno, sin prioridad, sin datos del admin.
 *
 * @author marti
 */
public class SeguimientoSolicitudDTO {

    private final String codigoTicket;
    private final String asunto;
    private final String tipo;          // Reclamo, Queja, Sugerencia
    private final String categoria;     // Infraestructura, Tecnología, ...
    private final String estado;        // Recibido, En revisión, Resuelto, Rechazado
    private final boolean finalizada;   // true si el estado es final (Resuelto / Rechazado)
    private final LocalDate fechaCreacion;
    private final LocalDate fechaCierre; // null mientras siga abierta

    private SeguimientoSolicitudDTO(Solicitud s) {
        this.codigoTicket = s.getCodigoTicket();
        this.asunto = s.getAsunto();
        this.tipo = s.getTipoSolicitud() != null ? s.getTipoSolicitud().getNombre() : null;
        this.categoria = s.getCategoria() != null ? s.getCategoria().getNombre() : null;
        this.estado = s.getEstado() != null ? s.getEstado().getNombre() : null;
        this.finalizada = s.getEstado() != null && s.getEstado().getEsFinal();
        this.fechaCreacion = aLocalDate(s.getFechaCreacion());
        this.fechaCierre = aLocalDate(s.getFechaCierre());
    }

    /** Convierte la entidad en el DTO de seguimiento. */
    public static SeguimientoSolicitudDTO desde(Solicitud s) {
        return s == null ? null : new SeguimientoSolicitudDTO(s);
    }

    /**
     * Hibernate devuelve las columnas DATE como java.sql.Date, y su toInstant()
     * lanza UnsupportedOperationException. Pasando por java.sql.Date funciona
     * con cualquier Date. En el JSON sale como "2026-11-21".
     */
    private static LocalDate aLocalDate(Date fecha) {
        return fecha == null ? null : new java.sql.Date(fecha.getTime()).toLocalDate();
    }

    public String getCodigoTicket() {
        return codigoTicket;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }
}
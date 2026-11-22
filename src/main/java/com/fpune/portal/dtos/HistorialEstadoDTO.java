package com.fpune.portal.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpune.portal.entity.HistorialEstados;
import com.fpune.portal.entity.Usuario;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Un cambio en la línea de tiempo de una solicitud.
 *
 * - paraSolicitante(): sin datos del administrador (el solicitante no debe ver quién lo atendió).
 * - paraAdmin():      incluye nombre y apellido del administrador que hizo el cambio.
 *
 * @author marti
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // los campos null no aparecen en el JSON
public class HistorialEstadoDTO {

    private final LocalDateTime fecha;        // "2026-11-22T15:47:10"
    private final String estadoAnterior;      // null solo si no había estado
    private final String estadoNuevo;
    private final String prioridad;           // solo si en este cambio se asignó prioridad
    private final String comentario;
    private final String admin;               // solo en paraAdmin()

    private HistorialEstadoDTO(HistorialEstados h, boolean incluirAdmin) {
        this.fecha = aLocalDateTime(h.getFechaCambio());
        this.estadoAnterior = h.getEstadoAnterior() != null ? h.getEstadoAnterior().getNombre() : null;
        this.estadoNuevo = h.getEstadoNuevo() != null ? h.getEstadoNuevo().getNombre() : null;
        this.prioridad = h.getPrioridad() != null ? h.getPrioridad().getNombre() : null;
        this.comentario = h.getComentario();
        Usuario u = incluirAdmin ? h.getAdmin() : null;
        this.admin = u != null ? u.getNombre() + " " + u.getApellido() : null;
    }

    public static HistorialEstadoDTO paraSolicitante(HistorialEstados h) {
        return new HistorialEstadoDTO(h, false);
    }

    /** Requiere que h.getAdmin() venga cargado (la consulta usa JOIN FETCH h.admin). */
    public static HistorialEstadoDTO paraAdmin(HistorialEstados h) {
        return new HistorialEstadoDTO(h, true);
    }

    /** Hibernate devuelve TIMESTAMP como java.sql.Timestamp; esta conversión sirve para cualquier Date. */
    private static LocalDateTime aLocalDateTime(Date fecha) {
        return fecha == null ? null : new java.sql.Timestamp(fecha.getTime()).toLocalDateTime().withNano(0);
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getComentario() {
        return comentario;
    }

    public String getAdmin() {
        return admin;
    }
}
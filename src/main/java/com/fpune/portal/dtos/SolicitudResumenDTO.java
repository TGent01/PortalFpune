package com.fpune.portal.dtos;

import java.time.LocalDate;

/**
 * Los datos mínimos de UNA solicitud que hacen falta para calcular estadísticas.
 * Uso interno (no se devuelve en ningún endpoint): no tiene asunto, descripción,
 * ubicación ni ticket.
 *
 * @author marti
 */
public class SolicitudResumenDTO {

    private final Long id;
    private final String estado;
    private final boolean estadoFinal;
    private final String categoria;
    private final String tipo;
    private final String prioridad;       // null = sin asignar
    private final LocalDate fechaCreacion;
    private final LocalDate fechaCierre;  // null = abierta

    public SolicitudResumenDTO(Long id, String estado, boolean estadoFinal, String categoria, String tipo,
            String prioridad, LocalDate fechaCreacion, LocalDate fechaCierre) {
        this.id = id;
        this.estado = estado;
        this.estadoFinal = estadoFinal;
        this.categoria = categoria;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
    }

    public Long getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isEstadoFinal() {
        return estadoFinal;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }
}
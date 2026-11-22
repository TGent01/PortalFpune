package com.fpune.portal.dtos;

/**
 * Cuerpo de PUT /solicitudes/{id}/estado (solo admin).
 *
 * {
 *   "idEstado": 3,          obligatorio (puede ser el mismo estado si solo cambia prioridad o comenta)
 *   "idPrioridad": 2,       opcional: si no viene se mantiene la actual
 *   "comentario": "..."     opcional, OBLIGATORIO al pasar a un estado final (Resuelto / Rechazado)
 * }
 *
 * @author marti
 */
public class CambioEstadoRequestDTO {

    private Integer idEstado;
    private Integer idPrioridad;
    private String comentario;

    public CambioEstadoRequestDTO() {
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public Integer getIdPrioridad() {
        return idPrioridad;
    }

    public void setIdPrioridad(Integer idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
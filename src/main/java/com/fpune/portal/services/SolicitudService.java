/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.dtos.CambioEstadoRequestDTO;
import com.fpune.portal.dtos.HistorialEstadoDTO;
import com.fpune.portal.dtos.SeguimientoSolicitudDTO;
import com.fpune.portal.entity.Solicitud;
import java.util.ArrayList;

/**
 *
 * @author marti
 */
public interface SolicitudService {
    public ArrayList<Solicitud> listarSolicitud();
    public Solicitud obtenerSolicitud(long id);
    public int crearSolicitud(Solicitud solicitud);
    public int eliminarSolicitud(long id);
    public SeguimientoSolicitudDTO consultarPorTicket(String codigoTicket);
    public int cambiarEstado(long idSolicitud, CambioEstadoRequestDTO cambio, String correoAdmin);
    public ArrayList<Solicitud> listarSolicitudes(Integer idEstado, Integer idCategoria,
        Integer idTipo, Integer idPrioridad);

    public ArrayList<HistorialEstadoDTO> listarHistorial(long idSolicitud);
}

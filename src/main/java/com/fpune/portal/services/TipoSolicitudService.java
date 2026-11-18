/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.entity.TipoSolicitud;
import java.util.ArrayList;

/**
 * Tipos de solicitud: Reclamo, Queja, Sugerencia.
 *
 * @author marti
 */
public interface TipoSolicitudService {

    public ArrayList<TipoSolicitud> listarTipos();

    public TipoSolicitud obtenerTipo(int id);

    public int insertarTipo(TipoSolicitud t);

    /** Modifica nombre y/o descripción. Los campos que no vengan en el JSON se mantienen. */
    public int modificarTipo(TipoSolicitud t);
}

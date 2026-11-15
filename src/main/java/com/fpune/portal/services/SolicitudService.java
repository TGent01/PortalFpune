/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.entity.Solicitud;
import java.util.ArrayList;

/**
 *
 * @author marti
 */
public interface SolicitudService {
    public ArrayList<Solicitud> listarSolicitud();
    public Solicitud obtenerSolicitud(long id);
    public int eliminarSolicitud(long id);
}

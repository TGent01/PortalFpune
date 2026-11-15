
package com.fpune.portal.services;

import com.fpune.portal.controllers.SolicitudJpaController;
import com.fpune.portal.entity.Solicitud;
import java.util.ArrayList;
import org.springframework.stereotype.Service;


@Service
public class SolicitudServiceMysqlImpl implements SolicitudService{

    @Override
    public ArrayList<Solicitud> listarSolicitud() {
        return new ArrayList<Solicitud>(new SolicitudJpaController().findSolicitudEntities());

    }

    @Override
    public Solicitud obtenerSolicitud(long id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int eliminarSolicitud(long id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}


package com.fpune.portal.services;

import com.fpune.portal.Util.TicketGenerator;
import com.fpune.portal.controllers.SolicitudJpaController;
import com.fpune.portal.entity.Estado;
import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.status.SolicitudStatusEnum;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.stereotype.Service;


@Service
public class SolicitudServiceMysqlImpl implements SolicitudService{
    
    
    SolicitudStatusEnum status;
    SolicitudJpaController solicitudJpaController=new SolicitudJpaController();
   

    @Override
    public ArrayList<Solicitud> listarSolicitud() {
        return new ArrayList<Solicitud>(solicitudJpaController.findSolicitudEntities());

    }

    @Override
    public Solicitud obtenerSolicitud(long id) {
        return solicitudJpaController.findSolicitud(id);
    }

    @Override
    public int crearSolicitud(Solicitud solicitud) {
        if(solicitud.getAsunto()==null && solicitud.getCategoria()==null && solicitud.getDescripcion()==null && solicitud.getTipoSolicitud()==null){
            return status.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
        
        LocalDate localDate=LocalDate.now();
        //Esto hai que cambiar luego cuando este el servicio Estado
        solicitud.setEstado(new Estado(1,"Recibido",false,1));
        Date fecha_de_hoy = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fecha_de_cierre= Date.from(localDate.plusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant());
        solicitud.setFechaCreacion(fecha_de_hoy);
        solicitud.setFechaCierre(fecha_de_cierre);
        solicitud.setCodigoTicket(TicketGenerator.generarUnico(codigo -> solicitudJpaController.existeTicket(codigo)));
        solicitud.setPrioridad(new Prioridad(1,"Baja",1));
        try {
            new SolicitudJpaController().create(solicitud);
            return status.SOLICITUD_CREADO.getCodigo();
        } catch (Exception e) {
            
            System.out.println("error "+e.getMessage());
            return status.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
    }
    @Override
    public int eliminarSolicitud(long id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}

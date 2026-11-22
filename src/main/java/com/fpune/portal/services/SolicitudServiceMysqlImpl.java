
package com.fpune.portal.services;

import com.fpune.portal.controllers.EvidenciaJpaController;
import com.fpune.portal.util.TicketGenerator;
import com.fpune.portal.controllers.SolicitudJpaController;
import com.fpune.portal.controllers.exceptions.IllegalOrphanException;
import com.fpune.portal.controllers.exceptions.NonexistentEntityException;
import com.fpune.portal.entity.Estado;
import com.fpune.portal.entity.Evidencia;
import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.exceptions.StorageException;
import com.fpune.portal.status.SolicitudStatusEnum;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SolicitudServiceMysqlImpl implements SolicitudService{
    
    
    SolicitudStatusEnum status;
    SolicitudJpaController solicitudJpaController=new SolicitudJpaController();
    
    private final EvidenciaJpaController evidenciaJpaController = new EvidenciaJpaController();

    @Autowired
    private FileStorageService fileStorageService;
   

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
        if(solicitud==null || estaVacio(solicitud.getAsunto()) || solicitud.getCategoria()==null || estaVacio(solicitud.getDescripcion()) || solicitud.getTipoSolicitud()==null){
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
        solicitud.setId(null);
        LocalDate localDate=LocalDate.now();
        //Esto hai que cambiar luego cuando este el servicio Estado
        solicitud.setEstado(new Estado(1,"Recibido",false,1));
        Date fecha_de_hoy = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        //Date fecha_de_cierre= Date.from(localDate.plusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant());
        solicitud.setFechaCreacion(fecha_de_hoy);
        solicitud.setFechaCierre(null);
        solicitud.setCodigoTicket(TicketGenerator.generarUnico(codigo -> solicitudJpaController.existeTicket(codigo)));
        solicitud.setPrioridad(new Prioridad(1,"Baja",1));
        try {
            new SolicitudJpaController().create(solicitud);
            return SolicitudStatusEnum.SOLICITUD_CREADO.getCodigo();
        } catch (Exception e) {
            
            System.out.println("error "+e.getMessage());
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
    }
    @Override
    public int eliminarSolicitud(long id) {
        if (solicitudJpaController.findSolicitud(id) == null) {
        return SolicitudStatusEnum.SOLICITUD_NO_EXISTE.getCodigo();
        }
        // 1. Guardar las rutas de los archivos antes de borrar
        List<Evidencia> evidencias = evidenciaJpaController.findEvidenciasBySolicitud(id);
        // 2. Borrar en la BD (la solicitud + evidencias + historial)
        try {
            solicitudJpaController.destroy(id);
        } catch (NonexistentEntityException ex) {
            return SolicitudStatusEnum.SOLICITUD_NO_EXISTE.getCodigo();
        } catch (RuntimeException ex) {
            System.out.println("error " + ex.getMessage());
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
        // 3. Borrar los archivos del disco
        for (Evidencia ev : evidencias) {
            try {
                fileStorageService.eliminar(ev.getRutaArchivo());
            } catch (StorageException ex) {
                System.out.println("No se pudo borrar el archivo " + ev.getRutaArchivo());
            }
        }
        return SolicitudStatusEnum.SOLICITUD_ELIMINADO.getCodigo();
    }
    private static boolean estaVacio(String s) {
        return s == null || s.isBlank();
    }
}

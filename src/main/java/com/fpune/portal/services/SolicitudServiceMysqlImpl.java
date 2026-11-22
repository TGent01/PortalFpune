
package com.fpune.portal.services;

import com.fpune.portal.controllers.EstadoJpaController;
import com.fpune.portal.controllers.EvidenciaJpaController;
import com.fpune.portal.controllers.HistorialEstadosJpaController;
import com.fpune.portal.controllers.PrioridadJpaController;
import com.fpune.portal.util.TicketGenerator;
import com.fpune.portal.controllers.SolicitudJpaController;
import com.fpune.portal.controllers.UsuarioJpaController;
import com.fpune.portal.controllers.exceptions.IllegalOrphanException;
import com.fpune.portal.controllers.exceptions.NonexistentEntityException;
import com.fpune.portal.dtos.CambioEstadoRequestDTO;
import com.fpune.portal.dtos.HistorialEstadoDTO;
import com.fpune.portal.dtos.SeguimientoSolicitudDTO;
import com.fpune.portal.entity.Estado;
import com.fpune.portal.entity.Evidencia;
import com.fpune.portal.entity.HistorialEstados;
import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.entity.Usuario;
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
    private final HistorialEstadosJpaController historialJpaController = new HistorialEstadosJpaController();
    
    private final EvidenciaJpaController evidenciaJpaController = new EvidenciaJpaController();
    private final EstadoJpaController estadoJpaC=new EstadoJpaController();
    

    private final PrioridadJpaController prioridadJpaController = new PrioridadJpaController();
    private final UsuarioJpaController usuarioJpaController = new UsuarioJpaController();
    
    private static final int max_comentario=1000;

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
        solicitud.setEstado(estadoJpaC.findEstadoInicial());
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

    @Override
    public SeguimientoSolicitudDTO consultarPorTicket(String codigoTicket) {
            // findSolicitudByCodigoTicket ya normaliza (espacios y mayúsculas)
        Solicitud s = solicitudJpaController.findSolicitudByCodigoTicket(codigoTicket);
        return SeguimientoSolicitudDTO.desde(s);

    }
    
    @Override
    public int cambiarEstado(long idSolicitud, CambioEstadoRequestDTO cambio, String correoAdmin) {
        // 1. Datos mínimos
        if (cambio == null || cambio.getIdEstado() == null) {
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
        String comentario = estaVacio(cambio.getComentario()) ? null : cambio.getComentario().trim();
        if (comentario != null && comentario.length() > max_comentario) {
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }

        // 2. La solicitud existe y sigue abierta
        Solicitud solicitud = solicitudJpaController.findSolicitud(idSolicitud);
        if (solicitud == null) {
            return SolicitudStatusEnum.SOLICITUD_NO_EXISTE.getCodigo();
        }
        if (solicitud.getEstado() != null && solicitud.getEstado().getEsFinal()) {
            return SolicitudStatusEnum.SOLICITUD_CERRADA.getCodigo();
        }

        // 3. El estado y la prioridad pedidos existen
        Estado estadoNuevo = estadoJpaC.findEstado(cambio.getIdEstado());
        if (estadoNuevo == null) {
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }
        if (cambio.getIdPrioridad() != null && prioridadJpaController.findPrioridad(cambio.getIdPrioridad()) == null) {
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }

        // 4. Para cerrar (Resuelto / Rechazado) hay que explicar el motivo al solicitante
        if (estadoNuevo.getEsFinal() && comentario == null) {
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }

        // 5. Tiene que cambiar algo: el estado, la prioridad o al menos agregar un comentario
        boolean mismoEstado = solicitud.getEstado() != null
                && solicitud.getEstado().getId().equals(estadoNuevo.getId());
        boolean mismaPrioridad = cambio.getIdPrioridad() == null
                || (solicitud.getPrioridad() != null
                && solicitud.getPrioridad().getId().equals(cambio.getIdPrioridad()));
        if (mismoEstado && mismaPrioridad && comentario == null) {
            return SolicitudStatusEnum.SOLICITUD_SIN_CAMBIOS.getCodigo();
        }

        // 6. El admin que hace el cambio (sale del token)
        Usuario admin = usuarioJpaController.findUsuarioByCorreo(correoAdmin);
        if (admin == null) {
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        }

        // 7. Guardar todo en una transacción
        try {
            solicitudJpaController.cambiarEstado(idSolicitud, estadoNuevo.getId(),
                    cambio.getIdPrioridad(), comentario, admin.getId());
            return SolicitudStatusEnum.SOLICITUD_EDITADO.getCodigo();
        } catch (RuntimeException ex) {
            System.out.println("error " + ex.getMessage());
            return SolicitudStatusEnum.SOLICITUD_NO_PROCESABLE.getCodigo();
        } catch (NonexistentEntityException ex) {
        return SolicitudStatusEnum.SOLICITUD_NO_EXISTE.getCodigo();

        }
    }

    @Override
    public ArrayList<HistorialEstadoDTO> listarHistorial(long idSolicitud) {
        ArrayList<HistorialEstadoDTO> lista = new ArrayList<>();
        for (HistorialEstados h : historialJpaController.findBySolicitud(idSolicitud)) {
            lista.add(HistorialEstadoDTO.paraAdmin(h));
        }
        return lista;
    }
}


package com.fpune.portal.RestControllers;

import com.fpune.portal.dtos.CambioEstadoRequestDTO;
import com.fpune.portal.dtos.HistorialEstadoDTO;
import com.fpune.portal.dtos.SeguimientoSolicitudDTO;
import com.fpune.portal.entity.Evidencia;
import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.services.EvidenciaService;
import com.fpune.portal.services.SolicitudService;
import com.fpune.portal.status.EvidenciaStatusEnum;
import com.fpune.portal.status.SolicitudStatusEnum;
import java.net.URI;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/solicitudes")
public class SolicitudRestController {
    
    @Autowired
    SolicitudService solicitudService;
    
    
    @Autowired
    EvidenciaService evidenciaService;
    
    SolicitudStatusEnum status;
    
    /**
 * Panel admin. Filtros opcionales, ej:
 * GET /solicitudes?estado=2&categoria=3&tipo=1&prioridad=3
 */
    @GetMapping
    public ResponseEntity<ArrayList<Solicitud>> getSolicitudes(
            @RequestParam(name = "estado", required = false) Integer idEstado,
            @RequestParam(name = "categoria", required = false) Integer idCategoria,
            @RequestParam(name = "tipo", required = false) Integer idTipo,
            @RequestParam(name = "prioridad", required = false) Integer idPrioridad) {
        return ResponseEntity.ok(solicitudService.listarSolicitudes(idEstado, idCategoria, idTipo, idPrioridad));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> getSolicitud(@PathVariable long id){
        
        Solicitud solicitud=solicitudService.obtenerSolicitud(id);
        if(solicitud==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{
        return ResponseEntity.ok(solicitudService.obtenerSolicitud(id));
        }
    }
    
     @GetMapping("/{id}/historial")
    public ResponseEntity<ArrayList<HistorialEstadoDTO>> getHistorial(@PathVariable long id) {
        if (solicitudService.obtenerSolicitud(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(solicitudService.listarHistorial(id));
    }
    
    @PostMapping
    public ResponseEntity<Solicitud> postSolicitud(@RequestBody Solicitud solicitud){
        int r=solicitudService.crearSolicitud(solicitud);
        if(r==status.SOLICITUD_CREADO.getCodigo()){
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(solicitud.getId())
                .toUri();
            return ResponseEntity.created(location).body(solicitud);
        }else{
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSolicitudes(@PathVariable long id){
        
        Solicitud solicitud=solicitudService.obtenerSolicitud(id);
        if(solicitud==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La solicitud no existe"); 
        }
        
        int r=solicitudService.eliminarSolicitud(id);
        if(r==SolicitudStatusEnum.SOLICITUD_ELIMINADO.getCodigo()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Solicitud eliminada con exito");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> putEstado(@PathVariable long id, @RequestBody CambioEstadoRequestDTO cambio,
            Authentication authentication) {
        int r = solicitudService.cambiarEstado(id, cambio, authentication.getName());
        if (r == SolicitudStatusEnum.SOLICITUD_EDITADO.getCodigo()) {
            return ResponseEntity.ok(solicitudService.obtenerSolicitud(id));
        } else if (r == SolicitudStatusEnum.SOLICITUD_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La solicitud no existe");
        } else if (r == SolicitudStatusEnum.SOLICITUD_CERRADA.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya está cerrada y no admite cambios");
        } else if (r == SolicitudStatusEnum.SOLICITUD_SIN_CAMBIOS.getCodigo()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("No hay cambios: elegí otro estado, otra prioridad o agregá un comentario");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                    "Datos no válidos. Verificá el estado y la prioridad; para Resuelto o Rechazado el comentario es obligatorio");
        }
    }
    
    
    @GetMapping("/ticket/{codigo}")
    public ResponseEntity<SeguimientoSolicitudDTO> getSeguimiento(@PathVariable String codigo) {
        SeguimientoSolicitudDTO seguimiento = solicitudService.consultarPorTicket(codigo);
        if (seguimiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(seguimiento);
}
}

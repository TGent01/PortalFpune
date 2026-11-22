
package com.fpune.portal.RestControllers;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    
    @GetMapping
    public ResponseEntity<ArrayList<Solicitud>> getSolicitudes(){
        return ResponseEntity.ok(solicitudService.listarSolicitud());
    
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
    
    
    @GetMapping("/ticket/{codigo}")
    public ResponseEntity<SeguimientoSolicitudDTO> getSeguimiento(@PathVariable String codigo) {
        SeguimientoSolicitudDTO seguimiento = solicitudService.consultarPorTicket(codigo);
        if (seguimiento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(seguimiento);
}
}

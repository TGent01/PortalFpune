
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.services.SolicitudService;
import com.fpune.portal.status.SolicitudStatusEnum;
import java.net.URI;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    
}

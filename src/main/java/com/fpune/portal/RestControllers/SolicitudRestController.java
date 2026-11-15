
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.services.SolicitudService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/solicitudes")
public class SolicitudRestController {
    
    @Autowired
    SolicitudService solicitudService;
    
    @GetMapping
    public ResponseEntity<ArrayList<Solicitud>> getSolicitudes(){
        return ResponseEntity.ok(solicitudService.listarSolicitud());
    
    }
    
}

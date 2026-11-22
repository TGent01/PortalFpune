/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Evidencia;
import com.fpune.portal.services.EvidenciaService;
import com.fpune.portal.status.EvidenciaStatusEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author marti
 */

@RestController
@RequestMapping("/evidencias")
public class EvidenciaRestController {
    
    @Autowired
    private EvidenciaService evidenciaService;
    
    @PostMapping(value = "/ticket/{codigo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirEvidencias(@PathVariable String codigo,
            @RequestParam("archivos") List<MultipartFile> archivos) {
        int r = evidenciaService.subirEvidencias(codigo, archivos);
        if (r == EvidenciaStatusEnum.EVIDENCIA_SUBIDA.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(evidenciaService.listarPorTicket(codigo));
        } else if (r == EvidenciaStatusEnum.EVIDENCIA_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe una solicitud con ese ticket");
        } else if (r == EvidenciaStatusEnum.EVIDENCIA_SOLICITUD_CERRADA.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya está cerrada, no admite más evidencias");
        } else if (r == EvidenciaStatusEnum.EVIDENCIA_LIMITE_SUPERADO.getCodigo()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Se supera el máximo de archivos por solicitud");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("No se pudieron guardar las evidencias");
        }
    }
}

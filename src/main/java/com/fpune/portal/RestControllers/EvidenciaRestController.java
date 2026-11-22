/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Evidencia;
import com.fpune.portal.services.EvidenciaService;
import com.fpune.portal.status.EvidenciaStatusEnum;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    
     @GetMapping("/ticket/{codigo}")
    public ResponseEntity<ArrayList<Evidencia>> getEvidenciasPorTicket(@PathVariable String codigo) {
        ArrayList<Evidencia> lista = evidenciaService.listarPorTicket(codigo);
        return lista == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(lista);
    }

    @GetMapping("/ticket/{codigo}/{idEvidencia}/archivo")
    public ResponseEntity<Resource> getArchivoPorTicket(@PathVariable String codigo, @PathVariable long idEvidencia) {
        Evidencia ev = evidenciaService.obtenerEvidenciaDeTicket(codigo, idEvidencia);
        return ev == null ? ResponseEntity.notFound().build() : archivo(ev);
    }

    // =====================================================================
    //  ADMINISTRADOR
    // =====================================================================

    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<ArrayList<Evidencia>> getEvidenciasPorSolicitud(@PathVariable long idSolicitud) {
        return ResponseEntity.ok(evidenciaService.listarPorSolicitud(idSolicitud));
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<Resource> getArchivo(@PathVariable long id) {
        Evidencia ev = evidenciaService.obtenerEvidencia(id);
        return ev == null ? ResponseEntity.notFound().build() : archivo(ev);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvidencia(@PathVariable long id) {
        int r = evidenciaService.eliminarEvidencia(id);
        if (r == EvidenciaStatusEnum.EVIDENCIA_ELIMINADA.getCodigo()) {
            return ResponseEntity.noContent().build();
        } else if (r == EvidenciaStatusEnum.EVIDENCIA_NO_EXISTE.getCodigo()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("No se pudo eliminar la evidencia");
    }

    // =====================================================================

    /**
     * Devuelve el archivo con su tipo (image/jpeg, image/png, application/pdf).
     * "inline": el navegador lo muestra (imagen o visor de PDF) en vez de descargarlo.
     */
    private ResponseEntity<Resource> archivo(Evidencia ev) {
        Resource recurso = evidenciaService.obtenerArchivo(ev);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ev.getTipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(ev.getNombreArchivo(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(recurso);
    }
}


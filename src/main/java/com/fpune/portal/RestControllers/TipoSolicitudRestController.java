/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.TipoSolicitud;
import com.fpune.portal.services.TipoSolicitudService;
import com.fpune.portal.status.TipoSolicitudStatusEnum;
import java.net.URI;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * No hay DELETE: las solicitudes referencian el tipo (FK fk_sol_tipo).
 *
 * @author marti
 */
@RestController
@RequestMapping("/tipos-solicitud")
public class TipoSolicitudRestController {

    @Autowired
    private TipoSolicitudService tipoSolicitudService;

    /** Público: para el combo "Tipo" del formulario de solicitud. */
    @GetMapping
    public ResponseEntity<ArrayList<TipoSolicitud>> getTipos() {
        return ResponseEntity.ok(tipoSolicitudService.listarTipos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoSolicitud> getTipo(@PathVariable int id) {
        TipoSolicitud tipo = tipoSolicitudService.obtenerTipo(id);
        return tipo == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(tipo);
    }

    @PostMapping
    public ResponseEntity<?> postTipo(@RequestBody TipoSolicitud tipo) {
        int r = tipoSolicitudService.insertarTipo(tipo);
        if (r == TipoSolicitudStatusEnum.TIPO_CREADO.getCodigo()) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(tipo.getId())
                    .toUri();
            return ResponseEntity.created(location).body(tipo);
        } else if (r == TipoSolicitudStatusEnum.TIPO_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un tipo de solicitud con ese nombre");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos del tipo de solicitud no válidos");
        }
    }

    @PutMapping
    public ResponseEntity<?> putTipo(@RequestBody TipoSolicitud tipo) {
        int r = tipoSolicitudService.modificarTipo(tipo);
        if (r == TipoSolicitudStatusEnum.TIPO_EDITADO.getCodigo()) {
            return ResponseEntity.ok(tipoSolicitudService.obtenerTipo(tipo.getId()));
        } else if (r == TipoSolicitudStatusEnum.TIPO_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El tipo de solicitud no existe");
        } else if (r == TipoSolicitudStatusEnum.TIPO_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un tipo de solicitud con ese nombre");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos del tipo de solicitud no válidos");
        }
    }
}
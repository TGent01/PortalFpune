/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Estado;
import com.fpune.portal.services.EstadoService;
import com.fpune.portal.status.EstadoStatusEnum;
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
 * No hay DELETE: las solicitudes y el historial referencian el estado (FK),
 * y el sistema depende de que existan los estados del flujo.
 *
 * @author marti
 */
@RestController
@RequestMapping("/estados")
public class EstadoRestController {

    @Autowired
    private EstadoService estadoService;

    /** Público: lista ordenada, sirve para dibujar la línea de tiempo del seguimiento. */
    @GetMapping
    public ResponseEntity<ArrayList<Estado>> getEstados() {
        return ResponseEntity.ok(estadoService.listarEstados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estado> getEstado(@PathVariable int id) {
        Estado estado = estadoService.obtenerEstado(id);
        return estado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(estado);
    }

    @PostMapping
    public ResponseEntity<?> postEstado(@RequestBody Estado estado) {
        int r = estadoService.insertarEstado(estado);
        if (r == EstadoStatusEnum.ESTADO_CREADO.getCodigo()) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(estado.getId())
                    .toUri();
            return ResponseEntity.created(location).body(estado);
        } else if (r == EstadoStatusEnum.ESTADO_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un estado con ese nombre u orden");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos del estado no válidos");
        }
    }

    @PutMapping
    public ResponseEntity<?> putEstado(@RequestBody Estado estado) {
        int r = estadoService.modificarEstado(estado);
        if (r == EstadoStatusEnum.ESTADO_EDITADO.getCodigo()) {
            return ResponseEntity.ok(estadoService.obtenerEstado(estado.getId()));
        } else if (r == EstadoStatusEnum.ESTADO_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El estado no existe");
        } else if (r == EstadoStatusEnum.ESTADO_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un estado con ese nombre u orden");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos del estado no válidos");
        }
    }
}
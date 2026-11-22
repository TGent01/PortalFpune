/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.services.PrioridadService;
import com.fpune.portal.status.PrioridadStatusEnum;
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
 * Solo para el administrador. No hay DELETE: las solicitudes y el historial
 * referencian la prioridad (FK).
 *
 * @author marti
 */
@RestController
@RequestMapping("/prioridades")
public class PrioridadRestController {

    @Autowired
    private PrioridadService prioridadService;

    /** Lista ordenada por nivel: para el combo "Asignar prioridad" del panel admin. */
    @GetMapping
    public ResponseEntity<ArrayList<Prioridad>> getPrioridades() {
        return ResponseEntity.ok(prioridadService.listarPrioridades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prioridad> getPrioridad(@PathVariable int id) {
        Prioridad prioridad = prioridadService.obtenerPrioridad(id);
        return prioridad == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(prioridad);
    }

    @PostMapping
    public ResponseEntity<?> postPrioridad(@RequestBody Prioridad prioridad) {
        int r = prioridadService.insertarPrioridad(prioridad);
        if (r == PrioridadStatusEnum.PRIORIDAD_CREADO.getCodigo()) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(prioridad.getId())
                    .toUri();
            return ResponseEntity.created(location).body(prioridad);
        } else if (r == PrioridadStatusEnum.PRIORIDAD_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una prioridad con ese nombre o nivel");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos de la prioridad no válidos");
        }
    }

    @PutMapping
    public ResponseEntity<?> putPrioridad(@RequestBody Prioridad prioridad) {
        int r = prioridadService.modificarPrioridad(prioridad);
        if (r == PrioridadStatusEnum.PRIORIDAD_EDITADO.getCodigo()) {
            return ResponseEntity.ok(prioridadService.obtenerPrioridad(prioridad.getId()));
        } else if (r == PrioridadStatusEnum.PRIORIDAD_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La prioridad no existe");
        } else if (r == PrioridadStatusEnum.PRIORIDAD_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una prioridad con ese nombre o nivel");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos de la prioridad no válidos");
        }
    }
}
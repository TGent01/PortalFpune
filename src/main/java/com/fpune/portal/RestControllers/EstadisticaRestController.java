/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.dtos.EstadisticasDTO;
import com.fpune.portal.services.EstadisticaService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard del administrador.
 *
 * GET /estadisticas                                  → todo el historial
 * GET /estadisticas?desde=2026-11-01&hasta=2026-11-30 → solo ese periodo (por fecha de creación)
 *
 * @author marti
 */
@RestController
@RequestMapping("/estadisticas")
public class EstadisticaRestController {

    @Autowired
    private EstadisticaService estadisticaService;

    @GetMapping
    public ResponseEntity<?> getEstadisticas(
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        EstadisticasDTO estadisticas = estadisticaService.obtenerEstadisticas(desde, hasta);
        return ResponseEntity.ok(estadisticas);
    }
}
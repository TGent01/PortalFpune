/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.dtos.DatosAbiertosDTO;
import com.fpune.portal.services.DatosAbiertosService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Datos abiertos (PÚBLICO, sin token).
 *
 * GET /datos-abiertos                    → JSON con metadatos, distribuciones e indicadores
 * GET /datos-abiertos/solicitudes.csv    → CSV de distribuciones
 * GET /datos-abiertos/indicadores.csv    → CSV de indicadores
 *
 * Todos aceptan ?desde=AAAA-MM-DD&hasta=AAAA-MM-DD (opcionales, por fecha de creación).
 *
 * @author marti
 */
@RestController
@RequestMapping("/datos-abiertos")
public class DatosAbiertosRestController {

    private static final MediaType CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    @Autowired
    private DatosAbiertosService datosAbiertosService;

    @GetMapping
    public ResponseEntity<?> getDatosAbiertos(
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (periodoInvalido(desde, hasta)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        DatosAbiertosDTO datos = datosAbiertosService.obtenerDatosAbiertos(desde, hasta);
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/solicitudes.csv")
    public ResponseEntity<?> getCsvSolicitudes(
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (periodoInvalido(desde, hasta)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        return csv(datosAbiertosService.csvSolicitudes(desde, hasta), "fpune-qrs-solicitudes.csv");
    }

    @GetMapping("/indicadores.csv")
    public ResponseEntity<?> getCsvIndicadores(
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        if (periodoInvalido(desde, hasta)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        return csv(datosAbiertosService.csvIndicadores(desde, hasta), "fpune-qrs-indicadores.csv");
    }

    // =====================================================================

    private static boolean periodoInvalido(LocalDate desde, LocalDate hasta) {
        return desde != null && hasta != null && desde.isAfter(hasta);
    }

    /** "attachment": el navegador lo descarga como archivo con ese nombre. */
    private static ResponseEntity<byte[]> csv(String contenido, String nombreArchivo) {
        return ResponseEntity.ok()
                .contentType(CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(nombreArchivo)
                        .build()
                        .toString())
                .body(contenido.getBytes(StandardCharsets.UTF_8));
    }
}
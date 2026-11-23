/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.dtos.DatosAbiertosDTO;
import com.fpune.portal.util.AnonimizadorDatosAbiertos;
import com.fpune.portal.util.CsvDatosAbiertos;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Reutiliza las estadísticas del admin y les aplica la anonimización.
 * No consulta la base directamente: así lo público nunca puede mostrar más que
 * lo que permite AnonimizadorDatosAbiertos.
 *
 * @author marti
 */
@Service
public class DatosAbiertosServiceImpl implements DatosAbiertosService {

    @Autowired
    private EstadisticaService estadisticaService;

    @Override
    public DatosAbiertosDTO obtenerDatosAbiertos(LocalDate desde, LocalDate hasta) {
        return AnonimizadorDatosAbiertos.anonimizar(estadisticaService.obtenerEstadisticas(desde, hasta));
    }

    @Override
    public String csvSolicitudes(LocalDate desde, LocalDate hasta) {
        return CsvDatosAbiertos.solicitudes(obtenerDatosAbiertos(desde, hasta));
    }

    @Override
    public String csvIndicadores(LocalDate desde, LocalDate hasta) {
        return CsvDatosAbiertos.indicadores(obtenerDatosAbiertos(desde, hasta));
    }
}
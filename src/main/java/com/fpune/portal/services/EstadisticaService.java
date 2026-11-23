/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.dtos.EstadisticasDTO;
import java.time.LocalDate;

/**
 * Módulo de estadísticas (dashboard del administrador) y KPIs de la sección 12.2.
 *
 * @author marti
 */
public interface EstadisticaService {

    /**
     * Estadísticas de las solicitudes creadas en el periodo.
     *
     * @param desde null = desde el inicio
     * @param hasta null = hasta hoy
     */
    public EstadisticasDTO obtenerEstadisticas(LocalDate desde, LocalDate hasta);
}
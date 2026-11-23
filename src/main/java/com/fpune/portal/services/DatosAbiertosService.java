/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.dtos.DatosAbiertosDTO;
import java.time.LocalDate;

/**
 * Datos abiertos: estadísticas públicas y anonimizadas (transparencia activa).
 *
 * @author marti
 */
public interface DatosAbiertosService {

    public DatosAbiertosDTO obtenerDatosAbiertos(LocalDate desde, LocalDate hasta);

    /** CSV de distribuciones (categoría, tipo, estado, mes). */
    public String csvSolicitudes(LocalDate desde, LocalDate hasta);

    /** CSV de indicadores (total, tiempos de resolución y respuesta, evidencias). */
    public String csvIndicadores(LocalDate desde, LocalDate hasta);
}
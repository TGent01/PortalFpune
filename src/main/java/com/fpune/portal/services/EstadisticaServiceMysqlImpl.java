/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.controllers.CategoriaJpaController;
import com.fpune.portal.controllers.EstadisticaJpaController;
import com.fpune.portal.controllers.EstadoJpaController;
import com.fpune.portal.controllers.PrioridadJpaController;
import com.fpune.portal.controllers.TipoSolicitudJpaController;
import com.fpune.portal.dtos.EstadisticasDTO;
import com.fpune.portal.entity.Categoria;
import com.fpune.portal.entity.Estado;
import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.entity.TipoSolicitud;
import com.fpune.portal.util.CalculadoraEstadisticas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author marti
 */
@Service
public class EstadisticaServiceMysqlImpl implements EstadisticaService {

    private final EstadisticaJpaController estadisticaJpaController = new EstadisticaJpaController();
    private final EstadoJpaController estadoJpaController = new EstadoJpaController();
    private final CategoriaJpaController categoriaJpaController = new CategoriaJpaController();
    private final TipoSolicitudJpaController tipoSolicitudJpaController = new TipoSolicitudJpaController();
    private final PrioridadJpaController prioridadJpaController = new PrioridadJpaController();

    @Override
    public EstadisticasDTO obtenerEstadisticas(LocalDate desde, LocalDate hasta) {
        // Catálogos: para que aparezcan también las opciones con 0 solicitudes, en su orden
        List<Estado> listaEstados = new ArrayList<>(estadoJpaController.findEstadoEntities());
        listaEstados.sort(Comparator.comparingInt(Estado::getOrden));          // orden del flujo
        List<String> estados = new ArrayList<>();
        for (Estado e : listaEstados) {
            estados.add(e.getNombre());
        }

        List<String> categorias = new ArrayList<>();
        for (Categoria c : categoriaJpaController.findCategoriasActivas()) { // las inactivas aparecen solo si tienen solicitudes
            categorias.add(c.getNombre());
        }

        List<TipoSolicitud> listaTipos = new ArrayList<>(tipoSolicitudJpaController.findTipoSolicitudEntities());
        listaTipos.sort(Comparator.comparing(TipoSolicitud::getId));
        List<String> tipos = new ArrayList<>();
        for (TipoSolicitud t : listaTipos) {
            tipos.add(t.getNombre());
        }

        List<Prioridad> listaPrioridades = new ArrayList<>(prioridadJpaController.findPrioridadEntities());
        listaPrioridades.sort(Comparator.comparingInt(Prioridad::getNivel));   // Baja, Media, Alta
        List<String> prioridades = new ArrayList<>();
        for (Prioridad p : listaPrioridades) {
            prioridades.add(p.getNombre());
        }

        return CalculadoraEstadisticas.calcular(
                estadisticaJpaController.findResumenSolicitudes(desde, hasta),
                estadisticaJpaController.findPrimerCambioPorSolicitud(),
                estadisticaJpaController.findCantidadEvidenciasPorSolicitud(),
                estados, categorias, tipos, prioridades,
                estadisticaJpaController.countUsuariosHabilitados(),
                desde, hasta);
    }
}
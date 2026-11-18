/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.controllers.EstadoJpaController;
import com.fpune.portal.entity.Estado;
import com.fpune.portal.status.EstadoStatusEnum;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *
 * @author marti
 */
@Service
public class EstadoServiceMysqlImpl implements EstadoService {

    // Largo máximo según la tabla estados
    private static final int MAX_NOMBRE = 50;

    private final EstadoJpaController estadoJpaController = new EstadoJpaController();

    @Override
    public ArrayList<Estado> listarEstados() {
        return new ArrayList<>(estadoJpaController.findEstadoEntities());
    }

    @Override
    public Estado obtenerEstado(int id) {
        return estadoJpaController.findEstado(id);
    }

    @Override
    public Estado obtenerEstadoInicial() {
        return estadoJpaController.findEstadoInicial();
    }

    @Override
    public int insertarEstado(Estado e) {
        if (e == null || estaVacio(e.getNombre())) {
            return EstadoStatusEnum.ESTADO_NO_PROCESABLE.getCodigo();
        }
        e.setNombre(e.getNombre().trim());
        if (!datosValidos(e)) {
            return EstadoStatusEnum.ESTADO_NO_PROCESABLE.getCodigo();
        }
        // nombre es UNIQUE en la base; orden no, pero lo exigimos para que el flujo no sea ambiguo
        if (estadoJpaController.findEstadoByNombre(e.getNombre()) != null
                || estadoJpaController.findEstadoByOrden(e.getOrden()) != null) {
            return EstadoStatusEnum.ESTADO_YA_EXISTE.getCodigo();
        }
        e.setId(null); // lo genera la base
        try {
            estadoJpaController.create(e);
            return EstadoStatusEnum.ESTADO_CREADO.getCodigo();
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return EstadoStatusEnum.ESTADO_NO_PROCESABLE.getCodigo();
        }
    }

    @Override
    public int modificarEstado(Estado e) {
        if (e == null || e.getId() == null || estaVacio(e.getNombre())) {
            return EstadoStatusEnum.ESTADO_NO_PROCESABLE.getCodigo();
        }
        if (estadoJpaController.findEstado(e.getId()) == null) {
            return EstadoStatusEnum.ESTADO_NO_EXISTE.getCodigo();
        }
        e.setNombre(e.getNombre().trim());
        if (!datosValidos(e)) {
            return EstadoStatusEnum.ESTADO_NO_PROCESABLE.getCodigo();
        }
        // Nombre y orden no pueden repetirse con OTRO estado
        Estado mismoNombre = estadoJpaController.findEstadoByNombre(e.getNombre());
        Estado mismoOrden = estadoJpaController.findEstadoByOrden(e.getOrden());
        if ((mismoNombre != null && !mismoNombre.getId().equals(e.getId()))
                || (mismoOrden != null && !mismoOrden.getId().equals(e.getId()))) {
            return EstadoStatusEnum.ESTADO_YA_EXISTE.getCodigo();
        }
        try {
            estadoJpaController.edit(e);
            return EstadoStatusEnum.ESTADO_EDITADO.getCodigo();
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return EstadoStatusEnum.ESTADO_NO_PROCESABLE.getCodigo();
        }
    }

    // ------------------------------------------------------------------ helpers

    private static boolean estaVacio(String s) {
        return s == null || s.isBlank();
    }

    /** Nombre dentro del largo de la columna y orden positivo. */
    private static boolean datosValidos(Estado e) {
        return e.getNombre().length() <= MAX_NOMBRE && e.getOrden() > 0;
    }
}
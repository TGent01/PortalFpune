/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.controllers.PrioridadJpaController;
import com.fpune.portal.entity.Prioridad;
import com.fpune.portal.status.PrioridadStatusEnum;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *
 * @author marti
 */
@Service
public class PrioridadServiceMysqlImpl implements PrioridadService {

    // Largo máximo según la tabla prioridades
    private static final int MAX_NOMBRE = 50;

    private final PrioridadJpaController prioridadJpaController = new PrioridadJpaController();

    @Override
    public ArrayList<Prioridad> listarPrioridades() {
        return new ArrayList<>(prioridadJpaController.findPrioridadEntities());
    }

    @Override
    public Prioridad obtenerPrioridad(int id) {
        return prioridadJpaController.findPrioridad(id);
    }

    @Override
    public int insertarPrioridad(Prioridad p) {
        if (p == null || estaVacio(p.getNombre())) {
            return PrioridadStatusEnum.PRIORIDAD_NO_PROCESABLE.getCodigo();
        }
        p.setNombre(p.getNombre().trim());
        if (!datosValidos(p)) {
            return PrioridadStatusEnum.PRIORIDAD_NO_PROCESABLE.getCodigo();
        }
        // nombre es UNIQUE en la base; nivel no, pero lo exigimos para poder ordenar sin ambigüedad
        if (prioridadJpaController.findPrioridadByNombre(p.getNombre()) != null
                || prioridadJpaController.findPrioridadByNivel(p.getNivel()) != null) {
            return PrioridadStatusEnum.PRIORIDAD_YA_EXISTE.getCodigo();
        }
        p.setId(null); // lo genera la base
        try {
            prioridadJpaController.create(p);
            return PrioridadStatusEnum.PRIORIDAD_CREADO.getCodigo();
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return PrioridadStatusEnum.PRIORIDAD_NO_PROCESABLE.getCodigo();
        }
    }

    @Override
    public int modificarPrioridad(Prioridad p) {
        if (p == null || p.getId() == null || estaVacio(p.getNombre())) {
            return PrioridadStatusEnum.PRIORIDAD_NO_PROCESABLE.getCodigo();
        }
        if (prioridadJpaController.findPrioridad(p.getId()) == null) {
            return PrioridadStatusEnum.PRIORIDAD_NO_EXISTE.getCodigo();
        }
        p.setNombre(p.getNombre().trim());
        if (!datosValidos(p)) {
            return PrioridadStatusEnum.PRIORIDAD_NO_PROCESABLE.getCodigo();
        }
        // Nombre y nivel no pueden repetirse con OTRA prioridad
        Prioridad mismoNombre = prioridadJpaController.findPrioridadByNombre(p.getNombre());
        Prioridad mismoNivel = prioridadJpaController.findPrioridadByNivel(p.getNivel());
        if ((mismoNombre != null && !mismoNombre.getId().equals(p.getId()))
                || (mismoNivel != null && !mismoNivel.getId().equals(p.getId()))) {
            return PrioridadStatusEnum.PRIORIDAD_YA_EXISTE.getCodigo();
        }
        try {
            prioridadJpaController.edit(p);
            return PrioridadStatusEnum.PRIORIDAD_EDITADO.getCodigo();
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return PrioridadStatusEnum.PRIORIDAD_NO_PROCESABLE.getCodigo();
        }
    }

    // ------------------------------------------------------------------ helpers

    private static boolean estaVacio(String s) {
        return s == null || s.isBlank();
    }

    /** Nombre dentro del largo de la columna y nivel positivo. */
    private static boolean datosValidos(Prioridad p) {
        return p.getNombre().length() <= MAX_NOMBRE && p.getNivel() > 0;
    }
}
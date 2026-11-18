/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.entity.Prioridad;
import java.util.ArrayList;

/**
 * Prioridades que el administrador asigna a una solicitud (Baja, Media, Alta).
 *
 * @author marti
 */
public interface PrioridadService {

    /** Todas las prioridades ordenadas por nivel (de menor a mayor). */
    public ArrayList<Prioridad> listarPrioridades();

    public Prioridad obtenerPrioridad(int id);

    public int insertarPrioridad(Prioridad p);

    /**
     * Modifica una prioridad. Como nivel es primitivo, el JSON debe traer
     * nombre y nivel; si falta nivel llegaría 0 y se rechaza.
     */
    public int modificarPrioridad(Prioridad p);
}
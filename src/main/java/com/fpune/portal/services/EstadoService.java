/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.entity.Estado;
import java.util.ArrayList;

/**
 * Estados del flujo de una solicitud (Recibido, En revisión, Resuelto, Rechazado).
 *
 * @author marti
 */
public interface EstadoService {

    /** Todos los estados ordenados por "orden" (el orden del flujo). */
    public ArrayList<Estado> listarEstados();

    public Estado obtenerEstado(int id);

    /** El estado con menor "orden": el que se asigna a toda solicitud nueva. */
    public Estado obtenerEstadoInicial();

    public int insertarEstado(Estado e);

    /**
     * Modifica un estado. Como esFinal y orden son primitivos, el JSON debe traer
     * los TRES campos (nombre, esFinal, orden); si falta alguno llegaría false/0.
     */
    public int modificarEstado(Estado e);
}
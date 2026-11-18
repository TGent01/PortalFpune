/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.entity.Categoria;
import java.util.ArrayList;

/**
 *
 * @author marti
 */
public interface CategoriaService {

    /** Todas las categorías (activas e inactivas). Para el panel admin. */
    public ArrayList<Categoria> listarCategorias();

    /** Solo las activas. Para el combo del formulario de solicitud. */
    public ArrayList<Categoria> listarCategoriasActivas();

    public Categoria obtenerCategoria(int id);

    public int insertarCategoria(Categoria c);

    /** Modifica nombre y/o descripción. NO cambia isActiva (para eso está cambiarEstadoCategoria). */
    public int modificarCategoria(Categoria c);

    /**
     * Activa o desactiva una categoría. Las categorías no se borran porque
     * las solicitudes existentes las referencian (FK fk_sol_categoria).
     */
    public int cambiarEstadoCategoria(int id, boolean activa);
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.controllers.CategoriaJpaController;
import com.fpune.portal.entity.Categoria;
import com.fpune.portal.status.CategoriaStatusEnum;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *
 * @author marti
 */
@Service
public class CategoriaServiceMysqlImpl implements CategoriaService {

    // Largos máximos según la tabla categorias
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_DESCRIPCION = 255;

    private final CategoriaJpaController categoriaJpaController = new CategoriaJpaController();

    @Override
    public ArrayList<Categoria> listarCategorias() {
        return new ArrayList<>(categoriaJpaController.findCategoriaEntities());
    }

    @Override
    public ArrayList<Categoria> listarCategoriasActivas() {
        return new ArrayList<>(categoriaJpaController.findCategoriasActivas());
    }

    @Override
    public Categoria obtenerCategoria(int id) {
        return categoriaJpaController.findCategoria(id);
    }

    @Override
    public int insertarCategoria(Categoria c) {
        if (c == null || estaVacio(c.getNombre())) {
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
        c.setNombre(c.getNombre().trim());
        c.setDescripcion(limpiar(c.getDescripcion()));
        if (!largosValidos(c)) {
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
        // El nombre es UNIQUE en la base (uk_categoria_nombre)
        if (categoriaJpaController.findCategoriaByNombre(c.getNombre()) != null) {
            return CategoriaStatusEnum.CATEGORIA_YA_EXISTE.getCodigo();
        }
        c.setId(null);        // el id lo genera la base
        c.setIsActiva(true);  // toda categoría nueva arranca activa
        try {
            categoriaJpaController.create(c);
            return CategoriaStatusEnum.CATEGORIA_CREADO.getCodigo();
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
    }

    @Override
    public int modificarCategoria(Categoria c) {
        if (c == null || c.getId() == null) {
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
        Categoria old = categoriaJpaController.findCategoria(c.getId());
        if (old == null) {
            return CategoriaStatusEnum.CATEGORIA_NO_EXISTE.getCodigo();
        }
        // Nombre: si no viene se mantiene; si viene vacío es un error
        if (c.getNombre() == null) {
            c.setNombre(old.getNombre());
        } else if (estaVacio(c.getNombre())) {
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        } else {
            c.setNombre(c.getNombre().trim());
        }
        // Descripción: si no viene se mantiene
        c.setDescripcion(c.getDescripcion() == null ? old.getDescripcion() : limpiar(c.getDescripcion()));
        // isActiva es boolean primitivo: si el JSON no lo trae llega false y
        // desactivaría la categoría sin querer. Por eso aquí siempre se conserva.
        c.setIsActiva(old.getIsActiva());

        if (!largosValidos(c)) {
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
        // Si cambia el nombre, que no lo tenga otra categoría
        Categoria mismoNombre = categoriaJpaController.findCategoriaByNombre(c.getNombre());
        if (mismoNombre != null && !mismoNombre.getId().equals(c.getId())) {
            return CategoriaStatusEnum.CATEGORIA_YA_EXISTE.getCodigo();
        }
        try {
            categoriaJpaController.edit(c);
            return CategoriaStatusEnum.CATEGORIA_EDITADO.getCodigo();
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
    }

    @Override
    public int cambiarEstadoCategoria(int id, boolean activa) {
        Categoria c = categoriaJpaController.findCategoria(id);
        if (c == null) {
            return CategoriaStatusEnum.CATEGORIA_NO_EXISTE.getCodigo();
        }
        c.setIsActiva(activa);
        try {
            categoriaJpaController.edit(c);
            return CategoriaStatusEnum.CATEGORIA_EDITADO.getCodigo();
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return CategoriaStatusEnum.CATEGORIA_NO_PROCESABLE.getCodigo();
        }
    }

    // ------------------------------------------------------------------ helpers

    private static boolean estaVacio(String s) {
        return s == null || s.isBlank();
    }

    /** Quita espacios; una descripción vacía se guarda como null. */
    private static String limpiar(String s) {
        return estaVacio(s) ? null : s.trim();
    }

    private static boolean largosValidos(Categoria c) {
        return c.getNombre().length() <= MAX_NOMBRE
                && (c.getDescripcion() == null || c.getDescripcion().length() <= MAX_DESCRIPCION);
    }
}
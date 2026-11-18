/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.controllers.TipoSolicitudJpaController;
import com.fpune.portal.entity.TipoSolicitud;
import com.fpune.portal.status.TipoSolicitudStatusEnum;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *
 * @author marti
 */
@Service
public class TipoSolicitudServiceMysqlImpl implements TipoSolicitudService {

    // Largos máximos según la tabla tipos_solicitud
    private static final int MAX_NOMBRE = 50;
    private static final int MAX_DESCRIPCION = 255;

    private final TipoSolicitudJpaController tipoSolicitudJpaController = new TipoSolicitudJpaController();

    @Override
    public ArrayList<TipoSolicitud> listarTipos() {
        return new ArrayList<>(tipoSolicitudJpaController.findTipoSolicitudEntities());
    }

    @Override
    public TipoSolicitud obtenerTipo(int id) {
        return tipoSolicitudJpaController.findTipoSolicitud(id);
    }

    @Override
    public int insertarTipo(TipoSolicitud t) {
        if (t == null || estaVacio(t.getNombre())) {
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
        }
        t.setNombre(t.getNombre().trim());
        t.setDescripcion(limpiar(t.getDescripcion()));
        if (!largosValidos(t)) {
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
        }
        // El nombre es UNIQUE en la base (uk_tipo_nombre)
        if (tipoSolicitudJpaController.findTipoSolicitudByNombre(t.getNombre()) != null) {
            return TipoSolicitudStatusEnum.TIPO_YA_EXISTE.getCodigo();
        }
        t.setId(null); // lo genera la base
        try {
            tipoSolicitudJpaController.create(t);
            return TipoSolicitudStatusEnum.TIPO_CREADO.getCodigo();
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
        }
    }

    @Override
    public int modificarTipo(TipoSolicitud t) {
        if (t == null || t.getId() == null) {
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
        }
        TipoSolicitud old = tipoSolicitudJpaController.findTipoSolicitud(t.getId());
        if (old == null) {
            return TipoSolicitudStatusEnum.TIPO_NO_EXISTE.getCodigo();
        }
        // Nombre: si no viene se mantiene; si viene vacío es un error
        if (t.getNombre() == null) {
            t.setNombre(old.getNombre());
        } else if (estaVacio(t.getNombre())) {
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
        } else {
            t.setNombre(t.getNombre().trim());
        }
        // Descripción: si no viene se mantiene
        t.setDescripcion(t.getDescripcion() == null ? old.getDescripcion() : limpiar(t.getDescripcion()));

        if (!largosValidos(t)) {
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
        }
        // Si cambia el nombre, que no lo tenga otro tipo
        TipoSolicitud mismoNombre = tipoSolicitudJpaController.findTipoSolicitudByNombre(t.getNombre());
        if (mismoNombre != null && !mismoNombre.getId().equals(t.getId())) {
            return TipoSolicitudStatusEnum.TIPO_YA_EXISTE.getCodigo();
        }
        try {
            tipoSolicitudJpaController.edit(t);
            return TipoSolicitudStatusEnum.TIPO_EDITADO.getCodigo();
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return TipoSolicitudStatusEnum.TIPO_NO_PROCESABLE.getCodigo();
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

    private static boolean largosValidos(TipoSolicitud t) {
        return t.getNombre().length() <= MAX_NOMBRE
                && (t.getDescripcion() == null || t.getDescripcion().length() <= MAX_DESCRIPCION);
    }
}
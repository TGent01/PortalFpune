/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum SolicitudStatusEnum {
    SOLICITUD_NO_EXISTE(-1),
    SOLICITUD_YA_EXISTE(0),
    SOLICITUD_CREADO(1),
    SOLICITUD_EDITADO(2),
    SOLICITUD_ELIMINADO(3),
    SOLICITUD_NO_PROCESABLE(-2),
    SOLICITUD_CERRADA(-3),     // está en un estado final: ya no admite cambios
    SOLICITUD_SIN_CAMBIOS(-4); // mismo estado, sin prioridad nueva ni comentario
    private final int codigo;
    
    SolicitudStatusEnum(int codigo){
        this.codigo=codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

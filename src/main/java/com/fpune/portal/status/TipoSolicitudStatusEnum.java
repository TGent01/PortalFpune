/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum TipoSolicitudStatusEnum {
    TIPO_NO_EXISTE(-1),
    TIPO_YA_EXISTE(0),
    TIPO_CREADO(1),
    TIPO_EDITADO(2),
    TIPO_NO_PROCESABLE(-2);

    private final int codigo;

    TipoSolicitudStatusEnum(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

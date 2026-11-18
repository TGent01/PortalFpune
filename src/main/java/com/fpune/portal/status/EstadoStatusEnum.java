/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum EstadoStatusEnum {
    ESTADO_NO_EXISTE(-1),
    ESTADO_YA_EXISTE(0),
    ESTADO_CREADO(1),
    ESTADO_EDITADO(2),
    ESTADO_NO_PROCESABLE(-2);

    private final int codigo;

    EstadoStatusEnum(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum PrioridadStatusEnum {
    PRIORIDAD_NO_EXISTE(-1),
    PRIORIDAD_YA_EXISTE(0),
    PRIORIDAD_CREADO(1),
    PRIORIDAD_EDITADO(2),
    PRIORIDAD_NO_PROCESABLE(-2);

    private final int codigo;

    PrioridadStatusEnum(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
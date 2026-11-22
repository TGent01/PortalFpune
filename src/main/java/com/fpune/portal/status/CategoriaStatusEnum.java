/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum CategoriaStatusEnum {
    CATEGORIA_NO_EXISTE(-1),
    CATEGORIA_YA_EXISTE(0),
    CATEGORIA_CREADO(1),
    CATEGORIA_EDITADO(2),
    CATEGORIA_NO_PROCESABLE(-2);

    private final int codigo;

    CategoriaStatusEnum(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
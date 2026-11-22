/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum UsuarioStatusEnum {
    USUARIO_NO_EXISTE(-1),
    USUARIO_YA_EXISTE(0),
    USUARIO_CREADO(1),
    USUARIO_EDITADO(2),
    USUARIO_ELIMINADO(3),
    USUARIO_NO_PROCESABLE(-2);
    private final int codigo;
    
    UsuarioStatusEnum(int codigo){
        this.codigo=codigo;
    }

    public int getCodigo() {
        return codigo;
    }
    
}

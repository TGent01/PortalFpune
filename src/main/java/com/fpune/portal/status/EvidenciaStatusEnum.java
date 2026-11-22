/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.fpune.portal.status;

/**
 *
 * @author marti
 */
public enum EvidenciaStatusEnum {
    EVIDENCIA_NO_EXISTE(-1),          // evidencia o ticket inexistente
    EVIDENCIA_SUBIDA(1),
    EVIDENCIA_ELIMINADA(2),
    EVIDENCIA_NO_PROCESABLE(-2),      // sin archivos o error al guardar en la BD
    EVIDENCIA_SOLICITUD_CERRADA(-3),  // la solicitud está en un estado final
    EVIDENCIA_LIMITE_SUPERADO(-4);    // se supera el máximo de archivos por solicitud

    private final int codigo;

    EvidenciaStatusEnum(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
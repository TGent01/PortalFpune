/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.entity.Evidencia;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Evidencias (imágenes y PDF) de una solicitud.
 *
 * El solicitante (que puede ser anónimo) trabaja con el CÓDIGO DE TICKET:
 * tener el ticket es la prueba de que la solicitud es suya.
 * El administrador trabaja con los ids.
 *
 * @author marti
 */
public interface EvidenciaService {

    /**
     * Sube uno o varios archivos a la solicitud del ticket.
     * Si un archivo no es válido lanza ArchivoInvalidoException (HTTP 400) y no se guarda NINGUNO.
     */
    public int subirEvidencias(String codigoTicket, List<MultipartFile> archivos);

    /** Evidencias de la solicitud del ticket; null si el ticket no existe. */
    public ArrayList<Evidencia> listarPorTicket(String codigoTicket);

    /** Evidencias de una solicitud (panel admin). */
    public ArrayList<Evidencia> listarPorSolicitud(long idSolicitud);

    public Evidencia obtenerEvidencia(long id);

    /** La evidencia solo si pertenece a la solicitud del ticket; si no, null. */
    public Evidencia obtenerEvidenciaDeTicket(String codigoTicket, long idEvidencia);

    /** El archivo en disco de la evidencia, para descargarlo o mostrarlo. */
    public Resource obtenerArchivo(Evidencia evidencia);

    /** Borra el registro y el archivo del disco (solo admin). */
    public int eliminarEvidencia(long id);
}
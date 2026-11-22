/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.services;

import com.fpune.portal.controllers.EvidenciaJpaController;
import com.fpune.portal.controllers.SolicitudJpaController;
import com.fpune.portal.dtos.ArchivoGuardadoDTO;
import com.fpune.portal.entity.Evidencia;
import com.fpune.portal.entity.Solicitud;
import com.fpune.portal.exceptions.StorageException;
import com.fpune.portal.status.EvidenciaStatusEnum;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author marti
 */
@Service
public class EvidenciaServiceMysqlImpl implements EvidenciaService{
    
    EvidenciaJpaController evidenciaJpaC=new EvidenciaJpaController();
    private static final int max_evidencia=5;
    private final SolicitudJpaController solicitudJpaC=new SolicitudJpaController();
    
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public int subirEvidencias(String codigoTicket, List<MultipartFile> archivos) {
         // 1. Hay al menos un archivo con contenido
        if (archivos == null || archivos.isEmpty()
                || archivos.stream().allMatch(a -> a == null || a.isEmpty())) {
            return EvidenciaStatusEnum.EVIDENCIA_NO_PROCESABLE.getCodigo();
        }
        // 2. El ticket existe
        Solicitud solicitud = solicitudJpaC.findSolicitudByCodigoTicket(codigoTicket);
        if (solicitud == null) {
            return EvidenciaStatusEnum.EVIDENCIA_NO_EXISTE.getCodigo();
        }
        // 3. La solicitud sigue abierta (no Resuelto / Rechazado)
        if (solicitud.getEstado() != null && solicitud.getEstado().getEsFinal()) {
            return EvidenciaStatusEnum.EVIDENCIA_SOLICITUD_CERRADA.getCodigo();
        }
        // 4. No se supera el máximo
        long actuales = evidenciaJpaC.countBySolicitud(solicitud.getId());
        if (actuales + archivos.size() > max_evidencia) {
            return EvidenciaStatusEnum.EVIDENCIA_LIMITE_SUPERADO.getCodigo();
        }

        // 5. Guardar en disco y luego en la BD. Si algo falla, se borran los
        //    archivos ya escritos para no dejar huérfanos.
        List<String> rutasGuardadas = new ArrayList<>();
        try {
            List<Evidencia> nuevas = new ArrayList<>();
            for (MultipartFile archivo : archivos) {
                ArchivoGuardadoDTO guardado = fileStorageService.guardar(archivo); // valida tipo y tamaño
                rutasGuardadas.add(guardado.getRutaRelativa());

                Evidencia ev = new Evidencia();
                ev.setNombreArchivo(guardado.getNombreOriginal());
                ev.setRutaArchivo(guardado.getRutaRelativa());
                ev.setTipoMime(guardado.getTipoMime());
                ev.setTamanoBytes(guardado.getTamanoBytes());
                ev.setSolicitud(solicitud);
                nuevas.add(ev);
            }
            evidenciaJpaC.crearVarias(nuevas); // una sola transacción
            return EvidenciaStatusEnum.EVIDENCIA_SUBIDA.getCodigo();

        } catch (StorageException ex) {
            // Archivo inválido (400) o error de disco (500): lo responde el GlobalExceptionHandler
            borrarArchivos(rutasGuardadas);
            throw ex;
        } catch (RuntimeException ex) {
            // Error de base de datos
            borrarArchivos(rutasGuardadas);
            System.out.println("error " + ex.getMessage());
            return EvidenciaStatusEnum.EVIDENCIA_NO_PROCESABLE.getCodigo();
        }
        
    }

    @Override
    public ArrayList<Evidencia> listarPorTicket(String codigoTicket) {
        Solicitud solicitud=solicitudJpaC.findSolicitudByCodigoTicket(codigoTicket);
                
        return solicitud == null ? null
        : new ArrayList<>(evidenciaJpaC.findEvidenciasBySolicitud(solicitud.getId()));    
        //return solicitud==null?null: new ArrayList<>(solicitud.getEvidenciaList());

    }

    @Override
    public ArrayList<Evidencia> listarPorSolicitud(long idSolicitud) {
        return new ArrayList<>(evidenciaJpaC.findEvidenciasBySolicitud(idSolicitud));
    }

    @Override
    public Evidencia obtenerEvidencia(long id) {

        return evidenciaJpaC.findEvidencia(id);
    }

    @Override
    public Evidencia obtenerEvidenciaDeTicket(String codigoTicket, long idEvidencia) {
        Solicitud solicitud=solicitudJpaC.findSolicitudByCodigoTicket(codigoTicket);
        if(solicitud==null){
            return null;
        }
        for (Evidencia ev : evidenciaJpaC.findEvidenciasBySolicitud(solicitud.getId())) {
            if (ev.getId() == idEvidencia) {
                return ev;
            }
        }
        return null;
    }

    @Override
    public Resource obtenerArchivo(Evidencia evidencia) {
        return fileStorageService.cargar(evidencia.getRutaArchivo());
    }

    @Override
    public int eliminarEvidencia(long id) {
        Evidencia ev = evidenciaJpaC.findEvidencia(id);
        if (ev == null) {
            return EvidenciaStatusEnum.EVIDENCIA_NO_EXISTE.getCodigo();
        }
        try {
            evidenciaJpaC.destroy(id); // primero la BD...
        } catch (Exception ex) {
            System.out.println("error " + ex.getMessage());
            return EvidenciaStatusEnum.EVIDENCIA_NO_PROCESABLE.getCodigo();
        }
        try {
            fileStorageService.eliminar(ev.getRutaArchivo()); // ...después el disco
        } catch (StorageException ex) {
            // El registro ya no existe; un archivo que quedó en disco no rompe nada
            System.out.println("No se pudo borrar el archivo " + ev.getRutaArchivo() + ": " + ex.getMessage());
        }
        return EvidenciaStatusEnum.EVIDENCIA_ELIMINADA.getCodigo();
    }

    private void borrarArchivos(List<String> rutas) {
        for (String ruta : rutas) {
            try {
                fileStorageService.eliminar(ruta);
            } catch (StorageException ignorada) {
                System.out.println("No se pudo borrar el archivo " + ruta);
            }
        }
    }
}

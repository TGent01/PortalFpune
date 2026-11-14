package com.fpune.portal.services;

import com.fpune.portal.dtos.ArchivoGuardadoDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Almacenamiento de archivos de evidencia.
 * Hoy se implementa en disco local (FileStorageServiceLocalImpl); en el futuro
 * se podría cambiar por S3/MinIO creando otra implementación, sin tocar el resto.
 *
 * @author marti
 */
public interface FileStorageService {

    /**
     * Valida (tipo y tamaño) y guarda el archivo.
     *
     * @return datos para crear la entidad Evidencia
     * @throws com.fpune.portal.exceptions.ArchivoInvalidoException si el archivo no es válido
     * @throws com.fpune.portal.exceptions.StorageException si falla la escritura en disco
     */
    public ArchivoGuardadoDTO guardar(MultipartFile archivo);

    /**
     * Devuelve el archivo para descargarlo/mostrarlo.
     *
     * @param rutaRelativa valor guardado en evidencias.ruta_archivo
     */
    public Resource cargar(String rutaRelativa);

    /**
     * Borra el archivo del disco (si no existe, no hace nada).
     *
     * @param rutaRelativa valor guardado en evidencias.ruta_archivo
     */
    public void eliminar(String rutaRelativa);
}
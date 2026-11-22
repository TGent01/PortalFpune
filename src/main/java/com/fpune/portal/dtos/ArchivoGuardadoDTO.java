package com.fpune.portal.dtos;

/**
 * Datos de un archivo ya guardado en disco. Trae exactamente lo que necesita
 * la entidad Evidencia: nombre_archivo, ruta_archivo, tipo_mime y tamano_bytes.
 *
 * @author marti
 */
public class ArchivoGuardadoDTO {

    private final String rutaRelativa;   // ej: 2026/11/3f2a...-....pdf  -> ruta_archivo
    private final String nombreOriginal; // nombre que subió el usuario  -> nombre_archivo
    private final String tipoMime;       // image/jpeg, image/png, application/pdf -> tipo_mime
    private final int tamanoBytes;       // -> tamano_bytes

    public ArchivoGuardadoDTO(String rutaRelativa, String nombreOriginal, String tipoMime, int tamanoBytes) {
        this.rutaRelativa = rutaRelativa;
        this.nombreOriginal = nombreOriginal;
        this.tipoMime = tipoMime;
        this.tamanoBytes = tamanoBytes;
    }

    public String getRutaRelativa() {
        return rutaRelativa;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public int getTamanoBytes() {
        return tamanoBytes;
    }
}
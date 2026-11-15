package com.fpune.portal.services;

import com.fpune.portal.dtos.ArchivoGuardadoDTO;
import com.fpune.portal.exceptions.ArchivoInvalidoException;
import com.fpune.portal.exceptions.StorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda las evidencias en una carpeta del disco del servidor.
 *
 * Estructura: {app.storage.evidencias-dir}/AAAA/MM/{uuid}.{ext}
 * En la base (evidencias.ruta_archivo) se guarda solo la parte relativa: AAAA/MM/{uuid}.{ext}
 *
 * @author marti
 */
@Service
public class FileStorageServiceLocalImpl implements FileStorageService {

    /** Largo máximo de la columna evidencias.nombre_archivo */
    private static final int MAX_NOMBRE = 100;

    /**
     * Tipos permitidos. Se detectan por los primeros bytes del archivo ("firma"),
     * NO por el Content-Type que manda el navegador, que se puede falsificar.
     */
    private enum TipoPermitido {
        JPG("image/jpeg", ".jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}),
        PNG("image/png", ".png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}),
        PDF("application/pdf", ".pdf", new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D}); // "%PDF-"

        final String mime;
        final String extension;
        final byte[] firma;

        TipoPermitido(String mime, String extension, byte[] firma) {
            this.mime = mime;
            this.extension = extension;
            this.firma = firma;
        }

        boolean coincide(byte[] cabecera) {
            return cabecera.length >= firma.length
                    && Arrays.equals(Arrays.copyOf(cabecera, firma.length), firma);
        }
    }

    private final Path directorioBase;
    private final long maxBytes;

    public FileStorageServiceLocalImpl(
            @Value("${app.storage.evidencias-dir}") String directorio,
            @Value("${app.storage.max-bytes:5242880}") long maxBytes) {
        this.directorioBase = Paths.get(directorio).toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
        // Se crea la carpeta al arrancar: si no hay permisos, el servidor avisa enseguida
        try {
            Files.createDirectories(directorioBase);
        } catch (IOException e) {
            throw new StorageException("No se pudo crear la carpeta de evidencias: " + directorioBase, e);
        }
    }

    @Override
    public ArchivoGuardadoDTO guardar(MultipartFile archivo) {
        // 1. Validaciones
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoInvalidoException("El archivo está vacío");
        }
        if (archivo.getSize() > maxBytes) {
            throw new ArchivoInvalidoException("El archivo supera el tamaño máximo de "
                    + (maxBytes / (1024 * 1024)) + " MB");
        }
        TipoPermitido tipo = detectarTipo(archivo);
        if (tipo == null) {
            throw new ArchivoInvalidoException("Tipo de archivo no permitido. Solo se aceptan JPG, PNG y PDF");
        }

        // 2. Ruta destino: AAAA/MM/uuid.ext  (nunca se usa el nombre original en disco)
        LocalDate hoy = LocalDate.now();
        String subcarpeta = hoy.getYear() + "/" + String.format("%02d", hoy.getMonthValue());
        String nombreEnDisco = UUID.randomUUID() + tipo.extension;
        String rutaRelativa = subcarpeta + "/" + nombreEnDisco;
        Path destino = resolverSeguro(rutaRelativa);

        // 3. Escritura
        try {
            Files.createDirectories(destino.getParent());
            try (InputStream in = archivo.getInputStream()) {
                Files.copy(in, destino);
            }
        } catch (IOException e) {
            throw new StorageException("No se pudo guardar el archivo en el servidor", e);
        }

        return new ArchivoGuardadoDTO(
                rutaRelativa,
                limpiarNombre(archivo.getOriginalFilename(), tipo.extension),
                tipo.mime,
                (int) archivo.getSize());
    }

    @Override
    public Resource cargar(String rutaRelativa) {
        Path archivo = resolverSeguro(rutaRelativa);
        if (!Files.isRegularFile(archivo)) {
            throw new StorageException("El archivo no existe en el servidor: " + rutaRelativa);
        }
        return new FileSystemResource(archivo);
    }

    @Override
    public void eliminar(String rutaRelativa) {
        try {
            Files.deleteIfExists(resolverSeguro(rutaRelativa));
        } catch (IOException e) {
            throw new StorageException("No se pudo eliminar el archivo: " + rutaRelativa, e);
        }
    }

    // ------------------------------------------------------------------ helpers

    /** Lee los primeros 8 bytes y los compara con las firmas permitidas. */
    private TipoPermitido detectarTipo(MultipartFile archivo) {
        byte[] cabecera;
        try (InputStream in = archivo.getInputStream()) {
            cabecera = in.readNBytes(8);
        } catch (IOException e) {
            throw new StorageException("No se pudo leer el archivo", e);
        }
        for (TipoPermitido tipo : TipoPermitido.values()) {
            if (tipo.coincide(cabecera)) {
                return tipo;
            }
        }
        return null;
    }

    /**
     * Convierte la ruta relativa en absoluta y verifica que quede DENTRO de la
     * carpeta base (evita ataques tipo "../../windows/system32").
     */
    private Path resolverSeguro(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isBlank()) {
            throw new StorageException("Ruta de archivo vacía");
        }
        Path ruta = directorioBase.resolve(rutaRelativa).normalize();
        if (!ruta.startsWith(directorioBase)) {
            throw new StorageException("Ruta de archivo no permitida");
        }
        return ruta;
    }

    /**
     * Deja el nombre original apto para guardarlo en nombre_archivo (varchar 100):
     * quita rutas, caracteres raros y lo recorta conservando la extensión real.
     */
    private String limpiarNombre(String original, String extension) {
        String nombre = (original == null) ? "" : original;
        // quitar cualquier ruta que mande el navegador (C:\fakepath\..., /home/...)
        nombre = nombre.substring(Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\')) + 1);
        // quitar extensión original (se usa la detectada) y caracteres no seguros
        int punto = nombre.lastIndexOf('.');
        if (punto > 0) {
            nombre = nombre.substring(0, punto);
        }
        nombre = nombre.replaceAll("[^\\p{L}\\p{N} ._-]", "_").trim();
        if (nombre.isEmpty()) {
            nombre = "evidencia";
        }
        int maxBase = MAX_NOMBRE - extension.length();
        if (nombre.length() > maxBase) {
            nombre = nombre.substring(0, maxBase);
        }
        return nombre + extension;
    }
}
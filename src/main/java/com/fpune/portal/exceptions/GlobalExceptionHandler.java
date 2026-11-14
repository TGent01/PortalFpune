package com.fpune.portal.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Convierte las excepciones en respuestas JSON claras para el frontend:
 * { "error": "mensaje" }
 *
 * @author marti
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Archivo vacío, de tipo no permitido o demasiado grande (validado por el servicio). */
    @ExceptionHandler(ArchivoInvalidoException.class)
    public ResponseEntity<Map<String, String>> archivoInvalido(ArchivoInvalidoException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    /** Error al escribir/leer en disco: es un problema del servidor. */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, String>> errorAlmacenamiento(StorageException ex) {
        System.out.println("Error de almacenamiento: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "No se pudo procesar el archivo en el servidor"));
    }

    /** La petición supera spring.servlet.multipart.max-file-size / max-request-size. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> archivoMuyGrande(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(413) // Payload Too Large
                .body(Map.of("error", "El archivo supera el tamaño máximo permitido"));
    }
}
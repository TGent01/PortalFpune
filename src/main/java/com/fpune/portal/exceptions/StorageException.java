package com.fpune.portal.exceptions;

/**
 * Error interno al guardar, leer o borrar un archivo en el disco del servidor
 * (permisos, disco lleno, carpeta inexistente...). Se responde con HTTP 500.
 *
 * @author marti
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
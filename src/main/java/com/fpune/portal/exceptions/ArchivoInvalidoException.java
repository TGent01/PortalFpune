package com.fpune.portal.exceptions;

/**
 * El archivo enviado por el usuario no es válido (vacío, muy grande o de un
 * tipo no permitido). Es un error del cliente: se responde con HTTP 400.
 *
 * @author marti
 */
public class ArchivoInvalidoException extends StorageException {

    public ArchivoInvalidoException(String message) {
        super(message);
    }
}
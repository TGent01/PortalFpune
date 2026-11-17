package com.fpune.portal.util;

import java.security.SecureRandom;
import java.time.Year;
import java.util.function.Predicate;

/**
 * Genera el código de ticket de una solicitud. Formato: QRS-AAAA-XXXXXX
 * (ej: QRS-2026-7K2M9X, 15 caracteres; la columna codigo_ticket es varchar(20)).
 *
 * El código es ALEATORIO (no correlativo) porque las solicitudes anónimas se
 * consultan solo con el ticket: si fuera QRS-0001, QRS-0002... cualquiera
 * podría ver solicitudes ajenas probando números.
 *
 * @author marti
 */
public final class TicketGenerator {

    private static final String PREFIJO = "QRS";
    private static final int LARGO_ALEATORIO = 6;
    private static final int MAX_INTENTOS = 10;

    /**
     * Sin 0/O, 1/I/L para evitar confusiones al leer o dictar el código.
     * 31 caracteres ^ 6 posiciones = ~887 millones de combinaciones por año.
     */
    private static final char[] ALFABETO = "23456789ABCDEFGHJKMNPQRSTUVWXYZ".toCharArray();

    /** SecureRandom: no se puede predecir el siguiente código (Random sí). */
    private static final SecureRandom RANDOM = new SecureRandom();

    private TicketGenerator() {
        // clase utilitaria, no se instancia
    }

    /** Genera un código nuevo (sin verificar si ya existe en la base). */
    private static String generar() {
        StringBuilder sb = new StringBuilder(PREFIJO)
                .append('-').append(Year.now().getValue()).append('-');
        for (int i = 0; i < LARGO_ALEATORIO; i++) {
            sb.append(ALFABETO[RANDOM.nextInt(ALFABETO.length)]);
        }
        return sb.toString();
    }

    /**
     * Genera un código que no exista todavía en la base.
     *
     * Uso: TicketGenerator.generarUnico(c -> solicitudJpaController.findByCodigoTicket(c) != null)
     *
     * @param existe función que devuelve true si el código ya está usado
     */
    public static String generarUnico(Predicate<String> existe) {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            String codigo = generar();
            if (!existe.test(codigo)) {
                return codigo;
            }
        }
        // Prácticamente imposible: 10 choques seguidos entre ~887 millones de opciones
        throw new IllegalStateException("No se pudo generar un código de ticket único");
    }

    /**
     * Normaliza lo que escribe el usuario al consultar: quita espacios y pasa a
     * mayúsculas (" qrs-2026-7k2m9x " -> "QRS-2026-7K2M9X").
     */
    public static String normalizar(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase();
    }
}
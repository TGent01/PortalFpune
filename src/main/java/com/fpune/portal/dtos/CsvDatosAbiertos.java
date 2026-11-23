package com.fpune.portal.util;

import com.fpune.portal.dtos.DatosAbiertosDTO;
import java.util.List;

/**
 * Genera los CSV de datos abiertos.
 *
 * Formato:
 *  - UTF-8 con BOM (para que Excel muestre bien las tildes), separador coma y
 *    decimales con punto, como recomiendan los portales de datos abiertos.
 *  - Formato "largo": una fila por dato, así se agrega o filtra fácil en
 *    Excel, LibreOffice, Python o R.
 *  - Los valores reservados van como cantidad vacía, con reservado = "si".
 *
 * @author marti
 */
public final class CsvDatosAbiertos {

    /** Marca BOM: le indica a Excel que el archivo es UTF-8. */
    private static final String BOM = "﻿";
    private static final String FIN = "\r\n";

    private CsvDatosAbiertos() {
    }

    /**
     * solicitudes.csv
     * periodo_desde,periodo_hasta,dimension,valor,cantidad,porcentaje,reservado,promedio_resolucion_dias
     */
    public static String solicitudes(DatosAbiertosDTO d) {
        StringBuilder sb = new StringBuilder(BOM);
        sb.append("periodo_desde,periodo_hasta,dimension,valor,cantidad,porcentaje,reservado,promedio_resolucion_dias").append(FIN);
        String desde = texto(d.getMetadatos().getPeriodoDesde());
        String hasta = texto(d.getMetadatos().getPeriodoHasta());
        filas(sb, desde, hasta, "categoria", d.getPorCategoria());
        filas(sb, desde, hasta, "tipo", d.getPorTipo());
        filas(sb, desde, hasta, "estado", d.getPorEstado());
        filas(sb, desde, hasta, "mes", d.getPorMes());
        return sb.toString();
    }

    /**
     * indicadores.csv
     * periodo_desde,periodo_hasta,indicador,valor,unidad
     */
    public static String indicadores(DatosAbiertosDTO d) {
        StringBuilder sb = new StringBuilder(BOM);
        sb.append("periodo_desde,periodo_hasta,indicador,valor,unidad").append(FIN);
        String desde = texto(d.getMetadatos().getPeriodoDesde());
        String hasta = texto(d.getMetadatos().getPeriodoHasta());
        DatosAbiertosDTO.Resumen r = d.getResumen();
        DatosAbiertosDTO.Indicadores i = d.getIndicadores();

        indicador(sb, desde, hasta, "total_solicitudes", r.getTotalSolicitudes(), "solicitudes");
        indicador(sb, desde, hasta, "promedio_resolucion", i.getPromedioResolucionDias(), "dias");
        indicador(sb, desde, hasta, "mediana_resolucion", i.getMedianaResolucionDias(), "dias");
        indicador(sb, desde, hasta, "promedio_primera_respuesta", i.getPromedioRespuestaDias(), "dias");
        indicador(sb, desde, hasta, "evidencias_digitales", i.getEvidenciasDigitales(), "archivos");
        return sb.toString();
    }

    // ------------------------------------------------------------------ helpers

    private static void filas(StringBuilder sb, String desde, String hasta, String dimension,
            List<DatosAbiertosDTO.Item> items) {
        if (items == null) {
            return;
        }
        for (DatosAbiertosDTO.Item it : items) {
            sb.append(desde).append(',')
                    .append(hasta).append(',')
                    .append(dimension).append(',')
                    .append(campo(it.getNombre())).append(',')
                    .append(texto(it.getCantidad())).append(',')
                    .append(texto(it.getPorcentaje())).append(',')
                    .append(it.isReservado() ? "si" : "no").append(',')
                    .append(texto(it.getPromedioResolucionDias()))
                    .append(FIN);
        }
    }

    private static void indicador(StringBuilder sb, String desde, String hasta, String nombre,
            Object valor, String unidad) {
        sb.append(desde).append(',')
                .append(hasta).append(',')
                .append(nombre).append(',')
                .append(texto(valor)).append(',')
                .append(unidad)
                .append(FIN);
    }

    /** null → vacío. */
    private static String texto(Object v) {
        return v == null ? "" : v.toString();
    }

    /**
     * Escapa un texto para CSV (RFC 4180): si tiene coma, comillas o salto de línea,
     * va entre comillas y las comillas internas se duplican.
     * También neutraliza "inyección de fórmulas": un texto que empieza con = + - @
     * Excel lo ejecutaría como fórmula, por eso se le antepone un apóstrofo.
     */
    private static String campo(String s) {
        if (s == null) {
            return "";
        }
        String v = s;
        if (!v.isEmpty() && "=+-@".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
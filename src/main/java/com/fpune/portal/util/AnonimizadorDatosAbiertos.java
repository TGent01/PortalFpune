package com.fpune.portal.util;

import com.fpune.portal.dtos.DatosAbiertosDTO;
import com.fpune.portal.dtos.EstadisticasDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Convierte las estadísticas internas en un conjunto de datos abiertos anonimizado
 * (Ley 7593/2025 de Protección de Datos Personales y Ley 5282/2014 de Transparencia).
 *
 * Reglas:
 *  1. Supresión primaria: toda cantidad entre 1 y (UMBRAL - 1) se publica como
 *     "reservado". El 0 sí se publica: no corresponde a ninguna persona.
 *  2. Supresión secundaria: si en una distribución hay UNA sola celda reservada,
 *     se podría despejar restando al total. Por eso se reserva también la siguiente
 *     celda más chica que no sea 0.
 *  3. Un promedio o mediana calculado con menos de UMBRAL casos no se publica.
 *  4. Cada dimensión se publica por separado (sin cruces como categoría × estado),
 *     y no se publican subtotales que permitan despejar celdas (abiertas / cerradas).
 *  5. Se publican solo dimensiones generales: categoría, tipo, estado y mes.
 *     Se excluyen la prioridad (decisión interna), los pendientes sin atender y
 *     los usuarios.
 *
 * Java puro (sin Spring ni JPA): se puede probar sin base de datos.
 *
 * @author marti
 */
public final class AnonimizadorDatosAbiertos {

    /** Cantidad mínima de casos para publicar un valor. */
    public static final int UMBRAL = 3;

    private AnonimizadorDatosAbiertos() {
    }

    public static DatosAbiertosDTO anonimizar(EstadisticasDTO e) {
        DatosAbiertosDTO d = new DatosAbiertosDTO();

        // ---- Metadatos ----
        DatosAbiertosDTO.Metadatos m = d.getMetadatos();
        m.setTitulo("Quejas, reclamos y sugerencias de la comunidad universitaria - FPUNE");
        m.setInstitucion("Facultad Politécnica - Universidad Nacional del Este");
        m.setDescripcion("Cantidad de solicitudes recibidas por el Portal de Quejas, Reclamos y Sugerencias, "
                + "por categoría, tipo, estado y mes, junto con los tiempos de resolución. "
                + "Datos agregados: no contiene información personal ni el texto de las solicitudes.");
        m.setLicencia("CC BY 4.0 (Creative Commons Atribución 4.0 Internacional)");
        m.setFrecuenciaActualizacion("Tiempo real (se calcula en cada consulta)");
        m.setPeriodoDesde(e.getDesde());
        m.setPeriodoHasta(e.getHasta());
        m.setGeneradoEn(e.getGeneradoEn());
        m.setUmbralAnonimizacion(UMBRAL);
        m.setNotaAnonimizacion("Los valores entre 1 y " + (UMBRAL - 1) + " se publican como reservados "
                + "(cantidad nula), así como el valor necesario para que no puedan deducirse restando al total. "
                + "Los promedios calculados con menos de " + UMBRAL + " casos no se publican.");
        m.setUnidadTiempo("días calendario");

        // ---- Resumen ----
        DatosAbiertosDTO.Resumen r = d.getResumen();
        // Un total muy chico (1 o 2) también se reserva: si no, se deduciría la única celda con datos
        r.setTotalSolicitudes(esPequeno(e.getTotalSolicitudes()) ? null : e.getTotalSolicitudes());

        // ---- Distribuciones ----
        d.setPorCategoria(distribucion(e.getPorCategoria(), true));
        d.setPorTipo(distribucion(e.getPorTipo(), false));
        d.setPorEstado(distribucion(e.getPorEstado(), false));
        d.setPorMes(distribucion(e.getPorMes(), false));

        // ---- Indicadores ----
        DatosAbiertosDTO.Indicadores ind = d.getIndicadores();
        EstadisticasDTO.Tiempos t = e.getTiempos();
        if (t != null) {
            if (t.getSolicitudesResueltas() >= UMBRAL) {
                ind.setPromedioResolucionDias(t.getPromedioResolucionDias());
                ind.setMedianaResolucionDias(t.getMedianaResolucionDias());
            }
            if (t.getSolicitudesConRespuesta() >= UMBRAL) {
                ind.setPromedioRespuestaDias(t.getPromedioRespuestaDias());
            }
        }
        ind.setEvidenciasDigitales(e.getEvidencias() != null ? e.getEvidencias().getTotalArchivos() : 0);

        return d;
    }

    // ------------------------------------------------------------------ helpers

    private static List<DatosAbiertosDTO.Item> distribucion(List<EstadisticasDTO.Item> origen, boolean conResolucion) {
        List<DatosAbiertosDTO.Item> items = new ArrayList<>();
        if (origen == null) {
            return items;
        }

        // 1. Supresión primaria
        boolean[] reservar = new boolean[origen.size()];
        int reservadas = 0;
        for (int i = 0; i < origen.size(); i++) {
            if (esPequeno(origen.get(i).getCantidad())) {
                reservar[i] = true;
                reservadas++;
            }
        }

        // 2. Supresión secundaria. Restando al total se obtiene la SUMA de las celdas
        //    reservadas; si es una sola celda, o si la suma es menor al umbral
        //    (ej. dos celdas que suman 2 → valen 1 y 1), se deducen. Se reserva la
        //    siguiente celda más chica (distinta de 0) hasta que eso no pase.
        int sumaReservada = 0;
        for (int i = 0; i < origen.size(); i++) {
            if (reservar[i]) {
                sumaReservada += origen.get(i).getCantidad();
            }
        }
        while (reservadas > 0 && (reservadas == 1 || sumaReservada < UMBRAL)) {
            int menor = -1;
            for (int i = 0; i < origen.size(); i++) {
                int c = origen.get(i).getCantidad();
                if (!reservar[i] && c > 0 && (menor == -1 || c < origen.get(menor).getCantidad())) {
                    menor = i;
                }
            }
            if (menor == -1) {
                break; // no quedan celdas: el total del periodo también se reserva (ver anonimizar)
            }
            reservar[menor] = true;
            reservadas++;
            sumaReservada += origen.get(menor).getCantidad();
        }

        // 3. Armar las filas
        for (int i = 0; i < origen.size(); i++) {
            EstadisticasDTO.Item o = origen.get(i);
            DatosAbiertosDTO.Item item = reservar[i]
                    ? new DatosAbiertosDTO.Item(o.getNombre(), null, null, true)
                    : new DatosAbiertosDTO.Item(o.getNombre(), o.getCantidad(), o.getPorcentaje(), false);
            if (conResolucion && !reservar[i] && o.getCerradas() != null && o.getCerradas() >= UMBRAL) {
                item.setPromedioResolucionDias(o.getPromedioResolucionDias());
            }
            items.add(item);
        }
        return items;
    }

    /** Entre 1 y UMBRAL - 1 (el 0 no identifica a nadie). */
    private static boolean esPequeno(int cantidad) {
        return cantidad > 0 && cantidad < UMBRAL;
    }
}
package com.fpune.portal.util;

import com.fpune.portal.dtos.EstadisticasDTO;
import com.fpune.portal.dtos.EstadisticasDTO.Item;
import com.fpune.portal.dtos.SolicitudResumenDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Calcula las estadísticas a partir de datos ya leídos de la base.
 *
 * Es Java puro (sin JPA ni Spring) a propósito: así se puede probar con datos
 * inventados, sin base de datos.
 *
 * @author marti
 */
public final class CalculadoraEstadisticas {

    /** Nombre que se muestra para las solicitudes sin prioridad asignada. */
    public static final String SIN_PRIORIDAD = "Sin asignar";

    private CalculadoraEstadisticas() {
    }

    /**
     * @param solicitudes        solicitudes del periodo
     * @param primerCambio       id solicitud → fecha del primer cambio hecho por un admin
     * @param evidencias         id solicitud → cantidad de archivos
     * @param estados            nombres de los estados en el orden del flujo (para mostrar también los que tienen 0)
     * @param categorias         nombres de las categorías
     * @param tipos              nombres de los tipos de solicitud
     * @param prioridades        nombres de las prioridades, de menor a mayor
     * @param usuariosHabilitados null si no se quiere informar
     * @param desde              inicio del periodo (null = sin límite)
     * @param hasta              fin del periodo (null = sin límite)
     */
    public static EstadisticasDTO calcular(List<SolicitudResumenDTO> solicitudes,
            Map<Long, LocalDate> primerCambio, Map<Long, Long> evidencias,
            List<String> estados, List<String> categorias, List<String> tipos, List<String> prioridades,
            Long usuariosHabilitados, LocalDate desde, LocalDate hasta) {

        List<SolicitudResumenDTO> lista = solicitudes == null ? Collections.emptyList() : solicitudes;
        Map<Long, LocalDate> cambios = primerCambio == null ? Collections.emptyMap() : primerCambio;
        Map<Long, Long> archivos = evidencias == null ? Collections.emptyMap() : evidencias;
        int total = lista.size();

        EstadisticasDTO r = new EstadisticasDTO();
        r.setDesde(desde);
        r.setHasta(hasta);
        r.setGeneradoEn(LocalDateTime.now().withNano(0));
        r.setUsuariosHabilitados(usuariosHabilitados);

        // ---- Volumen ----
        int cerradas = 0;
        int sinAtender = 0;
        for (SolicitudResumenDTO s : lista) {
            if (s.isEstadoFinal()) {
                cerradas++;
            } else if (!cambios.containsKey(s.getId())) {
                sinAtender++;
            }
        }
        r.setTotalSolicitudes(total);
        r.setCerradas(cerradas);
        r.setAbiertas(total - cerradas);
        r.setSinAtender(sinAtender);

        // ---- Distribuciones ----
        r.setPorEstado(distribucion(lista, estados, SolicitudResumenDTO::getEstado, total));
        r.setPorTipo(distribucion(lista, tipos, SolicitudResumenDTO::getTipo, total));
        r.setPorPrioridad(distribucion(lista, conSinPrioridad(prioridades),
                s -> s.getPrioridad() == null ? SIN_PRIORIDAD : s.getPrioridad(), total));
        r.setPorCategoria(porCategoria(lista, categorias, total));
        r.setPorMes(porMes(lista, desde, hasta, total));

        // ---- Tiempos ----
        EstadisticasDTO.Tiempos t = new EstadisticasDTO.Tiempos();
        List<Long> diasRespuesta = new ArrayList<>();
        List<Long> diasResolucion = new ArrayList<>();
        for (SolicitudResumenDTO s : lista) {
            LocalDate primera = cambios.get(s.getId());
            if (primera != null) {
                diasRespuesta.add(dias(s.getFechaCreacion(), primera));
            }
            if (s.isEstadoFinal() && s.getFechaCierre() != null) {
                diasResolucion.add(dias(s.getFechaCreacion(), s.getFechaCierre()));
            }
        }
        t.setSolicitudesConRespuesta(diasRespuesta.size());
        t.setPromedioRespuestaDias(promedio(diasRespuesta));
        t.setSolicitudesResueltas(diasResolucion.size());
        t.setPromedioResolucionDias(promedio(diasResolucion));
        t.setMedianaResolucionDias(mediana(diasResolucion));
        r.setTiempos(t);

        // ---- Evidencias (papel cero) ----
        EstadisticasDTO.Evidencias ev = new EstadisticasDTO.Evidencias();
        long totalArchivos = 0;
        int conEvidencia = 0;
        for (SolicitudResumenDTO s : lista) {
            long n = archivos.getOrDefault(s.getId(), 0L);
            totalArchivos += n;
            if (n > 0) {
                conEvidencia++;
            }
        }
        ev.setTotalArchivos(totalArchivos);
        ev.setSolicitudesConEvidencia(conEvidencia);
        r.setEvidencias(ev);

        return r;
    }

    // ------------------------------------------------------------------ distribuciones

    /**
     * Cuenta por un campo. Primero aparecen los nombres del catálogo (en su orden,
     * aunque tengan 0); después, cualquier valor que no esté en el catálogo
     * (por ejemplo, una categoría que se renombró).
     */
    private static List<Item> distribucion(List<SolicitudResumenDTO> lista, List<String> catalogo,
            Function<SolicitudResumenDTO, String> campo, int total) {
        Map<String, Integer> conteo = contar(lista, catalogo, campo);
        List<Item> items = new ArrayList<>();
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            items.add(new Item(e.getKey(), e.getValue(), porcentaje(e.getValue(), total)));
        }
        return items;
    }

    /** Igual que distribucion(), más cerradas y promedio de resolución por categoría. */
    private static List<Item> porCategoria(List<SolicitudResumenDTO> lista, List<String> catalogo, int total) {
        Map<String, Integer> conteo = contar(lista, catalogo, SolicitudResumenDTO::getCategoria);
        List<Item> items = new ArrayList<>();
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            Item item = new Item(e.getKey(), e.getValue(), porcentaje(e.getValue(), total));
            List<Long> dias = new ArrayList<>();
            int cerradas = 0;
            for (SolicitudResumenDTO s : lista) {
                if (e.getKey().equals(s.getCategoria()) && s.isEstadoFinal()) {
                    cerradas++;
                    if (s.getFechaCierre() != null) {
                        dias.add(dias(s.getFechaCreacion(), s.getFechaCierre()));
                    }
                }
            }
            item.setCerradas(cerradas);
            item.setPromedioResolucionDias(promedio(dias));
            items.add(item);
        }
        return items;
    }

    /**
     * Solicitudes por mes de creación ("2026-11"). Se completan con 0 los meses
     * sin solicitudes para que el gráfico de línea no tenga huecos.
     */
    private static List<Item> porMes(List<SolicitudResumenDTO> lista, LocalDate desde, LocalDate hasta, int total) {
        List<Item> items = new ArrayList<>();
        Map<YearMonth, Integer> conteo = new LinkedHashMap<>();
        YearMonth min = null;
        YearMonth max = null;
        for (SolicitudResumenDTO s : lista) {
            if (s.getFechaCreacion() == null) {
                continue;
            }
            YearMonth m = YearMonth.from(s.getFechaCreacion());
            conteo.merge(m, 1, Integer::sum);
            min = (min == null || m.isBefore(min)) ? m : min;
            max = (max == null || m.isAfter(max)) ? m : max;
        }
        if (desde != null) {
            min = YearMonth.from(desde);
        }
        if (hasta != null) {
            max = YearMonth.from(hasta);
        }
        if (min == null || max == null) {
            return items; // sin datos
        }
        // Tope de seguridad: no generar más de 10 años de meses vacíos
        for (YearMonth m = min; !m.isAfter(max) && items.size() < 120; m = m.plusMonths(1)) {
            int n = conteo.getOrDefault(m, 0);
            items.add(new Item(m.toString(), n, porcentaje(n, total)));
        }
        return items;
    }

    private static Map<String, Integer> contar(List<SolicitudResumenDTO> lista, List<String> catalogo,
            Function<SolicitudResumenDTO, String> campo) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        if (catalogo != null) {
            for (String nombre : catalogo) {
                conteo.put(nombre, 0);
            }
        }
        for (SolicitudResumenDTO s : lista) {
            String valor = campo.apply(s);
            if (valor != null) {
                conteo.merge(valor, 1, Integer::sum);
            }
        }
        return conteo;
    }

    private static List<String> conSinPrioridad(List<String> prioridades) {
        List<String> l = new ArrayList<>(prioridades == null ? Collections.emptyList() : prioridades);
        l.add(SIN_PRIORIDAD);
        return l;
    }

    // ------------------------------------------------------------------ números

    /** Días calendario entre dos fechas (nunca negativo). */
    private static long dias(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(inicio, fin));
    }

    private static double porcentaje(int parte, int total) {
        return total == 0 ? 0.0 : redondear(parte * 100.0 / total);
    }

    private static Double promedio(List<Long> valores) {
        if (valores.isEmpty()) {
            return null;
        }
        long suma = 0;
        for (long v : valores) {
            suma += v;
        }
        return redondear((double) suma / valores.size());
    }

    private static Double mediana(List<Long> valores) {
        if (valores.isEmpty()) {
            return null;
        }
        List<Long> ord = new ArrayList<>(valores);
        Collections.sort(ord);
        int n = ord.size();
        double m = (n % 2 == 1) ? ord.get(n / 2) : (ord.get(n / 2 - 1) + ord.get(n / 2)) / 2.0;
        return redondear(m);
    }

    /** Redondea a 1 decimal. */
    private static double redondear(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
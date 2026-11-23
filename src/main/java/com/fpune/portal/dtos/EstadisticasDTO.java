package com.fpune.portal.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta de GET /estadisticas (panel admin).
 *
 * Contiene SOLO datos agregados (cantidades, porcentajes, promedios): ningún
 * texto escrito por el usuario ni identificador de personas. Por eso, más
 * adelante, una parte se puede publicar como datos abiertos.
 *
 * Los tiempos se miden en DÍAS CALENDARIO porque solicitudes.fecha_creacion y
 * fecha_cierre son columnas DATE (sin hora).
 *
 * @author marti
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadisticasDTO {

    // ---- Periodo consultado ----
    private LocalDate desde;              // null = desde el inicio
    private LocalDate hasta;              // null = hasta hoy
    private LocalDateTime generadoEn;

    // ---- Volumen ----
    private int totalSolicitudes;
    private int abiertas;                 // estado no final
    private int cerradas;                 // estado final (Resuelto / Rechazado)
    private int sinAtender;               // abiertas que el admin todavía no tocó (sin historial)

    // ---- Distribuciones (índice de incidencias) ----
    private List<Item> porEstado = new ArrayList<>();
    private List<Item> porCategoria = new ArrayList<>();
    private List<Item> porTipo = new ArrayList<>();
    private List<Item> porPrioridad = new ArrayList<>();
    private List<Item> porMes = new ArrayList<>();      // nombre = "2026-11"

    // ---- Tiempos, evidencias y adopción ----
    private Tiempos tiempos = new Tiempos();
    private Evidencias evidencias = new Evidencias();
    private Long usuariosHabilitados;     // adopción digital (las solicitudes no guardan el usuario)

    // =====================================================================

    /** Una fila de una distribución: "Tecnología: 10 solicitudes (23.8 %)". */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {

        private String nombre;
        private int cantidad;
        private double porcentaje;                // sobre el total del periodo, 1 decimal
        private Integer cerradas;                 // solo en porCategoria
        private Double promedioResolucionDias;    // solo en porCategoria; null si ninguna cerrada

        public Item() {
        }

        public Item(String nombre, int cantidad, double porcentaje) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.porcentaje = porcentaje;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPorcentaje() {
            return porcentaje;
        }

        public void setPorcentaje(double porcentaje) {
            this.porcentaje = porcentaje;
        }

        public Integer getCerradas() {
            return cerradas;
        }

        public void setCerradas(Integer cerradas) {
            this.cerradas = cerradas;
        }

        public Double getPromedioResolucionDias() {
            return promedioResolucionDias;
        }

        public void setPromedioResolucionDias(Double promedioResolucionDias) {
            this.promedioResolucionDias = promedioResolucionDias;
        }
    }

    /** KPI "Tiempo promedio de respuesta y resolución" (sección 12.2). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tiempos {

        /** Creación → primer cambio hecho por un admin. Null si ninguna fue atendida. */
        private Double promedioRespuestaDias;
        private int solicitudesConRespuesta;

        /** Creación → fecha de cierre (estado final). Null si ninguna se cerró. */
        private Double promedioResolucionDias;
        private Double medianaResolucionDias;     // menos sensible a casos extremos que el promedio
        private int solicitudesResueltas;

        public Double getPromedioRespuestaDias() {
            return promedioRespuestaDias;
        }

        public void setPromedioRespuestaDias(Double promedioRespuestaDias) {
            this.promedioRespuestaDias = promedioRespuestaDias;
        }

        public int getSolicitudesConRespuesta() {
            return solicitudesConRespuesta;
        }

        public void setSolicitudesConRespuesta(int solicitudesConRespuesta) {
            this.solicitudesConRespuesta = solicitudesConRespuesta;
        }

        public Double getPromedioResolucionDias() {
            return promedioResolucionDias;
        }

        public void setPromedioResolucionDias(Double promedioResolucionDias) {
            this.promedioResolucionDias = promedioResolucionDias;
        }

        public Double getMedianaResolucionDias() {
            return medianaResolucionDias;
        }

        public void setMedianaResolucionDias(Double medianaResolucionDias) {
            this.medianaResolucionDias = medianaResolucionDias;
        }

        public int getSolicitudesResueltas() {
            return solicitudesResueltas;
        }

        public void setSolicitudesResueltas(int solicitudesResueltas) {
            this.solicitudesResueltas = solicitudesResueltas;
        }
    }

    /** KPI "Impacto de la política Papel Cero" (sección 12.2). */
    public static class Evidencias {

        private long totalArchivos;
        private int solicitudesConEvidencia;

        public long getTotalArchivos() {
            return totalArchivos;
        }

        public void setTotalArchivos(long totalArchivos) {
            this.totalArchivos = totalArchivos;
        }

        public int getSolicitudesConEvidencia() {
            return solicitudesConEvidencia;
        }

        public void setSolicitudesConEvidencia(int solicitudesConEvidencia) {
            this.solicitudesConEvidencia = solicitudesConEvidencia;
        }
    }

    // =====================================================================

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }

    public LocalDateTime getGeneradoEn() {
        return generadoEn;
    }

    public void setGeneradoEn(LocalDateTime generadoEn) {
        this.generadoEn = generadoEn;
    }

    public int getTotalSolicitudes() {
        return totalSolicitudes;
    }

    public void setTotalSolicitudes(int totalSolicitudes) {
        this.totalSolicitudes = totalSolicitudes;
    }

    public int getAbiertas() {
        return abiertas;
    }

    public void setAbiertas(int abiertas) {
        this.abiertas = abiertas;
    }

    public int getCerradas() {
        return cerradas;
    }

    public void setCerradas(int cerradas) {
        this.cerradas = cerradas;
    }

    public int getSinAtender() {
        return sinAtender;
    }

    public void setSinAtender(int sinAtender) {
        this.sinAtender = sinAtender;
    }

    public List<Item> getPorEstado() {
        return porEstado;
    }

    public void setPorEstado(List<Item> porEstado) {
        this.porEstado = porEstado;
    }

    public List<Item> getPorCategoria() {
        return porCategoria;
    }

    public void setPorCategoria(List<Item> porCategoria) {
        this.porCategoria = porCategoria;
    }

    public List<Item> getPorTipo() {
        return porTipo;
    }

    public void setPorTipo(List<Item> porTipo) {
        this.porTipo = porTipo;
    }

    public List<Item> getPorPrioridad() {
        return porPrioridad;
    }

    public void setPorPrioridad(List<Item> porPrioridad) {
        this.porPrioridad = porPrioridad;
    }

    public List<Item> getPorMes() {
        return porMes;
    }

    public void setPorMes(List<Item> porMes) {
        this.porMes = porMes;
    }

    public Tiempos getTiempos() {
        return tiempos;
    }

    public void setTiempos(Tiempos tiempos) {
        this.tiempos = tiempos;
    }

    public Evidencias getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(Evidencias evidencias) {
        this.evidencias = evidencias;
    }

    public Long getUsuariosHabilitados() {
        return usuariosHabilitados;
    }

    public void setUsuariosHabilitados(Long usuariosHabilitados) {
        this.usuariosHabilitados = usuariosHabilitados;
    }
}
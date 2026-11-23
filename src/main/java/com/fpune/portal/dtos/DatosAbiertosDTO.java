package com.fpune.portal.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta PÚBLICA de GET /datos-abiertos.
 *
 * Solo datos agregados y anonimizados: los valores entre 1 y (umbral - 1) se
 * publican como "reservado" (cantidad = null) para que un caso aislado no pueda
 * identificarse. No incluye prioridades, pendientes internos ni usuarios.
 *
 * @author marti
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosAbiertosDTO {

    private Metadatos metadatos = new Metadatos();
    private Resumen resumen = new Resumen();
    private List<Item> porCategoria = new ArrayList<>();
    private List<Item> porTipo = new ArrayList<>();
    private List<Item> porEstado = new ArrayList<>();
    private List<Item> porMes = new ArrayList<>();
    private Indicadores indicadores = new Indicadores();

    // =====================================================================

    /** Descripción del conjunto de datos (lo que pide un portal de datos abiertos). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Metadatos {

        private String titulo;
        private String institucion;
        private String descripcion;
        private String licencia;
        private String frecuenciaActualizacion;
        private LocalDate periodoDesde;          // null = desde el inicio del registro
        private LocalDate periodoHasta;          // null = hasta la fecha de generación
        private LocalDateTime generadoEn;
        private int umbralAnonimizacion;
        private String notaAnonimizacion;
        private String unidadTiempo;

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getInstitucion() {
            return institucion;
        }

        public void setInstitucion(String institucion) {
            this.institucion = institucion;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getLicencia() {
            return licencia;
        }

        public void setLicencia(String licencia) {
            this.licencia = licencia;
        }

        public String getFrecuenciaActualizacion() {
            return frecuenciaActualizacion;
        }

        public void setFrecuenciaActualizacion(String frecuenciaActualizacion) {
            this.frecuenciaActualizacion = frecuenciaActualizacion;
        }

        public LocalDate getPeriodoDesde() {
            return periodoDesde;
        }

        public void setPeriodoDesde(LocalDate periodoDesde) {
            this.periodoDesde = periodoDesde;
        }

        public LocalDate getPeriodoHasta() {
            return periodoHasta;
        }

        public void setPeriodoHasta(LocalDate periodoHasta) {
            this.periodoHasta = periodoHasta;
        }

        public LocalDateTime getGeneradoEn() {
            return generadoEn;
        }

        public void setGeneradoEn(LocalDateTime generadoEn) {
            this.generadoEn = generadoEn;
        }

        public int getUmbralAnonimizacion() {
            return umbralAnonimizacion;
        }

        public void setUmbralAnonimizacion(int umbralAnonimizacion) {
            this.umbralAnonimizacion = umbralAnonimizacion;
        }

        public String getNotaAnonimizacion() {
            return notaAnonimizacion;
        }

        public void setNotaAnonimizacion(String notaAnonimizacion) {
            this.notaAnonimizacion = notaAnonimizacion;
        }

        public String getUnidadTiempo() {
            return unidadTiempo;
        }

        public void setUnidadTiempo(String unidadTiempo) {
            this.unidadTiempo = unidadTiempo;
        }
    }

    /**
     * Total del periodo (null si es muy chico).
     * No se publican "abiertas" y "cerradas" por separado: combinadas con porEstado
     * permitirían despejar celdas reservadas (ej. cerradas - Resuelto = Rechazado).
     * Se pueden calcular desde porEstado.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Resumen {

        private Integer totalSolicitudes;

        public Integer getTotalSolicitudes() {
            return totalSolicitudes;
        }

        public void setTotalSolicitudes(Integer totalSolicitudes) {
            this.totalSolicitudes = totalSolicitudes;
        }

    }

    /** Una fila de una distribución. Si reservado = true, cantidad y porcentaje son null. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {

        private String nombre;
        private Integer cantidad;
        private Double porcentaje;
        private boolean reservado;
        private Double promedioResolucionDias;   // solo en porCategoria

        public Item() {
        }

        public Item(String nombre, Integer cantidad, Double porcentaje, boolean reservado) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.porcentaje = porcentaje;
            this.reservado = reservado;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public Double getPorcentaje() {
            return porcentaje;
        }

        public void setPorcentaje(Double porcentaje) {
            this.porcentaje = porcentaje;
        }

        public boolean isReservado() {
            return reservado;
        }

        public void setReservado(boolean reservado) {
            this.reservado = reservado;
        }

        public Double getPromedioResolucionDias() {
            return promedioResolucionDias;
        }

        public void setPromedioResolucionDias(Double promedioResolucionDias) {
            this.promedioResolucionDias = promedioResolucionDias;
        }
    }

    /**
     * Indicadores de gestión. Un promedio es null si se calculó con menos casos que el umbral.
     * No se publica la cantidad de casos usada ("base"): sería igual a la cantidad de
     * cerradas y permitiría despejar celdas reservadas de porEstado.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Indicadores {

        private Double promedioResolucionDias;
        private Double medianaResolucionDias;
        private Double promedioRespuestaDias;
        private long evidenciasDigitales;      // "Papel Cero": archivos que no se imprimieron

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


        public Double getPromedioRespuestaDias() {
            return promedioRespuestaDias;
        }

        public void setPromedioRespuestaDias(Double promedioRespuestaDias) {
            this.promedioRespuestaDias = promedioRespuestaDias;
        }


        public long getEvidenciasDigitales() {
            return evidenciasDigitales;
        }

        public void setEvidenciasDigitales(long evidenciasDigitales) {
            this.evidenciasDigitales = evidenciasDigitales;
        }
    }

    // =====================================================================

    public Metadatos getMetadatos() {
        return metadatos;
    }

    public void setMetadatos(Metadatos metadatos) {
        this.metadatos = metadatos;
    }

    public Resumen getResumen() {
        return resumen;
    }

    public void setResumen(Resumen resumen) {
        this.resumen = resumen;
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

    public List<Item> getPorEstado() {
        return porEstado;
    }

    public void setPorEstado(List<Item> porEstado) {
        this.porEstado = porEstado;
    }

    public List<Item> getPorMes() {
        return porMes;
    }

    public void setPorMes(List<Item> porMes) {
        this.porMes = porMes;
    }

    public Indicadores getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(Indicadores indicadores) {
        this.indicadores = indicadores;
    }
}
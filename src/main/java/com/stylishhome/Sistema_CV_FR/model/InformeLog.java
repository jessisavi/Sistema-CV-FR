package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa el log de generación de informes
 */
@Entity
@Table(name = "informe_logs")
@Data
public class InformeLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe_log")
    private Integer idInformeLog;
    
    @Column(name = "tipo_informe", nullable = false, length = 50)
    private String tipoInforme;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idempleado", nullable = false, referencedColumnName = "idempleado")
    private Usuario empleado;
    
    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion = LocalDateTime.now();
    
    @Column(name = "parametros", columnDefinition = "TEXT")
    private String parametros;
    
    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;
    
    @Column(name = "tamano_archivo")
    private Long tamanoArchivo;
    
    @Column(name = "estado", length = 20)
    private String estado = "COMPLETADO"; 
    
    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;
    
    /**
     * Tipos de informe predefinidos
     */
    public static class Tipos {
        public static final String RESUMEN_GENERAL = "RESUMEN_GENERAL";
        public static final String INFORME_VENTAS = "INFORME_VENTAS";
        public static final String INFORME_COTIZACIONES = "INFORME_COTIZACIONES";
        public static final String INFORME_PEDIDOS = "INFORME_PEDIDOS";
        public static final String INFORME_CLIENTES = "INFORME_CLIENTES";
        public static final String INFORME_PRODUCTOS = "INFORME_PRODUCTOS";
        public static final String FILTRO_APLICADO = "FILTRO_APLICADO";
        
        public static String[] getAllTipos() {
            return new String[]{
                RESUMEN_GENERAL,
                INFORME_VENTAS,
                INFORME_COTIZACIONES,
                INFORME_PEDIDOS,
                INFORME_CLIENTES,
                INFORME_PRODUCTOS,
                FILTRO_APLICADO
            };
        }
    }
    
    /**
     * Estados de generación de informe
     */
    public static class Estados {
        public static final String EN_PROCESO = "EN_PROCESO";
        public static final String COMPLETADO = "COMPLETADO";
        public static final String FALLIDO = "FALLIDO";
        
        public static String[] getAllEstados() {
            return new String[]{
                EN_PROCESO,
                COMPLETADO,
                FALLIDO
            };
        }
    }
    
    /**
     * Constructor por defecto
     */
    public InformeLog() {
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = Estados.COMPLETADO;
    }
    
    /**
     * Constructor con parámetros básicos
     */
    public InformeLog(String tipoInforme, LocalDate fechaInicio, LocalDate fechaFin, Usuario empleado) {
        this();
        this.tipoInforme = tipoInforme;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.empleado = empleado;
    }
    
    /**
     * Constructor completo
     */
    public InformeLog(String tipoInforme, LocalDate fechaInicio, LocalDate fechaFin, 
                     Usuario empleado, String parametros, String nombreArchivo, Long tamanoArchivo) {
        this(tipoInforme, fechaInicio, fechaFin, empleado);
        this.parametros = parametros;
        this.nombreArchivo = nombreArchivo;
        this.tamanoArchivo = tamanoArchivo;
    }
    
    /**
     * Verifica si el informe se generó exitosamente
     */
    public boolean isCompletado() {
        return Estados.COMPLETADO.equals(this.estado);
    }
    
    /**
     * Verifica si el informe falló
     */
    public boolean isFallido() {
        return Estados.FALLIDO.equals(this.estado);
    }
    
    /**
     * Verifica si el informe está en proceso
     */
    public boolean isEnProceso() {
        return Estados.EN_PROCESO.equals(this.estado);
    }
    
    /**
     * Marca el informe como completado
     */
    public void marcarCompletado() {
        this.estado = Estados.COMPLETADO;
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    /**
     * Marca el informe como fallido
     */
    public void marcarFallido(String mensajeError) {
        this.estado = Estados.FALLIDO;
        this.mensajeError = mensajeError;
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    /**
     * Marca el informe como en proceso
     */
    public void marcarEnProceso() {
        this.estado = Estados.EN_PROCESO;
    }
    
    /**
     * Obtiene la duración de la generación en segundos
     */
    public Long getDuracionSegundos() {
        if (this.fechaGeneracion == null) {
            return null;
        }
        // En una implementación real, podrías tener un campo fechaInicioProceso
        return 0L; // Placeholder - implementar lógica real si es necesaria
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.fechaGeneracion == null) {
            this.fechaGeneracion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = Estados.COMPLETADO;
        }
    }
}
package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una cotización en el sistema
 */
@Entity
@Table(name = "cotizaciones")
@Data
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcotizacion")
    private Integer idCotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idempleado", nullable = false)
    private Usuario empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "valido_hasta", nullable = false)
    private LocalDate validoHasta;

    @Column(name = "proyecto", length = 255)
    private String proyecto;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "terminos", columnDefinition = "TEXT")
    private String terminos;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento", precision = 15, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "iva", precision = 15, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Relación uno a muchos con los detalles de la cotización
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CotizacionDetalle> detalles = new ArrayList<>();

    /**
     * Enum para los estados de la cotización
     */
    public enum EstadoCotizacion {
        PENDIENTE,
        APROBADA,
        RECHAZADA,
        VENCIDA
    }

    /**
     * Método helper para agregar un detalle a la cotización
     */
    public void agregarDetalle(CotizacionDetalle detalle) {
        detalles.add(detalle);
        detalle.setCotizacion(this);
    }

    /**
     * Método helper para remover un detalle de la cotización
     */
    public void removerDetalle(CotizacionDetalle detalle) {
        detalles.remove(detalle);
        detalle.setCotizacion(null);
    }

    /**
     * Inicializa valores por defecto
     */
    @PrePersist
    public void inicializarValores() {
        // Establecer fecha actual si no está definida
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
        
        // Establecer fecha de validez por defecto (15 días)
        if (this.validoHasta == null) {
            this.validoHasta = this.fecha.plusDays(15);
        }
        
        // Establecer fecha de creación si no está definida
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        
        // Calcular totales iniciales
        calcularTotales();
    }

    /**
     * Calcula los totales de la cotización
     */
    @PreUpdate
    public void calcularTotales() {
        if (this.detalles != null && !this.detalles.isEmpty()) {
            this.subtotal = detalles.stream()
                    .map(CotizacionDetalle::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.iva = this.subtotal.multiply(new BigDecimal("0.19")); // 19% IVA
            this.total = this.subtotal.add(this.iva).subtract(this.descuento);
        } else {
            this.subtotal = BigDecimal.ZERO;
            this.iva = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
        }
    }
}
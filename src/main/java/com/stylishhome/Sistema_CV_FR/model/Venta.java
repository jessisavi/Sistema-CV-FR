package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una venta en el sistema
 */
@Entity
@Table(name = "ventas")
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idventa")
    private Integer idVenta;

    @Column(name = "numero_factura", nullable = false, unique = true, length = 20)
    private String numeroFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idempleado", nullable = false)
    private Usuario empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento", precision = 15, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "iva", precision = 15, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Relación uno a muchos con los detalles de la venta
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    /**
     * Enum para los métodos de pago
     */
    public enum MetodoPago {
        EFECTIVO,
        TARJETA_CREDITO,
        TRANSFERENCIA,
        CHEQUE
    }

    /**
     * Enum para los estados de la venta
     */
    public enum EstadoVenta {
        PENDIENTE,
        COMPLETADA,
        CANCELADA
    }

    /**
     * Método para obtener código de venta (usado en vistas)
     */
    public String getCodigo() {
        return this.numeroFactura != null ? this.numeroFactura : "VENTA-" + this.idVenta;
    }

    /**
     * Método helper para agregar un detalle a la venta
     */
    public void agregarDetalle(VentaDetalle detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    /**
     * Método helper para remover un detalle de la venta
     */
    public void removerDetalle(VentaDetalle detalle) {
        if (this.detalles != null) {
            detalles.remove(detalle);
            detalle.setVenta(null);
        }
    }

    /**
     * Inicializa valores y calcula totales - ÚNICO método @PrePersist
     */
    @PrePersist
    public void inicializarValores() {
        // Establecer fecha si no está definida
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }

        // Establecer fecha de creación si no está definida
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }

        // Generar número de factura automáticamente si no existe
        if (this.numeroFactura == null || this.numeroFactura.isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            this.numeroFactura = "FAC-" + timestamp.substring(timestamp.length() - 8);
        }

        // Calcular totales iniciales
        calcularTotales();
    }

    /**
     * Calcula los totales de la venta
     */
    @PreUpdate
    public void calcularTotales() {
        if (this.detalles != null && !this.detalles.isEmpty()) {
            this.subtotal = detalles.stream()
                    .map(VentaDetalle::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.iva = this.subtotal.multiply(new BigDecimal("0.19")); // 19% IVA
            this.total = this.subtotal.add(this.iva).subtract(this.descuento != null ? this.descuento : BigDecimal.ZERO);
        } else {
            this.subtotal = BigDecimal.ZERO;
            this.iva = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
        }

        // Asegurar que los valores no sean nulos
        if (this.subtotal == null) {
            this.subtotal = BigDecimal.ZERO;
        }
        if (this.iva == null) {
            this.iva = BigDecimal.ZERO;
        }
        if (this.total == null) {
            this.total = BigDecimal.ZERO;
        }
        if (this.descuento == null) {
            this.descuento = BigDecimal.ZERO;
        }
    }
}

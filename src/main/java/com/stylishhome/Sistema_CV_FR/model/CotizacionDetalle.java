package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entidad que representa los detalles de una cotización
 */
@Entity
@Table(name = "cotizacion_detalles")
@Data
public class CotizacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcotizacion", nullable = false)
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @Column(name = "descuento_monto", precision = 15, scale = 2)
    private BigDecimal descuentoMonto = BigDecimal.ZERO;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Calcula el total del detalle antes de persistir
     */
    @PrePersist
    @PreUpdate
    public void calcularTotal() {
        // Validar que tenemos los valores necesarios
        if (this.precioUnitario == null) {
            this.precioUnitario = BigDecimal.ZERO;
        }
        if (this.cantidad == null) {
            this.cantidad = 1;
        }
        if (this.descuentoMonto == null) {
            this.descuentoMonto = BigDecimal.ZERO;
        }
        if (this.descuentoPorcentaje == null) {
            this.descuentoPorcentaje = BigDecimal.ZERO;
        }

        // Calcular subtotal
        BigDecimal subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));

        // Calcular descuento total
        BigDecimal descuentoTotal = this.descuentoMonto;

        // Si hay descuento por porcentaje, calcularlo
        if (this.descuentoPorcentaje.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoPorPorcentaje = subtotal.multiply(this.descuentoPorcentaje)
                    .divide(new BigDecimal(100));
            // Sumar ambos descuentos (monto + porcentaje)
            descuentoTotal = descuentoTotal.add(descuentoPorPorcentaje);
        }

        // Asegurar que el descuento no sea mayor al subtotal
        if (descuentoTotal.compareTo(subtotal) > 0) {
            descuentoTotal = subtotal;
        }

        this.total = subtotal.subtract(descuentoTotal);

        // Asegurar que el total no sea negativo
        if (this.total.compareTo(BigDecimal.ZERO) < 0) {
            this.total = BigDecimal.ZERO;
        }
    }

    /**
     * Establece el precio unitario y recalcula automáticamente
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario != null ? precioUnitario : BigDecimal.ZERO;
        calcularTotal();
    }

    /**
     * Establece la cantidad y recalcula automáticamente
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad != null ? cantidad : 1;
        calcularTotal();
    }

    /**
     * Establece el descuento por porcentaje y recalcula automáticamente
     */
    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje != null ? descuentoPorcentaje : BigDecimal.ZERO;
        calcularTotal();
    }

    /**
     * Establece el descuento por monto y recalcula automáticamente
     */
    public void setDescuentoMonto(BigDecimal descuentoMonto) {
        this.descuentoMonto = descuentoMonto != null ? descuentoMonto : BigDecimal.ZERO;
        calcularTotal();
    }

    /**
     * Método helper para obtener descripción del producto
     */
    public String getDescripcionProducto() {
        return this.producto != null ? this.producto.getNombre() : "Producto no disponible";
    }

    /**
     * Método helper para obtener código del producto
     */
    public String getCodigoProducto() {
        return this.producto != null ? this.producto.getCodigo() : "N/A";
    }
}

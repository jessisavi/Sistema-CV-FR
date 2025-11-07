package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entidad que representa los detalles de una venta
 */
@Entity
@Table(name = "venta_detalles")
@Data
public class VentaDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idventa", nullable = false)
    private Venta venta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto producto;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario", precision = 15, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;
    
    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    /**
     * Calcula el total del detalle antes de persistir
     */
    @PrePersist
    @PreUpdate
    public void calcularTotal() {
        // Validar valores nulos
        if (this.precioUnitario == null) {
            this.precioUnitario = BigDecimal.ZERO;
        }
        if (this.cantidad == null) {
            this.cantidad = 1;
        }
        
        this.total = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        
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
}
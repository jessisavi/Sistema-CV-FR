package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el inventario
 */
@Entity
@Table(name = "productos")
@Data
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto")
    private Integer idProducto;
    
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;
    
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "color", length = 30)
    private String color;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "MT", length = 10)
    private String unidadMedida;
    
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;
    
    @Column(name = "ubicacion", length = 50)
    private String ubicacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;
    
    @Column(name = "acabado", length = 50)
    private String acabado;
    
    @Column(name = "trafico", length = 50)
    private String trafico;
    
    @Column(name = "rectificado", length = 10)
    private String rectificado = "0";
    
    @Column(name = "imagen", length = 100)
    private String imagen;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}
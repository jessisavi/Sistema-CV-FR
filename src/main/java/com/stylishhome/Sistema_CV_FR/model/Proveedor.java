package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa un proveedor de productos
 */
@Entity
@Table(name = "proveedores")
@Data
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproveedor")
    private Integer idProveedor;
    
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    
    @Column(name = "contacto", length = 50)
    private String contacto;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;
}
package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entidad que representa un cliente del sistema
 */
@Entity
@Table(name = "clientes")
@Data
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcliente")
    private Integer idCliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idempleado")
    private Usuario empleadoAsignado;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
    
    @Column(name = "tipo_documento", length = 10)
    private String tipoDocumento;
    
    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;
    
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;
    
    @Column(name = "ciudad", length = 100)
    private String ciudad;
    
    @Column(name = "celular", length = 20)
    private String celular;
    
    @Column(name = "correo_electronico", length = 100)
    private String correoElectronico;
    
    @Column(name = "tipo_cliente", length = 20)
    private String tipoCliente = "Regular";
}
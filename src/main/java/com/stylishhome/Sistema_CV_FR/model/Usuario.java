package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa un usuario/empleado del sistema
 */
@Entity
@Table(name = "portalempleados")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idempleado")
    private Integer idEmpleado;

    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(name = "contraseña", nullable = false, length = 50)
    private String contraseña;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol = "empleado";

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean tieneRol(String nombreRol) {
        return this.rol != null && this.rol.equalsIgnoreCase(nombreRol);
    }
}

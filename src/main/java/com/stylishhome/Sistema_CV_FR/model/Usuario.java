package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    @Column(name = "contraseña", nullable = false, length = 100)
    private String contraseña;
    
    @Column(name = "rol", nullable = false, length = 50)
    private String rol = "empleado";
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    // Relación muchos a muchos con roles
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<Rol> roles = new ArrayList<>();
    
    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean tieneRol(String nombreRol) {
        return roles.stream().anyMatch(rol -> rol.getNombre().equals(nombreRol));
    }
    
    /**
     * Verifica si el usuario tiene un permiso específico
     */
    public boolean tienePermiso(String permiso) {
        return roles.stream()
                .filter(Rol::getActivo)
                .flatMap(rol -> rol.getPermisos().stream())
                .anyMatch(p -> p.equals(permiso));
    }
}
package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un rol en el sistema
 */
@Entity
@Table(name = "roles")
@Data
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;
    
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relación con permisos
    @ElementCollection
    @CollectionTable(name = "rol_permisos", joinColumns = @JoinColumn(name = "rol_id"))
    @Column(name = "permiso")
    private List<String> permisos = new ArrayList<>();
    
    // Relación inversa ManyToMany con Usuario
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
    
    //Roles predefinidos del sistema
    public static class Nombres {
        public static final String ADMINISTRADOR = "Administrador";
        public static final String GERENTE = "Gerente";
        public static final String ASESOR = "Asesor";
        public static final String EMPLEADO = "empleado";
    }
    
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
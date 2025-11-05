package com.stylishhome.Sistema_CV_FR.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa un rol en el sistema.
 //Los roles definen los permisos y niveles de acceso de los usuarios.

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    
    //Identificador único del rol.
    // Generado automáticamente por la base de datos.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    //Nombre del rol.
    //Ejemplos: ADMIN, ASESOR, GERENTE
    //Debe ser único en el sistema.
    
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;
    
    //Descripción detallada del rol y sus responsabilidades.
  
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    
    //Número de usuarios asignados a este rol.
    
    @Transient
    private Integer numeroUsuarios = 0;
    
    // Lista de permisos asociados al rol.
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "rol_permisos",
        joinColumns = @JoinColumn(name = "rol_id")
    )
    @Column(name = "permiso")
    private List<String> permisos = new ArrayList<>();
    
    // Indica si el rol está activo en el sistema.
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
   
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    //constructor
    public Rol(Long id, String nombre, String descripcion, Integer numeroUsuarios) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.numeroUsuarios = numeroUsuarios;
    }
    
    //Método ejecutado automáticamente antes de persistir la entidad.
    //Inicializa las fechas de creación y actualización.
     
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        
        // Inicializar lista de permisos si es null
        if (this.permisos == null) {
            this.permisos = new ArrayList<>();
        }
    }
    
    //Método ejecutado automáticamente antes de actualizar la entidad.
    // Actualiza la fecha de modificación.
     
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    //Agrega un permiso a la lista de permisos del rol.
    // @param permiso Permiso a agregar
     
    public void agregarPermiso(String permiso) {
        if (this.permisos == null) {
            this.permisos = new ArrayList<>();
        }
        if (!this.permisos.contains(permiso)) {
            this.permisos.add(permiso);
        }
    }
    
    //Elimina un permiso de la lista de permisos del rol.
    //@param permiso Permiso a eliminar
     
    public void eliminarPermiso(String permiso) {
        if (this.permisos != null) {
            this.permisos.remove(permiso);
        }
    }
    
    //Verifica si el rol tiene un permiso específico.
    // @param permiso Permiso a verificar
    //@return true si el rol tiene el permiso, false en caso contrario
     
    public boolean tienePermiso(String permiso) {
        return this.permisos != null && this.permisos.contains(permiso);
    }
}

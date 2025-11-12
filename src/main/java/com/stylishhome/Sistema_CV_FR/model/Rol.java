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
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "rol_permisos", 
        joinColumns = @JoinColumn(name = "rol_id")
    )
    @Column(name = "permiso")
    private List<String> permisos = new ArrayList<>();
    
    // Roles predefinidos del sistema
    public static class Nombres {
        public static final String ADMINISTRADOR = "Administrador";
        public static final String GERENTE = "Gerente";
        public static final String ASESOR = "Asesor";
    }
    
    // Constantes para permisos del sistema
    public static class Permisos {
        // Permisos de Ventas
        public static final String VENTAS_ACCESO = "VENTAS_ACCESO";
        public static final String VENTAS_CREAR = "VENTAS_CREAR";
        public static final String VENTAS_EDITAR = "VENTAS_EDITAR";
        public static final String VENTAS_ELIMINAR = "VENTAS_ELIMINAR";
        public static final String VENTAS_DETALLE_ACCESO = "VENTAS_DETALLE_ACCESO";
        
        // Permisos de Productos
        public static final String PRODUCTOS_ACCESO = "PRODUCTOS_ACCESO";
        public static final String PRODUCTOS_CREAR = "PRODUCTOS_CREAR";
        public static final String PRODUCTOS_EDITAR = "PRODUCTOS_EDITAR";
        public static final String PRODUCTOS_ELIMINAR = "PRODUCTOS_ELIMINAR";
        
        // Permisos de Clientes
        public static final String CLIENTES_ACCESO = "CLIENTES_ACCESO";
        public static final String CLIENTES_CREAR = "CLIENTES_CREAR";
        public static final String CLIENTES_EDITAR = "CLIENTES_EDITAR";
        public static final String CLIENTES_ELIMINAR = "CLIENTES_ELIMINAR";
        
        // Permisos de Categorías
        public static final String CATEGORIAS_ACCESO = "CATEGORIAS_ACCESO";
        public static final String CATEGORIAS_CREAR = "CATEGORIAS_CREAR";
        public static final String CATEGORIAS_EDITAR = "CATEGORIAS_EDITAR";
        public static final String CATEGORIAS_ELIMINAR = "CATEGORIAS_ELIMINAR";
        
        // Permisos de Cotizaciones
        public static final String COTIZACIONES_ACCESO = "COTIZACIONES_ACCESO";
        public static final String COTIZACIONES_CREAR = "COTIZACIONES_CREAR";
        public static final String COTIZACIONES_EDITAR = "COTIZACIONES_EDITAR";
        public static final String COTIZACIONES_ELIMINAR = "COTIZACIONES_ELIMINAR";
        public static final String COTIZACION_DETALLE_ACCESO = "COTIZACION_DETALLE_ACCESO";
        public static final String COTIZACION_DETALLE_EDITAR = "COTIZACION_DETALLE_EDITAR";
        
        // Permisos de Proveedores
        public static final String PROVEEDORES_ACCESO = "PROVEEDORES_ACCESO";
        public static final String PROVEEDORES_CREAR = "PROVEEDORES_CREAR";
        public static final String PROVEEDORES_EDITAR = "PROVEEDORES_EDITAR";
        public static final String PROVEEDORES_ELIMINAR = "PROVEEDORES_ELIMINAR";
        
        // Permisos de Informes
        public static final String INFORMES_ACCESO = "INFORMES_ACCESO";
        public static final String INFORMES_GENERAR = "INFORMES_GENERAR";
        public static final String INFORMES_EXPORTAR = "INFORMES_EXPORTAR";
        
        // Permisos de Empleados
        public static final String PORTALEMPLEADOS_ACCESO = "PORTALEMPLEADOS_ACCESO";
        public static final String PORTALEMPLEADOS_CREAR = "PORTALEMPLEADOS_CREAR";
        public static final String PORTALEMPLEADOS_EDITAR = "PORTALEMPLEADOS_EDITAR";
        public static final String PORTALEMPLEADOS_ELIMINAR = "PORTALEMPLEADOS_ELIMINAR";
        
        // Permisos de Roles
        public static final String ROLES_ACCESO = "ROLES_ACCESO";
        public static final String ROLES_CREAR = "ROLES_CREAR";
        public static final String ROLES_EDITAR = "ROLES_EDITAR";
        public static final String ROLES_ELIMINAR = "ROLES_ELIMINAR";
        public static final String ROL_PERMISOS_ACCESO = "ROL_PERMISOS_ACCESO";
        public static final String ROL_PERMISOS_EDITAR = "ROL_PERMISOS_EDITAR";
    }
    
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Verifica si el rol tiene un permiso específico
     * @param permiso Permiso a verificar
     * @return true si tiene el permiso, false en caso contrario
     */
    public boolean tienePermiso(String permiso) {
        return this.permisos != null && this.permisos.contains(permiso);
    }
    
    /**
     * Verifica si el rol tiene al menos uno de los permisos especificados
     * @param permisos Lista de permisos a verificar
     * @return true si tiene al menos uno de los permisos
     */
    public boolean tieneAlgunPermiso(List<String> permisos) {
        if (this.permisos == null || permisos == null) return false;
        return permisos.stream().anyMatch(this.permisos::contains);
    }
}
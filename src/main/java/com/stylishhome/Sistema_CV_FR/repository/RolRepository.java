package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de roles del sistema
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByNombre(String nombre);
    
    List<Rol> findByNombreContainingIgnoreCase(String nombre);
    
    List<Rol> findByActivoTrue();
    
    List<Rol> findByActivoFalse();
    
    boolean existsByNombre(String nombre);
    
    List<Rol> findAllByOrderByNombreAsc();
    
    List<Rol> findAllByOrderByFechaCreacionDesc();
    
    Long countByActivoTrue();
    
    Long countByActivoFalse();
    
    //Consulta para buscar roles por permiso usando la relación @ElementCollection
    @Query("SELECT DISTINCT r FROM Rol r JOIN r.permisos p WHERE p = :permiso")
    List<Rol> findByPermisosContaining(@Param("permiso") String permiso);
    
    @Query("SELECT r FROM Rol r WHERE " +
           "(:nombre IS NULL OR r.nombre LIKE %:nombre%) AND " +
           "(:activo IS NULL OR r.activo = :activo)")
    List<Rol> buscarRoles(@Param("nombre") String nombre, 
                         @Param("activo") Boolean activo);
    
    //Consulta para obtener permisos únicos
    @Query("SELECT DISTINCT p FROM Rol r JOIN r.permisos p WHERE r.activo = true ORDER BY p")
    List<String> findPermisosUnicos();
    
    //Consulta para roles sin permisos
    @Query("SELECT r FROM Rol r WHERE SIZE(r.permisos) = 0")
    List<Rol> findRolesSinPermisos();
    
    //Consulta para obtener los últimos N roles
    @Query("SELECT r FROM Rol r ORDER BY r.fechaCreacion DESC")
    List<Rol> findTopNByOrderByFechaCreacionDesc(@Param("limite") int limite);
    
    //Consulta para verificar si un rol tiene un permiso específico
    @Query("SELECT COUNT(r) > 0 FROM Rol r JOIN r.permisos p WHERE r.id = :rolId AND p = :permiso")
    boolean tienePermiso(@Param("rolId") Long rolId, @Param("permiso") String permiso);
    
    //Buscar roles por múltiples permisos
    @Query("SELECT DISTINCT r FROM Rol r JOIN r.permisos p WHERE p IN :permisos")
    List<Rol> findByPermisosEn(@Param("permisos") List<String> permisos);
}
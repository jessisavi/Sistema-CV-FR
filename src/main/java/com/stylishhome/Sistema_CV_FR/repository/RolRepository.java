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
 * Proporciona métodos para operaciones CRUD y consultas personalizadas de roles
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    /**
     * Busca un rol por su nombre
     * @param nombre Nombre del rol a buscar
     * @return Optional con el rol encontrado o empty si no existe
     */
    Optional<Rol> findByNombre(String nombre);
    
    /**
     * Busca roles por nombre (búsqueda parcial case-insensitive)
     * @param nombre Fragmento del nombre a buscar
     * @return Lista de roles que coinciden con el criterio
     */
    List<Rol> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca roles activos
     * @return Lista de roles con estado activo
     */
    List<Rol> findByActivoTrue();
    
    /**
     * Busca roles inactivos
     * @return Lista de roles con estado inactivo
     */
    List<Rol> findByActivoFalse();
    
    /**
     * Verifica si existe un rol con el nombre especificado
     * @param nombre Nombre del rol a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtiene todos los roles ordenados por nombre de forma ascendente
     * @return Lista de roles ordenados alfabéticamente
     */
    List<Rol> findAllByOrderByNombreAsc();
    
    /**
     * Obtiene roles ordenados por fecha de creación (más recientes primero)
     * @return Lista de roles ordenados por fecha descendente
     */
    List<Rol> findAllByOrderByFechaCreacionDesc();
    
    /**
     * Cuenta el número de roles activos
     * @return Número de roles activos
     */
    Long countByActivoTrue();
    
    /**
     * Cuenta el número de roles inactivos
     * @return Número de roles inactivos
     */
    Long countByActivoFalse();
    
    /**
     * Busca roles que contengan un permiso específico
     * @param permiso Permiso a buscar
     * @return Lista de roles que tienen el permiso
     */
    @Query("SELECT r FROM Rol r JOIN r.permisos p WHERE p = :permiso")
    List<Rol> findByPermisosContaining(@Param("permiso") String permiso);
    
    /**
     * Busca roles por múltiples criterios
     * @param nombre Nombre del rol (opcional)
     * @param activo Estado activo/inactivo (opcional)
     * @return Lista de roles que coinciden con los criterios
     */
    @Query("SELECT r FROM Rol r WHERE " +
           "(:nombre IS NULL OR r.nombre LIKE %:nombre%) AND " +
           "(:activo IS NULL OR r.activo = :activo)")
    List<Rol> buscarRoles(@Param("nombre") String nombre, 
                         @Param("activo") Boolean activo);
    
    /**
     * Obtiene los permisos únicos de todos los roles activos
     * @return Lista de permisos únicos
     */
    @Query("SELECT DISTINCT p FROM Rol r JOIN r.permisos p WHERE r.activo = true ORDER BY p")
    List<String> findPermisosUnicos();
    
    /**
     * Obtiene roles con sus permisos para un usuario específico
     * @param usuarioId ID del usuario
     * @return Lista de roles asignados al usuario
     */
    @Query("SELECT r FROM Rol r JOIN r.usuarios u WHERE u.id = :usuarioId")
    List<Rol> findRolesByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    /**
     * Obtiene estadísticas de uso de roles
     * @return Lista con conteo de usuarios por rol
     */
    @Query("SELECT r.nombre, COUNT(u) FROM Rol r LEFT JOIN r.usuarios u GROUP BY r.nombre ORDER BY COUNT(u) DESC")
    List<Object[]> findEstadisticasUsoRoles();
    
    /**
     * Busca roles que no tienen permisos asignados
     * @return Lista de roles sin permisos
     */
    @Query("SELECT r FROM Rol r WHERE r.permisos IS EMPTY")
    List<Rol> findRolesSinPermisos();
    
    /**
     * Obtiene los N roles más recientes
     * @param limite Número máximo de roles a retornar
     * @return Lista de roles más recientes
     */
    @Query("SELECT r FROM Rol r ORDER BY r.fechaCreacion DESC LIMIT :limite")
    List<Rol> findTopNByOrderByFechaCreacionDesc(@Param("limite") int limite);
}
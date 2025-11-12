package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Rol;
import com.stylishhome.Sistema_CV_FR.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de roles
 * Contiene la lógica de negocio para operaciones relacionadas con roles
 */
@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    /**
     * Obtiene todos los roles activos ordenados por nombre
     * @return Lista de roles activos
     */
    public List<Rol> obtenerTodosLosRoles() {
        return rolRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Obtiene todos los roles activos
     * @return Lista de roles activos
     */
    public List<Rol> obtenerRolesActivos() {
        return rolRepository.findByActivoTrue();
    }

    /**
     * Busca un rol por su ID
     * @param id ID del rol a buscar
     * @return Optional con el rol encontrado o empty si no existe
     */
    public Optional<Rol> obtenerRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    /**
     * Busca un rol por su nombre
     * @param nombre Nombre del rol a buscar
     * @return Optional con el rol encontrado o empty si no existe
     */
    public Optional<Rol> obtenerRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    /**
     * Guarda un rol en la base de datos (crear o actualizar)
     * @param rol Rol a guardar
     * @return Rol guardado con ID generado
     * @throws RuntimeException Si el nombre del rol ya existe
     */
    public Rol guardarRol(Rol rol) {
        // Validar que el nombre no exista (excepto para actualizaciones)
        if (rol.getId() == null) {
            // Es un nuevo rol, verificar que el nombre no exista
            if (rolRepository.existsByNombre(rol.getNombre())) {
                throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombre());
            }
            // Establecer fecha de creación si no está definida
            if (rol.getFechaCreacion() == null) {
                rol.setFechaCreacion(LocalDateTime.now());
            }
        } else {
            // Es una actualización, verificar que el nombre no esté siendo usado por otro rol
            Optional<Rol> rolExistente = rolRepository.findById(rol.getId());
            if (rolExistente.isPresent() && 
                !rolExistente.get().getNombre().equals(rol.getNombre()) &&
                rolRepository.existsByNombre(rol.getNombre())) {
                throw new RuntimeException("Ya existe otro rol con el nombre: " + rol.getNombre());
            }
            // Actualizar fecha de modificación
            rol.setFechaActualizacion(LocalDateTime.now());
        }

        return rolRepository.save(rol);
    }

    /**
     * Elimina un rol por su ID (eliminación lógica)
     * @param id ID del rol a eliminar
     * @throws RuntimeException Si el rol no existe
     */
    public void eliminarRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));
        
        // Eliminación lógica en lugar de física
        rol.setActivo(false);
        rol.setFechaActualizacion(LocalDateTime.now());
        
        rolRepository.save(rol);
    }

    /**
     * Activa un rol previamente desactivado
     * @param id ID del rol a activar
     * @return Rol activado
     */
    public Rol activarRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));
        
        rol.setActivo(true);
        rol.setFechaActualizacion(LocalDateTime.now());
        
        return rolRepository.save(rol);
    }

    /**
     * Desactiva un rol
     * @param id ID del rol a desactivar
     * @return Rol desactivado
     */
    public Rol desactivarRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + id));
        
        rol.setActivo(false);
        rol.setFechaActualizacion(LocalDateTime.now());
        
        return rolRepository.save(rol);
    }

    /**
     * Verifica si existe un rol con el nombre especificado
     * @param nombre Nombre del rol a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeRol(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    /**
     * Busca roles por nombre (búsqueda parcial)
     * @param nombre Fragmento del nombre a buscar
     * @return Lista de roles que coinciden con el criterio
     */
    public List<Rol> buscarRolesPorNombre(String nombre) {
        return rolRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtiene roles que contienen un permiso específico
     * @param permiso Permiso a buscar
     * @return Lista de roles que tienen el permiso
     */
    public List<Rol> obtenerRolesPorPermiso(String permiso) {
        return rolRepository.findByPermisosContaining(permiso);
    }

    /**
     * Obtiene todos los permisos únicos del sistema
     * @return Lista de permisos únicos
     */
    public List<String> obtenerPermisosUnicos() {
        return rolRepository.findPermisosUnicos();
    }

    /**
     * Agrega un permiso a un rol
     * @param rolId ID del rol
     * @param permiso Permiso a agregar
     * @return Rol actualizado
     */
    public Rol agregarPermiso(Long rolId, String permiso) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + rolId));
        
        if (!rol.getPermisos().contains(permiso)) {
            rol.getPermisos().add(permiso);
            rol.setFechaActualizacion(LocalDateTime.now());
            return rolRepository.save(rol);
        }
        
        return rol;
    }

    /**
     * Remueve un permiso de un rol
     * @param rolId ID del rol
     * @param permiso Permiso a remover
     * @return Rol actualizado
     */
    public Rol removerPermiso(Long rolId, String permiso) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con ID: " + rolId));
        
        if (rol.getPermisos().contains(permiso)) {
            rol.getPermisos().remove(permiso);
            rol.setFechaActualizacion(LocalDateTime.now());
            return rolRepository.save(rol);
        }
        
        return rol;
    }

    /**
     * Obtiene el número total de roles activos
     * @return Total de roles activos
     */
    public Long contarRolesActivos() {
        return rolRepository.countByActivoTrue();
    }

    /**
     * Obtiene el número total de roles inactivos
     * @return Total de roles inactivos
     */
    public Long contarRolesInactivos() {
        return rolRepository.countByActivoFalse();
    }

    /**
     * Verifica si un rol tiene un permiso específico
     * @param rolId ID del rol
     * @param permiso Permiso a verificar
     * @return true si tiene el permiso, false en caso contrario
     */
    public boolean verificarPermiso(Long rolId, String permiso) {
        return rolRepository.tienePermiso(rolId, permiso);
    }

    /**
     * Obtiene estadísticas de uso de roles
     * @return Mapa con estadísticas de roles
     */
    public java.util.Map<String, Object> obtenerEstadisticasRoles() {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
        
        estadisticas.put("totalRoles", rolRepository.count());
        estadisticas.put("rolesActivos", contarRolesActivos());
        estadisticas.put("rolesInactivos", contarRolesInactivos());
        estadisticas.put("permisosUnicos", obtenerPermisosUnicos().size());
        
        return estadisticas;
    }

    /**
     * Inicializa los roles predefinidos del sistema con los permisos correctos
     */
    @Transactional
    public void inicializarRolesPredefinidos() {
        // Rol Administrador
        if (!rolRepository.existsByNombre(Rol.Nombres.ADMINISTRADOR)) {
            Rol admin = new Rol();
            admin.setNombre(Rol.Nombres.ADMINISTRADOR);
            admin.setDescripcion("Acceso a ventas, ventas detalle, productos, información de asesores, clientes, categorías, cotizaciones, proveedor, cotización detalle y informes");
            admin.setActivo(true);
            admin.setFechaCreacion(LocalDateTime.now());
            
            // Permisos de administrador basados en tu estructura
            admin.getPermisos().add(Rol.Permisos.VENTAS_ACCESO);
            admin.getPermisos().add(Rol.Permisos.VENTAS_DETALLE_ACCESO);
            admin.getPermisos().add(Rol.Permisos.PRODUCTOS_ACCESO);
            admin.getPermisos().add(Rol.Permisos.PRODUCTOS_CREAR);
            admin.getPermisos().add(Rol.Permisos.PRODUCTOS_EDITAR);
            admin.getPermisos().add(Rol.Permisos.CLIENTES_ACCESO);
            admin.getPermisos().add(Rol.Permisos.CLIENTES_CREAR);
            admin.getPermisos().add(Rol.Permisos.CLIENTES_EDITAR);
            admin.getPermisos().add(Rol.Permisos.CATEGORIAS_ACCESO);
            admin.getPermisos().add(Rol.Permisos.CATEGORIAS_CREAR);
            admin.getPermisos().add(Rol.Permisos.CATEGORIAS_EDITAR);
            admin.getPermisos().add(Rol.Permisos.COTIZACIONES_ACCESO);
            admin.getPermisos().add(Rol.Permisos.COTIZACIONES_CREAR);
            admin.getPermisos().add(Rol.Permisos.COTIZACIONES_EDITAR);
            admin.getPermisos().add(Rol.Permisos.PROVEEDORES_ACCESO);
            admin.getPermisos().add(Rol.Permisos.PROVEEDORES_CREAR);
            admin.getPermisos().add(Rol.Permisos.PROVEEDORES_EDITAR);
            admin.getPermisos().add(Rol.Permisos.COTIZACION_DETALLE_ACCESO);
            admin.getPermisos().add(Rol.Permisos.INFORMES_ACCESO);
            admin.getPermisos().add(Rol.Permisos.INFORMES_GENERAR);
            admin.getPermisos().add(Rol.Permisos.PORTALEMPLEADOS_ACCESO);
            
            rolRepository.save(admin);
        }

        // Rol Gerente
        if (!rolRepository.existsByNombre(Rol.Nombres.GERENTE)) {
            Rol gerente = new Rol();
            gerente.setNombre(Rol.Nombres.GERENTE);
            gerente.setDescripcion("Acceso completo a todo el sistema");
            gerente.setActivo(true);
            gerente.setFechaCreacion(LocalDateTime.now());
            
            // Permisos completos del gerente
            gerente.getPermisos().addAll(List.of(
                Rol.Permisos.VENTAS_ACCESO,
                Rol.Permisos.VENTAS_CREAR,
                Rol.Permisos.VENTAS_EDITAR,
                Rol.Permisos.VENTAS_ELIMINAR,
                Rol.Permisos.VENTAS_DETALLE_ACCESO,
                Rol.Permisos.PRODUCTOS_ACCESO,
                Rol.Permisos.PRODUCTOS_CREAR,
                Rol.Permisos.PRODUCTOS_EDITAR,
                Rol.Permisos.PRODUCTOS_ELIMINAR,
                Rol.Permisos.CLIENTES_ACCESO,
                Rol.Permisos.CLIENTES_CREAR,
                Rol.Permisos.CLIENTES_EDITAR,
                Rol.Permisos.CLIENTES_ELIMINAR,
                Rol.Permisos.CATEGORIAS_ACCESO,
                Rol.Permisos.CATEGORIAS_CREAR,
                Rol.Permisos.CATEGORIAS_EDITAR,
                Rol.Permisos.CATEGORIAS_ELIMINAR,
                Rol.Permisos.COTIZACIONES_ACCESO,
                Rol.Permisos.COTIZACIONES_CREAR,
                Rol.Permisos.COTIZACIONES_EDITAR,
                Rol.Permisos.COTIZACIONES_ELIMINAR,
                Rol.Permisos.COTIZACION_DETALLE_ACCESO,
                Rol.Permisos.COTIZACION_DETALLE_EDITAR,
                Rol.Permisos.PROVEEDORES_ACCESO,
                Rol.Permisos.PROVEEDORES_CREAR,
                Rol.Permisos.PROVEEDORES_EDITAR,
                Rol.Permisos.PROVEEDORES_ELIMINAR,
                Rol.Permisos.INFORMES_ACCESO,
                Rol.Permisos.INFORMES_GENERAR,
                Rol.Permisos.INFORMES_EXPORTAR,
                Rol.Permisos.PORTALEMPLEADOS_ACCESO,
                Rol.Permisos.PORTALEMPLEADOS_CREAR,
                Rol.Permisos.PORTALEMPLEADOS_EDITAR,
                Rol.Permisos.PORTALEMPLEADOS_ELIMINAR,
                Rol.Permisos.ROLES_ACCESO,
                Rol.Permisos.ROLES_CREAR,
                Rol.Permisos.ROLES_EDITAR,
                Rol.Permisos.ROLES_ELIMINAR,
                Rol.Permisos.ROL_PERMISOS_ACCESO,
                Rol.Permisos.ROL_PERMISOS_EDITAR
            ));
            
            rolRepository.save(gerente);
        }

        // Rol Asesor
        if (!rolRepository.existsByNombre(Rol.Nombres.ASESOR)) {
            Rol asesor = new Rol();
            asesor.setNombre(Rol.Nombres.ASESOR);
            asesor.setDescripcion("Acceso a ventas, ventas detalle, productos, clientes, categorías, cotizaciones, cotizaciones detalle, informes");
            asesor.setActivo(true);
            asesor.setFechaCreacion(LocalDateTime.now());
            
            // Permisos de asesor
            asesor.getPermisos().add(Rol.Permisos.VENTAS_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.VENTAS_CREAR);
            asesor.getPermisos().add(Rol.Permisos.VENTAS_DETALLE_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.PRODUCTOS_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.CLIENTES_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.CLIENTES_CREAR);
            asesor.getPermisos().add(Rol.Permisos.CLIENTES_EDITAR);
            asesor.getPermisos().add(Rol.Permisos.CATEGORIAS_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.COTIZACIONES_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.COTIZACIONES_CREAR);
            asesor.getPermisos().add(Rol.Permisos.COTIZACIONES_EDITAR);
            asesor.getPermisos().add(Rol.Permisos.COTIZACION_DETALLE_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.COTIZACION_DETALLE_EDITAR);
            asesor.getPermisos().add(Rol.Permisos.INFORMES_ACCESO);
            asesor.getPermisos().add(Rol.Permisos.INFORMES_GENERAR);
            
            rolRepository.save(asesor);
        }
    }
}
package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Búsqueda por nombre o apellido 
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:criterio% OR c.apellido LIKE %:criterio%")
    List<Cliente> findByNombreOrApellidoContaining(@Param("criterio") String criterio);

    //Métodos de búsqueda básicos
    List<Cliente> findByTipoCliente(String tipoCliente);

    List<Cliente> findByCiudad(String ciudad);

    List<Cliente> findByEstado(String estado);

    //Métodos de verificación de existencia
    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByCodigo(String codigo);

    boolean existsByEmail(String email);

    //Métodos de conteo
    Long countByTipoCliente(String tipoCliente);

    Long countByEstado(String estado);

    //Búsqueda por rangos de fecha
    List<Cliente> findByFechaRegistroBetween(LocalDate fechaInicio, LocalDate fechaFin);

    //Búsqueda con límite
    @Query("SELECT c FROM Cliente c ORDER BY c.fechaRegistro DESC LIMIT :limite")
    List<Cliente> findTopNByOrderByFechaRegistroDesc(@Param("limite") int limite);

    //Búsqueda avanzada con múltiples filtros 
    @Query("SELECT c FROM Cliente c WHERE "
            + "(:nombre IS NULL OR c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%) AND "
            + "(:tipoCliente IS NULL OR c.tipoCliente = :tipoCliente) AND "
            + "(:ciudad IS NULL OR c.ciudad LIKE %:ciudad%)")
    List<Cliente> buscarClientes(@Param("nombre") String nombre,
            @Param("tipoCliente") String tipoCliente,
            @Param("ciudad") String ciudad);

    // Búsqueda con filtros completos 
    @Query("SELECT c FROM Cliente c WHERE "
            + "(:search IS NULL OR c.nombre LIKE %:search% OR c.apellido LIKE %:search% OR c.email LIKE %:search% OR c.numeroDocumento LIKE %:search%) AND "
            + "(:tipo IS NULL OR c.tipoCliente = :tipo) AND "
            + "(:estado IS NULL OR c.estado = :estado)")
    List<Cliente> buscarClientesConFiltros(@Param("search") String search,
            @Param("tipo") String tipo,
            @Param("estado") String estado);

    //Búsqueda con TODOS los filtros 
    @Query("SELECT c FROM Cliente c WHERE "
            + "(:search IS NULL OR c.nombre LIKE %:search% OR c.apellido LIKE %:search% OR c.email LIKE %:search% OR c.numeroDocumento LIKE %:search%) AND "
            + "(:tipo IS NULL OR c.tipoCliente = :tipo) AND "
            + "(:estado IS NULL OR c.estado = :estado) AND "
            + "(:ciudad IS NULL OR c.ciudad LIKE %:ciudad%)")
    List<Cliente> buscarClientesCompletos(@Param("search") String search,
            @Param("tipo") String tipo,
            @Param("estado") String estado,
            @Param("ciudad") String ciudad);

    //Búsqueda por empleado asignado
    List<Cliente> findByEmpleadoAsignadoIdEmpleado(Integer idEmpleado);

    //Búsqueda por diferentes campos únicos
    Optional<Cliente> findByCorreoElectronico(String correoElectronico);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCodigo(String codigo);

    List<Cliente> findByEmpleadoAsignadoIsNull();

    //Estadísticas
    @Query("SELECT c.ciudad, COUNT(c) FROM Cliente c GROUP BY c.ciudad ORDER BY COUNT(c) DESC")
    List<Object[]> findEstadisticasPorCiudad();

    //Conteo por rango de fechas
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaRegistroBetween(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    //Búsqueda por teléfono o celular
    @Query("SELECT c FROM Cliente c WHERE c.telefono LIKE %:telefono% OR c.celular LIKE %:telefono%")
    List<Cliente> findByTelefonoOrCelularContaining(@Param("telefono") String telefono);

    //Búsqueda de clientes activos
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Activo' ORDER BY c.nombre, c.apellido")
    List<Cliente> findClientesActivos();

    //Búsqueda de clientes inactivos
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Inactivo' ORDER BY c.nombre, c.apellido")
    List<Cliente> findClientesInactivos();

    //Búsqueda por tipo y estado combinados
    List<Cliente> findByTipoClienteAndEstado(String tipoCliente, String estado);

    //Búsqueda por ciudad y estado
    List<Cliente> findByCiudadAndEstado(String ciudad, String estado);

    //Conteo por ciudad
    @Query("SELECT c.ciudad, COUNT(c) FROM Cliente c WHERE c.estado = 'Activo' GROUP BY c.ciudad")
    List<Object[]> countClientesActivosPorCiudad();

    //Obtener clientes con fecha de registro reciente
    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro >= :fecha ORDER BY c.fechaRegistro DESC")
    List<Cliente> findClientesRecientes(@Param("fecha") LocalDate fecha);

    //Búsqueda por nombre completo (nombre + apellido)
    @Query("SELECT c FROM Cliente c WHERE CONCAT(c.nombre, ' ', c.apellido) LIKE %:nombreCompleto%")
    List<Cliente> findByNombreCompletoContaining(@Param("nombreCompleto") String nombreCompleto);

    //Verificar si existe por email (alternativo)
    boolean existsByCorreoElectronico(String correoElectronico);

    //Obtener clientes por tipo de documento
    List<Cliente> findByTipoDocumento(String tipoDocumento);

    //Búsqueda por número de documento exacto
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);

    //Conteo total de clientes activos
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'Activo'")
    Long countClientesActivos();

    //Conteo total de clientes inactivos
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'Inactivo'")
    Long countClientesInactivos();

    //Contar nuevos clientes
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaRegistro = :hoy")
    Long countByFechaRegistro(@Param("hoy") LocalDate hoy);
}

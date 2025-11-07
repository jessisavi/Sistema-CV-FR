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
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%")
    List<Cliente> findByNombreOrApellidoContaining(@Param("nombre") String nombre);
    
    List<Cliente> findByTipoCliente(String tipoCliente);
    List<Cliente> findByCiudad(String ciudad);
    boolean existsByNumeroDocumento(String numeroDocumento);
    Long countByTipoCliente(String tipoCliente);
    List<Cliente> findByFechaRegistroBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    @Query("SELECT c FROM Cliente c ORDER BY c.fechaRegistro DESC LIMIT :limite")
    List<Cliente> findTopNByOrderByFechaRegistroDesc(@Param("limite") int limite);
    
    @Query("SELECT c FROM Cliente c WHERE " +
           "(:nombre IS NULL OR c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%) AND " +
           "(:tipoCliente IS NULL OR c.tipoCliente = :tipoCliente) AND " +
           "(:ciudad IS NULL OR c.ciudad LIKE %:ciudad%)")
    List<Cliente> buscarClientes(@Param("nombre") String nombre,
                               @Param("tipoCliente") String tipoCliente,
                               @Param("ciudad") String ciudad);
    
    List<Cliente> findByEmpleadoAsignadoIdEmpleado(Integer idEmpleado);
    Optional<Cliente> findByCorreoElectronico(String correoElectronico);
    
    @Query("SELECT c.ciudad, COUNT(c) FROM Cliente c GROUP BY c.ciudad ORDER BY COUNT(c) DESC")
    List<Object[]> findEstadisticasPorCiudad();
    
    List<Cliente> findByEmpleadoAsignadoIsNull();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaRegistroBetween(@Param("fechaInicio") LocalDate fechaInicio, 
                                   @Param("fechaFin") LocalDate fechaFin);
}
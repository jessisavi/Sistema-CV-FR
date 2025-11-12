package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    List<Cotizacion> findByClienteIdCliente(Integer idCliente);

    List<Cotizacion> findByEmpleadoIdEmpleado(Integer idEmpleado);

    List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado);

    List<Cotizacion> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT c FROM Cotizacion c WHERE c.validoHasta BETWEEN :hoy AND :limite AND c.estado = 'PENDIENTE'")
    List<Cotizacion> findCotizacionesPorVencer(@Param("hoy") LocalDate hoy,
            @Param("limite") LocalDate limite);

    @Query("SELECT c.estado, COUNT(c) FROM Cotizacion c GROUP BY c.estado")
    List<Object[]> countCotizacionesPorEstado();

    // Obtener últimas cotizaciones
    @Query("SELECT c FROM Cotizacion c ORDER BY c.fecha DESC LIMIT :limite")
    List<Cotizacion> findTopNByOrderByFechaDesc(@Param("limite") int limite);

    @Query("SELECT c FROM Cotizacion c LEFT JOIN FETCH c.detalles WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Cotizacion> findCotizacionesConDetallesPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // Informes
    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin AND c.estado = :estado")
    Long countByFechaBetweenAndEstado(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estado") Cotizacion.EstadoCotizacion estado);

    @Query("SELECT c, cli.nombre, cli.apellido, u.usuario, SUM(cd.cantidad) "
            + "FROM Cotizacion c "
            + "JOIN c.cliente cli "
            + "JOIN c.empleado u "
            + "LEFT JOIN c.detalles cd "
            + "WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin "
            + "GROUP BY c.idCotizacion, c.fecha, c.validoHasta, c.total, c.estado, cli.nombre, cli.apellido, u.usuario")
    List<Object[]> findCotizacionesDetalladasPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT c.estado, COUNT(c), SUM(c.total) "
            + "FROM Cotizacion c "
            + "WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin "
            + "GROUP BY c.estado")
    List<Object[]> countCotizacionesPorEstadoPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT u.usuario, COUNT(c), SUM(c.total) "
            + "FROM Cotizacion c "
            + "JOIN c.empleado u "
            + "WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin "
            + "GROUP BY u.usuario")
    List<Object[]> findCotizacionesPorVendedorPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}

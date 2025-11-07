package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    List<Venta> findByClienteIdCliente(Integer idCliente);
    List<Venta> findByEmpleadoIdEmpleado(Integer idEmpleado);
    
    List<Venta> findByEstado(Venta.EstadoVenta estado);
    List<Venta> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    Optional<Venta> findByNumeroFactura(String numeroFactura);
    
    @Query("SELECT v.metodoPago, COUNT(v) FROM Venta v WHERE v.estado = 'COMPLETADA' GROUP BY v.metodoPago")
    List<Object[]> countVentasPorMetodoPago();
    
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findVentasConDetallesPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
                                            @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    Double calcularTotalVentasPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                    @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio, 
                           @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = :estado")
    Long countByFechaBetweenAndEstado(@Param("fechaInicio") LocalDate fechaInicio,
                                    @Param("fechaFin") LocalDate fechaFin,
                                    @Param("estado") Venta.EstadoVenta estado);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = :estado")
    Long countByEstado(@Param("estado") Venta.EstadoVenta estado);
    
    @Query("SELECT v, c.nombre, c.apellido, u.usuario, SUM(vd.cantidad) " +
           "FROM Venta v " +
           "JOIN v.cliente c " +
           "JOIN v.empleado u " +
           "LEFT JOIN v.detalles vd " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY v.idVenta, v.numeroFactura, v.fecha, v.total, v.estado, v.metodoPago, c.nombre, c.apellido, u.usuario")
    List<Object[]> findVentasDetalladasPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
                                              @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT v.metodoPago, COUNT(v), SUM(v.total) " +
           "FROM Venta v " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA' " +
           "GROUP BY v.metodoPago")
    List<Object[]> countVentasPorMetodoPagoPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT u.usuario, COUNT(v), SUM(v.total) " +
           "FROM Venta v " +
           "JOIN v.empleado u " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA' " +
           "GROUP BY u.usuario")
    List<Object[]> findVentasPorVendedorPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                              @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT COALESCE(SUM(vd.cantidad), 0) " +
           "FROM Venta v " +
           "JOIN v.detalles vd " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    Long sumProductosVendidosPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                   @Param("fechaFin") LocalDate fechaFin);
}
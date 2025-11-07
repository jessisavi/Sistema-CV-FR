package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.InformeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la gestión de logs de informes
 */
@Repository
public interface InformeLogRepository extends JpaRepository<InformeLog, Integer> {
    
    /**
     * Busca logs por tipo de informe
     */
    List<InformeLog> findByTipoInforme(String tipoInforme);
    
    /**
     * Busca logs por empleado 
     */
    List<InformeLog> findByEmpleado_IdEmpleado(Integer empleadoId);
    
    /**
     * Busca logs por rango de fechas de generación
     */
    List<InformeLog> findByFechaGeneracionBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Obtiene los tipos de informe más utilizados
     */
    @Query("SELECT il.tipoInforme, COUNT(il) FROM InformeLog il GROUP BY il.tipoInforme ORDER BY COUNT(il) DESC")
    List<Object[]> findTiposInformeMasUtilizados();
    
    /**
     * Obtiene estadísticas de generación de informes por empleado
     */
    @Query("SELECT il.empleado.usuario, COUNT(il) FROM InformeLog il GROUP BY il.empleado.usuario ORDER BY COUNT(il) DESC")
    List<Object[]> findEstadisticasPorEmpleado();

    /**
     * Obtiene todos los logs de informes ordenados por fecha de generación descendente
     */
    List<InformeLog> findAllByOrderByFechaGeneracionDesc();

    /**
     * Busca logs por empleado y ordena por fecha descendente
     */
    List<InformeLog> findByEmpleado_IdEmpleadoOrderByFechaGeneracionDesc(Integer empleadoId);

    /**
     * Busca logs por tipo de informe y ordena por fecha descendente
     */
    List<InformeLog> findByTipoInformeOrderByFechaGeneracionDesc(String tipoInforme);

    /**
     * Cuenta los logs de informes por tipo
     */
    Long countByTipoInforme(String tipoInforme);

    /**
     * Obtiene los últimos N logs de informes
     */
    @Query("SELECT il FROM InformeLog il ORDER BY il.fechaGeneracion DESC LIMIT :limite")
    List<InformeLog> findTopNByOrderByFechaGeneracionDesc(@Param("limite") int limite);

    /**
     * Busca logs por rango de fechas de informe
     */
    @Query("SELECT il FROM InformeLog il WHERE il.fechaInicio BETWEEN :fechaInicio AND :fechaFin OR il.fechaFin BETWEEN :fechaInicio AND :fechaFin")
    List<InformeLog> findByRangoFechasInforme(@Param("fechaInicio") LocalDate fechaInicio, 
                                            @Param("fechaFin") LocalDate fechaFin);
}
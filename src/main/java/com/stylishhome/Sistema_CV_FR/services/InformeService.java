package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.InformeLog;
import com.stylishhome.Sistema_CV_FR.repository.InformeLogRepository;
import com.stylishhome.Sistema_CV_FR.repository.VentaRepository;
import com.stylishhome.Sistema_CV_FR.repository.CotizacionRepository;
import com.stylishhome.Sistema_CV_FR.repository.ClienteRepository;
import com.stylishhome.Sistema_CV_FR.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para la generación de informes y reportes
 */
@Service
public class InformeService {

    @Autowired
    private InformeLogRepository informeLogRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Genera un informe de resumen general
     */
    public Map<String, Object> generarInformeResumenGeneral(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId) {
        Map<String, Object> informe = new HashMap<>();

        // Estadísticas de ventas
        Double totalVentas = ventaRepository.calcularTotalVentasPeriodo(fechaInicio, fechaFin);
        Long ventasCompletadas = ventaRepository.countByFechaBetweenAndEstado(fechaInicio, fechaFin,
                com.stylishhome.Sistema_CV_FR.model.Venta.EstadoVenta.COMPLETADA);

        // Estadísticas de cotizaciones
        Long totalCotizaciones = cotizacionRepository.countByFechaBetween(fechaInicio, fechaFin);
        Long cotizacionesAprobadas = cotizacionRepository.countByFechaBetweenAndEstado(fechaInicio, fechaFin,
                com.stylishhome.Sistema_CV_FR.model.Cotizacion.EstadoCotizacion.APROBADA);

        // Estadísticas de clientes
        Long nuevosClientes = clienteRepository.countByFechaRegistroBetween(fechaInicio, fechaFin);

        informe.put("fechaInicio", fechaInicio);
        informe.put("fechaFin", fechaFin);
        informe.put("totalVentas", totalVentas != null ? totalVentas : 0.0);
        informe.put("ventasCompletadas", ventasCompletadas != null ? ventasCompletadas : 0L);
        informe.put("totalCotizaciones", totalCotizaciones != null ? totalCotizaciones : 0L);
        informe.put("cotizacionesAprobadas", cotizacionesAprobadas != null ? cotizacionesAprobadas : 0L);
        informe.put("nuevosClientes", nuevosClientes != null ? nuevosClientes : 0L);
        informe.put("productosVendidos", contarProductosVendidos(fechaInicio, fechaFin));

        return informe;
    }

    /**
     * Genera un informe detallado de ventas
     */
    public Map<String, Object> generarInformeVentas(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId) {
        Map<String, Object> informe = new HashMap<>();

        List<Object[]> ventasDetalladas = ventaRepository.findVentasDetalladasPorFecha(fechaInicio, fechaFin);
        List<Object[]> ventasPorMetodoPago = ventaRepository.countVentasPorMetodoPagoPeriodo(fechaInicio, fechaFin);
        List<Object[]> ventasPorVendedor = ventaRepository.findVentasPorVendedorPeriodo(fechaInicio, fechaFin);

        Double totalPeriodo = ventaRepository.calcularTotalVentasPeriodo(fechaInicio, fechaFin);

        informe.put("fechaInicio", fechaInicio);
        informe.put("fechaFin", fechaFin);
        informe.put("ventasDetalladas", ventasDetalladas != null ? ventasDetalladas : List.of());
        informe.put("ventasPorMetodoPago", ventasPorMetodoPago != null ? ventasPorMetodoPago : List.of());
        informe.put("ventasPorVendedor", ventasPorVendedor != null ? ventasPorVendedor : List.of());
        informe.put("totalPeriodo", totalPeriodo != null ? totalPeriodo : 0.0);

        return informe;
    }

    /**
     * Genera un informe detallado de cotizaciones
     */
    public Map<String, Object> generarInformeCotizaciones(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId) {
        Map<String, Object> informe = new HashMap<>();

        List<Object[]> cotizacionesDetalladas = cotizacionRepository.findCotizacionesDetalladasPorFecha(fechaInicio, fechaFin);
        List<Object[]> cotizacionesPorEstado = cotizacionRepository.countCotizacionesPorEstadoPeriodo(fechaInicio, fechaFin);
        List<Object[]> cotizacionesPorVendedor = cotizacionRepository.findCotizacionesPorVendedorPeriodo(fechaInicio, fechaFin);

        Long totalCotizaciones = cotizacionRepository.countByFechaBetween(fechaInicio, fechaFin);

        informe.put("fechaInicio", fechaInicio);
        informe.put("fechaFin", fechaFin);
        informe.put("cotizacionesDetalladas", cotizacionesDetalladas != null ? cotizacionesDetalladas : List.of());
        informe.put("cotizacionesPorEstado", cotizacionesPorEstado != null ? cotizacionesPorEstado : List.of());
        informe.put("cotizacionesPorVendedor", cotizacionesPorVendedor != null ? cotizacionesPorVendedor : List.of());
        informe.put("totalCotizaciones", totalCotizaciones != null ? totalCotizaciones : 0L);
        informe.put("tasaConversion", calcularTasaConversionCotizaciones(fechaInicio, fechaFin));

        return informe;
    }

    /**
     * Genera un informe de productos más vendidos
     */
    public Map<String, Object> generarInformeProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> informe = new HashMap<>();

        List<Object[]> productosMasVendidos = productoRepository.findProductosMasVendidosPeriodo(fechaInicio, fechaFin);
        List<Object[]> categoriasMasVendidas = productoRepository.findCategoriasMasVendidasPeriodo(fechaInicio, fechaFin);

        informe.put("fechaInicio", fechaInicio);
        informe.put("fechaFin", fechaFin);
        informe.put("productosMasVendidos", productosMasVendidos != null ? productosMasVendidos : List.of());
        informe.put("categoriasMasVendidas", categoriasMasVendidas != null ? categoriasMasVendidas : List.of());

        return informe;
    }

    /**
     * Registra la generación de un informe en el log
     */
    public void registrarGeneracionInforme(String tipoInforme, LocalDate fechaInicio, LocalDate fechaFin,
            Integer empleadoId, String parametros) {
        try {
            InformeLog log = new InformeLog();
            log.setTipoInforme(tipoInforme);
            log.setFechaInicio(fechaInicio);
            log.setFechaFin(fechaFin);

            // Usar el servicio de usuario para obtener el empleado
            usuarioService.obtenerUsuarioPorId(empleadoId).ifPresent(log::setEmpleado);

            log.setParametros(parametros);

            informeLogRepository.save(log);
        } catch (Exception e) {
            // Log del error pero no interrumpir el flujo principal
            System.err.println("Error registrando log de informe: " + e.getMessage());
        }
    }

    /**
     * Obtiene el historial de generación de informes
     */
    public List<InformeLog> obtenerHistorialInformes() {
        return informeLogRepository.findAllByOrderByFechaGeneracionDesc();
    }

    /**
     * Obtiene los últimos N informes generados
     */
    public List<InformeLog> obtenerUltimosInformes(int limite) {
        return informeLogRepository.findTopNByOrderByFechaGeneracionDesc(limite);
    }

    /**
     * Obtiene informes por tipo
     */
    public List<InformeLog> obtenerInformesPorTipo(String tipoInforme) {
        return informeLogRepository.findByTipoInformeOrderByFechaGeneracionDesc(tipoInforme);
    }

    /**
     * Obtiene informes por empleado
     */
    public List<InformeLog> obtenerInformesPorEmpleado(Integer empleadoId) {
        return informeLogRepository.findByEmpleado_IdEmpleadoOrderByFechaGeneracionDesc(empleadoId);
    }

    /**
     * Obtiene estadísticas de uso de informes
     */
    public Map<String, Object> obtenerEstadisticasInformes() {
        Map<String, Object> estadisticas = new HashMap<>();

        List<Object[]> tiposMasUtilizados = informeLogRepository.findTiposInformeMasUtilizados();
        List<Object[]> estadisticasPorEmpleado = informeLogRepository.findEstadisticasPorEmpleado();

        estadisticas.put("tiposMasUtilizados", tiposMasUtilizados != null ? tiposMasUtilizados : List.of());
        estadisticas.put("estadisticasPorEmpleado", estadisticasPorEmpleado != null ? estadisticasPorEmpleado : List.of());
        estadisticas.put("totalInformesGenerados", informeLogRepository.count());

        // Conteo por tipo de informe usando las constantes de InformeLog
        estadisticas.put("resumenGeneralCount", informeLogRepository.countByTipoInforme(InformeLog.Tipos.RESUMEN_GENERAL));
        estadisticas.put("ventasCount", informeLogRepository.countByTipoInforme(InformeLog.Tipos.INFORME_VENTAS));
        estadisticas.put("cotizacionesCount", informeLogRepository.countByTipoInforme(InformeLog.Tipos.INFORME_COTIZACIONES));
        estadisticas.put("pedidosCount", informeLogRepository.countByTipoInforme(InformeLog.Tipos.INFORME_PEDIDOS));

        return estadisticas;
    }

    /**
     * Busca informes por rango de fechas
     */
    public List<InformeLog> buscarInformesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return informeLogRepository.findByRangoFechasInforme(fechaInicio, fechaFin);
    }

    /**
     * Calcula la tasa de conversión de cotizaciones a ventas
     */
    private Double calcularTasaConversionCotizaciones(LocalDate fechaInicio, LocalDate fechaFin) {
        Long totalCotizaciones = cotizacionRepository.countByFechaBetween(fechaInicio, fechaFin);
        Long cotizacionesAprobadas = cotizacionRepository.countByFechaBetweenAndEstado(fechaInicio, fechaFin,
                com.stylishhome.Sistema_CV_FR.model.Cotizacion.EstadoCotizacion.APROBADA);

        if (totalCotizaciones == null || totalCotizaciones == 0) {
            return 0.0;
        }

        return (cotizacionesAprobadas != null ? cotizacionesAprobadas.doubleValue() : 0.0) / totalCotizaciones.doubleValue() * 100;
    }

    /**
     * Cuenta el total de productos vendidos en un período
     */
    private Long contarProductosVendidos(LocalDate fechaInicio, LocalDate fechaFin) {
        Long total = ventaRepository.sumProductosVendidosPeriodo(fechaInicio, fechaFin);
        return total != null ? total : 0L;
    }

    /**
     * Genera un informe personalizado basado en parámetros
     */
    public Map<String, Object> generarInformePersonalizado(String tipoInforme, Map<String, Object> parametros) {
        Map<String, Object> informe = new HashMap<>();

        LocalDate fechaInicio = (LocalDate) parametros.get("fechaInicio");
        LocalDate fechaFin = (LocalDate) parametros.get("fechaFin");
        Integer empleadoId = (Integer) parametros.get("empleadoId");

        // Usar constantes de InformeLog en lugar de strings literales
        switch (tipoInforme) {
            case InformeLog.Tipos.RESUMEN_GENERAL:
                informe = generarInformeResumenGeneral(fechaInicio, fechaFin, empleadoId);
                break;
            case InformeLog.Tipos.INFORME_VENTAS:
                informe = generarInformeVentas(fechaInicio, fechaFin, empleadoId);
                break;
            case InformeLog.Tipos.INFORME_COTIZACIONES:
                informe = generarInformeCotizaciones(fechaInicio, fechaFin, empleadoId);
                break;
            case InformeLog.Tipos.INFORME_PEDIDOS:
                informe = generarInformeProductosMasVendidos(fechaInicio, fechaFin);
                break;
            default:
                throw new RuntimeException("Tipo de informe no válido: " + tipoInforme);
        }

        // Registrar la generación del informe
        if (empleadoId != null) {
            registrarGeneracionInforme(tipoInforme, fechaInicio, fechaFin, empleadoId, parametros.toString());
        }

        return informe;
    }

    /**
     * Método adicional para obtener informes simples por empleado
     */
    public List<InformeLog> obtenerInformesPorEmpleadoSimple(Integer empleadoId) {
        return informeLogRepository.findByEmpleado_IdEmpleado(empleadoId);
    }
}

package com.stylishhome.Sistema_CV_FR.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para el dashboard principal con métricas y KPI
 */
@Service
public class DashboardService {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InformeService informeService;

    /**
     * Obtiene todas las métricas para el dashboard
     */
    public Map<String, Object> obtenerMetricasDashboard() {
        Map<String, Object> metricas = new HashMap<>();
        
        // Métricas principales
        metricas.put("totalVentasHoy", calcularVentasHoy());
        metricas.put("totalCotizacionesHoy", calcularCotizacionesHoy());
        metricas.put("nuevosClientesHoy", calcularNuevosClientesHoy());
        metricas.put("productosStockBajo", productoService.contarProductosStockBajo());
        
        // Métricas del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        
        metricas.put("ventasEsteMes", ventaService.calcularTotalVentasPeriodo(inicioMes, finMes));
        metricas.put("cotizacionesEsteMes", cotizacionRepository.countByFechaBetween(inicioMes, finMes));
        
        // Estadísticas adicionales
        metricas.putAll(ventaService.obtenerEstadisticasVentas());
        metricas.putAll(cotizacionService.obtenerEstadisticasCotizaciones());
        metricas.putAll(clienteService.obtenerEstadisticasClientes());
        metricas.putAll(productoService.obtenerEstadisticasProductos());
        
        // Alertas y notificaciones
        metricas.put("alertas", obtenerAlertas());
        
        return metricas;
    }

    /**
     * Obtiene datos para gráficos del dashboard
     */
    public Map<String, Object> obtenerDatosGraficos() {
        Map<String, Object> datos = new HashMap<>();
        
        // Ventas de los últimos 6 meses
        datos.put("ventasUltimosMeses", obtenerVentasUltimosMeses(6));
        
        // Cotizaciones por estado
        datos.put("cotizacionesPorEstado", cotizacionRepository.countCotizacionesPorEstado());
        
        // Productos más vendidos
        datos.put("productosMasVendidos", productoRepository.findTop10ProductosMasVendidos());
        
        // Métodos de pago más utilizados
        datos.put("metodosPago", ventaRepository.countVentasPorMetodoPago());
        
        return datos;
    }

    /**
     * Calcula las ventas del día actual
     */
    private Double calcularVentasHoy() {
        LocalDate hoy = LocalDate.now();
        Double total = ventaService.calcularTotalVentasPeriodo(hoy, hoy);
        return total != null ? total : 0.0;
    }

    /**
     * Calcula las cotizaciones del día actual
     */
    private Long calcularCotizacionesHoy() {
        LocalDate hoy = LocalDate.now();
        return cotizacionRepository.countByFechaBetween(hoy, hoy);
    }

    /**
     * Calcula los nuevos clientes del día actual
     */
    private Long calcularNuevosClientesHoy() {
        LocalDate hoy = LocalDate.now();
        return clienteRepository.countByFechaRegistroBetween(hoy, hoy);
    }

    /**
     * Obtiene las ventas de los últimos N meses
     */
    private Map<String, Double> obtenerVentasUltimosMeses(int meses) {
        Map<String, Double> ventasPorMes = new HashMap<>();
        LocalDate ahora = LocalDate.now();
        
        for (int i = meses - 1; i >= 0; i--) {
            LocalDate inicioMes = ahora.minusMonths(i).withDayOfMonth(1);
            LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
            
            String mes = inicioMes.getMonth().toString() + " " + inicioMes.getYear();
            Double total = ventaService.calcularTotalVentasPeriodo(inicioMes, finMes);
            
            ventasPorMes.put(mes, total != null ? total : 0.0);
        }
        
        return ventasPorMes;
    }

    /**
     * Obtiene alertas y notificaciones para el dashboard
     */
    private Map<String, Object> obtenerAlertas() {
        Map<String, Object> alertas = new HashMap<>();
        
        // Productos con stock crítico
        alertas.put("stockCritico", productoService.obtenerProductosStockCritico());
        
        // Cotizaciones por vencer
        alertas.put("cotizacionesPorVencer", cotizacionService.obtenerCotizacionesPorVencer());
        
        // Ventas pendientes
        alertas.put("ventasPendientes", ventaService.obtenerVentasPorEstado(
            com.stylishhome.Sistema_CV_FR.model.Venta.EstadoVenta.PENDIENTE));
        
        return alertas;
    }

    // Inyección de repositorios adicionales necesarios
    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.VentaRepository ventaRepository;
    
    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.CotizacionRepository cotizacionRepository;
    
    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.ClienteRepository clienteRepository;
    
    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.ProductoRepository productoRepository;
}
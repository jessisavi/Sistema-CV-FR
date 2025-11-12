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

    // Inyección de repositorios
    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.VentaRepository ventaRepository;

    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.CotizacionRepository cotizacionRepository;

    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.ClienteRepository clienteRepository;

    @Autowired
    private com.stylishhome.Sistema_CV_FR.repository.ProductoRepository productoRepository;

    /**
     * Obtiene todas las métricas para el dashboard
     */
    public Map<String, Object> obtenerMetricasDashboard() {
        Map<String, Object> metricas = new HashMap<>();

        try {
            System.out.println("Iniciando obtenerMetricasDashboard...");

            // Métricas principales que espera la plantilla
            Long clientesActivos = clienteService.contarClientesActivos();
            metricas.put("totalClientesActivos", clientesActivos != null ? clientesActivos : 0);
            System.out.println("Clientes activos: " + clientesActivos);

            metricas.put("crecimientoClientes", 5);

            Long totalCotizaciones = cotizacionService.contarTotalCotizaciones();
            metricas.put("totalCotizaciones", totalCotizaciones != null ? totalCotizaciones : 0L);
            System.out.println("Total cotizaciones: " + totalCotizaciones);

            metricas.put("crecimientoCotizaciones", 8);

            Double ventasTotales = ventaService.obtenerVentasTotales();
            metricas.put("ventasTotales", ventasTotales != null ? ventasTotales : 0.0);
            System.out.println("Ventas totales: " + ventasTotales);

            metricas.put("crecimientoVentas", 12);

            Long totalProductos = productoService.contarProductosActivos();
            metricas.put("totalProductos", totalProductos != null ? totalProductos : 0);
            System.out.println("Total productos: " + totalProductos);

            metricas.put("crecimientoProductos", 3);

            // Métricas adicionales
            metricas.put("totalVentasHoy", calcularVentasHoy());
            metricas.put("totalCotizacionesHoy", calcularCotizacionesHoy());
            metricas.put("nuevosClientesHoy", calcularNuevosClientesHoy());
            metricas.put("productosStockBajo", productoService.contarProductosStockBajo());

            // Alertas y notificaciones
            metricas.put("alertas", obtenerAlertas());

            System.out.println("Métricas obtenidas exitosamente: " + metricas.size() + " items");

        } catch (Exception e) {
            // En caso de error, retorna métricas básicas
            System.err.println("Error en obtenerMetricasDashboard: " + e.getMessage());
            e.printStackTrace();
            metricas = obtenerMetricasPorDefecto();
        }

        return metricas;
    }

    /**
     * Obtiene datos para gráficos del dashboard
     */
    public Map<String, Object> obtenerDatosGraficos() {
        Map<String, Object> datos = new HashMap<>();

        try {
            System.out.println("Obteniendo datos de gráficos...");

            // Datos de ejemplo para desarrollo
            datos.put("ventasUltimosMeses", obtenerVentasUltimosMesesEjemplo());
            datos.put("cotizacionesPorEstado", obtenerCotizacionesPorEstadoEjemplo());
            datos.put("productosMasVendidos", obtenerProductosMasVendidosEjemplo());
            datos.put("metodosPago", obtenerMetodosPagoEjemplo());

            System.out.println("Datos de gráficos obtenidos exitosamente");

        } catch (Exception e) {
            System.err.println("Error en obtenerDatosGraficos: " + e.getMessage());
            e.printStackTrace();
            datos = obtenerDatosGraficosPorDefecto();
        }

        return datos;
    }

    /**
     * Métodos de cálculo
     */
    private Double calcularVentasHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            // Usa el método seguro del servicio o retorna 0
            return 0.0; // Temporal hasta que tengas el servicio funcionando
        } catch (Exception e) {
            System.err.println("Error en calcularVentasHoy: " + e.getMessage());
            return 0.0;
        }
    }

    private Long calcularCotizacionesHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            if (cotizacionRepository != null) {
                // return cotizacionRepository.countByFechaBetween(hoy, hoy);
            }
            return 0L;
        } catch (Exception e) {
            System.err.println("Error en calcularCotizacionesHoy: " + e.getMessage());
            return 0L;
        }
    }

    private Long calcularNuevosClientesHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            if (clienteRepository != null) {
                // return clienteRepository.countByFechaRegistroBetween(hoy, hoy);
            }
            return 0L;
        } catch (Exception e) {
            System.err.println("Error en calcularNuevosClientesHoy: " + e.getMessage());
            return 0L;
        }
    }

    /**
     * Datos de ejemplo para desarrollo
     */
    private Map<String, Double> obtenerVentasUltimosMesesEjemplo() {
        Map<String, Double> ventas = new HashMap<>();
        ventas.put("Enero", 15000.0);
        ventas.put("Febrero", 18000.0);
        ventas.put("Marzo", 22000.0);
        ventas.put("Abril", 19000.0);
        ventas.put("Mayo", 25000.0);
        ventas.put("Junio", 28000.0);
        return ventas;
    }

    private Map<String, Long> obtenerCotizacionesPorEstadoEjemplo() {
        Map<String, Long> estados = new HashMap<>();
        estados.put("Pendiente", 15L);
        estados.put("Aprobada", 8L);
        estados.put("Rechazada", 3L);
        estados.put("Vencida", 2L);
        return estados;
    }

    private Map<String, Integer> obtenerProductosMasVendidosEjemplo() {
        Map<String, Integer> productos = new HashMap<>();
        productos.put("Sofá Moderno", 45);
        productos.put("Mesa Centro", 32);
        productos.put("Lámpara LED", 28);
        productos.put("Silla Oficina", 25);
        return productos;
    }

    private Map<String, Integer> obtenerMetodosPagoEjemplo() {
        Map<String, Integer> metodos = new HashMap<>();
        metodos.put("Tarjeta Crédito", 65);
        metodos.put("Transferencia", 25);
        metodos.put("Efectivo", 10);
        return metodos;
    }

    /**
     * Métricas por defecto en caso de error
     */
    private Map<String, Object> obtenerMetricasPorDefecto() {
        Map<String, Object> metricas = new HashMap<>();
        // Propiedades que espera la plantilla
        metricas.put("totalClientesActivos", 0);
        metricas.put("crecimientoClientes", 0);
        metricas.put("totalCotizaciones", 0);
        metricas.put("crecimientoCotizaciones", 0);
        metricas.put("ventasTotales", 0.0);
        metricas.put("crecimientoVentas", 0);
        metricas.put("totalProductos", 0);
        metricas.put("crecimientoProductos", 0);
        // Propiedades originales
        metricas.put("totalVentasHoy", 0.0);
        metricas.put("totalCotizacionesHoy", 0L);
        metricas.put("nuevosClientesHoy", 0L);
        metricas.put("productosStockBajo", 0);
        metricas.put("alertas", new HashMap<>());
        return metricas;
    }

    /**
     * Datos de gráficos por defecto en caso de error
     */
    private Map<String, Object> obtenerDatosGraficosPorDefecto() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("ventasUltimosMeses", new HashMap<>());
        datos.put("cotizacionesPorEstado", new HashMap<>());
        datos.put("productosMasVendidos", new HashMap<>());
        datos.put("metodosPago", new HashMap<>());
        return datos;
    }

    /**
     * Alertas simplificadas
     */
    private Map<String, Object> obtenerAlertas() {
        Map<String, Object> alertas = new HashMap<>();
        alertas.put("stockCritico", 0);
        alertas.put("cotizacionesPorVencer", 0);
        alertas.put("ventasPendientes", 0);
        return alertas;
    }
}

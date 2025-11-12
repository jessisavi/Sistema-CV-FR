package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Cotizacion;
import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.CotizacionService;
import com.stylishhome.Sistema_CV_FR.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para el dashboard principal
 */
@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CotizacionService cotizacionService;

    /**
     * Muestra el dashboard principal
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, HttpSession session) {
        System.out.println("=== ACCESO A DASHBOARD ===");

        try {
            // Validar usuario en sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            System.out.println("Usuario en sesión: " + (usuario != null ? usuario.getUsuario() : "NULL"));

            if (usuario == null) {
                System.out.println("Redirigiendo a login - sesión nula");
                return "redirect:/login";
            }

            // VALIDAR SERVICIOS
            if (dashboardService == null) {
                System.err.println("ERROR: DashboardService es NULL");
                throw new RuntimeException("DashboardService no está disponible");
            }

            if (cotizacionService == null) {
                System.err.println("ERROR: CotizacionService es NULL");
                throw new RuntimeException("CotizacionService no está disponible");
            }

            // Obtener métricas con manejo de errores
            System.out.println("Obteniendo métricas...");
            Map<String, Object> metricas = null;
            try {
                metricas = dashboardService.obtenerMetricasDashboard();
                System.out.println("Métricas obtenidas: " + (metricas != null ? metricas.size() + " items" : "NULL"));
            } catch (Exception e) {
                System.err.println("Error obteniendo métricas: " + e.getMessage());
                e.printStackTrace();
                metricas = crearMetricasVacias();
            }

            if (metricas == null) {
                System.out.println("Métricas nulas, creando vacías");
                metricas = crearMetricasVacias();
            }

            // Obtener gráficos con manejo de errores
            System.out.println("Obteniendo gráficos...");
            Map<String, Object> graficos = null;
            try {
                graficos = dashboardService.obtenerDatosGraficos();
                System.out.println("Gráficos obtenidos: " + (graficos != null ? graficos.size() + " items" : "NULL"));
            } catch (Exception e) {
                System.err.println("Error obteniendo gráficos: " + e.getMessage());
                e.printStackTrace();
                graficos = new HashMap<>();
            }

            if (graficos == null) {
                System.out.println("Gráficos nulos, creando vacíos");
                graficos = new HashMap<>();
            }

            // OBTENER ÚLTIMAS COTIZACIONES
            System.out.println("Obteniendo últimas cotizaciones...");
            List<Cotizacion> ultimasCotizaciones = new ArrayList<>();
            try {
                ultimasCotizaciones = cotizacionService.obtenerUltimasCotizaciones(5);
                if (ultimasCotizaciones == null) {
                    System.out.println("obtenerUltimasCotizaciones retornó NULL");
                    ultimasCotizaciones = new ArrayList<>();
                } else {
                    System.out.println("Cotizaciones obtenidas: " + ultimasCotizaciones.size());
                }
            } catch (Exception e) {
                System.err.println("Error obteniendo cotizaciones: " + e.getMessage());
                e.printStackTrace();
                ultimasCotizaciones = new ArrayList<>();
            }

            // Agregar atributos al modelo
            model.addAttribute("usuario", usuario);
            model.addAttribute("metricas", metricas);
            model.addAttribute("graficos", graficos);
            model.addAttribute("ultimasCotizaciones", ultimasCotizaciones);
            model.addAttribute("titulo", "Dashboard Principal");

            System.out.println("=== DASHBOARD CARGADO EXITOSAMENTE ===");
            System.out.println("Métricas: " + metricas.size() + " items");
            System.out.println("Gráficos: " + graficos.size() + " items");
            System.out.println("Cotizaciones: " + ultimasCotizaciones.size() + " items");

            return "dashboard";

        } catch (Exception e) {
            System.err.println("=== ERROR CRÍTICO EN DASHBOARD ===");
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("StackTrace:");
            e.printStackTrace();

            // En caso de error crítico, intentar cargar una versión mínima del dashboard
            try {
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                if (usuario != null) {
                    model.addAttribute("usuario", usuario);
                }
                model.addAttribute("metricas", crearMetricasVacias());
                model.addAttribute("graficos", new HashMap<>());
                model.addAttribute("ultimasCotizaciones", new ArrayList<>());
                model.addAttribute("titulo", "Dashboard Principal");
                model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());

                return "dashboard";
            } catch (Exception ex) {
                System.err.println("Error al intentar cargar dashboard de respaldo: " + ex.getMessage());
                return "redirect:/login";
            }
        }
    }

    /**
     * Crea un mapa de métricas vacías para evitar errores en la vista
     */
    private Map<String, Object> crearMetricasVacias() {
        Map<String, Object> metricas = new HashMap<>();
        metricas.put("totalClientesActivos", 0);
        metricas.put("crecimientoClientes", 0);
        metricas.put("totalCotizaciones", 0);
        metricas.put("crecimientoCotizaciones", 0);
        metricas.put("ventasTotales", 0.0);
        metricas.put("crecimientoVentas", 0);
        metricas.put("totalProductos", 0);
        metricas.put("crecimientoProductos", 0);
        metricas.put("totalVentasHoy", 0.0);
        metricas.put("totalCotizacionesHoy", 0L);
        metricas.put("nuevosClientesHoy", 0L);
        metricas.put("productosStockBajo", 0);
        metricas.put("alertas", new HashMap<>());
        return metricas;
    }
}
package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.InformeLog;
import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.InformeService;
import com.stylishhome.Sistema_CV_FR.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la generación de informes y reportes
 */
@Controller
@RequestMapping("/informes")
public class InformeController {
    
    @Autowired
    private InformeService informeService;
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * Muestra la página principal de informes
     */
    @GetMapping
    public String mostrarInformes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("titulo", "Generación de Informes");
        
        return "informes/principal";
    }
    
    /**
     * Genera y muestra el informe de resumen general
     */
    @PostMapping("/resumen-general")
    public String generarInformeResumenGeneral(@RequestParam LocalDate fechaInicio,
                                             @RequestParam LocalDate fechaFin,
                                             @RequestParam(required = false) Integer empleadoId,
                                             HttpSession session,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> informe = informeService.generarInformeResumenGeneral(fechaInicio, fechaFin, empleadoId);
            
            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                InformeLog.Tipos.RESUMEN_GENERAL,
                fechaInicio,
                fechaFin,
                usuario.getIdEmpleado(),
                "Resumen general del sistema"
            );
            
            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Informe de Resumen General");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "RESUMEN_GENERAL");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }
        
        return "informes/resumen-general";
    }
    
    /**
     * Genera y muestra el informe de ventas
     */
    @PostMapping("/ventas")
    public String generarInformeVentas(@RequestParam LocalDate fechaInicio,
                                     @RequestParam LocalDate fechaFin,
                                     @RequestParam(required = false) Integer empleadoId,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> informe = informeService.generarInformeVentas(fechaInicio, fechaFin, empleadoId);
            
            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                InformeLog.Tipos.INFORME_VENTAS,
                fechaInicio,
                fechaFin,
                usuario.getIdEmpleado(),
                "Informe detallado de ventas"
            );
            
            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Informe de Ventas");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "INFORME_VENTAS");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }
        
        return "informes/ventas";
    }
    
    /**
     * Genera y muestra el informe de cotizaciones
     */
    @PostMapping("/cotizaciones")
    public String generarInformeCotizaciones(@RequestParam LocalDate fechaInicio,
                                           @RequestParam LocalDate fechaFin,
                                           @RequestParam(required = false) Integer empleadoId,
                                           HttpSession session,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> informe = informeService.generarInformeCotizaciones(fechaInicio, fechaFin, empleadoId);
            
            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                InformeLog.Tipos.INFORME_COTIZACIONES,
                fechaInicio,
                fechaFin,
                usuario.getIdEmpleado(),
                "Informe detallado de cotizaciones"
            );
            
            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Informe de Cotizaciones");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "INFORME_COTIZACIONES");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }
        
        return "informes/cotizaciones";
    }
    
    /**
     * Genera y muestra el informe de productos más vendidos
     */
    @PostMapping("/productos-mas-vendidos")
    public String generarInformeProductosMasVendidos(@RequestParam LocalDate fechaInicio,
                                                   @RequestParam LocalDate fechaFin,
                                                   HttpSession session,
                                                   Model model,
                                                   RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> informe = informeService.generarInformeProductosMasVendidos(fechaInicio, fechaFin);
            
            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                InformeLog.Tipos.INFORME_PEDIDOS,
                fechaInicio,
                fechaFin,
                usuario.getIdEmpleado(),
                "Informe de productos más vendidos"
            );
            
            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Productos Más Vendidos");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "INFORME_PEDIDOS");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }
        
        return "informes/productos-mas-vendidos";
    }
    
    /**
     * Muestra el historial de informes generados
     */
    @GetMapping("/historial")
    public String mostrarHistorialInformes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<InformeLog> historial = informeService.obtenerHistorialInformes();
        model.addAttribute("historial", historial);
        model.addAttribute("titulo", "Historial de Informes");
        
        return "informes/historial";
    }
    
    /**
     * Muestra estadísticas de uso de informes
     */
    @GetMapping("/estadisticas")
    public String mostrarEstadisticasInformes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Map<String, Object> estadisticas = informeService.obtenerEstadisticasInformes();
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("titulo", "Estadísticas de Informes");
        
        return "informes/estadisticas";
    }
    
    /**
     * API para obtener datos del dashboard (AJAX)
     */
    @GetMapping("/api/dashboard")
    @ResponseBody
    public Map<String, Object> obtenerDatosDashboard(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return Map.of("error", "No autenticado");
        }
        
        try {
            Map<String, Object> metricas = dashboardService.obtenerMetricasDashboard();
            Map<String, Object> graficos = dashboardService.obtenerDatosGraficos();
            
            return Map.of(
                "metricas", metricas,
                "graficos", graficos,
                "success", true
            );
        } catch (Exception e) {
            return Map.of(
                "error", "Error al obtener datos del dashboard: " + e.getMessage(),
                "success", false
            );
        }
    }
    
    /**
     * API para generar informe rápido (AJAX)
     */
    @PostMapping("/api/generar-rapido")
    @ResponseBody
    public Map<String, Object> generarInformeRapido(@RequestParam String tipo,
                                                  @RequestParam String periodo,
                                                  HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return Map.of("success", false, "error", "No autenticado");
        }
        
        try {
            LocalDate fechaInicio;
            LocalDate fechaFin = LocalDate.now();
            
            switch (periodo.toUpperCase()) {
                case "HOY":
                    fechaInicio = LocalDate.now();
                    break;
                case "SEMANA":
                    fechaInicio = LocalDate.now().minusDays(7);
                    break;
                case "MES":
                    fechaInicio = LocalDate.now().withDayOfMonth(1);
                    break;
                case "TRIMESTRE":
                    fechaInicio = LocalDate.now().minusMonths(3);
                    break;
                default:
                    fechaInicio = LocalDate.now().withDayOfMonth(1);
                    fechaFin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            }
            
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Map<String, Object> parametros = Map.of(
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin,
                "empleadoId", usuario.getIdEmpleado()
            );
            
            Map<String, Object> informe = informeService.generarInformePersonalizado(tipo, parametros);
            
            return Map.of(
                "success", true,
                "informe", informe,
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin
            );
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", "Error al generar informe: " + e.getMessage()
            );
        }
    }
}
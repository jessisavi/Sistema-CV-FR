package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * Controlador para el dashboard principal
 */
@Controller
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * Muestra el dashboard principal
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> metricas = dashboardService.obtenerMetricasDashboard();
            Map<String, Object> graficos = dashboardService.obtenerDatosGraficos();
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("metricas", metricas);
            model.addAttribute("graficos", graficos);
            model.addAttribute("titulo", "Dashboard Principal");
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
        }
        
        return "dashboard";
    }
    
    /**
     * Muestra una versión simplificada del dashboard
     */
    @GetMapping("/inicio")
    public String mostrarInicio(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Inicio");
        
        return "inicio";
    }
}
package com.stylishhome.Sistema_CV_FR.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para páginas estáticas y redirecciones
 */
@Controller
public class HomeController {

    /**
     * Redirige a la página de login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login"; 
    }

    /**
     * Muestra página de error personalizada
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }

    /**
     * Muestra página de acceso denegado
     */
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}

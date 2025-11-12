package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador para el manejo de autenticación y sesiones
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de login
     */
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "logout", required = false) String logout,
                             Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas. Por favor, intente nuevamente.");
        }
        
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada exitosamente.");
        }
        
        return "login";
    }

    /**
     * Procesa el formulario de login
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(username, password);
        
        if (usuarioOpt.isPresent()) {
            // Autenticación exitosa
            Usuario usuarioAutenticado = usuarioOpt.get();
            session.setAttribute("usuario", usuarioAutenticado);
            session.setAttribute("rol", usuarioAutenticado.getRol());
            return "redirect:/dashboard";
        } else {
            // Autenticación fallida
            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/login?error=true";
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Sesión cerrada exitosamente");
        return "redirect:/login?logout=true";
    }
    
    /**
     * Endpoint temporal para debug - ver usuarios en la base de datos
     */
    @GetMapping("/debug/usuarios")
    public String debugUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        return "debug/usuarios";
    }
}
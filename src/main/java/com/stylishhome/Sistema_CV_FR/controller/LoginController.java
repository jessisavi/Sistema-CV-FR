package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
     *
     * @return Vista de login
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    /**
     * Procesa el formulario de login
     *
     * @param usuario Nombre de usuario
     * @param contraseña Contraseña
     * @param session Sesión HTTP
     * @param redirectAttributes Atributos para redirección
     * @return Redirección al dashboard si es exitoso, al login si falla
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
            @RequestParam String contraseña,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(usuario, contraseña);

        if (usuarioOpt.isPresent()) {
            // Autenticación exitosa
            Usuario usuarioAutenticado = usuarioOpt.get();
            session.setAttribute("usuario", usuarioAutenticado);
            session.setAttribute("rol", usuarioAutenticado.getRol());
            return "redirect:/dashboard";
        } else {
            // Autenticación fallida
            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/login";
        }
    }

    /**
     * Cierra la sesión del usuario
     *
     * @param session Sesión HTTP
     * @return Redirección al login
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

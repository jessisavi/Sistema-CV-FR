package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Cliente;
import com.stylishhome.Sistema_CV_FR.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de clientes
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    /**
     * Muestra la lista de todos los clientes
     */
    @GetMapping
    public String listarClientes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        model.addAttribute("clientes", clientes);
        model.addAttribute("titulo", "Lista de Clientes");
        
        return "clientes/lista";
    }
    
    /**
     * Muestra el formulario para crear un nuevo cliente
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoCliente(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        model.addAttribute("modo", "crear");
        
        return "clientes/formulario";
    }
    
    /**
     * Procesa el guardado de un nuevo cliente
     */
    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            clienteService.guardarCliente(cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("cliente", cliente);
            return "redirect:/clientes/nuevo";
        }
        
        return "redirect:/clientes";
    }
    
    /**
     * Muestra los detalles de un cliente específico
     */
    @GetMapping("/{id}")
    public String verDetallesCliente(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(id);
        if (clienteOpt.isEmpty()) {
            model.addAttribute("error", "Cliente no encontrado");
            return "redirect:/clientes";
        }
        
        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("titulo", "Detalles del Cliente");
        
        return "clientes/detalle";
    }
    
    /**
     * Muestra el formulario para editar un cliente existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCliente(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(id);
        if (clienteOpt.isEmpty()) {
            model.addAttribute("error", "Cliente no encontrado");
            return "redirect:/clientes";
        }
        
        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("titulo", "Editar Cliente");
        model.addAttribute("modo", "editar");
        
        return "clientes/formulario";
    }
    
    /**
     * Procesa la actualización de un cliente existente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarCliente(@PathVariable Integer id,
                                  @ModelAttribute Cliente cliente,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            cliente.setIdCliente(id); // Asegurar que el ID sea el correcto
            clienteService.guardarCliente(cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el cliente: " + e.getMessage());
            return "redirect:/clientes/editar/" + id;
        }
        
        return "redirect:/clientes";
    }
    
    /**
     * Elimina un cliente
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Integer id,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            clienteService.eliminarCliente(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el cliente: " + e.getMessage());
        }
        
        return "redirect:/clientes";
    }
    
    /**
     * Busca clientes por criterios
     */
    @GetMapping("/buscar")
    public String buscarClientes(@RequestParam(required = false) String criterio,
                               Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Cliente> clientes;
        if (criterio != null && !criterio.trim().isEmpty()) {
            clientes = clienteService.buscarClientesPorNombreOApellido(criterio);
            model.addAttribute("criterio", criterio);
        } else {
            clientes = clienteService.obtenerTodosLosClientes();
        }
        
        model.addAttribute("clientes", clientes);
        model.addAttribute("titulo", "Resultados de Búsqueda");
        
        return "clientes/lista";
    }
    
    /**
     * Muestra clientes por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public String listarClientesPorTipo(@PathVariable String tipo, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Cliente> clientes = clienteService.obtenerClientesPorTipo(tipo);
        model.addAttribute("clientes", clientes);
        model.addAttribute("titulo", "Clientes " + tipo);
        model.addAttribute("filtroTipo", tipo);
        
        return "clientes/lista";
    }
    
    /**
     * API para obtener clientes en formato JSON (para AJAX)
     */
    @GetMapping("/api/listar")
    @ResponseBody
    public List<Cliente> listarClientesApi(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return List.of();
        }
        return clienteService.obtenerTodosLosClientes();
    }
    
    /**
     * API para buscar clientes por criterio (para AJAX)
     */
    @GetMapping("/api/buscar")
    @ResponseBody
    public List<Cliente> buscarClientesApi(@RequestParam String criterio, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return List.of();
        }
        return clienteService.buscarClientesPorNombreOApellido(criterio);
    }
}
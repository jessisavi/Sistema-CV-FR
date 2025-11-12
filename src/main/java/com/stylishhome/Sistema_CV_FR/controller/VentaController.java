package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Venta;
import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.VentaService;
import com.stylishhome.Sistema_CV_FR.service.ProductoService;
import com.stylishhome.Sistema_CV_FR.service.ClienteService;
import com.stylishhome.Sistema_CV_FR.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de ventas
 */
@Controller
@RequestMapping("/ventas")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Muestra la lista de todas las ventas
     */
    @GetMapping
    public String listarVentas(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Venta> ventas = ventaService.obtenerTodasLasVentas();
        model.addAttribute("ventas", ventas);
        model.addAttribute("titulo", "Lista de Ventas");
        
        return "ventas/lista";
    }
    
    /**
     * Muestra el formulario para crear una nueva venta
     */
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaVenta(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("venta", new Venta());
        model.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("titulo", "Nueva Venta");
        model.addAttribute("modo", "crear");
        
        return "ventas/formulario";
    }
    
    /**
     * Procesa el guardado de una nueva venta 
     */
    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute Venta venta,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            // Establecer el empleado/vendedor desde la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            venta.setEmpleado(usuario);
            
            // Validar que el cliente esté seleccionado
            if (venta.getCliente() == null || venta.getCliente().getIdCliente() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un cliente");
                redirectAttributes.addFlashAttribute("venta", venta);
                return "redirect:/ventas/nueva";
            }
            
            ventaService.crearVenta(venta);
            redirectAttributes.addFlashAttribute("success", "Venta creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la venta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("venta", venta);
            return "redirect:/ventas/nueva";
        }
        
        return "redirect:/ventas";
    }
    
    /**
     * Muestra los detalles de una venta específica
     */
    @GetMapping("/{id}")
    public String verDetallesVenta(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Optional<Venta> ventaOpt = ventaService.obtenerVentaPorId(id);
        if (ventaOpt.isEmpty()) {
            model.addAttribute("error", "Venta no encontrada");
            return "redirect:/ventas";
        }
        
        model.addAttribute("venta", ventaOpt.get());
        model.addAttribute("titulo", "Detalles de Venta");
        
        return "ventas/detalle";
    }
    
    /**
     * Muestra el formulario para editar una venta existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarVenta(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Optional<Venta> ventaOpt = ventaService.obtenerVentaPorId(id);
        if (ventaOpt.isEmpty()) {
            model.addAttribute("error", "Venta no encontrada");
            return "redirect:/ventas";
        }
        
        model.addAttribute("venta", ventaOpt.get());
        model.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("titulo", "Editar Venta");
        model.addAttribute("modo", "editar");
        
        return "ventas/formulario";
    }
    
    /**
     * Procesa la actualización de una venta existente 
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarVenta(@PathVariable Integer id,
                                @ModelAttribute Venta venta,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            venta.setIdVenta(id);
            
            // Validar que el cliente esté seleccionado
            if (venta.getCliente() == null || venta.getCliente().getIdCliente() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un cliente");
                return "redirect:/ventas/editar/" + id;
            }
            
            ventaService.actualizarVenta(id, venta);
            redirectAttributes.addFlashAttribute("success", "Venta actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la venta: " + e.getMessage());
            return "redirect:/ventas/editar/" + id;
        }
        
        return "redirect:/ventas";
    }
    
    /**
     * Elimina una venta
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Integer id,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            ventaService.eliminarVenta(id);
            redirectAttributes.addFlashAttribute("success", "Venta eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
        }
        
        return "redirect:/ventas";
    }
    
    /**
     * Completa una venta (cambia estado a COMPLETADA)
     */
    @PostMapping("/completar/{id}")
    public String completarVenta(@PathVariable Integer id,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            ventaService.completarVenta(id);
            redirectAttributes.addFlashAttribute("success", "Venta completada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al completar la venta: " + e.getMessage());
        }
        
        return "redirect:/ventas";
    }
    
    /**
     * Cancela una venta
     */
    @PostMapping("/cancelar/{id}")
    public String cancelarVenta(@PathVariable Integer id,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            ventaService.cancelarVenta(id);
            redirectAttributes.addFlashAttribute("success", "Venta cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la venta: " + e.getMessage());
        }
        
        return "redirect:/ventas";
    }
    
    /**
     * Muestra ventas por estado
     */
    @GetMapping("/estado/{estado}")
    public String listarVentasPorEstado(@PathVariable String estado, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            Venta.EstadoVenta estadoEnum = Venta.EstadoVenta.valueOf(estado.toUpperCase());
            List<Venta> ventas = ventaService.obtenerVentasPorEstado(estadoEnum);
            
            model.addAttribute("ventas", ventas);
            model.addAttribute("titulo", "Ventas " + estado);
            model.addAttribute("filtroEstado", estado);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Estado no válido: " + estado);
            return "redirect:/ventas";
        }
        
        return "ventas/lista";
    }
    
    /**
     * Busca ventas por criterios
     */
    @GetMapping("/buscar")
    public String buscarVentas(@RequestParam(required = false) String numeroFactura,
                             Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Venta> ventas;
        if (numeroFactura != null && !numeroFactura.trim().isEmpty()) {
            Optional<Venta> ventaOpt = ventaService.obtenerVentaPorNumeroFactura(numeroFactura);
            if (ventaOpt.isPresent()) {
                ventas = List.of(ventaOpt.get());
            } else {
                ventas = List.of();
                model.addAttribute("info", "No se encontraron ventas con el número de factura: " + numeroFactura);
            }
            model.addAttribute("criterio", numeroFactura);
        } else {
            ventas = ventaService.obtenerTodasLasVentas();
        }
        
        model.addAttribute("ventas", ventas);
        model.addAttribute("titulo", "Resultados de Búsqueda");
        
        return "ventas/lista";
    }
    
    /**
     * API para verificar stock (AJAX)
     */
    @GetMapping("/api/verificar-stock")
    @ResponseBody
    public String verificarStock(@RequestParam Integer productoId,
                               @RequestParam Integer cantidad,
                               HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "ERROR: No autenticado";
        }
        
        try {
            boolean stockDisponible = productoService.verificarStock(productoId, cantidad);
            if (stockDisponible) {
                return "SUCCESS: Stock disponible";
            } else {
                return "ERROR: Stock insuficiente";
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
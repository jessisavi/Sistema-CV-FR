package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Cotizacion;
import com.stylishhome.Sistema_CV_FR.model.CotizacionDetalle;
import com.stylishhome.Sistema_CV_FR.model.Producto;
import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.CotizacionService;
import com.stylishhome.Sistema_CV_FR.service.ProductoService;
import com.stylishhome.Sistema_CV_FR.service.ClienteService;
import com.stylishhome.Sistema_CV_FR.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de cotizaciones
 */
@Controller
@RequestMapping("/cotizaciones")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la lista de todas las cotizaciones
     */
    @GetMapping
    public String listarCotizaciones(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        List<Cotizacion> cotizaciones = cotizacionService.obtenerTodasLasCotizaciones();
        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("titulo", "Lista de Cotizaciones");

        return "cotizaciones/lista";
    }

    /**
     * Muestra el formulario para crear una nueva cotización
     */
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaCotizacion(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("cotizacion", new Cotizacion());
        model.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("titulo", "Nueva Cotización");
        model.addAttribute("modo", "crear");

        return "cotizaciones/formulario";
    }

    /**
     * Procesa el guardado de una nueva cotización
     */
    @PostMapping("/guardar")
    public String guardarCotizacion(@ModelAttribute Cotizacion cotizacion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            // Establecer el empleado/vendedor desde la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            cotizacion.setEmpleado(usuario);

            // Validar que el cliente esté seleccionado
            if (cotizacion.getCliente() == null || cotizacion.getCliente().getIdCliente() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un cliente");
                redirectAttributes.addFlashAttribute("cotizacion", cotizacion);
                return "redirect:/cotizaciones/nueva";
            }

            cotizacionService.crearCotizacion(cotizacion);
            redirectAttributes.addFlashAttribute("success", "Cotización creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cotización: " + e.getMessage());
            redirectAttributes.addFlashAttribute("cotizacion", cotizacion);
            return "redirect:/cotizaciones/nueva";
        }

        return "redirect:/cotizaciones";
    }

    /**
     * Muestra los detalles de una cotización específica
     */
    @GetMapping("/{id}")
    public String verDetallesCotizacion(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        Optional<Cotizacion> cotizacionOpt = cotizacionService.obtenerCotizacionPorId(id);
        if (cotizacionOpt.isEmpty()) {
            model.addAttribute("error", "Cotización no encontrada");
            return "redirect:/cotizaciones";
        }

        model.addAttribute("cotizacion", cotizacionOpt.get());
        model.addAttribute("titulo", "Detalles de Cotización");

        return "cotizaciones/detalle";
    }

    /**
     * Muestra el formulario para editar una cotización existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCotizacion(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        Optional<Cotizacion> cotizacionOpt = cotizacionService.obtenerCotizacionPorId(id);
        if (cotizacionOpt.isEmpty()) {
            model.addAttribute("error", "Cotización no encontrada");
            return "redirect:/cotizaciones";
        }

        model.addAttribute("cotizacion", cotizacionOpt.get());
        model.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("titulo", "Editar Cotización");
        model.addAttribute("modo", "editar");

        return "cotizaciones/formulario";
    }

    /**
     * Procesa la actualización de una cotización existente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarCotizacion(@PathVariable Integer id,
            @ModelAttribute Cotizacion cotizacion,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            cotizacion.setIdCotizacion(id);

            // Validar que el cliente esté seleccionado
            if (cotizacion.getCliente() == null || cotizacion.getCliente().getIdCliente() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un cliente");
                return "redirect:/cotizaciones/editar/" + id;
            }

            cotizacionService.actualizarCotizacion(id, cotizacion);
            redirectAttributes.addFlashAttribute("success", "Cotización actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cotización: " + e.getMessage());
            return "redirect:/cotizaciones/editar/" + id;
        }

        return "redirect:/cotizaciones";
    }

    /**
     * Elimina una cotización
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCotizacion(@PathVariable Integer id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            cotizacionService.eliminarCotizacion(id);
            redirectAttributes.addFlashAttribute("success", "Cotización eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cotización: " + e.getMessage());
        }

        return "redirect:/cotizaciones";
    }

    /**
     * Aprueba una cotización
     */
    @PostMapping("/aprobar/{id}")
    public String aprobarCotizacion(@PathVariable Integer id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            cotizacionService.aprobarCotizacion(id);
            redirectAttributes.addFlashAttribute("success", "Cotización aprobada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar la cotización: " + e.getMessage());
        }

        return "redirect:/cotizaciones";
    }

    /**
     * Rechaza una cotización
     */
    @PostMapping("/rechazar/{id}")
    public String rechazarCotizacion(@PathVariable Integer id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            cotizacionService.rechazarCotizacion(id);
            redirectAttributes.addFlashAttribute("success", "Cotización rechazada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar la cotización: " + e.getMessage());
        }

        return "redirect:/cotizaciones";
    }

    /**
     * Muestra cotizaciones por estado
     */
    @GetMapping("/estado/{estado}")
    public String listarCotizacionesPorEstado(@PathVariable String estado, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            Cotizacion.EstadoCotizacion estadoEnum = Cotizacion.EstadoCotizacion.valueOf(estado.toUpperCase());
            List<Cotizacion> cotizaciones = cotizacionService.obtenerCotizacionesPorEstado(estadoEnum);

            model.addAttribute("cotizaciones", cotizaciones);
            model.addAttribute("titulo", "Cotizaciones " + estado);
            model.addAttribute("filtroEstado", estado);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Estado no válido: " + estado);
            return "redirect:/cotizaciones";
        }

        return "cotizaciones/lista";
    }

    /**
     * Muestra cotizaciones por vencer
     */
    @GetMapping("/por-vencer")
    public String listarCotizacionesPorVencer(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        List<Cotizacion> cotizaciones = cotizacionService.obtenerCotizacionesPorVencer();
        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("titulo", "Cotizaciones por Vencer");
        model.addAttribute("filtroPorVencer", true);

        return "cotizaciones/lista";
    }

    /**
     * API para agregar detalle a cotización (AJAX) 
     */
    @PostMapping("/{id}/agregar-detalle")
    @ResponseBody
    public String agregarDetalleCotizacion(@PathVariable Integer id,
            @RequestParam Integer productoId,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) BigDecimal precioUnitario,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "ERROR: No autenticado";
        }

        try {
            Optional<Producto> productoOpt = productoService.obtenerProductoPorId(productoId);
            if (productoOpt.isEmpty()) {
                return "ERROR: Producto no encontrado";
            }

            CotizacionDetalle detalle = new CotizacionDetalle();
            detalle.setProducto(productoOpt.get());
            detalle.setCantidad(cantidad);

            // Usar precio personalizado si se proporciona, sino usar precio del producto
            if (precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) > 0) {
                detalle.setPrecioUnitario(precioUnitario);
            } else {
                detalle.setPrecioUnitario(productoOpt.get().getPrecio());
            }

            cotizacionService.agregarDetalleCotizacion(id, detalle);
            return "SUCCESS: Detalle agregado correctamente";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * API para eliminar detalle de cotización (AJAX) 
     */
    @PostMapping("/{id}/eliminar-detalle/{detalleId}")
    @ResponseBody
    public String eliminarDetalleCotizacion(@PathVariable Integer id,
            @PathVariable Integer detalleId,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "ERROR: No autenticado";
        }

        try {
            cotizacionService.removerDetalleCotizacion(id, detalleId);
            return "SUCCESS: Detalle eliminado correctamente";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}

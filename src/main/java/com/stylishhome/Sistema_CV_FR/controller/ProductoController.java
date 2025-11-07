package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.Producto;
import com.stylishhome.Sistema_CV_FR.service.ProductoService;
import com.stylishhome.Sistema_CV_FR.service.CategoriaService;
import com.stylishhome.Sistema_CV_FR.service.ProveedorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de productos
 */
@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ProveedorService proveedorService;
    
    /**
     * Muestra la lista de todos los productos
     */
    @GetMapping
    public String listarProductos(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Lista de Productos");
        
        return "productos/lista";
    }
    
    /**
     * Muestra el formulario para crear un nuevo producto
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        model.addAttribute("proveedores", proveedorService.obtenerTodosLosProveedores());
        model.addAttribute("titulo", "Nuevo Producto");
        model.addAttribute("modo", "crear");
        
        return "productos/formulario";
    }
    
    /**
     * Procesa el guardado de un nuevo producto
     */
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            productoService.guardarProducto(producto);
            redirectAttributes.addFlashAttribute("success", "Producto guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("producto", producto);
            return "redirect:/productos/nuevo";
        }
        
        return "redirect:/productos";
    }
    
    /**
     * Muestra los detalles de un producto específico
     */
    @GetMapping("/{id}")
    public String verDetallesProducto(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);
        if (productoOpt.isEmpty()) {
            model.addAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }
        
        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("titulo", "Detalles del Producto");
        
        return "productos/detalle";
    }
    
    /**
     * Muestra el formulario para editar un producto existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);
        if (productoOpt.isEmpty()) {
            model.addAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }
        
        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        model.addAttribute("proveedores", proveedorService.obtenerTodosLosProveedores());
        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("modo", "editar");
        
        return "productos/formulario";
    }
    
    /**
     * Procesa la actualización de un producto existente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable Integer id,
                                   @ModelAttribute Producto producto,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            producto.setIdProducto(id); // Asegurar que el ID sea el correcto
            productoService.guardarProducto(producto);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "redirect:/productos/editar/" + id;
        }
        
        return "redirect:/productos";
    }
    
    /**
     * Elimina un producto
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        
        return "redirect:/productos";
    }
    
    /**
     * Busca productos por criterios
     */
    @GetMapping("/buscar")
    public String buscarProductos(@RequestParam(required = false) String criterio,
                                Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Producto> productos;
        if (criterio != null && !criterio.trim().isEmpty()) {
            productos = productoService.buscarProductosPorNombre(criterio);
            model.addAttribute("criterio", criterio);
        } else {
            productos = productoService.obtenerTodosLosProductos();
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Resultados de Búsqueda");
        
        return "productos/lista";
    }
    
    /**
     * Muestra productos con stock bajo
     */
    @GetMapping("/stock-bajo")
    public String listarProductosStockBajo(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        List<Producto> productos = productoService.obtenerProductosStockBajo();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Productos con Stock Bajo");
        model.addAttribute("filtroStockBajo", true);
        
        return "productos/lista";
    }
    
    /**
     * API para obtener productos en formato JSON (para AJAX)
     */
    @GetMapping("/api/listar")
    @ResponseBody
    public List<Producto> listarProductosApi(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return List.of();
        }
        return productoService.obtenerTodosLosProductos();
    }
    
    /**
     * API para buscar productos por criterio (para AJAX)
     */
    @GetMapping("/api/buscar")
    @ResponseBody
    public List<Producto> buscarProductosApi(@RequestParam String criterio, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return List.of();
        }
        return productoService.buscarProductosPorNombre(criterio);
    }
}
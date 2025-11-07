package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Producto;
import com.stylishhome.Sistema_CV_FR.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de productos
 * Contiene la lógica de negocio para operaciones relacionadas con productos
 */
@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorService proveedorService;

    /**
     * Obtiene todos los productos
     */
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su ID
     */
    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id);
    }

    /**
     * Busca un producto por su código
     */
    public Optional<Producto> obtenerProductoPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    /**
     * Guarda un producto (crear o actualizar)
     */
    public Producto guardarProducto(Producto producto) {
        // Validaciones antes de guardar
        validarProducto(producto);
        
        // Verificar duplicados de código para nuevos productos
        if (producto.getIdProducto() == null) {
            if (productoRepository.existsByCodigo(producto.getCodigo())) {
                throw new RuntimeException("Ya existe un producto con el código: " + producto.getCodigo());
            }
        } else {
            // Para actualizaciones, verificar que el código no esté siendo usado por otro producto
            Optional<Producto> productoExistente = productoRepository.findById(producto.getIdProducto());
            if (productoExistente.isPresent() && 
                !productoExistente.get().getCodigo().equals(producto.getCodigo()) &&
                productoRepository.existsByCodigo(producto.getCodigo())) {
                throw new RuntimeException("Ya existe otro producto con el código: " + producto.getCodigo());
            }
        }

        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID
     */
    public void eliminarProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No se encontró el producto con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Busca productos por nombre (búsqueda parcial)
     */
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Busca productos por categoría
     */
    public List<Producto> obtenerProductosPorCategoria(Integer categoriaId) {
        // ✅ CORREGIDO: Cambio de findByCategoriaId a findByCategoriaIdCategoria
        return productoRepository.findByCategoriaIdCategoria(categoriaId);
    }

    /**
     * Obtiene productos con stock bajo (menos de 10 unidades)
     */
    public List<Producto> obtenerProductosStockBajo() {
        return productoRepository.findByStockLessThan(10);
    }

    /**
     * Obtiene productos con stock crítico (menos de 5 unidades)
     */
    public List<Producto> obtenerProductosStockCritico() {
        return productoRepository.findProductosStockCritico();
    }

    /**
     * Actualiza el stock de un producto
     */
    public void actualizarStock(Integer productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    /**
     * Verifica si hay stock suficiente para una venta
     */
    public boolean verificarStock(Integer productoId, Integer cantidadRequerida) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return producto.getStock() >= cantidadRequerida;
    }

    /**
     * Busca productos por múltiples criterios
     */
    public List<Producto> buscarProductos(String nombre, Integer categoriaId, Integer proveedorId) {
        return productoRepository.buscarProductos(nombre, categoriaId, proveedorId);
    }

    /**
     * Obtiene productos por rango de precios
     */
    public List<Producto> obtenerProductosPorRangoPrecio(Double precioMin, Double precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax);
    }

    /**
     * Obtiene el número total de productos
     */
    public Long contarTotalProductos() {
        return productoRepository.count();
    }

    /**
     * Obtiene el número de productos con stock bajo
     */
    public Long contarProductosStockBajo() {
        return productoRepository.countByStockLessThan(10);
    }

    /**
     * Obtiene estadísticas de productos
     */
    public java.util.Map<String, Object> obtenerEstadisticasProductos() {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
        
        estadisticas.put("totalProductos", contarTotalProductos());
        estadisticas.put("productosStockBajo", contarProductosStockBajo());
        estadisticas.put("productosStockCritico", (long) obtenerProductosStockCritico().size());
        estadisticas.put("categoriasActivas", categoriaService.obtenerCategoriasConProductosEnStock().size());
        
        return estadisticas;
    }

    /**
     * Valida los datos de un producto antes de guardar
     */
    private void validarProducto(Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            throw new RuntimeException("El código del producto es obligatorio");
        }
        
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        
        if (producto.getPrecio() == null || producto.getPrecio().doubleValue() <= 0) {
            throw new RuntimeException("El precio del producto debe ser mayor a cero");
        }
        
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new RuntimeException("La categoría del producto es obligatoria");
        }
    }
}
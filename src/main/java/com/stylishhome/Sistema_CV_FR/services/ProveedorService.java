package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Proveedor;
import com.stylishhome.Sistema_CV_FR.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de proveedores
 */
@Service
public class ProveedorService {
    
    @Autowired
    private ProveedorRepository proveedorRepository;
    
    /**
     * Obtiene todos los proveedores ordenados por nombre
     */
    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorRepository.findAllByOrderByNombreAsc();
    }
    
    /**
     * Busca un proveedor por su ID
     */
    public Optional<Proveedor> obtenerProveedorPorId(Integer id) {
        return proveedorRepository.findById(id);
    }
    
    /**
     * Busca un proveedor por su nombre
     */
    public Optional<Proveedor> obtenerProveedorPorNombre(String nombre) {
        return proveedorRepository.findByNombre(nombre);
    }
    
    /**
     * Guarda un proveedor (crear o actualizar)
     */
    public Proveedor guardarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }
    
    /**
     * Elimina un proveedor por su ID
     */
    public void eliminarProveedor(Integer id) {
        proveedorRepository.deleteById(id);
    }
    
    /**
     * Verifica si existe un proveedor con el nombre especificado
     */
    public boolean existeProveedor(String nombre) {
        return proveedorRepository.existsByNombre(nombre);
    }
    
    /**
     * Verifica si existe un proveedor con el email especificado
     */
    public boolean existeProveedorPorEmail(String email) {
        return proveedorRepository.existsByEmail(email);
    }
    
    /**
     * Busca proveedores por nombre (búsqueda parcial)
     */
    public List<Proveedor> buscarProveedoresPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    /**
     * Obtiene el número de productos de un proveedor
     */
    public Long contarProductosPorProveedor(Integer proveedorId) {
        return proveedorRepository.countProductosByProveedorId(proveedorId);
    }
    
    /**
     * Obtiene los proveedores principales
     */
    public List<Object[]> obtenerProveedoresPrincipales(int limite) {
        return proveedorRepository.findProveedoresPrincipales(limite);
    }
    
    /**
     * Busca proveedores por múltiples criterios
     */
    public List<Proveedor> buscarProveedores(String nombre, String contacto) {
        return proveedorRepository.buscarProveedores(nombre, contacto);
    }
}
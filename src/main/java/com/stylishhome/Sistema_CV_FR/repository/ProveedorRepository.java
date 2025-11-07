package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de proveedores
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    
    /**
     * Busca un proveedor por su nombre (búsqueda exacta)
     * @param nombre Nombre del proveedor a buscar
     * @return Optional con el proveedor encontrado
     */
    Optional<Proveedor> findByNombre(String nombre);
    
    /**
     * Busca proveedores por nombre (búsqueda parcial case-insensitive)
     * @param nombre Fragmento del nombre a buscar
     * @return Lista de proveedores que coinciden con el criterio
     */
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca proveedores por contacto
     * @param contacto Nombre del contacto a buscar
     * @return Lista de proveedores que coinciden con el contacto
     */
    List<Proveedor> findByContactoContainingIgnoreCase(String contacto);
    
    /**
     * Busca proveedores por email
     * @param email Email del proveedor
     * @return Optional con el proveedor encontrado
     */
    Optional<Proveedor> findByEmail(String email);
    
    /**
     * Verifica si existe un proveedor con el nombre especificado
     * @param nombre Nombre del proveedor a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Verifica si existe un proveedor con el email especificado
     * @param email Email del proveedor a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Obtiene todos los proveedores ordenados por nombre de forma ascendente
     * @return Lista de proveedores ordenados alfabéticamente
     */
    List<Proveedor> findAllByOrderByNombreAsc();
    
    /**
     * Cuenta el número de productos asociados a un proveedor
     * @param proveedorId ID del proveedor
     * @return Número de productos del proveedor
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.proveedor.idProveedor = :proveedorId")
    Long countProductosByProveedorId(@Param("proveedorId") Integer proveedorId);
    
    /**
     * Obtiene el número total de proveedores activos
     * @return Conteo de proveedores
     */
    @Query("SELECT COUNT(p) FROM Proveedor p")
    Long countTotalProveedores();
    
    /**
     * Busca proveedores por múltiples criterios
     * @param nombre Nombre del proveedor (opcional)
     * @param contacto Contacto del proveedor (opcional)
     * @return Lista de proveedores que coinciden con los criterios
     */
    @Query("SELECT p FROM Proveedor p WHERE " +
           "(:nombre IS NULL OR p.nombre LIKE %:nombre%) AND " +
           "(:contacto IS NULL OR p.contacto LIKE %:contacto%)")
    List<Proveedor> buscarProveedores(@Param("nombre") String nombre,
                                    @Param("contacto") String contacto);
    
    /**
     * Obtiene proveedores con información de productos
     * @return Lista de proveedores con conteo de productos
     */
    @Query("SELECT p.idProveedor, p.nombre, p.contacto, p.telefono, p.email, p.notas, COUNT(prod) as productCount " +
           "FROM Proveedor p LEFT JOIN Producto prod ON prod.proveedor.idProveedor = p.idProveedor " +
           "GROUP BY p.idProveedor, p.nombre, p.contacto, p.telefono, p.email, p.notas " +
           "ORDER BY productCount DESC")
    List<Object[]> findProveedoresConConteoProductos();
    
    /**
     * Obtiene los proveedores principales (con más productos)
     * @param limit Número máximo de proveedores a retornar
     * @return Lista de proveedores ordenados por cantidad de productos
     */
    @Query("SELECT p.idProveedor, p.nombre, p.contacto, p.telefono, p.email, p.notas, COUNT(prod) as productCount " +
           "FROM Proveedor p LEFT JOIN Producto prod ON prod.proveedor.idProveedor = p.idProveedor " +
           "GROUP BY p.idProveedor, p.nombre, p.contacto, p.telefono, p.email, p.notas " +
           "ORDER BY productCount DESC LIMIT :limit")
    List<Object[]> findProveedoresPrincipales(@Param("limit") int limit);
    
    /**
     * Busca proveedores por teléfono
     * @param telefono Número de teléfono a buscar
     * @return Lista de proveedores que coinciden con el teléfono
     */
    List<Proveedor> findByTelefono(String telefono);
    
    /**
     * Obtiene proveedores con productos de una categoría específica
     * @param categoriaId ID de la categoría
     * @return Lista de proveedores que tienen productos en la categoría
     */
    @Query("SELECT DISTINCT p FROM Proveedor p JOIN Producto prod ON prod.proveedor.idProveedor = p.idProveedor " +
           "WHERE prod.categoria.idCategoria = :categoriaId")
    List<Proveedor> findProveedoresByCategoriaProducto(@Param("categoriaId") Integer categoriaId);
}